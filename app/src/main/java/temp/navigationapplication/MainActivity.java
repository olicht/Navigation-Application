package temp.navigationapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Scanner;

import AlgoDS.algo.graph.Dijkstra;
import AlgoDS.ds.graph.Vertex;
import AlgoDS.ds.graph.WeightedGraph;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Button button = findViewById(R.id.TestButton);

        button.setOnClickListener(v -> {
            Gson gson = new Gson();

            InputStream is = getResources().openRawResource(R.raw.graph);
            String json = (new Scanner(is)).useDelimiter("\\Z").next();

            assert json != null;
            WeightedGraph<LocationDataPoint> TestGraph = gson.fromJson(json, new TypeToken<WeightedGraph<LocationDataPoint>>(){}.getType());
//            TestGraph.addVertex(0);
//            TestGraph.addVertex(1);
//            TestGraph.addVertex(2);
//            TestGraph.addVertex(3);
//            TestGraph.addVertex(4);
//            TestGraph.addVertex(5);
//            TestGraph.addVertex(6);
//
//            TestGraph.addEdge(0, 1, 5.0, true);
//            TestGraph.addEdge(0, 2, 13.0, true);
//            TestGraph.addEdge(5, 6, 3.0, false);
//            TestGraph.addEdge(2, 4, 3.0, true);
//            TestGraph.addEdge(2, 5, 3.0, false);
//            TestGraph.addEdge(4, 6, 15.0, true);
//            TestGraph.addEdge(1, 5, 8.0, true);
//            TestGraph.addEdge(2, 3, 11.0, false);

            Dijkstra<LocationDataPoint> dijkstra = new Dijkstra<>(TestGraph);
            Vertex<LocationDataPoint> a = new Vertex<>();
            LocationDataPoint src = (LocationDataPoint)TestGraph.getVertices().toArray()[0];
            dijkstra.shortestPathOptimized(src, src, true);
//            StringBuilder output = new StringBuilder();
//            for(int i = 1; i < 7; i++)
//            {
//                output.append(dijkstra.getDistance().get(i)).append("\n");
//            }
//            TextView out = findViewById(R.id.output1);
//            out.setText(output.toString());
        });
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
