package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.apis.ApiEndPoint;
import com.example.myapplication.utils.SharedPreferenceClass;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class SearchFragment extends Fragment {
    SharedPreferenceClass sharedPreferenceClass;
    RequestQueue requestQueue;
    ImageView btnSwitch;
    TextInputLayout searchLayout, translateLayout;
    String from = "de", to = "vi", searchHint = "Wort importieren", translateHint = "Nhập từ";
    TextInputEditText textSearch, textTranslate;



    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        btnSwitch = view.findViewById(R.id.btn_switch);
        textSearch = view.findViewById(R.id.text_translate);
        textTranslate = view.findViewById(R.id.text_translation);
        searchLayout = view.findViewById(R.id.search_layout);
        translateLayout = view.findViewById(R.id.translate_layout);
        textSearch.setHint(searchHint);
        textTranslate.setHint(translateHint);
        searchLayout.setHint(searchHint);
        translateLayout.setHint(translateHint);
        sharedPreferenceClass = new SharedPreferenceClass(getContext());
        requestQueue = Volley.newRequestQueue(getContext());
        final long timeoutDebounced = 500;
        Handler handler = new Handler();
        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                textSearch.setText("");
                textTranslate.setText("");
                String c = from;
                from = to;
                to = c;
                String d = searchHint;
                searchHint = translateHint;
                translateHint = d;
                textSearch.setHint(searchHint);
                textTranslate.setHint(translateHint);
                searchLayout.setHint(searchHint);
                translateLayout.setHint(translateHint);
            }
        });
        textSearch.addTextChangedListener(new TextWatcher() {
            private Runnable runnable;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (runnable != null) {
                    handler.removeCallbacks(runnable);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        String input = editable.toString();
                        Log.d("Debounced", "IÏnput after debounced: " + input);
                        if (!TextUtils.isEmpty(input.trim())) {
                            translateText(input.trim());
                        }
                    }
                };
                handler.postDelayed(runnable, timeoutDebounced);
            }
        });
        return view;
    }
    private void translateText(String text) {
        String apiKey = ApiEndPoint.TRANSLATE;
        HashMap<String, String> params = new HashMap<>();
        params.put("text",text);
        params.put("from", from);
        params.put("to", to);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, apiKey, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String translateText = null;
                        try {
                            translateText = response.getString("translatedText");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        textTranslate.setText(translateText);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Không thể dịch từ. Vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
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
}