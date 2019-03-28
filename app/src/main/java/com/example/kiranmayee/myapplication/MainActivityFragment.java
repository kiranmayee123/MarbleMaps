package com.example.kiranmayee.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.kiranmayee.myapplication.GPSTracker.latitude;
import static com.example.kiranmayee.myapplication.GPSTracker.longitude;

public class MainActivityFragment extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    GPSTracker gps;
    Polyline line;
    private int PROXIMITY_RADIUS = 10000;
    static Location location;
    private static final int PERMISSION_REQUEST_GPS = 0;
    ArrayList<LatLng> location1 = new ArrayList<LatLng>();
    ArrayList<String> places=new ArrayList<>();
    HashMap<String, Double> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (!(ActivityCompat.checkSelfPermission(MainActivityFragment.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED))
            // Permission is not available,request permission
            requestGPSPermission();

        mapFragment.getMapAsync(this);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert mLocationManager != null;
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 20,mLocationListener);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markMap(R.drawable.map_marker);
            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button btnRestaurant = (Button) findViewById(R.id.places);
        btnRestaurant.setOnClickListener(new View.OnClickListener() {
            String Restaurant = "restaurant";
            @Override
            public void onClick(View v) {
                Log.d("onClick", "Button is Clicked");
                mMap.clear();
                String url = getUrl(latitude, longitude, Restaurant);
                Object[] DataTransfer = new Object[2];
                DataTransfer[0] = mMap;
                DataTransfer[1] = url;
                Log.d("onClick", url);
                NearbyPlaces getNearbyPlacesData = new NearbyPlaces();
                getNearbyPlacesData.execute(DataTransfer);
                Toast.makeText(MainActivityFragment.this,"Nearby Restaurants", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.places) {
            Intent i = new Intent(MainActivityFragment.this, MapListActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    int count=0;
    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.e("Lat",String.valueOf(location.getLatitude()));
            LatLng loc = new LatLng(location.getLatitude(),location.getLongitude());
            Boolean flag=false;
            float distance=0;
            for(LatLng i:location1)
            {
                Location temp = new Location(LocationManager.GPS_PROVIDER);
                temp.setLatitude(i.latitude);
                temp.setLongitude(i.longitude);
                distance = location.distanceTo(temp);
                if (distance<=10)
                {
                    flag = true;
                    Log.e("Map",String.valueOf(distance));
                }
            }
            Toast.makeText(getBaseContext(), String.valueOf(distance), Toast.LENGTH_SHORT).show();
            if(flag)
                redrawLine(loc,R.drawable.revmarker);
            else
                redrawLine(loc,R.drawable.midmarker);
            location1.add(loc);
            LatLng save = new LatLng(0,0);
            for(LatLng i:location1)
                if(i.latitude>0 && i.longitude>0 && count==0)
                {
                    save=i;
                    count++;
                }

            if(count==1)
            {
                DatabaseHelper db = new DatabaseHelper(MainActivityFragment.this);
                Maps map = new Maps();
                map.setLatitude(save.latitude);
                map.setLongitude(save.longitude);
                db.addMap(map);
            }
        }

        private void redrawLine(LatLng gps,int Marker){

            PolylineOptions options = new PolylineOptions().width(10).color(Color.BLUE).geodesic(true);
            for (int i = 0; i < location1.size(); i++) {
                LatLng point = location1.get(i);
                options.add(point);
            }
            drawMarker(gps,Marker); //add Marker in current position
            line = mMap.addPolyline(options); //add Polyline
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            //markMap(R.drawable.map_marker);
        }

        @Override
        public void onProviderEnabled(String s) {
            gps = new GPSTracker(MainActivityFragment.this);
            LatLng loc = new LatLng(gps.getLatitude(),gps.getLongitude());
           drawMarker(loc,R.drawable.map_marker);
        }

        @Override
        public void onProviderDisabled(String s) {
          //  markMap(R.drawable.midmarker);
        }
    };

    public void drawMarker(LatLng gps,int mapMarker) {
        if (mMap != null) {
           // mMap.clear();
            Marker mark=mMap.addMarker(new MarkerOptions()
                    .position(gps)
                    .title("This is me!!!")
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.fromResource(mapMarker)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gps,17));
            mark.showInfoWindow();
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        markMap(R.drawable.map_marker);
    }
    private void requestGPSPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.

            // Request the permission
            ActivityCompat.requestPermissions(MainActivityFragment.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_GPS);

        } else {
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_GPS);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == PERMISSION_REQUEST_GPS) {
            // Request for GPS permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start GPS Activity.
                markMap(R.drawable.map_marker);
            } else {
                // Permission request was denied.
                Toast.makeText(getApplicationContext(),"Permission denied",Toast.LENGTH_LONG).show();
            }
        }
        // END_INCLUDE(onRequestPermissionsResult)
    }

    public void markMap(int mapMarker)
    {
        gps = new GPSTracker(MainActivityFragment.this);
        LatLng gpsLat;
        if(gps.canGetLocation())
            gpsLat = new LatLng(gps.getLatitude(), gps.getLongitude());
        else
        {
            gps.showSettingsAlert();
            gpsLat = new LatLng(gps.getLatitude(), gps.getLongitude());
        }
        Log.e("fg",String.valueOf(gps.getLatitude()));
        drawMarker(gpsLat,mapMarker);
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace) {

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyATuUiZUkEc_UgHuqsBJa1oqaODI-3mLs0");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }
}