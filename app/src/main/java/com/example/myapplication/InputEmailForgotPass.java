package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.apis.ApiEndPoint;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.util.HashMap;


public class InputEmailForgotPass extends Fragment {
    TextInputEditText emailText;
    Button btnBack, btnNext;
    RequestQueue requestQueue;

    public static InputEmailForgotPass newInstance(String param1, String param2) {
        InputEmailForgotPass fragment = new InputEmailForgotPass();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_input_email_forgot_pass, container, false);
        emailText = view.findViewById(R.id.input_email);
        btnBack = view.findViewById(R.id.button_back);
        btnNext = view.findViewById(R.id.button_next);
        requestQueue = Volley.newRequestQueue(getContext());
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Login.class);
                startActivity(intent);
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = String.valueOf(emailText.getText());
                if (TextUtils.isEmpty(email.trim())) return;
                String apiKey = ApiEndPoint.RESET_PASSWORD_EMAIL;
                HashMap<String, String> params = new HashMap<>();
                params.put("email", email.trim());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, apiKey, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Fragment fragment = InputCodeFragment.newInstance(email.trim());
                                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                transaction.replace(R.id.forgot_pass_container, fragment);
                                transaction.addToBackStack(null);
                                transaction.commit();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getContext(), "Lỗi không xác định. Vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
                requestQueue.add(jsonObjectRequest);
            }
        });
        return view;
    }

}