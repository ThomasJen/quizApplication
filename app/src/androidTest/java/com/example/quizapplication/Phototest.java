package com.example.quizapplication;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;


import static junit.framework.TestCase.assertNotNull;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(AndroidJUnit4.class)
public class Phototest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        // 🔥 Initialize Espresso Intents before the test
        Intents.init();
    }

    @After
    public void tearDown() {
        // 🔥 Release Espresso Intents after the test
        Intents.release();
    }

    @Test
    public void testImageAddedToRecyclerView() throws InterruptedException {
        // Ensure MainActivity is launched and in foreground
        activityScenarioRule.getScenario().onActivity(activity -> assertNotNull(activity));

        // Wait for RecyclerView to appear
        waitForView(withId(R.id.my_recycler_view));

        // Get initial item count
        AtomicInteger initialCount = new AtomicInteger();
        activityScenarioRule.getScenario().onActivity(activity -> {
            QuizViewModel quizViewModel = new ViewModelProvider(activity).get(QuizViewModel.class);
            try {
                initialCount.set(getOrAwaitValue(quizViewModel.getPhotoCount())); // ✅ Get item count before adding
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // 🔥 Insert test image programmatically (skipping UI interactions)
        activityScenarioRule.getScenario().onActivity(activity -> {
            ((MainActivity) activity).insertTestPhoto();
        });

        // Wait for RecyclerView update
        Thread.sleep(1000); // Temporary delay (replace with IdlingResource)

        // Verify RecyclerView count increased by 1
        onView(withId(R.id.my_recycler_view))
                .check(new RecyclerViewItemCountAssertion(5));
    }


    // 🔥 Custom RecyclerView count assertion
    public static class RecyclerViewItemCountAssertion implements ViewAssertion {
        private final int expectedCount;

        public RecyclerViewItemCountAssertion(int expectedCount) {
            this.expectedCount = expectedCount;
        }

        @Override
        public void check(View view, NoMatchingViewException noViewFoundException) {
            if (noViewFoundException != null) {
                throw noViewFoundException;
            }

            RecyclerView recyclerView = (RecyclerView) view;
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            assert adapter != null;
            assertEquals(expectedCount, adapter.getItemCount());
        }
    }

    // 🔥 Helper method: Waits for a View to appear in the UI
    private void waitForView(final org.hamcrest.Matcher<View> matcher) {
        onView(matcher).check(matches(isDisplayed()));
    }

    // 🔥 Helper method: Retrieves LiveData values synchronously
    public static <T> T getOrAwaitValue(LiveData<T> liveData) throws InterruptedException {
        final Object[] data = new Object[1];
        CountDownLatch latch = new CountDownLatch(1);
        Observer<T> observer = new Observer<T>() {
            @Override
            public void onChanged(T t) {
                data[0] = t;
                liveData.removeObserver(this);
                latch.countDown();
            }
        };

        liveData.observeForever(observer);

        if (!latch.await(2, TimeUnit.SECONDS)) {
            throw new InterruptedException("LiveData value was never set.");
        }

        return (T) data[0];
    }



}
