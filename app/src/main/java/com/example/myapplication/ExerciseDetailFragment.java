package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.myapplication.model.Exercise;
import com.example.myapplication.model.Question;
import com.example.myapplication.utils.SharedPreferenceClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ExerciseDetailFragment extends Fragment implements ExerciseQuestionFragment.OnAnswerSelectedListener {
    private  static  final  String ARG_EXERCISE = "exercise";
    private  static  final  String ARG_EXERCISE_LIST = "exercise_list";
    private  static  final  String CURRENT_INDEX = "current_index";
    int currentIndexQuestion = 0;
    TextView paragraphTextView;
    SharedPreferenceClass sharedPreferenceClass;
    RequestQueue requestQueue;
    Button btnNext;
    List<Exercise> exerciseList;
    List<Boolean> questionAnswer = new ArrayList<>();
    boolean isAnswerSelected = false;






    public static ExerciseDetailFragment newInstance(Exercise exercise, ArrayList<Exercise> exercises, int currentIndex) {
        ExerciseDetailFragment fragment = new ExerciseDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_EXERCISE, exercise);
        args.putSerializable(ARG_EXERCISE_LIST, exercises);
        args.putInt(CURRENT_INDEX, currentIndex);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_exercise_detail, container, false);
        sharedPreferenceClass = new SharedPreferenceClass(getContext());
        requestQueue = Volley.newRequestQueue(getContext());
        btnNext = view.findViewById(R.id.btn_next);
        paragraphTextView = view.findViewById(R.id.tv_paragraph);
        int currentIndex = getArguments().getInt(CURRENT_INDEX);
        exerciseList = (List<Exercise>) getArguments().getSerializable(ARG_EXERCISE_LIST);
        Exercise exercise = exerciseList.get(currentIndex);
        paragraphTextView.setText(exercise.getDescription());
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentIndexQuestion == exercise.getQuestions().size() -1) {
                    boolean completed = questionAnswer.stream().allMatch(item -> item);
                    float score = questionAnswer.stream().filter(item -> item).count() * 10 / exercise.getQuestions().size();
                    completaExercise(exercise.get_id(),completed,score);
                } else {
                    currentIndexQuestion++;
                    navigationQuestionFragment(exercise);
                }
            }
        });
        navigationQuestionFragment(exerciseList.get(currentIndex));
        return view;
    }
    private void navigationQuestionFragment(Exercise exercise) {
        btnNext.setEnabled(false);
        Fragment fragment = ExerciseQuestionFragment.newInstance(exercise.getQuestions().get(currentIndexQuestion));
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.exercise_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void completaExercise(String exerciseId, boolean completed, float score) {
        String apiKey = ApiEndPoint.EXERCISE_COMPLETE;
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("exerciseId", exerciseId);
            requestBody.put("completed", completed);
            requestBody.put("score", score);
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
                            questionAnswer.clear();
                            FragmentManager fragmentManager = getParentFragmentManager();
                            fragmentManager.popBackStack();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(getContext(), "Không thể cập nhật bài tập. Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
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
    @Override
    public void onAnswerSelected(boolean isAnswerSelected, boolean result) {
        this.isAnswerSelected = isAnswerSelected;
        btnNext.setEnabled(this.isAnswerSelected);
        questionAnswer.add(result);
    }
}