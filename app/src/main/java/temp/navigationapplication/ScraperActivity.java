package temp.navigationapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ScraperActivity extends FragmentActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback {

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Constant used in the location settings dialog.
     */
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    /**
     * Code used in requesting runtime permissions.
     */
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private static final String TAG = ScraperActivity.class.getSimpleName();

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private FusedLocationProviderClient mFusedLocationClient;

    private LocationRequest mLocationRequest;

    private LocationSettingsRequest mLocationSettingsRequest;

    private boolean mPermissionDenied = false;

    private boolean mRequestingLocationUpdates;

    private SettingsClient mSettingsClient;

    private LocationCallback mLocationCallback;
    private ObjectOutputStream oos;
    private boolean isStairs;
    private FirebaseStorage storage;
    private File file;

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(ScraperActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(ScraperActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        //noinspection StatementWithEmptyBody
        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            // TODO: Do something useful
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
        private void showMissingPermissionError() {
            PermissionUtils.PermissionDeniedDialog
                    .newInstance(true).show(getSupportFragmentManager(), "dialog");
        }


    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setContentView(R.layout.activity_scraper);
        mRequestingLocationUpdates = false;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        storage = FirebaseStorage.getInstance();

        Button startRecording = (Button) findViewById(R.id.start_recording);
        startRecording.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    startLocationUpdates();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        final Button stopRecording = findViewById(R.id.stop_recording);
        stopRecording.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Snackbar.make(v, "תודה רבה על תרומתך!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                try {
                    stopLocationUpdates();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        final Button startStairs = findViewById(R.id.start_stairs);
        startStairs.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startStairs();
            }
        });

        final Button stopStairs = findViewById(R.id.stop_stairs);
        stopStairs.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stopStairs();
            }
        });

        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();
    }

    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    try {
                        //write new location to the json
                        oos.writeObject(new LocationDataPoint(location.getLongitude(), location.getLatitude(), !isStairs));

                        //update DB
                        Map<String, Object> locUpdates = new HashMap<>();
                        locUpdates.put("currentLongitude", location.getLongitude());
                        locUpdates.put("currentLatitude", location.getLatitude());
                        locUpdates.put("messageTime", new Date().getTime());
                        FirebaseDatabase.getInstance()
                                .getReference()
                                .child("locations")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .updateChildren(locUpdates);
                        oos.flush();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Within {@code onPause()}, we remove location updates. Here, we resume receiving
        // location updates if the user has requested them.
        if (mRequestingLocationUpdates && checkPermissions()) {
            resumeLocationUpdates();
        } else if (!checkPermissions()) {
            requestPermissions();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        // Remove location updates to save battery.
        pauseLocationUpdates();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void resumeLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG,
                                        "Location settings are not satisfied. Attempting to "
                                                + "upgrade "
                                                +
                                                "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and
                                    // check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(ScraperActivity.this,
                                            REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage =
                                        "Location settings are inadequate, and cannot be " +
                                                "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(ScraperActivity.this, errorMessage,
                                        Toast.LENGTH_LONG).show();
                                mRequestingLocationUpdates = false;
                        }
                    }

                });
    }

    /**
     * Requests location updates from the FusedLocationApi. Note: we don't call this unless location
     * runtime permission has been granted.
     */
    public void startLocationUpdates() throws IOException {
        if (!mRequestingLocationUpdates) {
            File dir = new File(
                    Environment.getExternalStorageDirectory().getAbsolutePath());

            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
            file = new File(dir, "record_" + System.currentTimeMillis() + ".rec1");
            FileOutputStream fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
            isStairs = false;
            resumeLocationUpdates();
            mRequestingLocationUpdates = true;
        }
    }

    private void pauseLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    public void stopLocationUpdates() throws IOException {
        if (mRequestingLocationUpdates) {
            pauseLocationUpdates();
            oos.close();
            StorageReference storageReference = storage.getReference();
            StorageReference record = storageReference.child("record"+System.currentTimeMillis()+".rec1");
            Uri fUri = Uri.fromFile(file);
            UploadTask uploadTask = record.putFile(fUri);
            mRequestingLocationUpdates = false;
        }
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    public void startStairs() {
        isStairs = true;
    }

    public void stopStairs() {
        isStairs = false;
    }


}

