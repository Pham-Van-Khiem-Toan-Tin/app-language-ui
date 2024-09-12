package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.model.Question;

import java.util.List;


public class ExerciseQuestionFragment extends Fragment {
    TextView questionViewText;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private final static String ARG_QUESTION = "question";
    private boolean hasAnswered = false;
    private OnAnswerSelectedListener listener;

    public interface OnAnswerSelectedListener {
        void onAnswerSelected(boolean isAnswerSelected, boolean result);
    }
    public static ExerciseQuestionFragment newInstance(Question question) {
        ExerciseQuestionFragment fragment = new ExerciseQuestionFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_QUESTION, question);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof OnAnswerSelectedListener) {
            listener = (OnAnswerSelectedListener) getParentFragment();
        } else if (context instanceof OnAnswerSelectedListener) {
            listener = (OnAnswerSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnAnswerSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_exercise_question, container, false);
        Question question = getArguments().getSerializable(ARG_QUESTION, Question.class);
        questionViewText = view.findViewById(R.id.tv_question);
        questionViewText.setText(question.getQuestion());
        RadioGroup radioGroup = view.findViewById(R.id.rg_options);

        // Lặp qua các options và thêm RadioButton động
        List<String> options = question.getOptions();
        for (int i = 0; i < options.size(); i++) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setText(options.get(i));
            radioButton.setId(View.generateViewId());
            radioGroup.addView(radioButton);
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (hasAnswered) {
                    return;
                }
                RadioButton selectedRadioButton = group.findViewById(checkedId);
                String selectedAnswer = selectedRadioButton.getText().toString();
                boolean result = selectedAnswer.equals(question.getCorrect_answer());
                if (result) {
                    Toast.makeText(getContext(), "Chính xác!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Sai rồi, thử lại nhé!", Toast.LENGTH_SHORT).show();
                }
                for (int i = 0; i < radioGroup.getChildCount(); i++) {
                    radioGroup.getChildAt(i).setEnabled(false);
                }

                hasAnswered = true;
                if (listener != null) {
                    listener.onAnswerSelected(true, result);
                }
            }
        });
        return view;
    }
}