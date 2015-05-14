package hk.ust.cse.comp4521.eventmaker.PassiveSearch;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hk.ust.cse.comp4521.eventmaker.Event.Event;
import hk.ust.cse.comp4521.eventmaker.Event.Event_T;
import hk.ust.cse.comp4521.eventmaker.R;
import hk.ust.cse.comp4521.eventmaker.SearchFrag;

import static hk.ust.cse.comp4521.eventmaker.Event.Event_T.locationDetection;

public class SearchHelper extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private String TAG = "SearchHelper";
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 60000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    protected Location mLastLocation, mCurrentLocation;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates;

    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;



    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;

    private ArrayList<CharSequence> interest;

    private NotificationManager mNotificationManager;
    private int noteId = 1;

    private List<Event> all;


    public SearchHelper() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //get access to the notification manager
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Toast.makeText(this, "Service starts", Toast.LENGTH_SHORT).show();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        // Build the Google API client so that connections can be established
        buildGoogleApiClient();

        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";

        mGoogleApiClient.connect();

        interest = intent.getCharSequenceArrayListExtra("Interest");
        Log.i(TAG, "Interests are received" );

        downloadAllEvents();

        putNotification();


        return super.onStartCommand(intent, flags, startId);
    }

    private void putNotification(){

        Bitmap largeIcon;

        largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.playing);

        Notification.Builder mBuilder =
                new Notification.Builder(this)
                        .setSmallIcon(R.drawable.playing)
                        .setLargeIcon(largeIcon.createScaledBitmap(largeIcon,72,72,false))
                        .setOngoing(true)
                        .setContentTitle("Searching for events...")
                        .setContentText("");

        // Creates an explicit intent for the Activity
        Intent resultIntent = new Intent(this, SearchFrag.class);

        // create a pending intent that will be fired when notification is touched.
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, 0);

        mBuilder.setContentIntent(resultPendingIntent);

        Log.i(TAG, "Service: putNotification()");

        // noteId allows you to update the notification later on.
        // set the service as a foreground service
        startForeground(noteId, mBuilder.build());

    }

    private void cancelNotification() {
        mNotificationManager.cancel(noteId);
        stopForeground(true);
    }

    @Override
    public void onDestroy() {
        cancelNotification();
        Toast.makeText(this, "Service ends", Toast.LENGTH_SHORT).show();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }




    public void downloadAllEvents(){
        Event_T eventdownloader = new Event_T();
        all = eventdownloader.getAllEvent();
    }

    protected GoogleApiClient mGoogleApiClient;

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            String message = "Last Location is: " +
                    "  Latitude = " + String.valueOf(mLastLocation.getLatitude()) +
                    "  Longitude = " + String.valueOf(mLastLocation.getLongitude());
            Log.i(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.no_location_detected, Toast.LENGTH_SHORT).show();
        }
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        String message = "Current Location is: " +
                "  Latitude = " + String.valueOf(mCurrentLocation.getLatitude()) +
                "  Longitude = " + String.valueOf(mCurrentLocation.getLongitude() +
                "\nLast Updated = " + mLastUpdateTime);
        Log.i(TAG, message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());

//        if (mResolvingError) {
//            // Already attempting to resolve an error.
//            return;
//        } else if (connectionResult.hasResolution()) {
//            try {
//                mResolvingError = true;
//                connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
//            } catch (IntentSender.SendIntentException e) {
//                // There was an error with the resolution intent. Try again.
//                mGoogleApiClient.connect();
//            }
//        } else {
//            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
//            showErrorDialog(connectionResult.getErrorCode());
//            mResolvingError = true;
//        }
    }

    private class CompareHelper extends AsyncTask<String, Integer, ArrayList<String>>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Event_T eventdownloader = new Event_T();
            all = eventdownloader.getAllEvent();

        }

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            ArrayList<String> temp = new ArrayList<>();
            for (int i = 0 ; i< all.size(); i++){
                float [] results={};
                Location.distanceBetween(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), all.get(i).latitude, all.get(i).longitude, results);
                if (results.length!=0 && results[0] < 500){
                    temp.add(all.get(i)._id);
                }
            }
            return temp;
        }


    }


}