package com.example.quizapplication;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

/*
 * holds a single instance of animalsmanager
 * ensures all activities share the same animalmanger
 * all activities access the same animalsmanager instance
 * if list is modified in mainActivity, changes automatically appear in Quizactivity2, or any other activity
 * data is lost when the app is closed */
public class MyApplication extends Application {
    private AppDatabase database;
    private QuizViewModel quizViewModel;

    @Override
    public void onCreate() {
        super.onCreate();

        database = AppDatabase.getDatabase(this);

        quizViewModel = new QuizViewModel(this);

    }

    public AppDatabase getDatabase() {
        return database;
    }

    public QuizViewModel getQuizViewModel() {
        return quizViewModel;
    }


}
