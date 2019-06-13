package com.bookclub.app.bookclub;

import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.widget.EditText;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.junit.Assert.*;

/**
 * Created by pc on 26.04.2019.
 */
public class SignupActivityTest {

    @Rule
    public ActivityTestRule<SignupActivity> signupActivityActivityTestRule = new ActivityTestRule<SignupActivity>(SignupActivity.class);

    private SignupActivity signupActivity = null;


    @Before
    public void setUp() throws Exception {
        signupActivity = signupActivityActivityTestRule.getActivity();
    }

    /*
        Username with space
        Birthday not received
        Email without @
        Location not received
        Passwordds do not match

     */

    @Test
    public void testSignUp_usernameWithSpace(){
        onView(withId(R.id.usernameText)).perform(typeText("john wick86"));
        onView(withId(R.id.email)).perform(typeText("john@gmail.com"));
        onView(withId(R.id.password)).perform(typeText("loveMyD0g"));
        onView(withId(R.id.passwordConfirm)).perform(typeText("love"));
        onView(withId(R.id.nameText)).perform(typeText("John"));
        onView(withId(R.id.surnameText)).perform(typeText("Wick"));
        onView(withId(R.id.phoneNumberText)).perform(typeText("05728838264"));
        for (int i = 0; i < 10; i++)onView(withId(R.id.phoneNumberText)).perform(swipeUp());
        onView(withId(R.id.changeDatePickButton)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.getLocationButton)).perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.sign_in_button)).perform(click());
        assertTrue(signupActivity.findViewById(R.id.usernameText).isFocused());
        assertTrue(((EditText)(signupActivity.findViewById(R.id.usernameText))).getError().equals("Username entry is invalid"));


    }



    @Test
    public void testSignUp_passwordsNoMatch(){
        // assertNotNull(signupActivity.findViewById(R.id.sign_in_button));

        onView(withId(R.id.usernameText)).perform(typeText("johnwick86"));
        onView(withId(R.id.email)).perform(typeText("john@gmail.com"));
        onView(withId(R.id.password)).perform(typeText("loveMyD0g"));
        onView(withId(R.id.passwordConfirm)).perform(typeText("love"));
        onView(withId(R.id.nameText)).perform(typeText("John"));
        onView(withId(R.id.surnameText)).perform(typeText("Wick"));
        onView(withId(R.id.phoneNumberText)).perform(typeText("05728838264"));
        for (int i = 0; i < 10; i++)onView(withId(R.id.phoneNumberText)).perform(swipeUp());
        onView(withId(R.id.changeDatePickButton)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.getLocationButton)).perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.sign_in_button)).perform(click());
        assertTrue(signupActivity.findViewById(R.id.passwordConfirm).isFocused());
        assertTrue(((EditText)(signupActivity.findViewById(R.id.passwordConfirm))).getError().equals("Passwords Do Not Match"));
    }

    @Test
    public void testSignUp_locationNotReceived(){

        onView(withId(R.id.usernameText)).perform(typeText("johnwick86"));
        onView(withId(R.id.email)).perform(typeText("john@gmail.com"));
        onView(withId(R.id.password)).perform(typeText("loveMyD0g"));
        onView(withId(R.id.passwordConfirm)).perform(typeText("loveMyD0g"));
        onView(withId(R.id.nameText)).perform(typeText("John"));
        onView(withId(R.id.surnameText)).perform(typeText("Wick"));
        onView(withId(R.id.phoneNumberText)).perform(typeText("05728838264"));
        for (int i = 0; i < 8; i++)onView(withId(R.id.phoneNumberText)).perform(swipeUp());
        onView(withId(R.id.changeDatePickButton)).perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withText("OK")).perform(click());
        onView(withId(R.id.sign_in_button)).perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(signupActivity.findViewById(R.id.getLocationButton).isFocused());

    }

    @Test
    public void testSignUp_email(){

        onView(withId(R.id.usernameText)).perform(typeText("johnwick86"));
        onView(withId(R.id.email)).perform(typeText("john"));
        onView(withId(R.id.password)).perform(typeText("loveMyD0g"));
        onView(withId(R.id.passwordConfirm)).perform(typeText("loveMyD0g"));
        onView(withId(R.id.nameText)).perform(typeText("John"));
        onView(withId(R.id.surnameText)).perform(typeText("Wick"));
        onView(withId(R.id.phoneNumberText)).perform(typeText("05728838264"));
        for (int i = 0; i < 10; i++)onView(withId(R.id.phoneNumberText)).perform(swipeUp());
        onView(withId(R.id.changeDatePickButton)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.getLocationButton)).perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.sign_in_button)).perform(click());
        assertTrue(signupActivity.findViewById(R.id.email).isFocused());
        assertTrue(((EditText)(signupActivity.findViewById(R.id.email))).getError().equals("Invalid email address"));

    }

    @Test
    public void testSignUp_birthdayNotReceived(){
        onView(withId(R.id.usernameText)).perform(typeText("johnwick"));
        onView(withId(R.id.email)).perform(typeText("john@gmail.com"));
        onView(withId(R.id.password)).perform(typeText("loveMyD0g"));
        onView(withId(R.id.passwordConfirm)).perform(typeText("loveMyD0g"));
        onView(withId(R.id.nameText)).perform(typeText("John"));
        onView(withId(R.id.surnameText)).perform(typeText("Wick"));
        onView(withId(R.id.phoneNumberText)).perform(typeText("05728838264"));
        for (int i = 0; i < 10; i++)onView(withId(R.id.phoneNumberText)).perform(swipeUp());
        onView(withId(R.id.getLocationButton)).perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.sign_in_button)).perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertTrue(signupActivity.findViewById(R.id.birthDateText).isFocused());

    }


    @After
    public void tearDown() throws Exception {
        signupActivity = null;
    }

}