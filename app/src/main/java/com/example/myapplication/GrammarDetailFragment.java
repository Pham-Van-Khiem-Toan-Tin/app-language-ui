package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.apis.ApiEndPoint;
import com.example.myapplication.model.Grammar;
import com.example.myapplication.utils.SharedPreferenceClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GrammarDetailFragment extends Fragment {
    TextView titleText, descriptionText;
    LinearLayout btnBack;
    Button btnNext;
    List<Grammar> grammarList;
    int currentIndex = 0;
    ScrollView scrollView;
    RequestQueue requestQueue;
    SharedPreferenceClass sharedPreferenceClass;

    private static final String ARG_GRAMMAR = "grammar";
    private static final String ARG_GRAMMAR_LIST = "grammars";
    private static final String CURRENT_INDEX = "current_index";



    public static GrammarDetailFragment newInstance(Grammar grammar, ArrayList<Grammar> grammars, int currentIndex) {
        GrammarDetailFragment fragment = new GrammarDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_GRAMMAR, grammar);
        args.putSerializable(ARG_GRAMMAR_LIST, grammars);
        args.putInt(CURRENT_INDEX, currentIndex);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_grammar_detail, container, false);
        btnBack = view.findViewById(R.id.btn_back);
        btnNext = view.findViewById(R.id.btn_next);
        requestQueue = Volley.newRequestQueue(getContext());
        scrollView = view.findViewById(R.id.grammar_scroll);
        sharedPreferenceClass = new SharedPreferenceClass(getContext());
        // Nhận dữ liệu từ arguments
        Grammar grammar = (Grammar) getArguments().getSerializable(ARG_GRAMMAR);
        grammarList = (List<Grammar>) getArguments().getSerializable(ARG_GRAMMAR_LIST);
        currentIndex = getArguments().getInt(CURRENT_INDEX);
        titleText = view.findViewById(R.id.grammar_detail_title);
        descriptionText = view.findViewById(R.id.grammar_detail_title);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentIndex > 0) {
                    currentIndex--;
                    displayGrammar(currentIndex);
                } else {
                    FragmentManager fragmentManager = getParentFragmentManager();
                    fragmentManager.popBackStack();
                }
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeGrammar(grammarList.get(currentIndex).getId());
                if (currentIndex < grammarList.size() - 1) {
                    currentIndex++;
                    displayGrammar(currentIndex);
                } else {
                    FragmentManager fragmentManager = getParentFragmentManager();
                    fragmentManager.popBackStack();
                }
            }
        });
        titleText.setText(grammar.getTitle());
        descriptionText.setText(Html.fromHtml(grammar.getDescription(), Html.FROM_HTML_MODE_LEGACY));
        // Hiển thị dữ liệu

        return view;
    }
    private void displayGrammar(int index) {
        Grammar currentGrammar = grammarList.get(index);
        titleText.setText(currentGrammar.getTitle());
        descriptionText.setText(Html.fromHtml(currentGrammar.getDescription(), Html.FROM_HTML_MODE_LEGACY));
        scrollView.scrollTo(0,0);
    }
    private void completeGrammar(String id) {
        String apiKey = ApiEndPoint.GRAMMAR_COMPLETE;
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("grammarId", id); // Thay đổi theo dữ liệu của bạn
            requestBody.put("completed", true); // Thay đổi theo dữ liệu của bạn
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, apiKey,requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(getContext(), "Complete fail", Toast.LENGTH_SHORT).show();
                    }
                }) {
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