package in.xeno.mawesome;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.greenrobot.eventbus.EventBus;

import in.xeno.mawesome.MainActivity;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class LocationHelper {
    Activity activity;

    LocationHelper(Activity activity)
    {
     this.activity=activity;
    }




    public void checkPermission()
    {
        if (ContextCompat.checkSelfPermission(activity,
                ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(activity,
                        new String[]{ACCESS_FINE_LOCATION},
                        MainActivity.REQUEST_LOCATION_PERMISSION);

                // MY_PERMISSIONS_REQUEST_ACCESS_FINE-LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else
        {
            //here permission is already provided
           changeSettings();
        }


    }

    public void changeSettings() {

        //protected void createLocationRequest();
        LocationRequest mLocationRequest = new LocationRequest();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        // LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();

// ...

        SettingsClient client = LocationServices.getSettingsClient(activity);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...//THE USER'S LOCATION IS ALREADY ON
//                locationGetter();
            }
        });

        task.addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //THE USER'S LOCATION IS NOT ON
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(activity, MainActivity.REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }
    public void getCurrentLocation(){
        MessageEvent messageEvent=new MessageEvent();
        messageEvent.setLatitude(String.valueOf(12.9716));
        messageEvent.setLongitude(String.valueOf(77.5946));


        EventBus.getDefault().post(messageEvent);
    }
    public void locationGetter()
    {
        if (ContextCompat.checkSelfPermission(activity,
                ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            FusedLocationProviderClient mFusedLocationClient;

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);

            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {

                                double latitude=location.getLatitude();
                                double longitude=location.getLongitude();

                                String result="Latitude "+latitude+" and Longitude "+longitude;
                                MessageEvent messageEvent=new MessageEvent();
                                messageEvent.setLatitude(String.valueOf(latitude));
                                messageEvent.setLongitude(String.valueOf(longitude));

                                EventBus.getDefault().post(messageEvent);


                                // Logic to handle location object
                            }
                        }
                    });


        }
    }


}