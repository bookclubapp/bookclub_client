package com.bookclub.app.bookclub;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bookclub.app.bookclub.bookclubapi.BookClubAPI;
import com.bookclub.app.bookclub.bookclubapi.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class ProfileActivity extends AppCompatActivity {

    private TextView userName, name, birthDay, country, phone, mail;
    User user;
    AlertDialog alertDialog;
    int userID;
    LinearLayout linearLayout, privateData;
    RatingBar ratingBar;
    boolean isCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        linearLayout = findViewById(R.id.linearLayout);
        privateData = findViewById(R.id.privateData);
        phone = findViewById(R.id.phoneText);
        mail = findViewById(R.id.mailText);
        userName = findViewById(R.id.userNameText);
        name = findViewById(R.id.nameText);
        birthDay = findViewById(R.id.birthdayText);
        country = findViewById(R.id.country);
        isCurrent = getIntent().getBooleanExtra("Current", false);
        userID = getIntent().getIntExtra("UserID", -1);

        ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setClickable(false);
        alertDialog = new SpotsDialog(this);

        alertDialog.show();
        new ProfileInfoTask().execute();


    }


    public class ProfileInfoTask extends AsyncTask<Void, Void, Boolean>{

        User user;
        double rating;

        @Override
        protected void onPostExecute(Boolean aVoid) {
            super.onPostExecute(aVoid);

            if (!isCurrent){
                privateData.setVisibility(View.GONE);
            }

            userName.setText(user.getUsername());
            name.setText(user.getName());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yyyy");
            birthDay.setText(dateFormat.format(user.getDateOfBirth()));

            String city = SignupActivity.hereLocation(ProfileActivity.this, user.getLat(), user.getLon());
            if (city == null || city.equals("")){
                country.setText(city);
            }
            else{
                country.setText(user.getCountry());
            }

            phone.setText(user.getPhoneNumber());
            mail.setText(user.getMail());
            ratingBar.setRating((float)rating);
            System.out.println("Before return");

            alertDialog.dismiss();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            System.out.println("After call");

            BookClubAPI api = new BookClubAPI();
            if (isCurrent){
                User u = (User)api.getSession().get(2);
                userID = u.getId();
            }
            ArrayList<Object> arr = api.getUserProfileID(userID);
            String status = (String)arr.get(0);
            String message = (String)arr.get(1);
            user = (User)arr.get(2);
            ArrayList<Object> a = api.userRating(userID);
            System.out.println(a);
            rating = (double)a.get(2);

            return true;
        }
    }


}
