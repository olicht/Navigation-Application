package temp.navigationapplication;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.InputStream;
import java.util.Scanner;

import AlgoDS.ds.graph.WeightedGraph;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private GoogleMap mMap;

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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Gson gson = new Gson();
        InputStream is = getResources().openRawResource(R.raw.graph);
        String json;
        json = (new Scanner(is)).useDelimiter("\\Z").next();

        WeightedGraph<LocationDataPoint> TestGraph;
        //TestGraph = gson.fromJson(json, new TypeToken<WeightedGraph<LocationDataPoint>>(){}.getType());
        // Add a marker and move the camera
        //LocationDataPoint src = (LocationDataPoint)TestGraph.getVertices().toArray()[0];
        LatLng start = new LatLng(32.763272, 35.016757);
        mMap.addMarker(new MarkerOptions().position(start).title("You are here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(start));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);// to implement on click event on items of menu
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.options_menu, popup.getMenu());
        popup.show();
    }

    @Override
    public void onClick(View v) {

        showMenu(v);

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }
}
