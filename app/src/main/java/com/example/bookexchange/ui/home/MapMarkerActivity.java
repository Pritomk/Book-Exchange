package com.example.bookexchange.ui.home;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapMarkerActivity extends AppCompatActivity implements OnMapReadyCallback {

    SupportMapFragment mapFragment;

    public MapMarkerActivity(SupportMapFragment mapFragment) {
        this.mapFragment = mapFragment;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng home = new LatLng(25.401625, 88.518760);
        googleMap.addMarker(new MarkerOptions()
            .position(home)
            .title("Marker home"));

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(home));
    }
}
