package com.bookclub.app.bookclub;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.bookclub.app.bookclub.bookclubapi.BookClubAPI;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class SignupActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;


    String[] countries = {"Turkey", "France", "Albania", "Spain", "UK", "USA"};


    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView, mconfirmPassword;
    private View mProgressView;
    private View mLoginFormView;
    private Spinner countrySpinner;
    private TextView birthDateText;
    private double lat, lon;
    private ImageButton changeDateButton, changeLocationButton;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private boolean locationReceived, dateReceived;
    TextInputEditText userName, name, surname, phoneNumber, date, locationText;
    private AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        locationReceived = false;
        dateReceived = false;
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptSignup();
                    return true;
                }
                return false;
            }
        });
        alertDialog = new SpotsDialog(this);
        Button mEmailSignInButton = (Button) findViewById(R.id.sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignup();
            }
        });

        userName = findViewById(R.id.usernameText);
        mconfirmPassword = findViewById(R.id.passwordConfirm);
        name = findViewById(R.id.nameText);
        surname = findViewById(R.id.surnameText);
        phoneNumber = findViewById(R.id.phoneNumberText);


        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        countrySpinner = findViewById(R.id.spinner);
        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, countries);
        countrySpinner.setAdapter(mAdapter);


        birthDateText = findViewById(R.id.birthDateText);

        changeDateButton = findViewById(R.id.changeDatePickButton);
        changeDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        SignupActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.setTitle("Pick your birthday");
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;

                String date = day + "/" + month + "/" + year;
                birthDateText.setText(date);
                dateReceived = true;
            }
        };

        locationText = findViewById(R.id.locationText);

        changeLocationButton = findViewById(R.id.getLocationButton);
        changeLocationButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)){
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
                }else {

                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                        buildAlertMessageNoGps();
                    }else {
                       // Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        Location location = getLastKnownLocation(locationManager);
                        System.out.println(location);

                        lat = location.getLatitude();
                        lon = location.getLongitude();

                        String city = hereLocation(SignupActivity.this, lat, lon);

                        locationText.setText(city);
                        locationReceived = true;

                    }
                }
            }
        });


    }

    private Location getLastKnownLocation(LocationManager mLocationManager) {
        mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {

            @SuppressLint("MissingPermission") Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }


    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public static String hereLocation(Context context, double lat, double lon){
        String cityName = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(lat, lon, 10);
            if (addresses.size() > 0){
                for (Address adr: addresses){
                    if (adr.getLocality() != null && adr.getCountryName() != null && adr.getLocality().length() > 0){
                        cityName = adr.getLocality() + ", " + adr.getCountryName();
                        System.out.println(adr.getCountryName() + " " + adr.getAddressLine(0) + " " + adr.getPostalCode() + " " + adr.getSubLocality() + " " + adr.getThoroughfare() + " " + adr.getSubThoroughfare());
                        break;
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        return cityName;
    }



    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */

   /* private void inputcheck () {
        boolean cancel = false;
        if (userName.getText().toString().contains(" ")){
            userName.requestFocus();
            userName.setError("Username entry is invalid");
            cancel = true;
        }
        if (!mPasswordView.getText().toString().equals(mconfirmPassword.getText().toString())){
            mconfirmPassword.requestFocus();
            mconfirmPassword.setError("Passwords do not match");
            cancel = true;
        }
        // locationReceived is made true inside the click listener
        // /if the changeLocationButton was clicked
        if (!locationReceived){
            changeLocationButton.requestFocus();
            cancel = true;
        }
        if (!mEmailView.getText().toString().contains("@")){
            mEmailView.requestFocus();
            mEmailView.setError("Invalid email address");

            cancel = true;
        }
        // dateReceived is made true inside the click listener
        // /if the changeDateButton was clicked
        if (!dateReceived){
            changeDateButton.requestFocus();
            cancel = true;
        }

        if (!cancel){
            alertDialog.show();
            new UserLoginTask().execute();
        }
    }*/

    private void attemptSignup() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mconfirmPassword.setError(null);
        name.setError(null);
        userName.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (userName.getText() == null || userName.getText().equals("") || userName.getText().toString().length() < 4 || userName.getText().toString().contains(" ")){
            userName.setError("Username entry is invalid");
            focusView = userName;
            cancel = true;
        } else if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        } else if (!dateReceived){
            System.out.println("Inside date received");
            focusView = birthDateText;
            cancel = true;
        } else if (!mPasswordView.getText().toString().equals(mconfirmPassword.getText().toString())){
            mconfirmPassword.setError("Passwords Do Not Match");
            focusView = mconfirmPassword;
            cancel = true;
        }
        else if (!isEmailValid(email)) {
            mEmailView.setError("Invalid email address");
            focusView = mEmailView;
            cancel = true;
        } else if (!locationReceived){
            focusView = changeLocationButton;
            cancel = true;
        }

        if (!cancel) {
            //showProgress(true);
            alertDialog.show();
            mAuthTask = new UserLoginTask();
            mAuthTask.execute((Void) null);
        }
        else {
            focusView.requestFocus();
            System.out.println("Date received"+dateReceived + " request: ");
        }
    }

    private boolean contains(String str, String search){
        for (int i = 0; i <= str.length()-search.length(); i++){
            if (str.substring(i, i+search.length()).equals(search)) return true;
        }
        return false;
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }*/

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(SignupActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        UserLoginTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
                SimpleDateFormat dt = new SimpleDateFormat("dd/mm/yyyy" );
                Date d1 = dt.parse(birthDateText.getText().toString());
                SimpleDateFormat nf = new SimpleDateFormat("yyyy-MM-dd");
                String d2 = nf.format(d1);
                Date d = nf.parse(d2);
                Log.d("signup attempt", d.toString());

                BookClubAPI api = new BookClubAPI();
                ArrayList<Object> status = api.signup(userName.getText().toString(), mPasswordView.getText().toString(),
                        mEmailView.getText().toString(), name.getText().toString(), surname.getText().toString(),
                        countrySpinner.getSelectedItem().toString(), phoneNumber.getText().toString(), d, lon, lat);
                Log.d("signup attempt", status.toString());

                if (status.get(0).equals("success")){
                    return true;
                }


            } catch (InterruptedException e) {
                return false;
            } catch (ParseException p){
                return false;
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            //showProgress(false);
            alertDialog.dismiss();
            if (success) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                builder.setMessage("Registration completed!")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                startActivity(intent);
                                dialog.cancel();
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();
            } else {
                final AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                builder.setMessage("Registration was not complete!")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                dialog.cancel();
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            //showProgress(false);
            alertDialog.dismiss();
        }
    }
}

