package com.bookclub.app.bookclub;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;


import com.bookclub.app.bookclub.bookclubapi.BookClubAPI;
import com.bookclub.app.bookclub.bookclubapi.User;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import dmax.dialog.SpotsDialog;

public class AccountSettingsActivity extends AppCompatActivity {

    Button changeDateButton;
    ImageButton saveButton, cancelButton, getLocationButton;
    TextInputEditText birthDateText, name, currentPassword, newPassword, newPasswordConfirm;
    TextInputEditText phoneNumber, email, location;
    DatePickerDialog.OnDateSetListener mDateSetListener;
    AlertDialog alertDialog;
    CheckBox availability, messagable, lastSeen;
    User user;

    boolean bdChanged, nameChanged, passwordChanged, phChanged, emailChanged, locationChanged, avChanged, mesCahnged, lsChanged;
    double lat, lon;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        bdChanged = false;
        nameChanged = false;
        passwordChanged = false;
        phChanged = false;
        emailChanged = false;
        locationChanged = false;
        avChanged = false;
        mesCahnged = false;
        lsChanged = false;

        alertDialog = new SpotsDialog(this);
        birthDateText = findViewById(R.id.birthdayText);
        changeDateButton = findViewById(R.id.changeDatePickButton);
        name = findViewById(R.id.nameEditText);
        currentPassword = findViewById(R.id.currentPassword);
        newPassword = findViewById(R.id.newPassword);
        newPassword.setEnabled(false);
        newPasswordConfirm = findViewById(R.id.newPasswordSecond);
        newPasswordConfirm.setEnabled(false);
        phoneNumber = findViewById(R.id.newPhoneNumberText);
        email = findViewById(R.id.newEmailText);
        saveButton = findViewById(R.id.doneButton);
        cancelButton = findViewById(R.id.cancelButton);
        availability = findViewById(R.id.availabilityCheckBox);
        messagable = findViewById(R.id.messagableCheckBox);
        lastSeen = findViewById(R.id.lastSeenCheckBox);
        location = findViewById(R.id.locationText);
        getLocationButton = findViewById(R.id.getLocationButton);


        setTextChangedListeners();


        alertDialog.show();
        System.out.println("Before userinfotask");
        new UserInfoTask().execute();
    }

    private void setTextChangedListeners(){

        changeDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        AccountSettingsActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String date = day + "/" + month + "/" + year;
                birthDateText.setText(date);
                bdChanged = true;
            }
        };

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                nameChanged = true;
            }
        });

        currentPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("inside " + s);
                if (s.toString().equals(user.getPassword())){
                    newPassword.setEnabled(true);
                    System.out.println("Equal : " + user.getPassword());
                    //newPasswordConfirm.setEnabled(true);
                }
                else{
                    newPassword.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(user.getPassword())){
                    newPassword.setEnabled(false);
                    newPasswordConfirm.setEnabled(false);
                    System.out.println("Equal : " + user.getPassword());
                    //newPasswordConfirm.setEnabled(true);
                }
            }
        });

        newPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() <= 3){
                    newPassword.requestFocus();
                    newPassword.setError("Password must at least be 4 characters long");
                }
                else{
                    newPassword.clearFocus();
                    newPassword.setError(null);
                    newPasswordConfirm.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        newPasswordConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!newPassword.getText().toString().equals(s)){
                    newPasswordConfirm.requestFocus();
                    newPasswordConfirm.setError("New passwords must match");
                    passwordChanged = true;
                }
                else {
                    newPasswordConfirm.clearFocus();
                    newPasswordConfirm.setError(null);
                    passwordChanged = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                phChanged = true;
            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                emailChanged = true;
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.show();
                new AccountSettingsChangeTask().execute();

            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountSettingsActivity.this, PreferencesActivity.class);
                startActivity(intent);
            }
        });
        availability.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                avChanged = !avChanged;
            }
        });

        messagable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mesCahnged = !mesCahnged;
            }
        });
        lastSeen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                lsChanged = !lsChanged;
            }
        });

        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)){
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
                }else {

                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                        Utils.buildAlertMessageNoGps(AccountSettingsActivity.this);
                    }else {
                        // Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        Location loc = Utils.getLastKnownLocation(locationManager, AccountSettingsActivity.this);
                        System.out.println(location);

                        lat = loc.getLatitude();
                        lon = loc.getLongitude();

                        String city = Utils.hereLocation(AccountSettingsActivity.this, lat, lon);

                        location.setText(city);
                        locationChanged = true;
                    }
                }
            }
        });

    }

    public class AccountSettingsChangeTask extends AsyncTask<Void, Void, Void>{


        @Override
        protected Void doInBackground(Void... voids) {
            BookClubAPI api = new BookClubAPI();

            if (bdChanged){
                //cahnge bd
                try {
                    SimpleDateFormat dt = new SimpleDateFormat("dd/mm/yyyy" );
                    Date d1 = null;
                    d1 = dt.parse(birthDateText.getText().toString());
                    SimpleDateFormat nf = new SimpleDateFormat("yyyy-MM-dd");
                    String d2 = nf.format(d1);
                    Date d = nf.parse(d2);
                    //
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (nameChanged){

            }
            if (passwordChanged){
                api.accountSettings_changePassword(newPassword.getText().toString());
            }
            if (phChanged){
                api.accountSettings_changePhoneNumber(phoneNumber.getText().toString());
            }
            if (emailChanged)api.accountSettings_changeMail(email.getText().toString());
            if (avChanged)api.accountSettings_changeAvailability();
            if (mesCahnged)api.accountSettings_changeMessagable();
            if (lsChanged)api.accountSettings_lastSeen();
            if (locationChanged)api.accountSettings_changeLocation(lat, lon, "");

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            alertDialog.dismiss();
        }
    }

    public class UserInfoTask extends AsyncTask<Void, Void, Boolean>{

        ArrayList<Object> prefs;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean u) {
            super.onPostExecute(u);

            if (u){
                System.out.println(user);
                name.setText(user.getName());
                phoneNumber.setText(user.getPhoneNumber());
                email.setText(user.getMail());
                location.setText(SignupActivity.hereLocation(AccountSettingsActivity.this, user.getLat(), user.getLon()));

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date date = user.getDateOfBirth();
                System.out.println("Date : " + date.toString() + "SDF : " + simpleDateFormat.format(date).toString());
                birthDateText.setText(simpleDateFormat.format(user.getDateOfBirth()));
                availability.setChecked((boolean)prefs.get(1));
                messagable.setChecked((boolean)prefs.get(2));
                lastSeen.setChecked((boolean)prefs.get(3));

            }
            else{

            }
            alertDialog.dismiss();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            BookClubAPI api = new BookClubAPI();
            user = ((User)api.getSession().get(2));
            prefs = (ArrayList<Object>) api.accountSettings_index().get(2);

            if (user != null && prefs != null) {
                ArrayList<Object> arr = api.getUserProfile(user.getId());
                String status = (String) arr.get(0);

                System.out.println("Status: "+ status + " Message : " + (String)arr.get(1));
                return true;
            }
            return false;
        }
    }


}
