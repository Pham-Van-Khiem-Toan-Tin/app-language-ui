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
import com.example.myapplication.utils.SharedPreferenceClass;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;


public class Login extends AppCompatActivity {
    TextInputEditText emailEdit, passwordEdit;
    Button btnReg, btnLogin, btnForgot;
    ProgressBar progressBar;
    SharedPreferenceClass sharedPreferenceClass;
    @Override
    protected void onStart() {
        super.onStart();
        String token = sharedPreferenceClass.getValue_string("token");
        String email = sharedPreferenceClass.getValue_string("email");
        String name = sharedPreferenceClass.getValue_string("name");
        String role = sharedPreferenceClass.getValue_string("role");
        if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(name) && !TextUtils.isEmpty(role)) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        emailEdit = findViewById(R.id.email);
        passwordEdit = findViewById(R.id.password);
        btnReg = findViewById(R.id.register);
        btnLogin = findViewById(R.id.login);
        btnForgot = findViewById(R.id.forgot);
        sharedPreferenceClass = new SharedPreferenceClass(this);
        btnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ForgotPassword.class);
                startActivity(intent);
                finish();
            }
        });
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                finish();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, password;
                email = String.valueOf(emailEdit.getText());
                password = String.valueOf(passwordEdit.getText());
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Login.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Login.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                String apiKey = ApiEndPoint.LOGIN;
                RequestQueue requestQueue = Volley.newRequestQueue(Login.this);
                final HashMap<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, apiKey, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String id = response.getString("id");
                                    String name = response.getString("name");
                                    String email = response.getString("email");
                                    String role = response.getString("role");
                                    String token = response.getString("token");
                                    sharedPreferenceClass.setValue_string("id", id);
                                    sharedPreferenceClass.setValue_string("token", token);
                                    sharedPreferenceClass.setValue_string("email", email);
                                    sharedPreferenceClass.setValue_string("name", name);
                                    sharedPreferenceClass.setValue_string("role", role);
                                    Toast.makeText(Login.this, "Login success", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(Login.this, "Login fail", Toast.LENGTH_SHORT).show();
                            }
                        }) {
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