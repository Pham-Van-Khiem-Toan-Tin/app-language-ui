package com.example.myapplication;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.apis.ApiEndPoint;
import com.example.myapplication.image.ImageEnum;
import com.example.myapplication.model.Vocabulary;
import com.example.myapplication.utils.SharedPreferenceClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;


public class ListeningFragment extends Fragment {

    TextView textViewFrom, textViewTo;
    String mp3Path;
    ImageView btnSpeak, imageViewVocabulary;
    MediaPlayer mediaPlayer;
    File audioFile;
    private static final String ARG_VOCABULARY = "vocabulary";
    SharedPreferenceClass sharedPreferenceClass;
    RequestQueue requestQueue;


    public static ListeningFragment newInstance(Vocabulary vocabulary) {
        ListeningFragment fragment = new ListeningFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_VOCABULARY, vocabulary);

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_listening, container, false);
        textViewFrom = view.findViewById(R.id.word_from);
        textViewTo = view.findViewById(R.id.word_to);
        imageViewVocabulary = view.findViewById(R.id.vocabulary_image);
        btnSpeak = view.findViewById(R.id.btn_speak);
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadAndPlayAudio();
            }
        });

        requestQueue = Volley.newRequestQueue(getContext());
        sharedPreferenceClass = new SharedPreferenceClass(getContext());
        Vocabulary vocabulary = (Vocabulary) getArguments().getSerializable(ARG_VOCABULARY);
        translateWord(vocabulary.getWord());
        return view;
    }

    private void translateWord(String text) {
        String apiKey = ApiEndPoint.TRANSLATE;
        HashMap<String, String> params = new HashMap<>();
        params.put("text", text);
        params.put("from", "de");
        params.put("to", "vi");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, apiKey, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            textViewFrom.setText(response.getString("text"));
                            textViewTo.setText(response.getString("translatedText"));
                            String imageFileName = convertToDrawableName(response.getString("text"));
                            int imageId = ImageEnum.getImageResourceId(imageFileName);
                            imageViewVocabulary.setImageResource(imageId);
                            mp3Path = response.getString("mp3Url");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + sharedPreferenceClass.getValue_string("token"));
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
    private String convertToDrawableName(String vocabulary) {
        vocabulary = vocabulary.toLowerCase();
        vocabulary = vocabulary.replace("ü", "u");
        vocabulary = vocabulary.replace(" ", "_");
        return vocabulary;
    }
    private void downloadAndPlayAudio() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, ApiEndPoint.URL + mp3Path,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do nothing for now
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) {
            @Override
            protected Response<String> parseNetworkResponse(com.android.volley.NetworkResponse response) {
                try {
                    InputStream inputStream = new ByteArrayInputStream(response.data);
                    File tempFile = new File(getContext().getCacheDir(), "audio.mp3");
                    OutputStream outputStream = new FileOutputStream(tempFile);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, length);
                    }
                    outputStream.close();
                    inputStream.close();

                    // Create MediaPlayer and start playing
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(tempFile.getAbsolutePath());
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return Response.success("", HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        requestQueue.add(stringRequest);
    }
}