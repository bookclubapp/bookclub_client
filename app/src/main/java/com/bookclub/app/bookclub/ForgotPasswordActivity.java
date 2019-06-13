package com.bookclub.app.bookclub;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.bookclub.app.bookclub.bookclubapi.BookClubAPI;

import java.util.ArrayList;

public class ForgotPasswordActivity extends AppCompatActivity {

    TextInputEditText username, email;
    ProgressBar progressBar;
    Button changePassword;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        scrollView = findViewById(R.id.form);
        username = findViewById(R.id.usernameText);
        email = findViewById(R.id.emailText);
        progressBar = findViewById(R.id.progress);

        changePassword = findViewById(R.id.changePasswordButton);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("forgot password attempt", "onclick");

                username.setError(null);
                email.setError(null);


                if ((username.getText().toString() == null || username.getText().toString().equals(""))
                        && (email.getText().toString() == null || email.getText().toString().equals(""))){
                    username.requestFocus();
                    username.setError("Both email and username cannot be empty");

                    email.requestFocus();
                    email.setError("Both email and username cannot be empty");

                }
                else{

                    Log.d("forgot password attempt", "onclick input correct");

                    ForgotPasswordTask forgotPasswordTask = new ForgotPasswordTask();
                    forgotPasswordTask.execute();

                }

            }
        });

    }


    class ForgotPasswordTask extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            showProgress(true);

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            showProgress(false);

            if (!aBoolean){
                new AlertDialog.Builder(ForgotPasswordActivity.this)
                        .setTitle("Error")
                        .setMessage("Account not found")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
            else{
                new AlertDialog.Builder(ForgotPasswordActivity.this)
                        .setTitle("Success")
                        .setMessage("Successfully changed your password")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                        }).show();
            }
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            try{

                BookClubAPI api = new BookClubAPI();
                ArrayList status = api.forgotPassword(username.getText().toString(), email.getText().toString());
                Log.d("forgot password attempt", status.toString());
                if (status.get(0).equals("success")){

                    return true;
                }
                else return false;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }


        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            scrollView.setVisibility(show ? View.GONE : View.VISIBLE);
            scrollView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    scrollView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            scrollView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


}
