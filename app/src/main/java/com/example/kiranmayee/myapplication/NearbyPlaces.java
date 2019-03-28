package com.example.kiranmayee.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NearbyPlaces extends AsyncTask<Object, String, String> {

    String googlePlacesData;
    ArrayList<MarkerOptions> nearplace = new ArrayList<>();
    ArrayList<LatLng> latLngs=new ArrayList<>();
    MarkerOptions markerOptions;
    GoogleMap mMap;
    String url;

    @Override
    protected String doInBackground(Object... params) {
        try {
            Log.d("GetNearbyPlacesData", "doInBackground entered");
            mMap = (GoogleMap) params[0];
            url = (String) params[1];
            DownloadUrl downloadUrl = new DownloadUrl();
            googlePlacesData = downloadUrl.readUrl(url);
            Log.d("GooglePlacesReadTask", "doInBackground Exit");
        } catch (Exception e) {
            Log.d("GooglePlacesReadTask", e.toString());
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String result) {
        LatLng latLng=null;
        Log.d("GooglePlacesReadTask", "onPostExecute Entered");
        List<HashMap<String, String>> nearbyPlacesList = null;
        DataParser dataParser = new DataParser();
        nearbyPlacesList =  dataParser.parse(result);
        for (int i = 0; i < nearbyPlacesList.size(); i++)
        {
            markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = nearbyPlacesList.get(i);
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));
            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            latLng = new LatLng(lat, lng);
            latLngs.add(latLng);
            markerOptions.position(latLng);
            markerOptions.title(placeName + " : " + vicinity);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            nearplace.add(markerOptions);
        }
        ShowNearbyPlaces(nearplace,latLngs);
        Log.d("GooglePlacesReadTask", "onPostExecute Exit");
    }

    private void ShowNearbyPlaces(ArrayList<MarkerOptions> nearplace,ArrayList<LatLng> latLng)
    {
        for(int i=0;i<nearplace.size();i++) {
            mMap.addMarker(nearplace.get(i));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng.get(i)));
        }
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
    }
}