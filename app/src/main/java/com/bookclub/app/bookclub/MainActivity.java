package com.bookclub.app.bookclub;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bookclub.app.bookclub.bookclubapi.BookClubAPI;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity implements MatchListFragment.OnFragmentInteractionListener,
                                                               GeneralListFragment.OnFragmentInteractionListener,
                                                               SuggestionListFragment.OnFragmentInteractionListener {

    private final String MATCHLIST_ID = "matchlistfragment";
    private final String SUGGESTIONLIST_ID = "suggestionlistfragment";
    private final String GENERALLIST_ID = "generallistfragment";
    
    private Fragment generalListFragment;
    private Fragment matchListFragment;
    private Fragment suggestionListFragment;
    private FragmentManager fm = getSupportFragmentManager();
    Fragment active;
    private boolean guestSession;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        alertDialog = new SpotsDialog(this);
        //guestSession = false;
        //alertDialog.show();
        //new GetSessionTask().execute();
        generalListFragment = new GeneralListFragment();
        active = generalListFragment;
        fm.beginTransaction().add(R.id.fragment_container, generalListFragment, GENERALLIST_ID).commit();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void setGuestSession(boolean b){
        guestSession = b;
    }
    public boolean isGuestSession(){
        return guestSession;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            if (!guestSession){
                switch (item.getItemId()) {
                    case R.id.generalList:
                        if (active instanceof GeneralListFragment) break;
                        if (generalListFragment == null) generalListFragment = new GeneralListFragment();

                        try{
                            fm.beginTransaction().hide(active).add(R.id.fragment_container, generalListFragment, GENERALLIST_ID).commit();
                            fm.beginTransaction().remove(active).commit();
                            active = null;
                            active = generalListFragment;

                        }catch (IllegalStateException e){
                            return true;
                        }
                        return true;


                    case R.id.matchList:
                        if (active instanceof MatchListFragment) break;
                        if (matchListFragment == null) matchListFragment = new MatchListFragment();

                        try{

                            fm.beginTransaction().hide(active).add(R.id.fragment_container, matchListFragment, MATCHLIST_ID).commit();
                            fm.beginTransaction().remove(active).commit();
                            active = null;
                            active = matchListFragment;
                            return true;
                        }
                        catch (IllegalStateException e){
                            return true;
                        }

                    case R.id.suggestionList:
                        if (active instanceof SuggestionListFragment) break;
                        if (suggestionListFragment == null) suggestionListFragment = new SuggestionListFragment();

                        try{

                            fm.beginTransaction().remove(active).add(R.id.fragment_container, suggestionListFragment, SUGGESTIONLIST_ID).commit();
                            //  fm.beginTransaction().remove(active).commit();
                            active = null;
                            active = suggestionListFragment;
                            return true;
                        }catch (IllegalStateException e){
                            return true;
                        }
                }


            }
            else{
                switch (item.getItemId()) {
                    case R.id.generalList:

                        return true;

                    case R.id.matchList:

                        showDialog();
                        return true;

                    case R.id.suggestionList:
                        showDialog();
                        return true;
                }
            }

            return false;
        }
    };



    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        new LovelyStandardDialog(MainActivity.this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                .setTopColorRes(R.color.buttonColor)
                .setButtonsColorRes(R.color.colorAccent)
                .setIcon(R.drawable.bookclub_icon)
                .setTitle("Logout?")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.show();
                        new LogoutTask(3).execute();
                    }
                })
                .setNeutralButton("Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .show();
    }

    private void showDialog(){
        new LovelyStandardDialog(MainActivity.this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                .setTopColorRes(R.color.buttonColor)
                .setButtonsColorRes(R.color.colorAccent)
                .setIcon(R.drawable.bookclub_icon)
                .setTitle("Login?")
                .setMessage("You have to login to have matches or suggestions. Would you like to login or register to BookClub?")
                .setPositiveButton("Login", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.show();
                        new LogoutTask(1).execute();
                    }
                })
                .setNeutralButton("Register", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.show();
                        new LogoutTask(2).execute();
                    }
                })
                .setNegativeButton("Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .show();
    }

    public class LogoutTask extends AsyncTask<Void,Void, Void>{

        int operation;

        public LogoutTask(int operation){

            this.operation = operation;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            alertDialog.dismiss();
            Intent intent = null;
            if (operation == 1)
                intent = new Intent(MainActivity.this, LoginActivity.class);
            else if (operation == 2)
                intent = new Intent(MainActivity.this, SignupActivity.class);
            else if (operation == 3)
                intent = new Intent(MainActivity.this, WelcomeActivity.class);

            SharedPreferences sp = MainActivity.this.getSharedPreferences(LoginActivity.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(LoginActivity.USERNAME_PREFERENCE_FILE_KEY, LoginActivity.NOT_EXIST);
            editor.putString(LoginActivity.PASSWORD_PREFERENCE_FILE_KEY, LoginActivity.NOT_EXIST);
            editor.commit();

            alertDialog.dismiss();
            startActivity(intent);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            BookClubAPI api = new BookClubAPI();
            api.signout();

            return null;
        }
    }


    public class GetSessionTask extends AsyncTask<Void, Void, Void>{


        @Override
        protected Void doInBackground(Void... voids) {

            BookClubAPI api = new BookClubAPI();
            ArrayList<Object> arr = api.getSession();
            if (arr.get(2) == null)guestSession = true;

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            alertDialog.dismiss();
        }
    }



}
