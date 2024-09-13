package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;

import androidx.appcompat.app.AppCompatActivity;


import com.android.volley.AuthFailureError;
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
import java.util.Map;


public class Register extends AppCompatActivity {
    TextInputEditText usernameEdit, emailEdit, passwordEdit, confirmPassEdit;
    Button btnReg, btnLogin;
    ProgressBar progressBar;

    @Override
    protected void onStart() {
        super.onStart();
//        if (user != null) {
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(intent);
//            finish();
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        usernameEdit = findViewById(R.id.username);
        emailEdit = findViewById(R.id.email);
        passwordEdit = findViewById(R.id.password);
        confirmPassEdit = findViewById(R.id.passwordConfirm);
        btnReg = findViewById(R.id.register);
        progressBar = findViewById(R.id.progressBar);
        btnLogin = findViewById(R.id.login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String username, email, password, confirmPass;
                username = String.valueOf(usernameEdit.getText());
                email = String.valueOf(emailEdit.getText());
                password = String.valueOf(passwordEdit.getText());
                confirmPass = String.valueOf(confirmPassEdit.getText());
                String apiKey = ApiEndPoint.REGISTER;
                if (TextUtils.isEmpty(confirmPass)) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Register.this, "Enter user name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Register.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Register.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(confirmPass) || !confirmPass.equals(password)) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Register.this, "Password not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                RequestQueue requestQueue = Volley.newRequestQueue(Register.this);
                final HashMap<String, String> params = new HashMap<>();
                params.put("name", username);
                params.put("email", email);
                params.put("password", password);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, apiKey, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(Register.this, "Account created", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                System.out.println("error: "+ error);
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(Register.this, "Account create fail", Toast.LENGTH_SHORT).show();
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/json");
                        return headers;
                    }
                };
                requestQueue.add(jsonObjectRequest);
            }
        });
    }
}