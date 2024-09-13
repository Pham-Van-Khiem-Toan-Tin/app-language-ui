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
import com.example.myapplication.adapter.GrammarAdapter;
import com.example.myapplication.apis.ApiEndPoint;
import com.example.myapplication.model.Grammar;
import com.example.myapplication.utils.SharedPreferenceClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GrammarFragment extends Fragment {
    RecyclerView recyclerView;
    GrammarAdapter grammarAdapter;
    List<Grammar> grammarList = new ArrayList<>();
    RequestQueue requestQueue;
    SharedPreferenceClass sharedPreferenceClass;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grammar, container, false);
        sharedPreferenceClass = new SharedPreferenceClass(getContext());
        recyclerView = view.findViewById(R.id.grammar_recycleview);
        grammarAdapter = new GrammarAdapter(getContext(), grammarList);
        recyclerView.setAdapter(grammarAdapter);
        grammarAdapter.setOnItemClickListener(new GrammarAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Grammar grammar, int position) {
                Fragment detailFragment = GrammarDetailFragment.newInstance(grammar, new ArrayList<>(grammarList), position );
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, detailFragment);
                transaction.addToBackStack(null); // Để quay lại fragment trước đó
                transaction.commit();
            }
        });
        requestQueue = Volley.newRequestQueue(getContext());
        fetchGrammarData();
        return view;
    }

    private void fetchGrammarData() {
        String apiKey = ApiEndPoint.GRAMMAR_ALL; // Thay đổi URL theo API của bạn

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, apiKey, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Xử lý dữ liệu JSON từ phản hồi
                            JSONArray grammarArray = response.getJSONArray("grammars");
                            List<Grammar> data = new ArrayList<>();

                            for (int i = 0; i < grammarArray.length(); i++) {
                                JSONObject item = grammarArray.getJSONObject(i);
                                Grammar grammarItem = new Grammar();
                                grammarItem.setId(item.getString("_id"));
                                grammarItem.setTitle(item.getString("title"));
                                grammarItem.setDescription(item.getString("description"));
                                data.add(grammarItem);
                            }
                            grammarList.clear();
                            grammarList.addAll(data);
                            grammarAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Error parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
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

        // Thêm yêu cầu vào RequestQueue
        requestQueue.add(jsonObjectRequest);
    }
}