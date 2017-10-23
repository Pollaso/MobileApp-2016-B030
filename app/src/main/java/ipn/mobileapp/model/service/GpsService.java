package ipn.mobileapp.model.service;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import ipn.mobileapp.R;

public class GpsService extends Service {

    private final Context context;

    boolean isPassiveEnabled = false;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;

    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    protected LocationManager locationManager;

    public GpsService(Context context) {
        this.context = context;

        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            showSettingsAlert();
    }

    public Location getLocation() {
        try {
            List<String> providers = locationManager.getProviders(true);
            location = null;
            if (providers != null) {
                this.canGetLocation = true;
                for (String provider : providers) {
                    Location temp = locationManager.getLastKnownLocation(provider);
                    if (temp == null) {
                        continue;
                    }
                    if (location == null || temp.getAccuracy() < location.getAccuracy()) {
                        location = temp;
                    }
                }
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    public ipn.mobileapp.model.pojo.Location getAddress() {
        List<Address> addresses = null;
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null) {
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
        }

        return null;
    }

    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setCancelable(false);

        alertDialog.setTitle(context.getString(R.string.title_dialog_gps_settings));

        alertDialog.setMessage(context.getString(R.string.tv_gps_disabled));

        alertDialog.setPositiveButton(context.getString(R.string.btn_activate), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });

        alertDialog.show();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}
