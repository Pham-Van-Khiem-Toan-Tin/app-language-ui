package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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


public class InputCodeFragment extends Fragment {
    public static final String ARG_EMAIL = "email";
    Button btnBack, btnNext;
    TextInputEditText codeTextEdit;
    RequestQueue requestQueue;

    public static InputCodeFragment newInstance(String email) {
        InputCodeFragment fragment = new InputCodeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_input_code, container, false);
        btnBack = view.findViewById(R.id.button_back);
        btnNext = view.findViewById(R.id.button_next);
        codeTextEdit = view.findViewById(R.id.input_code);
        requestQueue = Volley.newRequestQueue(getContext());
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.popBackStack();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = String.valueOf(codeTextEdit.getText());
                if (TextUtils.isEmpty(code.trim())) return;
                String apiKey = ApiEndPoint.RESET_PASSWORD_CODE + "/" + code;
                String email = getArguments().getString(ARG_EMAIL);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiKey, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Fragment fragment = NewPasswordFragment.newInstance(email);
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