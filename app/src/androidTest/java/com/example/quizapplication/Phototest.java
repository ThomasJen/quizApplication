package com.example.quizapplication;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBackUnconditionally;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;


import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.quizapplication.MainActivity;
import com.example.quizapplication.R;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

// Hjelpeklasse for å sjekke antall elementer i RecyclerView
class RecyclerViewItemCountAssertion implements ViewAssertion {
    private final int expectedCount;

    public RecyclerViewItemCountAssertion(int expectedCount) {
        this.expectedCount = expectedCount;
    }

    @Override
    public void check(android.view.View view, NoMatchingViewException noViewFoundException) {
        if (noViewFoundException != null) {
            throw noViewFoundException;
        }
        RecyclerView recyclerView = (RecyclerView) view;
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        assertThat(adapter.getItemCount(), is(expectedCount));
    }
}

@RunWith(AndroidJUnit4.class)
public class Phototest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    private int initialItemCount;

    @Before
    public void setUp() {
        // Init Espresso Intents
        Intents.init();

        // Hent initial antall elementer i RecyclerView
        activityScenarioRule.getScenario().onActivity(activity -> {
            RecyclerView recyclerView = activity.findViewById(R.id.my_recycler_view);
            initialItemCount = recyclerView.getAdapter().getItemCount();
        });
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testAddingAndDeletingPictureEntry() {
        // Stub image picking intent slik at et bilde fra res/drawable returneres
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        // Her bruker vi et bilde, for eksempel R.drawable.gorilla
        Uri imageUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.drawable.gorilla);
        Intent resultData = new Intent();
        resultData.setData(imageUri);
        Instrumentation.ActivityResult result =
                new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

        // Stub intenten med ACTION_GET_CONTENT
        intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(result);

        // Klikk på knappen for å plukke bilde (id: imageUpload)
        onView(withId(R.id.imageUpload)).perform(click());

        // Skriv inn et navn i EditText (id: edit_text) og klikk submit (id: button_submit)
        onView(withId(R.id.edit_text)).perform(typeText("UserImage"), closeSoftKeyboard());
        onView(withId(R.id.button_submit)).perform(click());

        // Etter at bildet er lagt til, skal antall elementer i RecyclerView øke med 1
        onView(withId(R.id.my_recycler_view))
                .check(new RecyclerViewItemCountAssertion(initialItemCount + 1));

        // Nå simulerer vi at vi sletter ett bilde. Klikk på et bilde i RecyclerView.
        // Dette forutsetter at onPhotoClick() kalles ved et klikk.
        onView(withId(R.id.my_recycler_view))
                .perform(androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition(initialItemCount, click()));

        // Etter sletting skal antall elementer reduseres med 1 tilbake til initialt antall
        onView(withId(R.id.my_recycler_view))
                .check(new RecyclerViewItemCountAssertion(initialItemCount));
    }
}
