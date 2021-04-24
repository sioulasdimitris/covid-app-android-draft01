package com.dimitrissioulas.covidapp;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    ArrayList<String> myLocations;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //get extra
        myLocations = (ArrayList<String>) getIntent().getSerializableExtra("myLocations");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }//onCreate

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
        ArrayList<LatLng> latlng = new ArrayList<>();

        for (int i = 0; i < myLocations.size(); i++){
            String[] fullLocation = myLocations.get(i).split("\\,");
            double latitude =  Double.parseDouble(fullLocation[0]);
            double longitude =  Double.parseDouble(fullLocation[1]);
            String name = fullLocation[2];
            latlng.add(new LatLng(latitude, longitude));
            mMap.addMarker(new MarkerOptions().position(latlng.get(i)).title(name));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng.get(i),14.0f));
        }


    }
}