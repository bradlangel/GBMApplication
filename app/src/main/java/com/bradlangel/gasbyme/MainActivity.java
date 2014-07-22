package com.bradlangel.gasbyme;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;

import java.util.List;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

import static android.app.ProgressDialog.show;


public class MainActivity extends ActionBarActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener,
        AdapterView.OnItemClickListener{



    // Global constants
    public final static String EXTRA_MESSAGE = "com.bradlangel.myapplication.MESSAGE";

    //Global constants for testing lat long
    private final static String latitude = "40.686647";
    private final static String longitude = "-73.991809";



    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */

    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    //Location Client to be used by Google Play Services
    private LocationClient mLocationClient;


    // Global variable to hold the current location
    public Location mCurrentLocation;


    //Global variable to use for grabbing api data
    private final ApiCredentials apiCredentials = new ApiCredentials();


    List<GasStation> gasStations;
    ListView gasStationListView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * Create a new location client, using the enclosing class to
         * handle callbacks.
         */
        mLocationClient = new LocationClient(this, this, this);

    }


    /*
     * Called when the Activity becomes visible.
     */
    @Override
    protected void onStart() {
        super.onStart();

        // Connect the client.
        mLocationClient.connect();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String gasPref = sharedPref.getString(SettingsActivity.KEY_PREF_GAS, "");

        //Setup API call
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader(apiCredentials.getKey(), apiCredentials.getValue());
            }
        };


        RestAdapter restAdapter =  new RestAdapter.Builder()
                .setEndpoint(apiCredentials.getUrl())
                .setRequestInterceptor(requestInterceptor)
                .build();

        DashService dashApiService = restAdapter.create(DashService.class);


        //Each call on the generated dashApiService makes an HTTP request to the remote web server.
        gasStations = dashApiService.listGasStations(latitude,
                longitude,
                gasPref);

        gasStationListView = (ListView)findViewById(R.id.list);
        GasStationAdaptor adaptor = new GasStationAdaptor(this, gasStations, gasPref);
        gasStationListView.setAdapter(adaptor);

        gasStationListView.setOnItemClickListener(this);


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String station_name = gasStations.get(position).getLongName();
        Toast.makeText(getApplicationContext(), "" + station_name,
                Toast.LENGTH_SHORT).show();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_search:
                openSearch();
                return true;
            case R.id.action_settings:
                openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openSearch() {


        /*
        mCurrentLocation = mLocationClient.getLastLocation();
        String latitude = Double.toString(mCurrentLocation.getLatitude());
        String longitude = Double.toString(mCurrentLocation.getLongitude());
        */
        Toast.makeText(this, "Latitude: "+ latitude + ", Longitude: " + longitude + "----"+ gasStations.get(0).getLongName(), Toast.LENGTH_SHORT).show();
    }

    private void openSettings() {
        Toast.makeText(this, "Settings Button has been pressed", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


    /*
     * Called when the Activity is no longer visible.
     */
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
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
        // Display the connection status
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();

    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(this, "Disconnected. Please re-connect.",
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
        BEGIN: GOOGLE PLAY SERVICES: Location Update
     */

    // Define the callback method that receives location updates
    @Override
    public void onLocationChanged(Location location) {
        // Report to the UI that the location was updated
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
