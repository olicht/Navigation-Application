package temp.navigationapplication;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.internal.Primitives;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import AlgoDS.ds.graph.Edge;
import AlgoDS.ds.graph.LocationEdge;
import AlgoDS.ds.graph.LocationWeightedGraph;
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
//        Button menu = findViewById(R.id.start);
//        menu.setOnClickListener(this);
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
        BufferedReader bufferedReader =  new BufferedReader(new InputStreamReader(inputStream));
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

    public static LocationWeightedGraph WeightedGraphToLocationGraph(WeightedGraph<LocationDataPoint> graph)
    {
        LocationWeightedGraph locGraph = new LocationWeightedGraph(graph.getUndirected());
        Set<Edge<LocationDataPoint>> edges = graph.getEdges();
        Set<LocationEdge> locSet = new HashSet<>();
        for (Edge<LocationDataPoint> edge: edges) {
            locSet.add(EdgeToLocationEdge(edge));
        }
        locGraph.setEdges(locSet);

        Map<LocationDataPoint, Set<LocationEdge>> locMap = new HashMap<>();
        Map<LocationDataPoint, Set<Edge<LocationDataPoint>>> map = graph.getVertices();

        for (LocationDataPoint loc : map.keySet()) {
            Set<LocationEdge> verSet = new HashSet<>();
            for (Edge<LocationDataPoint> edge : map.get(loc)) {
                verSet.add(EdgeToLocationEdge((edge)));
            }
            locMap.put(loc, verSet);
        }

        return locGraph;

    }

    public static LocationEdge EdgeToLocationEdge(Edge<LocationDataPoint> edge)
    {
        LocationEdge locEdge = new LocationEdge((LocationDataPoint) edge.getFrom(), (LocationDataPoint) edge.getTo(), edge.getWeight());
        return locEdge;
    }
    

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        JsonElement graphElement = null;
        WeightedGraph<LocationDataPoint> TestGraph;
        try {
            graphElement = fileToJsonElement(getResources().openRawResource(R.raw.graph));
            assert graphElement != null;
            TestGraph = (WeightedGraph)jsonToObject(graphElement, WeightedGraph.class);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

//        Gson gson = new Gson();
//        InputStream is = getResources().openRawResource(R.raw.graph);
//        String json = (new Scanner(is)).useDelimiter("\\Z").next();
//
//a        LocationWeightedGraph TestGraph = gson.fromJson(json, new TypeToken<LocationWeightedGraph>(){}.getType());
        // Add a marker in Sydney and move the camera

        LocationWeightedGraph locGraph = WeightedGraphToLocationGraph(TestGraph);
        LocationDataPoint src = (LocationDataPoint) locGraph.getVertices().toArray()[0];
        LatLng start = new LatLng(src.getLatitude(), src.getLongitude());
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
