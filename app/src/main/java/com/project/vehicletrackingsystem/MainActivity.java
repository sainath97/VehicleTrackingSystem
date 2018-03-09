package com.project.vehicletrackingsystem;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class MainActivity extends AppCompatActivity {

    private int PLACE_PICKER_REQUEST = 1;
    EditText phone;
    TextView placeDetails;
    Spinner location;
    Button register,pickLocation;
    SharedPreferences pref;
    Editor editor;
    boolean completed;
    LatLng lo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        phone=(EditText)findViewById(R.id.phone);
        //location=(Spinner)findViewById(R.id.location);
        register=(Button)findViewById(R.id.register);
        pickLocation=(Button)findViewById(R.id.pickLoc);
        placeDetails=(TextView)findViewById(R.id.pd);

        pref=getSharedPreferences("Registration",0);
        completed=pref.getBoolean("complete",false);
        if(completed==true)
        {
            Intent ob=new Intent(MainActivity.this,MapsActivity.class);
            ob.putExtra("lat",Double.parseDouble(pref.getString("lat",null)));
            ob.putExtra("lng",Double.parseDouble(pref.getString("lng",null)));
            startActivity(ob);
            this.finish();
        }
        else {
            editor = pref.edit();
            pickLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    builder.setLatLngBounds(new LatLngBounds(new LatLng(16.293825, 80.443341), new LatLng(16.293825, 80.443341)));
                    try {
                        startActivityForResult(builder.build(MainActivity.this), PLACE_PICKER_REQUEST);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                }
            });
            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String ph = phone.getText().toString();
                    //String loc = location.getSelectedItem().toString();
                    if(placeDetails.getText().toString().length()<=0)
                        Toast.makeText(MainActivity.this, "Please pick a boarding point", Toast.LENGTH_SHORT).show();
                    else if (phone.getText().length() <= 0)
                        Toast.makeText(MainActivity.this, "Enter Phone Number", Toast.LENGTH_SHORT).show();
                    else {
                        editor.putString("Phone", ph);
                        //editor.putString("Location", loc);
                        editor.putBoolean("complete", true);
                        editor.putString("lat",String.valueOf(lo.latitude));
                        editor.putString("lng",String.valueOf(lo.longitude));
                        editor.commit();
                        Intent i = new Intent(MainActivity.this, MapsActivity.class);
                        i.putExtra("lat",lo.latitude);
                        i.putExtra("lng",lo.longitude);
                        startActivity(i);
                        MainActivity.this.finish();
                    }
                }
            });
        }
    }
   @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String placename = String.format("%s", place.getName());
                String latitude = String.valueOf(place.getLatLng().latitude);
                String longitude = String.valueOf(place.getLatLng().longitude);

                lo = new LatLng(place.getLatLng().latitude,place.getLatLng().longitude);

                placeDetails.setText("Selected Boarding Point: "+placename);
            }
        }
    }
}
