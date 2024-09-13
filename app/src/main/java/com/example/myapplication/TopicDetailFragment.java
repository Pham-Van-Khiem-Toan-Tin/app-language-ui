package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.apis.ApiEndPoint;
import com.example.myapplication.model.Topic;
import com.example.myapplication.model.Vocabulary;
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


public class TopicDetailFragment extends Fragment implements QuestionTopicFragment.OnAnswerSelectedListener {


    private static final String ARG_TOPIC = "topic";
    private static final String ARG_TOPIC_LIST = "topic_list";
    private static final String CURRENT_INDEX = "current_index";
    int currentIndexWord = 0;
    boolean isAnswerSelected = false;
    int currentIndexQuestion = 0;
    List<Boolean> questionAnswer = new ArrayList<>();
    Button btnNext;
    boolean inListening;
    RequestQueue requestQueue;
    SharedPreferenceClass sharedPreferenceClass;
    List<Vocabulary> vocabularyList;
    List<Topic> topicList;

    public static TopicDetailFragment newInstance(Topic topic, ArrayList<Topic> topics, int currentIndex) {
        TopicDetailFragment fragment = new TopicDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TOPIC, topic);
        args.putSerializable(ARG_TOPIC_LIST, topics);
        args.putInt(CURRENT_INDEX, currentIndex);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_topic_detail, container, false);
        requestQueue = Volley.newRequestQueue(getContext());
        sharedPreferenceClass = new SharedPreferenceClass(getContext());
        btnNext = view.findViewById(R.id.btn_next);
        topicList = (List<Topic>) getArguments().getSerializable(ARG_TOPIC_LIST);
        int currentIndexTopic = getArguments().getInt(CURRENT_INDEX);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Vocabulary vocabulary = vocabularyList.get(currentIndexWord);
                if (currentIndexWord == vocabularyList.size() - 1 && currentIndexQuestion == vocabularyList.get(currentIndexWord).getQuestions().size() - 1) {
                    FragmentManager fragmentManager = getParentFragmentManager();
                    fragmentManager.popBackStack();
                } else {
                    if (inListening) {
                        navigateToQuestionFragment(vocabulary);
                    } else {
                        if (currentIndexQuestion < vocabulary.getQuestions().size() - 1) {
                            currentIndexQuestion++;
                            navigateToQuestionFragment(vocabulary);
                        } else {
                            boolean completeResult = questionAnswer.stream().allMatch((question) -> question);
                            completeWord(topicList.get(currentIndexTopic).getId(), vocabularyList.get(currentIndexWord).get_id(), completeResult);
                        }
                    }
                }
            }
        });

        // Inflate the layout for this fragment
        Topic topic = (Topic) getArguments().getSerializable(ARG_TOPIC);
        fetchTopicDetailData(topic.getId());
        return view;
    }

    private void fetchTopicDetailData(String topicId) {
        String apiKey = ApiEndPoint.VOCABULARY_TOPIC + topicId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiKey, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        Type vocabularyListType = new TypeToken<List<Vocabulary>>() {}.getType();
                        try {
                            vocabularyList = gson.fromJson(response.getString("words").toString(), vocabularyListType);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        if (vocabularyList != null && !vocabularyList.isEmpty()) {
                            Vocabulary firstVocabulary = vocabularyList.get(currentIndexWord);
                            navigateToListeningFragment(firstVocabulary);
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
    private void navigateToListeningFragment(Vocabulary vocabulary) {
        Fragment fragment = ListeningFragment.newInstance(vocabulary);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.topic_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        inListening = true;
    }
    private void navigateToQuestionFragment(Vocabulary vocabulary) {
        btnNext.setEnabled(false);
        Fragment fragment = QuestionTopicFragment.newInstance(vocabulary.getQuestions().get(currentIndexQuestion));
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.topic_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        inListening = false;
    }


    @Override
    public void onAnswerSelected(boolean isAnswerSelected, boolean result) {
        this.isAnswerSelected = isAnswerSelected;
        btnNext.setEnabled(this.isAnswerSelected);
        questionAnswer.add(result);
    }
    private void completeWord(String topicId, String wordId, boolean completeResult) {
        if (completeResult) {

            String apiKey = ApiEndPoint.VOCABULARY_TOPIC_COMPLETE;
            JSONObject requestBody = new JSONObject();

            try {
                requestBody.put("topicId", topicId);
                requestBody.put("wordId", wordId);
                requestBody.put("learned", true);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, apiKey, requestBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String message = response.getString("message");
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                currentIndexQuestion = 0;
                                currentIndexWord++;
                                navigateToListeningFragment(vocabularyList.get(currentIndexWord));
                                questionAnswer.clear();
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast.makeText(getContext(), "Không thể hoàn thành học từ mới", Toast.LENGTH_SHORT).show();
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
        } else {
            currentIndexQuestion = 0;
            currentIndexWord++;
            navigateToListeningFragment(vocabularyList.get(currentIndexWord));
            questionAnswer.clear();
        }
    }
}