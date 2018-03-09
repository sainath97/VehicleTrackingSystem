package com.project.vehicletrackingsystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LatLng loc;
    SharedPreferences pref;
    Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Bundle b=getIntent().getExtras();
        loc = new LatLng(b.getDouble("lat"),b.getDouble("lng"));

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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = loc;
        mMap.addMarker(new MarkerOptions().position(sydney).title("Bus No.49(30kmph)"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


    @Override
    public void onBackPressed() {
        finish();
    }

    public void deregister(View view)
    {
        pref=getSharedPreferences("Registration",0);
        editor = pref.edit();
        editor.putBoolean("complete", false);
        editor.commit();
        Toast.makeText(this, "Deregistered Successfully", Toast.LENGTH_SHORT).show();
        Intent ob=new Intent(MapsActivity.this,MainActivity.class);
        startActivity(ob);
        this.finish();
    }
}
