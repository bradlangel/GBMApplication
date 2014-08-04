package com.bradlangel.gasbyme;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.location.Location;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import java.util.List;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends ActionBarActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        AdapterView.OnItemClickListener,
        LocationListener {

    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 45;
    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 30;
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;

    //Location Client to be used by Google Play Services
    private LocationClient mLocationClient;

    // Global variable to hold the current location
    private Location mCurrentLocation;

    // Define an object that holds accuracy and frequency parameters
    private LocationRequest mLocationRequest;

    //Boolean to hold whether an update has been requested
    private boolean mUpdatesRequested;

    //Global variable to hold latitude and longitude values
    private String latitude;
    private String longitude;

    //Global variable to use for grabbing api data
    private final ApiCredentials apiCredentials = new ApiCredentials();
    public List<GasStation> gasStations;
    public ListView gasStationListView;

    //private preference for Location Updates
    private SharedPreferences mPrefs;
    //private editor to make changes to preference
    private SharedPreferences.Editor mEditor;

    //private preference for On Connection is called
    private SharedPreferences cPrefs;
    //private editor to make changes to preference
    private SharedPreferences.Editor cEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Open the shared preferences
        mPrefs = getSharedPreferences("SharedPreferences",
                Context.MODE_PRIVATE);
        // Get a SharedPreferences editor
        mEditor = mPrefs.edit();

        /*
         * Create a new location client, using the enclosing class to
         * handle callbacks.
         */
        mLocationClient = new LocationClient(this, this, this);

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create();
        // Use high accuracy
        mLocationRequest.setPriority(
                LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 5 seconds
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        // Set the fastest update interval to 1 second
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Start with updates turned off
        mUpdatesRequested = false;

    }


    /*
     * Called when the Activity becomes visible.
     */
    @Override
    protected void onStart() {
        super.onStart();

        // Connect the client.
        mLocationClient.connect();
    }

    @Override
    protected void onPause() {
        // Save the current setting for updates
        mEditor.putBoolean("KEY_UPDATES_ON", mUpdatesRequested);
        mEditor.commit();
        super.onPause();
    }

    @Override
    protected void onResume() {

        /*
         * Get any previous setting for location updates
         * Gets "false" if an error occurs
         */
        if (mPrefs.contains("KEY_UPDATES_ON")) {
            mUpdatesRequested =
                    mPrefs.getBoolean("KEY_UPDATES_ON", false);

            // Otherwise, turn off location updates
        } else {
            mEditor.putBoolean("KEY_UPDATES_ON", false);
            mEditor.commit();
        }

        super.onResume();
    }

    /*
     * Called when the Activity is no longer visible.
     */
    @Override
    protected void onStop() {
        // If the client is connected
        if (mLocationClient.isConnected()) {
            /*
             * Remove location updates for a listener.
             * The current Activity is the listener, so
             * the argument is "this".
             */
            mLocationClient.removeLocationUpdates(this);
        }
        /*
         * After disconnect() is called, the client is
         * considered "dead".
         */
        mLocationClient.disconnect();
        super.onStop();
    }

    /*
     * If list-view adapter item is clicked then start up navigation
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //Web Url for navigation
        String navigationUrl = "google.navigation:q="+gasStations.get(position).getLocation().getLatitude()+ ","
                                + gasStations.get(position).getLocation().getLongitude();

        Intent navIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(navigationUrl));
        startActivity(navIntent);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /*
     * Check to see what menu option has been selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
     * Start Settings Activity
     */
    private void openSettings() {

        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


    /*
        BEGIN: GOOGLE PLAY SERVICES: Check For
     */


    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {
        // Global field to contain the error dialog
        private Dialog mDialog;
        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }
        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }
        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

    /*
     * Handle results returned to the FragmentActivity
     * by Google Play services
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {
            case CONNECTION_FAILURE_RESOLUTION_REQUEST :
            /*
             * If the result code is Activity.RESULT_OK, try
             * to connect again
             */
                switch (resultCode) {
                    case Activity.RESULT_OK :
                    /*
                     * Try the request again
                     */
                        break;
                }

        }
    }

    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates",
                    "Google Play services is available.");
            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {
            // Get the error code
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    resultCode,
                    this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment =
                        new ErrorDialogFragment();
                // Set the dialog in the DialogFragment
                errorFragment.setDialog(errorDialog);
                // Show the error dialog in the DialogFragment
                errorFragment.show(getSupportFragmentManager(),
                        "Location Updates");
            }
            return false;
        }
    }


    /*
        BEGIN: GOOGLE PLAY SERVICES: Location Services
     */



    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle) {

        Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();
        //Grab Current Location with exception handling
        getCurrentLocation();

            /*
             * User retrofit to make call to api
             * Setup listview adapter with api data
             */
        setupAdapter();

    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(this, "Disconnected Location Services. Please re-connect.",
                Toast.LENGTH_SHORT).show();
    }


    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            int errorCode = connectionResult.getErrorCode();
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    errorCode,
                    this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);
            ErrorDialogFragment errorDialogFragment = new ErrorDialogFragment();
            errorDialogFragment.setDialog(errorDialog);
            errorDialogFragment.show(
                    getSupportFragmentManager(),
                    "Location Updates");

        }
    }

    /*
     * Sets up listview adaptor after api call
     */
    private void consumeApi(List<GasStation> gasStationList, String gasPref) {
        //Make sure list isn't null
        if (gasStationList != null) {
            gasStations = gasStationList;
            gasStationListView = (ListView) findViewById(R.id.list);
            GasStationAdaptor adaptor = new GasStationAdaptor(this, gasStationList, gasPref);
            gasStationListView.setAdapter(adaptor);

            gasStationListView.setOnItemClickListener(this);
        } else { //Give user reason for blank screen
            if(gasPref != null) {
                Toast.makeText(this, "Retrofit Failure", Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(this, "No Api Data: " + gasPref, Toast.LENGTH_LONG).show();
            }

        }

    }

    // Define the callback method that receives location updates
    @Override
    public void onLocationChanged(Location location) {

        /*
         * User retrofit to make call to api
         * Setup ListView adapter with api data
         */
        setupAdapter();
        /*
         * This tells us we have a location fix
         * Thus, turn off Update requests
         */
        mUpdatesRequested  = false;
        // If the client is connected
        if (mLocationClient.isConnected()) {
            /*
             * Remove location updates for a listener.
             */
            mLocationClient.removeLocationUpdates(this);
        }
    }
    /*
     * Used to grab current location.
     * Makes sure null pointer exceptions are caught and dealt with.
     */
    public void getCurrentLocation() {
        try
        {
            // If already requested, start periodic updates
            if(mUpdatesRequested) {
               mLocationClient.requestLocationUpdates(mLocationRequest, this);
            }
            mCurrentLocation = mLocationClient.getLastLocation();
            latitude = Double.toString(mCurrentLocation.getLatitude());
            longitude = Double.toString(mCurrentLocation.getLongitude());
        }
        catch (NullPointerException ne)
        {
            Log.e("Current Location", "Current Lat Lng is Null");
            Toast.makeText(this, "Null latLNG", Toast.LENGTH_SHORT).show();
            mUpdatesRequested = true;
            // Save the current setting for updates
            mEditor.putBoolean("KEY_UPDATES_ON", mUpdatesRequested);
            mEditor.commit();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /*
     * User retrofit to make call to api
     * Setup ListView adapter with api data
     */
    public void setupAdapter(){

        //Get Shared preferences from Settings
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        final String gasPref = sharedPref.getString(SettingsActivity.KEY_PREF_GAS, "");


        //Setup API call
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader(apiCredentials.getKey(), apiCredentials.getValue());
            }
        };


        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(apiCredentials.getUrl())
                .setRequestInterceptor(requestInterceptor)
                .build();

        DashService dashApiService = restAdapter.create(DashService.class);


        //Each call on the generated dashApiService makes an HTTP request to the remote web server.
        dashApiService.getGasStations(latitude,longitude, gasPref, new Callback<List<GasStation>>() {
            @Override
            public void success(List<GasStation> gasStationList, Response response) {
                consumeApi(gasStationList, gasPref);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                consumeApi(null, null);
            }
        });
    }

}


