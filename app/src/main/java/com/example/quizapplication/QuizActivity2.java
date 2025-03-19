package com.example.quizapplication;

import android.content.SharedPreferences;
import android.os.Bundle;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;


public class QuizActivity2 extends AppCompatActivity {

    private QuizViewModel quizViewModel;
    private String currentCorrectAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_quiz2);

        quizViewModel = new ViewModelProvider(this).get(QuizViewModel.class);

        SharedPreferences prefs = getSharedPreferences("quiz_prefs", MODE_PRIVATE);
        boolean willContinue = prefs.getBoolean("Continue", false);
        if(willContinue) {
            int savedIndex = prefs.getInt("savedIndex", 0);
            int savedScore = prefs.getInt("savedScore", 0);
            quizViewModel.currentIndex = savedIndex;
            quizViewModel.score = savedScore;
        }


        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new QuizFragment()).commit();


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("CURRENT_INDEX", quizViewModel.currentIndex);
        outState.putInt("SCORE", quizViewModel.score);

    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = getSharedPreferences("quiz_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("savedIndex", quizViewModel.currentIndex);
        editor.putInt("savedScore", quizViewModel.score);
        editor.apply();
    }
    public void setCurrentCorrectAnswer(String answer) {
        currentCorrectAnswer = answer;
    }
    public String getCorrectAnswer() {
        return currentCorrectAnswer;
    }

}
