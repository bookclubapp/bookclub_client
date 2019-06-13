package com.bookclub.app.bookclub;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by pc on 2.05.2019.
 */

public class Utils {

    public static Location getLastKnownLocation(LocationManager mLocationManager, Context context) {
        mLocationManager = (LocationManager)context.getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {

            @SuppressLint("MissingPermission") Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }


    public static void buildAlertMessageNoGps(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
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


}
