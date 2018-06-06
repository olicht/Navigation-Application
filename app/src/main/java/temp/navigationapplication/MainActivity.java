package temp.navigationapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private int SIGN_IN_REQUEST_CODE = 1;
    private FusedLocationProviderClient mFusedLocationClient;

//    public static ArrayList<LatLng> getLocs() {
//        return locs;
//    }

    //    public static ArrayList<LatLng> locs = new ArrayList<>();
    public static HashMap<String, LatLng> locsMap = new HashMap<>();

    private static final int LOCATION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle("Welcome!            ");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "פתח את התפריט שלמעלה מימין ובחר את הפעולה הרצויה :)", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //start sign in/up dialog
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start sign in/sign up activity
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .build(),
                    SIGN_IN_REQUEST_CODE
            );
        } else {
            // User is already signed in. Therefore, display
            // a welcome Toast
            Toast.makeText(this,
                    "Welcome " + FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getDisplayName(),
                    Toast.LENGTH_LONG)
                    .show();
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_REQUEST_CODE);

            } else {
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    //removing the old and pushing the new current location - works only like that!! :(
//                                FirebaseDatabase.getInstance()
//                                        .getReference()
//                                        .child("locations")
//                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                                        .removeValue();

                                    Map<String, Object> locUpdates = new HashMap<>();
                                    locUpdates.put("currentLongitude", location.getLongitude());
                                    locUpdates.put("currentLatitude", location.getLatitude());
                                    locUpdates.put("messageTime", new Date().getTime());
                                    locUpdates.put("messageUid", FirebaseAuth.getInstance().getCurrentUser().getUid());

                                    FirebaseDatabase.getInstance()
                                            .getReference()
                                            .child("locations")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .updateChildren(locUpdates);
//                                        .push()
//                                        .setValue(new LocationMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(), location.getLatitude(), location.getLongitude()));
                                }
                            }
                        });
            }
        }
        // Read from the database
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("locations");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                GenericTypeIndicator<HashMap<String, Object>> objectsGTypeInd = new GenericTypeIndicator<HashMap<String, Object>>() {
                };
                Map<String, Object> objectHashMap = dataSnapshot.getValue(objectsGTypeInd);
                if (objectHashMap != null) {
                    for (Map.Entry<String, Object> entry : objectHashMap.entrySet()) {

                        HashMap<String, Double> h1 = (HashMap<String, Double>) entry.getValue();
                        Double longitude = h1.get("currentLongitude");
                        Double latitude = h1.get("currentLatitude");
//                        locs.add(new LatLng(latitude, longitude));
                        if (locsMap.containsKey(FirebaseAuth.getInstance().getUid())) {
                            locsMap.replace(FirebaseAuth.getInstance().getUid(), new LatLng(latitude, longitude));
                        } else //the user does not exists
                        {
                            locsMap.put(FirebaseAuth.getInstance().getUid(), new LatLng(latitude, longitude));
                        }

//                    }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                // Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

//    private void getLocations(Map<String, Object> locations) {
//
//        //iterate through each user, ignoring their UID
//        for (Map.Entry<String, Object> entry : locations.entrySet()) {
//
//            //Get user map
//            Map singleUser = (Map) entry.getValue();
//            //Get phone field and append to list
//            locs.add(new LatLng((double) singleUser.get("currentLatitude"), (double) singleUser.get("currentLongitude")));
//
//            System.out.println(locs.toString());
//        }
//
//    }

    private void launchMapActivity() {

        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    private void launchHeatActivity() {
        Intent intent = new Intent(this, HeatmapActivity.class);
        startActivity(intent);
    }

    private void launchChatActivity() {
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.

        switch (item.getItemId()) {
            case (R.id.nav_navigate):
                launchMapActivity();
                break;
            case (R.id.nav_gallery):
                launchHeatActivity();
                break;
            case (R.id.nav_slideshow):
                launchChatActivity();
                break;
            default:
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mFusedLocationClient.getLastLocation()
                            .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare situations this can be null.
                                    if (location != null) {
                                        //removing the old and pushing the new current location - works only like that!! :(
//                                FirebaseDatabase.getInstance()
//                                        .getReference()
//                                        .child("locations")
//                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                                        .removeValue();

                                        Map<String, Object> locUpdates = new HashMap<>();
                                        locUpdates.put("currentLongitude", location.getLongitude());
                                        locUpdates.put("currentLatitude", location.getLatitude());
                                        locUpdates.put("messageTime", new Date().getTime());
                                        locUpdates.put("messageUid", FirebaseAuth.getInstance().getCurrentUser().getUid());

                                        FirebaseDatabase.getInstance()
                                                .getReference()
                                                .child("locations")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .updateChildren(locUpdates);
//                                        .push()
//                                        .setValue(new LocationMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(), location.getLatitude(), location.getLongitude()));
                                    }
                                }
                            });
                } else {
                    finish();
                }
                return;
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this,
                        "Successfully signed in. Welcome!",
                        Toast.LENGTH_LONG)
                        .show();
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    return;
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            LOCATION_REQUEST_CODE);

                } else {
                    mFusedLocationClient.getLastLocation()
                            .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare situations this can be null.
                                    if (location != null) {
                                        Map<String, Object> locUpdates = new HashMap<>();
                                        locUpdates.put("currentLongitude", location.getLongitude());
                                        locUpdates.put("currentLatitude", location.getLatitude());
                                        locUpdates.put("messageTime", new Date().getTime());
                                        locUpdates.put("messageUid", FirebaseAuth.getInstance().getCurrentUser().getUid());

                                        //push current location
                                        FirebaseDatabase.getInstance()
                                                .getReference()
                                                .child("locations")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .updateChildren(locUpdates);
//                                            .push()
//                                            .setValue(new LocationMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(), location.getLatitude(), location.getLongitude()));
                                    }
                                }
                            });
                }

            } else {
                Toast.makeText(this,
                        "We couldn't sign you in. Please try again later.",
                        Toast.LENGTH_LONG)
                        .show();

                // Close the app
                finish();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sign_out) {

            //delete the user's child in the DB
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("locations")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .removeValue();

            AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Toast.makeText(MainActivity.this,
                                    "You have been signed out.",
                                    Toast.LENGTH_LONG)
                                    .show();

                            // Close activity
                            finish();
                        }
                    });
        }
        return true;
    }

}
