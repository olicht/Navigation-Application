package temp.navigationapplication;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.internal.Primitives;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import AlgoDS.ds.graph.Edge;
import AlgoDS.ds.graph.WeightedGraph;

import static temp.navigationapplication.MainActivity.locsMap;
import static temp.navigationapplication.MapActivity.LOCATION_SETTINGS_REQUEST;

public class HeatmapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationDataPoint src;
    private Button mBtHGoBack;
    private WeightedGraph<LocationDataPoint> testGraph;
    private GoogleApiClient mGoogleApiClient;
    public static final String TAG = HeatmapActivity.class.getSimpleName();


    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                handleNewLocation(location);

            }
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission was granted.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
//                        if (mGoogleApiClient == null) {
//                            buildGoogleApiClient();
//                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            //You can add here other case statements according to your requirement.
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heatmap);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mBtHGoBack = (Button) findViewById(R.id.h_go_back);

        mBtHGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
        //for the location turn on dialog
        LocationSettingsRequest.Builder settingsBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        settingsBuilder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(this)
                .checkLocationSettings(settingsBuilder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response =
                            task.getResult(ApiException.class);
                } catch (ApiException ex) {
                    switch (ex.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvableApiException =
                                        (ResolvableApiException) ex;
                                resolvableApiException
                                        .startResolutionForResult(HeatmapActivity.this,
                                                LOCATION_SETTINGS_REQUEST);
                            } catch (IntentSender.SendIntentException e) {

                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                            break;
                    }
                }
            }
        });


    }



    private void addHeatMap() {

        // Create a heat map tile provider
        if (locsMap.size() > 0) {
            ArrayList<LatLng> locs = new ArrayList<>(locsMap.values());
            HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                    .data(locs)
                    .build();
            // Add a tile overlay to the map, using the heat map tile provider.
            TileOverlay mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    /**
     * Converts a file to a json element
     */
    public static JsonElement fileToJsonElement(InputStream inputStream) throws IOException {
        String jsonElementString;
        com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        while ((jsonElementString = bufferedReader.readLine()) != null) {
            stringBuilder.append(jsonElementString);
        }
        bufferedReader.close();
        return parser.parse(stringBuilder.toString());
    }

    /**
     * Converts any json element to an object of a given type
     */
    public static <T> T jsonToObject(JsonElement element, Class<T> classOfT) throws IOException {
        Gson gson = new Gson();
        Object object = gson.fromJson(element, classOfT);
        return Primitives.wrap(classOfT).cast(object);
    }

    public static WeightedGraph<LocationDataPoint> toRealGraph(WeightedGraph<LocationDataPoint> graph) {

        Set<Edge<LocationDataPoint>> set = graph.getEdges();
        Set<Edge<LocationDataPoint>> realSet = new HashSet<>();
        WeightedGraph<LocationDataPoint> realGraph = new WeightedGraph<>(graph.getUndirected());

        //casting the edges
        for (Edge<LocationDataPoint> e : set) {
            //LocationDataPoint l=e.getFrom();
            Object from = e.getFrom();
            Object to = e.getTo();
            LinkedTreeMap<String, Double> from1 = (LinkedTreeMap<String, Double>) from;
            LinkedTreeMap<String, Double> to1 = (LinkedTreeMap<String, Double>) to;
            Object accF = from1.get("accessible");
            Object accT = to1.get("accessible");
            LocationDataPoint locFrom = new LocationDataPoint(from1.get("longitude"), from1.get("latitude"), (boolean) accF);
            LocationDataPoint locTo = new LocationDataPoint(to1.get("longitude"), to1.get("latitude"), (boolean) accT);
            Edge<LocationDataPoint> realEdge = new Edge<LocationDataPoint>(locFrom, locTo, e.getWeight(), e.getAccessible());
            realSet.add(realEdge);
            //realSet.add(realEdge.getOpposite());
        }

        for (Edge<LocationDataPoint> e : realSet) {
            if (!realGraph.getVertices().contains(e.getFrom())) realGraph.addVertex(e.getFrom());
            if (!realGraph.getVertices().contains(e.getTo())) realGraph.addVertex(e.getTo());

            realGraph.addEdge(e);
        }
        return realGraph;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        src = null;
                        if (location != null) {
                            src = new LocationDataPoint(location.getLongitude(), location.getLatitude(), true);
                            LatLng start = new LatLng(src.getLatitude(), src.getLongitude());
//
//                            mMap.addMarker(new MarkerOptions().position(start).title("You are here"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(start));
                            mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                        }
                    }
                });
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);

        addHeatMap();
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        handleNewLocation(location);
                    }
                });

    }

    private void handleNewLocation(Location location) {

        Map<String, Object> locUpdates = new HashMap<>();
        locUpdates.put("currentLongitude", location.getLongitude());
        locUpdates.put("currentLatitude", location.getLatitude());
        locUpdates.put("messageTime", new Date().getTime());


        FirebaseDatabase.getInstance()
                .getReference()
                .child("locations")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .updateChildren(locUpdates);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);

    }
}
