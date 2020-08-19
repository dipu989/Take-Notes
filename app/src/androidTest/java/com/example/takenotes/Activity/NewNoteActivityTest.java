package com.example.takenotes.Activity;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.takenotes.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class NewNoteActivityTest {

    private static String title = "Title";
    private static String body = "Testing Note";

    @Rule
    public ActivityTestRule<NewNoteActivity> activityTestRule = new ActivityTestRule<>(NewNoteActivity.class,true,true);

    @Test
    public void confirmSaveButtonVisible(){
        Espresso.onView(withId(R.id.save_note)).check(matches(isDisplayed()));
    }

    @Test
    public void checkTitleDisplayed(){
        Espresso.onView(withId(R.id.note_title)).check(matches(isDisplayed()));
    }

    @Test
    public void checkBodyDisplayed(){
        Espresso.onView(withId(R.id.note_body)).check(matches(isDisplayed()));
    }

    @Test
    public void checkToolbarDisplayed(){
        Espresso.onView(withId(R.id.new_note_toolbar)).check(matches(isDisplayed()));
    }

    @Test
    public void checkSaveWithBodyOnly(){
        Espresso.onView(withId(R.id.note_body)).perform(typeText(body));
        Espresso.onView(withId(R.id.save_note)).perform(click());
    }

    @Test
    public void checkSaveWithTitleOnly(){
        Espresso.onView(withId(R.id.note_title)).perform(typeText(title));
        Espresso.onView(withId(R.id.save_note)).perform(click());
    }

    @Test
    public void checkSaveWithTitleBody(){
        Espresso.onView(withId(R.id.note_title)).perform(typeText(title));
        Espresso.onView(withId(R.id.note_body)).perform(typeText(body));
        Espresso.onView(withId(R.id.save_note)).perform(click());
    }

    @Test
    public void checkSaveEmptyNote(){
        Espresso.onView(withId(R.id.note_title)).perform(typeText(""));
        Espresso.onView(withId(R.id.note_body)).perform(typeText(""));
        Espresso.onView(withId(R.id.save_note)).perform(click());
    }

    @Test
    public void isShareOptionVisible(){
        Espresso.onView(withId(R.id.share_note)).check(matches(isDisplayed()));
    }

    @Test
    public void checkShareOptionWorking(){
        Espresso.onView(withId(R.id.share_note)).perform(click());
    }

}