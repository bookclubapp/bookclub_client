package com.bookclub.app.bookclub;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bookclub.app.bookclub.bookclubapi.BookClubAPI;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.util.ArrayList;
import java.util.Date;

import dmax.dialog.SpotsDialog;

public class WelcomeActivity extends AppCompatActivity {

    Button loginButton, signInButton, guestButton;
    SharedPreferences sp;
    String username, password;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        loginButton = findViewById(R.id.loginButton);
        signInButton = findViewById(R.id.signInButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(WelcomeActivity.this, "Login Test", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });

        guestButton = findViewById(R.id.noLoginButton);
        guestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //  Toast.makeText(WelcomeActivity.this, "Signin Test", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(WelcomeActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        alertDialog = new SpotsDialog(this);

        sp = this.getSharedPreferences(LoginActivity.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        username = sp.getString(LoginActivity.USERNAME_PREFERENCE_FILE_KEY, LoginActivity.NOT_EXIST);
        if (!username.equals(LoginActivity.NOT_EXIST)){
            sp = this.getSharedPreferences(LoginActivity.PASSWORD_PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
            password = sp.getString(LoginActivity.PASSWORD_PREFERENCE_FILE_KEY, LoginActivity.NOT_EXIST);
            alertDialog.show();
            new UserLoginTask().execute();
        }



    }

    @Override
    public void onBackPressed() {
        new LovelyStandardDialog(WelcomeActivity.this, LovelyStandardDialog.ButtonLayout.HORIZONTAL)
                .setTopColorRes(R.color.buttonColor)
                .setButtonsColorRes(R.color.colorAccent)
                .setIcon(R.drawable.bookclub_icon)
                .setTitle("Is this is a goodbye?")
                .setMessage("Are you sure you want to close BookClub?")
                .setPositiveButton("Confirm", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        System.exit(0);
                    }
                })
                .setNeutralButton("Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .show();
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {
                BookClubAPI api = new BookClubAPI();
                ArrayList<Object> status = api.login(username, password);
                Log.d("login attempt", status.toString());
                if (status.get(0).equals("success"))
                    return true;
                else
                    return false;
                //TODO: login credential checking will be done here

            } catch (Exception e) {
                e.printStackTrace();


                return false;
            }


        }
        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
            }
            else{
            }
            alertDialog.dismiss();
        }

    }
}
