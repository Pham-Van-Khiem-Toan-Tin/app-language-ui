package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class NewPasswordFragment extends Fragment {
    public static final String ARG_EMAIL = "email";
    RequestQueue requestQueue;
    Button btnCancel, btnSubmit;
    TextInputEditText passwordEditText, confirmPasswordEditText;
    public static NewPasswordFragment newInstance(String email) {
        NewPasswordFragment fragment = new NewPasswordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_password, container, false);
        requestQueue = Volley.newRequestQueue(getContext());
        btnCancel = view.findViewById(R.id.button_back);
        btnSubmit = view.findViewById(R.id.button_next);
        passwordEditText = view.findViewById(R.id.input_password);
        confirmPasswordEditText = view.findViewById(R.id.confirm_password);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Login.class);
                startActivity(intent);
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = String.valueOf(passwordEditText.getText());
                String confirmPassword = String.valueOf(confirmPasswordEditText.getText());
                if (TextUtils.isEmpty(password.trim()) || TextUtils.isEmpty(confirmPassword.trim())) {
                    Toast.makeText(getContext(), "Vui lòng điền ầy đủ các trường", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(getContext(), "Mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    String apiKey = ApiEndPoint.RESET_PASSWORD_CHANGE;
                    String email = getArguments().getString(ARG_EMAIL);
                    HashMap<String, String> params = new HashMap<>();
                    params.put("email", email);
                    params.put("newPass", password.trim());
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, apiKey, new JSONObject(params),
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    String message = null;
                                    try {
                                        message = response.getString("message");
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getContext(), Login.class);
                                    startActivity(intent);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getContext(), "Không thể cập nhật mật khẩu. Vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                    requestQueue.add(jsonObjectRequest);
                }
            }
        });
        return view;
    }
}