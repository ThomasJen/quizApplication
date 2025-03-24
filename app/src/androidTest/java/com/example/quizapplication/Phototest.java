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
            new ActivityScenarioRule<>(MainActivity.class);             //Definerer en JUnit test som starter MainActivity for hver test og lukker den etterpå automatisk. Tester med en fersk instans

    @Before
    public void setUp() {
        //  Initialiserer Espresso Intents før testen
        Intents.init();
    }

    @After
    public void tearDown() {
        // Frigjør Espresso Intents etter testen
        Intents.release();
    }

    @Test
    public void testImageAddedToRecyclerView() throws InterruptedException {  // Testmetode for å sjekke om bildene blir lagt til i Recyclerview
        // Forsikrer at MainActicity starter før testen og at den ikke er null
        activityScenarioRule.getScenario().onActivity(activity -> assertNotNull(activity));

        // Venter på at RecyclerView vises før tester kjøres
        waitForView(withId(R.id.my_recycler_view));

        // Oppretter en AtomicInteger for å holde styr på antall elementer i RecyclerView før testen starter
        AtomicInteger initialCount = new AtomicInteger();

        // Henter QuizViewModel fra aktiviteten og bruker hjelpemetode for å få verdien av LiveData før et nytt bilde settes inn
        activityScenarioRule.getScenario().onActivity(activity -> {
            QuizViewModel quizViewModel = new ViewModelProvider(activity).get(QuizViewModel.class);
            try {
                initialCount.set(getOrAwaitValue(quizViewModel.getPhotoCount())); // ✅ Get item count before adding
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Setter inn et testbilde, kaller på metoden fra mainactivity, Hvorfor? Unngår å gå via UI-handlinger
        activityScenarioRule.getScenario().onActivity(activity -> {
            ((MainActivity) activity).insertTestPhoto();
        });

        // Gir UI tid til å oppdatere seg
        Thread.sleep(1000);

        // Bekrefter at recyclerview har det forventede antall elementer
        onView(withId(R.id.my_recycler_view))
                .check(new RecyclerViewItemCountAssertion(5));
    }


    // Intern klasse som implementerer ViewAssertion for å sjekke antall elementer i RecyclerView
    public static class RecyclerViewItemCountAssertion implements ViewAssertion {
        private final int expectedCount;

        // Konstruktør som tar inn forventede antall elementer
        public RecyclerViewItemCountAssertion(int expectedCount) {
            this.expectedCount = expectedCount;
        }

        // Metoden sjekker om view finnes, hvis ikke den finnes kaster vi en exception
        @Override
        public void check(View view, NoMatchingViewException noViewFoundException) {
            if (noViewFoundException != null) {
                throw noViewFoundException;
            }

            // Hvis view finnes, sjekker vi om adapteren har forventede antall elementer
            RecyclerView recyclerView = (RecyclerView) view;
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            assert adapter != null;
            assertEquals(expectedCount, adapter.getItemCount());
        }
    }

    // Hjelpemetode som venter til en bestemt view er synlig, hjelper med synkronisering
    private void waitForView(final org.hamcrest.Matcher<View> matcher) {
        onView(matcher).check(matches(isDisplayed()));
    }

    //  Hjelpemetode som henter verdier fra LiveData synkront
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
