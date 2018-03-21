package temp.navigationapplication;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.internal.Primitives;
import com.google.gson.reflect.TypeToken;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import AlgoDS.ds.graph.Edge;
import AlgoDS.ds.graph.LocationEdge;
import AlgoDS.ds.graph.LocationWeightedGraph;
import AlgoDS.ds.graph.WeightedGraph;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        Map<LocationDataPoint, Set<LocationEdge>> locMap;
        Set<LocationDataPoint> set = graph.getVertices();



    }

    public static LocationEdge EdgeToLocationEdge(Edge<LocationDataPoint> edge)
    {
        LocationEdge locEdge = new LocationEdge(edge.getFrom(), edge.getTo(), edge.getWeight());
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

        LocationDataPoint src = (LocationDataPoint) TestGraph.getVertices().toArray()[0];
        LatLng start = new LatLng(src.getLatitude(), src.getLongitude());
        mMap.addMarker(new MarkerOptions().position(start).title("You are here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(start));

    }
}
