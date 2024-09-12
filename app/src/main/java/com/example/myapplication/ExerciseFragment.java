package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.adapter.ExerciserApdater;
import com.example.myapplication.model.Exercise;
import com.example.myapplication.utils.SharedPreferenceClass;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ExerciseFragment extends Fragment {
    RecyclerView recyclerView;
    SharedPreferenceClass sharedPreferenceClass;
    RequestQueue requestQueue;
    List<Exercise> exerciseList = new ArrayList<>();
    ExerciserApdater exerciserApdater;
    public static ExerciseFragment newInstance() {
        ExerciseFragment fragment = new ExerciseFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_exercise, container, false);
        sharedPreferenceClass = new SharedPreferenceClass(getContext());
        requestQueue = Volley.newRequestQueue(getContext());
        recyclerView = view.findViewById(R.id.exercise_recycle_view);
        exerciserApdater = new ExerciserApdater(getContext(), exerciseList);
        recyclerView.setAdapter(exerciserApdater);
        exerciserApdater.setOnItemClickListener(new ExerciserApdater.OnItemClickListener() {
            @Override
            public void onItemClick(Exercise exercise, int position) {
                Fragment fragment = ExerciseDetailFragment.newInstance(exercise, new ArrayList<>(exerciseList), position);
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        fetchData();
        return view;
    }
    private void fetchData() {
        String apiKey = "http://192.168.1.7:8000/api/exercise/all";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiKey, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        Type exerciseListType = new TypeToken<List<Exercise>>() {}.getType();
                        try {
                            List<Exercise> data = gson.fromJson(response.getString("exercises").toString(), exerciseListType);
                            exerciseList.clear();
                            exerciseList.addAll(data);
                            exerciserApdater.notifyDataSetChanged();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(getContext(), "Không thể tải danh sách bài tập. Vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
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