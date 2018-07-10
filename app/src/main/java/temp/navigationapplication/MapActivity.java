package temp.navigationapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.internal.Primitives;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import AlgoDS.algo.graph.Dijkstra;
import AlgoDS.ds.graph.Edge;
import AlgoDS.ds.graph.WeightedGraph;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, PopupMenu.OnMenuItemClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    //private FusedLocationProviderApi fusedLocationProviderApi = LocationServices.API;

    private WeightedGraph<LocationDataPoint> testGraph;
    private LocationDataPoint src; //last known location
    private LocationDataPoint destination; //current destination
    private HashMap<String, LocationDataPoint> checkPoints = new HashMap<>();
    private HashMap<String, LocationDataPoint> FoodPoints = new HashMap<>();
    private boolean accessible = false;
    private Button mBtGoBack;

    private static GoogleApiClient mGoogleApiClient;
    public static final String TAG = MapActivity.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private ArrayList<Marker> mapMarkers = new ArrayList<>(); //holds all of the markers placed on the map!
    protected static final int LOCATION_SETTINGS_REQUEST = 3;

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

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
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
    public void onConnected(@Nullable Bundle bundle) {

        Log.i(TAG, "Location services connected.");
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//        if (location == null) {
//            // Blank for a moment...
//        } else {
//            handleNewLocation(location);
//        }
//        ;

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
//        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//
//        if (location == null) {
////            mGoogleApiClient.connect();
//        } else {
//            handleNewLocation(location);
//        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this); //request it

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

        if(closestPoint(testGraph, new LocationDataPoint(location.getLongitude(), location.getLatitude(), true))==destination)
            Toast.makeText(this, "הגעת ליעד!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
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
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    public class Pair<VT> implements Comparable<Pair<VT>> {
        VT label;
        Double weight;

        Pair(VT name, Double weight) {
            this.label = name;
            this.weight = weight;
        }

        @Override
        public int compareTo(@NonNull Pair p) {
            return (int) (this.weight - p.weight);
        }
    }


//TODO: change every "eshkol" with current location and test it at the university!

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Button menu = (Button) findViewById(R.id.start);
        menu.setOnClickListener(this);

        Button colorsMenu = (Button) findViewById(R.id.changeColor);
        colorsMenu.setOnClickListener(this::onClick3);

        Switch sw = (Switch) findViewById(R.id.switchButton);
        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            this.accessible = isChecked;
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        initializeMap();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(3 * 1000)        // 3 seconds, in milliseconds
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
                                        .startResolutionForResult(MapActivity.this,
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

        mBtGoBack = (Button) findViewById(R.id.go_back);

        mBtGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
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
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Call GoogleApiClient connection when starting the Activity
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Disconnect GoogleApiClient when stopping Activity
        mGoogleApiClient.disconnect();
    }

    public void initializeMap() {
        checkPoints.put("hecht", new LocationDataPoint(35.018046, 32.763212, true));
        checkPoints.put("eshkol", new LocationDataPoint(35.017679, 32.762889, true));
        checkPoints.put("northParking", new LocationDataPoint(35.016614, 32.764143, true));
        checkPoints.put("rabin", new LocationDataPoint(35.020226, 32.761343, true));
        checkPoints.put("stairs", new LocationDataPoint(35.021115, 32.760776, true));
        checkPoints.put("smell", new LocationDataPoint(35.020779, 32.761295, true));
        checkPoints.put("academon", new LocationDataPoint(35.017769, 32.763016, true));
        checkPoints.put("greg", new LocationDataPoint(35.019785, 32.761953, true));
        checkPoints.put("food", new LocationDataPoint(35.017857, 32.763148, true));
        checkPoints.put("studentHouse", new LocationDataPoint(35.021108, 32.761718, true));
        checkPoints.put("jacobs", new LocationDataPoint(35.018913, 32.761446, true));
        checkPoints.put("education", new LocationDataPoint(35.018377, 32.761893, true));
        checkPoints.put("aguda", new LocationDataPoint(35.018997, 32.762180, true));
        checkPoints.put("library", new LocationDataPoint(35.018907, 32.762170, true));
        checkPoints.put("haias", new LocationDataPoint(35.018995, 32.761290, true));
        checkPoints.put("meonot", new LocationDataPoint(35.023319, 32.759218, true));
        checkPoints.put("sportRoom", new LocationDataPoint(35.021314, 32.760980, true));
        checkPoints.put("tennis", new LocationDataPoint(35.022324, 32.760309, true));
        checkPoints.put("multiPurpose", new LocationDataPoint(35.021498, 32.760405, true));
        checkPoints.put("grass", new LocationDataPoint(35.020078, 32.762034, true));
        checkPoints.put("parkingBridge", new LocationDataPoint(35.019187, 32.760959, true));
        checkPoints.put("northEntrance", new LocationDataPoint(35.016716, 32.763237, true));
        checkPoints.put("pilpelet", new LocationDataPoint(35.021007, 32.760468, true));
        checkPoints.put("shopsRoad", new LocationDataPoint(35.019084, 32.761974, true));
        checkPoints.put("educationParking", new LocationDataPoint(35.017147, 32.762698, true));
        checkPoints.put("upperParking", new LocationDataPoint(35.018844, 32.761799, true));

        FoodPoints.put("food", new LocationDataPoint(35.017857, 32.763148, true));
        FoodPoints.put("aguda", new LocationDataPoint(35.018997, 32.762180, true));
        FoodPoints.put("education", new LocationDataPoint(35.018377, 32.761893, true));
        FoodPoints.put("pilpelet", new LocationDataPoint(35.021007, 32.760468, true));
        FoodPoints.put("shopsRoad", new LocationDataPoint(35.019084, 32.761974, true));
        FoodPoints.put("greg", new LocationDataPoint(35.019785, 32.761953, true));
        FoodPoints.put("smell", new LocationDataPoint(35.020779, 32.761295, true));
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

    //This method finds the point on the graph which is closest to the user's current location.
    public static LocationDataPoint closestPoint(WeightedGraph<LocationDataPoint> graph, LocationDataPoint currentLoc) {
        double dist;
        double minDist = Double.POSITIVE_INFINITY;
        LocationDataPoint closest = null;
        for (LocationDataPoint loc : graph.getVertices()) {
            dist = CalculateDistance.distanceBetween(loc.getLatitude(), loc.getLongitude(), currentLoc.getLatitude(), currentLoc.getLongitude())[0];
            if (dist < minDist) {
                closest = loc;
                minDist = dist;
            }
        }
        return closest;
    }

    public void changeMarkersColor(int color) {
        for (Marker m : mapMarkers) {
            m.setIcon(BitmapDescriptorFactory.defaultMarker(color));
        }
    }

    public void showPath(List<LocationDataPoint> list) {
        Iterator<LocationDataPoint> iterator = list.iterator();
        LocationDataPoint prev = null;
        while (iterator.hasNext()) {
            LocationDataPoint dest = iterator.next();
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(dest.getLatitude(), dest.getLongitude()));
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(200));
            Marker m = mMap.addMarker(markerOptions);
            mapMarkers.add(m);
            if (prev != null) {
                PolylineOptions polylineOptions = new PolylineOptions()
                        .add(new LatLng(dest.getLatitude(), dest.getLongitude())) // Point A.
                        .add(new LatLng(prev.getLatitude(), prev.getLongitude())); // Point B.
                Polyline polyline = mMap.addPolyline(polylineOptions);
            }
            prev = dest;
        }

        if(accessible==true && list.size()==1)
        {
            Toast.makeText(this, "מצטערים, לא קיים מסלול נגיש ליעד.", Toast.LENGTH_SHORT).show();
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        // We ask for the bounds if they have been set as they would be most
        // correct, then we check we are  > 0
        final int width = !drawable.getBounds().isEmpty() ?
                drawable.getBounds().width() : drawable.getIntrinsicWidth();

        final int height = !drawable.getBounds().isEmpty() ?
                drawable.getBounds().height() : drawable.getIntrinsicHeight();

        // Now we check we are > 0
        final Bitmap bitmap = Bitmap.createBitmap(width <= 0 ? 1 : width, height <= 0 ? 1 : height,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public WeightedGraph<LocationDataPoint> toRealGraph(WeightedGraph<LocationDataPoint> graph) {

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

        //adding two additional edges
        LocationDataPoint lib = checkPoints.get("library");
        LocationDataPoint edu = checkPoints.get("education");
        LocationDataPoint jac = checkPoints.get("jacobs");
        realGraph.addEdge(lib, edu , CalculateDistance.distanceBetween(lib.getLatitude(), lib.getLongitude(), edu.getLatitude(), edu.getLongitude())[0]);
        realGraph.addEdge(lib, jac , CalculateDistance.distanceBetween(lib.getLatitude(), lib.getLongitude(), jac.getLatitude(), jac.getLongitude())[0]);

        return realGraph;
    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public String translateToHebrew(String s) {
        switch (s) {
            case "food":
                return "מתחם המזון";
            case "smell":
                return "ארומה";
            case "shopsRoad":
                return "קפה עלית";
            case "education":
                return "קפה מרפסת";
            case "greg":
                return "גרג";
            case "pilpelet":
                return "פלפלת";
            case "aguda":
                return "קפה אגודה";
            default:
                return " ";
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) throws SecurityException {
        mMap = googleMap;

        JsonElement graphElement;
        WeightedGraph<LocationDataPoint> TestGraph;
        try {
            graphElement = fileToJsonElement(getResources().openRawResource(R.raw.graph2));
            assert graphElement != null;
            TestGraph = (WeightedGraph) jsonToObject(graphElement, WeightedGraph.class);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        testGraph = toRealGraph(TestGraph);

        LocationDataPoint src1 = (LocationDataPoint) testGraph.getVertices().toArray()[0];

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        src = src1;
                        if (location != null) {
                            src = new LocationDataPoint(location.getLongitude(), location.getLatitude(), true);
                            LatLng start = new LatLng(src.getLatitude(), src.getLongitude());

                            //mMap.addMarker(new MarkerOptions().position(start).title("You are here"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(start));
                            mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                        }
                    }
                });
        if (mGoogleApiClient == null) {
            buildGoogleApiClient();
        }
        mMap.setMyLocationEnabled(true);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
//        mGoogleApiClient.connect();
    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);// to implement on click event on items of menu
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.options_menu, popup.getMenu());
        popup.show();
    }

    public void showColorsMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this::onMenuItemClickColor);// to implement on click event on items of menu
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.colors_menu, popup.getMenu());
        popup.show();
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_SETTINGS_REQUEST) {
            if (resultCode == RESULT_OK) {
                LocationDataPoint src1 = (LocationDataPoint) testGraph.getVertices().toArray()[0];

                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                src = src1;
                                if (location != null) {
                                    src = new LocationDataPoint(location.getLongitude(), location.getLatitude(), true);
                                    LatLng start = new LatLng(src.getLatitude(), src.getLongitude());

                                    //mMap.addMarker(new MarkerOptions().position(start).title("You are here"));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(start));
                                    mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                                }
                            }
                        });

            }
        }
    }

    @Override
    public void onClick(View v) {
        showMenu(v);
    }

    public void onClick1(View v) {
        mMap.clear();
        mapMarkers = new ArrayList<>();
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        mFusedLocationClient.getLastLocation()
//                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                    @Override
//                    public void onSuccess(Location location) {
//                        // Got last known location. In some rare situations this can be null.
//                        if (location != null) {
//                            src = new LocationDataPoint(location.getLongitude(), location.getLatitude(), true);
//                            LatLng start = new LatLng(src.getLatitude(), src.getLongitude());
//
//                            mMap.addMarker(new MarkerOptions().position(start).title("You are here"));
//                            mMap.moveCamera(CameraUpdateFactory.newLatLng(start));
//                            mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
//                        }
//                    }
//                });
    }

    public void onClick2(View v) {
        Dijkstra<LocationDataPoint> dijkstra = new Dijkstra<>(testGraph);
        Map<LocationDataPoint, Dijkstra<LocationDataPoint>.Three> path = new HashMap<>();
        LocationDataPoint start = closestPoint(testGraph, src);
        List<LocationDataPoint> way;
        way = dijkstra.shortestPathOptimized(start, FoodPoints.get("food"), accessible);
        double dist;

        Iterator it = FoodPoints.entrySet().iterator();
        Pair<LocationDataPoint>[] restArr = new Pair[FoodPoints.size()];
        int i = 0;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            dist = dijkstra.getDistance().get(FoodPoints.get(pair.getKey())).getWeight();
            restArr[i] = new Pair((LocationDataPoint) pair.getValue(), dist);
            i++;
        }
        Arrays.sort(restArr);

        PopupMenu menu = new PopupMenu(this, v);
        for (int j = 0; j < restArr.length; j++) {
            if (j == 0) {
                menu.getMenu().add(0, Menu.FIRST + j, Menu.NONE, "הקרובה ביותר: " + translateToHebrew(getKeyByValue(FoodPoints, restArr[j].label)));
            } else if (j == restArr.length - 1) {
                menu.getMenu().add(0, Menu.FIRST + j, Menu.NONE, "הרחוקה ביותר: " + translateToHebrew(getKeyByValue(FoodPoints, restArr[j].label)));
            } else {
                menu.getMenu().add(0, Menu.FIRST + j, Menu.NONE, translateToHebrew(getKeyByValue(FoodPoints, restArr[j].label)));
            }
        }
        menu.setOnMenuItemClickListener(item -> onMenuItemClick1(item, restArr));// to implement on click event on items of menu
        MenuInflater inflater = menu.getMenuInflater();
        inflater.inflate(R.menu.rest_menu, menu.getMenu());
        menu.show();


    }

    public void onClick3(View v) {
        PopupMenu menu = new PopupMenu(this, v);
        menu.setOnMenuItemClickListener(this::onMenuItemClickColor);// to implement on click event on items of menu
        MenuInflater inflater = menu.getMenuInflater();
        inflater.inflate(R.menu.colors_menu, menu.getMenu());
        menu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        // Handle item selection
        Dijkstra<LocationDataPoint> dijkstra = new Dijkstra<>(testGraph);
        Map<LocationDataPoint, Dijkstra<LocationDataPoint>.Three> path = new HashMap<>();
        LocationDataPoint start = closestPoint(testGraph, src);
        List<LocationDataPoint> way;
        switch (item.getItemId()) {
            case R.id.hecht:
                way = dijkstra.shortestPathOptimized(start, checkPoints.get("hecht"), accessible /*, checkPoints.get("hecht"), false*/);
                destination = checkPoints.get("hecht");
                showPath(way);
                return true;
            case R.id.eshkol:
                way = dijkstra.shortestPathOptimized(start, checkPoints.get("eshkol"), accessible /*, checkPoints.get("hecht"), false*/);
                destination = checkPoints.get("eshkol");
                showPath(way);
                return true;
            case R.id.northParking:
                way = dijkstra.shortestPathOptimized(start, checkPoints.get("northParking"), accessible /*, checkPoints.get("hecht"), false*/);
                destination = checkPoints.get("northParking");
                showPath(way);
                return true;
            case R.id.rabin:
                way = dijkstra.shortestPathOptimized(start, checkPoints.get("rabin"), accessible /*, checkPoints.get("hecht"), false*/);
                destination = checkPoints.get("rabin");
                showPath(way);
                return true;
            case R.id.stairs:
                way = dijkstra.shortestPathOptimized(start, checkPoints.get("stairs"), accessible /*, checkPoints.get("hecht"), false*/);
                destination = checkPoints.get("stairs");
                showPath(way);
                return true;
            case R.id.smell:
                way = dijkstra.shortestPathOptimized(start, checkPoints.get("smell"), accessible /*, checkPoints.get("hecht"), false*/);
                destination = checkPoints.get("smell");
                showPath(way);
                return true;
            case R.id.academon:
                way = dijkstra.shortestPathOptimized(start, checkPoints.get("academon"), accessible /*, checkPoints.get("hecht"), false*/);
                destination = checkPoints.get("academon");
                showPath(way);
                return true;
            case R.id.greg:
                way = dijkstra.shortestPathOptimized(start, checkPoints.get("greg"), accessible /*, checkPoints.get("hecht"), false*/);
                destination = checkPoints.get("greg");
                showPath(way);
                return true;
            case R.id.food:
                way = dijkstra.shortestPathOptimized(start, checkPoints.get("food"), accessible /*, checkPoints.get("hecht"), false*/);
                destination = checkPoints.get("food");
                showPath(way);
                return true;
            case R.id.studentHouse:
                way = dijkstra.shortestPathOptimized(start, checkPoints.get("studentHouse"), accessible /*, checkPoints.get("hecht"), false*/);
                destination = checkPoints.get("studentHouse");
                showPath(way);
                return true;
            case R.id.jacobs:
                way = dijkstra.shortestPathOptimized(start, checkPoints.get("jacobs"), accessible /*, checkPoints.get("hecht"), false*/);
                destination = checkPoints.get("jacobs");
                showPath(way);
                return true;
            case R.id.education:
                way = dijkstra.shortestPathOptimized(start, checkPoints.get("education"), accessible /*, checkPoints.get("hecht"), false*/);
                destination = checkPoints.get("education");
                showPath(way);
                return true;
            case R.id.aguda:
                way = dijkstra.shortestPathOptimized(start, checkPoints.get("aguda"), accessible /*, checkPoints.get("hecht"), false*/);
                destination = checkPoints.get("aguda");
                showPath(way);
                return true;
            case R.id.library:
                way = dijkstra.shortestPathOptimized(start, checkPoints.get("library"), accessible /*, checkPoints.get("hecht"), false*/);
                destination = checkPoints.get("library");
                showPath(way);
                return true;
            case R.id.haias:
                way = dijkstra.shortestPathOptimized(start, checkPoints.get("haias"), accessible /*, checkPoints.get("hecht"), false*/);
                destination = checkPoints.get("haias");
                showPath(way);
                return true;
            case R.id.meonot:
                way = dijkstra.shortestPathOptimized(start, checkPoints.get("meonot"), accessible /*, checkPoints.get("hecht"), false*/);
                destination = checkPoints.get("meonot");
                showPath(way);
                return true;
            case R.id.sportRoom:
                way = dijkstra.shortestPathOptimized(start, checkPoints.get("sportRoom"), accessible /*, checkPoints.get("hecht"), false*/);
                destination = checkPoints.get("sportRoom");
                showPath(way);
                return true;
            case R.id.tennis:
                way = dijkstra.shortestPathOptimized(start, checkPoints.get("tennis"), accessible /*, checkPoints.get("hecht"), false*/);
                destination = checkPoints.get("tennis");
                showPath(way);
                return true;
            case R.id.multiPurpose:
                way = dijkstra.shortestPathOptimized(start, checkPoints.get("multiPurpose"), accessible /*, checkPoints.get("hecht"), false*/);
                destination = checkPoints.get("multiPurpose");
                showPath(way);
                return true;
            case R.id.grass:
                way = dijkstra.shortestPathOptimized(start, checkPoints.get("grass"), accessible /*, checkPoints.get("hecht"), false*/);
                destination = checkPoints.get("grass");
                showPath(way);
                return true;
            case R.id.parkingBridge:
                way = dijkstra.shortestPathOptimized(start, checkPoints.get("parkingBridge"), accessible /*, checkPoints.get("hecht"), false*/);
                destination = checkPoints.get("parkingBridge");
                showPath(way);
                return true;
            case R.id.northEnter:
                way = dijkstra.shortestPathOptimized(start, checkPoints.get("northEntrance"), accessible /*, checkPoints.get("hecht"), false*/);
                destination = checkPoints.get("northEntrance");
                showPath(way);
                return true;
            case R.id.pilpeplet:
                way = dijkstra.shortestPathOptimized(start, checkPoints.get("pilpeplet"), accessible /*, checkPoints.get("hecht"), false*/);
                destination = checkPoints.get("pilpeplet");
                showPath(way);
                return true;
            case R.id.shopsRoad:
                way = dijkstra.shortestPathOptimized(start, checkPoints.get("shopsRoad"), accessible /*, checkPoints.get("hecht"), false*/);
                destination = checkPoints.get("shopsRoad");
                showPath(way);
                return true;
            case R.id.educationParking:
                way = dijkstra.shortestPathOptimized(start, checkPoints.get("educationParking"), accessible /*, checkPoints.get("hecht"), false*/);
                destination = checkPoints.get("educationParking");
                showPath(way);
                return true;
            default:
                return true;
        }
    }

    public boolean onMenuItemClick1(MenuItem item, Pair<LocationDataPoint>[] arr) {
        // Handle item selection
        Dijkstra<LocationDataPoint> dijkstra = new Dijkstra<>(testGraph);
        Map<LocationDataPoint, Dijkstra<LocationDataPoint>.Three> path = new HashMap<>();
        LocationDataPoint start = closestPoint(testGraph, src);
        List<LocationDataPoint> way;
        int index = (item.getItemId() - Menu.FIRST);
        way = dijkstra.shortestPathOptimized(start, arr[index].label, accessible);
        showPath(way);
        return true;
    }

    public boolean onMenuItemClickColor(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.blue:
                changeMarkersColor(240);
                break;
            case R.id.red:
                changeMarkersColor(0);
                break;
            case R.id.yellow:
                changeMarkersColor(60);
                break;
            case R.id.green:
                changeMarkersColor(120);
                break;
            case R.id.orange:
                changeMarkersColor(30);
                break;
            case R.id.purple:
                changeMarkersColor(270);
                break;
            case R.id.pink:
                changeMarkersColor(315);
                break;
            case R.id.cyan:
                changeMarkersColor(180);
                break;
            default:

        }
        return true;
    }
}

