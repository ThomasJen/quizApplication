package com.example.quizapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

public class QuizActivityTest {
    @Rule
    public ActivityScenarioRule<QuizActivity2> activityScenarioRule =
            new ActivityScenarioRule<>(QuizActivity2.class);

    @Test
    public void testScoreUpdatesCorrectly() {
        // Finner korrekt svar fra activity først.
        final String[] correctAnswer = new String[1];

        activityScenarioRule.getScenario().onActivity(activity -> {
            correctAnswer[0] = activity.getCorrectAnswer();
        });

        if (correctAnswer[0] != null) {
            onView(withText(correctAnswer[0])).perform(click()); // Click the correct answer dynamically
        }

        // venter til UI har oppdatert før den sjekker scoren
        onView(withId(R.id.scoreText)).check(matches(withText("Score: 1")));
    }
}
