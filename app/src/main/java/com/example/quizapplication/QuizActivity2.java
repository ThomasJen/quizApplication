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
        EdgeToEdge.enable(this);            // Aktivere edge-to-edge view

        setContentView(R.layout.activity_quiz2);                // Laster layouten til QuizActivity2

        quizViewModel = new ViewModelProvider(this).get(QuizViewModel.class);  // Oppretter en ViewModel for QuizActivity2

        SharedPreferences prefs = getSharedPreferences("quiz_prefs", MODE_PRIVATE);
        boolean willContinue = prefs.getBoolean("Continue", false);
        if(willContinue) {
            int savedIndex = prefs.getInt("savedIndex", 0);
            int savedScore = prefs.getInt("savedScore", 0);
            quizViewModel.currentIndex = savedIndex;
            quizViewModel.score = savedScore;
        }


        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new QuizFragment()).commit();   // Starter QuizFragment

    }

    // Lagrer nåværende index og score
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("CURRENT_INDEX", quizViewModel.currentIndex);
        outState.putInt("SCORE", quizViewModel.score);

    }

    // Lagrer nåværende index og score til sharedpreferences
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = getSharedPreferences("quiz_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("savedIndex", quizViewModel.currentIndex);
        editor.putInt("savedScore", quizViewModel.score);
        editor.apply();
    }


    // Metoder for å lagre og hente det riktige svaret for det nåværende spørsmålet i quizen
    public void setCurrentCorrectAnswer(String answer) {
        currentCorrectAnswer = answer;
    }
    public String getCorrectAnswer() {
        return currentCorrectAnswer;
    }

}
