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
import com.example.myapplication.adapter.TopicAdapter;
import com.example.myapplication.apis.ApiEndPoint;
import com.example.myapplication.model.Topic;
import com.example.myapplication.utils.SharedPreferenceClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VocabularyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VocabularyFragment extends Fragment {
    RecyclerView recyclerView;
    RequestQueue requestQueue;
    SharedPreferenceClass sharedPreferenceClass;
    List<Topic> topicList = new ArrayList<>();
    TopicAdapter topicAdapter;

    public static VocabularyFragment newInstance(String param1, String param2) {
        VocabularyFragment fragment = new VocabularyFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vocabulary, container, false);
        sharedPreferenceClass = new SharedPreferenceClass(getContext());
        recyclerView= view.findViewById(R.id.topic_recycle_view);
        topicAdapter = new TopicAdapter(getContext(), topicList);
        recyclerView.setAdapter(topicAdapter);
        topicAdapter.setOnItemClickListener(new  TopicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Topic topic, int position) {
                Fragment detailFragment = TopicDetailFragment.newInstance(topic, new ArrayList<>(topicList), position);
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, detailFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        requestQueue = Volley.newRequestQueue(getContext());
        fetchTopicData();
        return view;
    }

    private void fetchTopicData() {
        String apiKey = ApiEndPoint.VOCABULARY_ALL; // Thay đổi URL theo API của bạn
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiKey, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray topicArray = response.getJSONArray("topics");
                            List<Topic> data = new ArrayList<>();
                            for (int i = 0; i < topicArray.length(); i++) {
                                JSONObject item = topicArray.getJSONObject(i);
                                Topic topic = new Topic();
                                topic.setId(item.getString("_id"));
                                topic.setName(item.getString("name"));
                                topic.setDescription(item.getString("description"));
                                data.add(topic);
                            }
                            topicList.clear();
                            topicList.addAll(data);
                            topicAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Error parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
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