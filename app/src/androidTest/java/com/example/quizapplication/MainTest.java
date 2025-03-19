package com.example.quizapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testNavigationToPhotoActivity() {
        onView(withId(R.id.btnStartQuiz)).perform(click());

        onView(withId(R.id.btnBackToMain)).check(matches(isDisplayed()));
    }



   /* @Test
    public void testnavigationToActivity2() {
        onView(withId(R.id.btnactivity2)).perform(click());

        onView(withId(R.id.textViewName)).check(matches(isDisplayed()));
    }*/
}
