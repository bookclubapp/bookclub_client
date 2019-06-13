package com.bookclub.app.bookclub;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.app.Fragment;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;

import static org.junit.Assert.*;

/**
 * Created by pc on 26.04.2019.
 */
public class MainActivityTest {


    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);
    private MainActivity mainActivity = null;

    @Before
    public void setUp() throws Exception {
        mainActivity = mainActivityActivityTestRule.getActivity();
    }

    @Test
    public void onCreate() throws Exception {

        assertNotNull("Main Activity is Null", mainActivity);
        assertNotNull("Bottom navigation is null", mainActivity.findViewById(R.id.navigation));
        onView(ViewMatchers.withId(R.id.generalList)).perform(click());

    }

    @Test
    public void onDestroy() throws Exception {



    }

    @After
    public void tearDown() throws Exception {
    }

}