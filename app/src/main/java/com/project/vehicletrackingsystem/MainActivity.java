package com.project.vehicletrackingsystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private int PLACE_PICKER_REQUEST = 1;
    static EditText phone;
    TextView placeDetails;
    //Spinner location;
    Button register,pickLocation;
    static SharedPreferences pref;
    static Editor editor;
    boolean completed;
    static  LatLng lo;
    static int res;
    static String message="";

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
        if(completed==true) {
            Intent ob = new Intent(MainActivity.this, NavDrawer.class);
            ob.putExtra("lat", Double.parseDouble(pref.getString("lat", null)));
            ob.putExtra("lng", Double.parseDouble(pref.getString("lng", null)));
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
                    else if (phone.getText().length() < 10||phone.getText().length() > 10)
                        Toast.makeText(MainActivity.this, "Enter valid Phone Number", Toast.LENGTH_SHORT).show();
                    else {
                        new GetUserDetails().execute();
                        if(res==0)
                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                        else {
                            editor.putString("Phone", ph);
                            //editor.putString("Location", loc);
                            editor.putBoolean("complete", true);
                            editor.putString("lat", String.valueOf(lo.latitude));
                            editor.putString("lng", String.valueOf(lo.longitude));
                            editor.commit();
                            Intent i = new Intent(MainActivity.this, NavDrawer.class);
                            i.putExtra("lat", lo.latitude);
                            i.putExtra("lng", lo.longitude);
                            startActivity(i);
                            MainActivity.this.finish();
                        }
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

class GetUserDetails extends AsyncTask<Void, Void, Void> {
    int resCode = -1;
    InputStream stream;
    HttpURLConnection con=null;
    String dbres="";
    BufferedReader bufferedReader=null;
    StringBuffer sb;
    JSONArray jArray;
    JSONObject json_data = null;
    String phn=MainActivity.phone.getText().toString();
    @Override
    protected Void doInBackground(Void... voids) {

        try {
            String urls = "http://vts.store/VTS/loc/get_details.php?phno="+phn;
            URL url = new URL(urls);
            con = (HttpURLConnection) url.openConnection();
            con.setReadTimeout(15000);
            con.setConnectTimeout(9000);
            con.setRequestMethod("GET");
            //con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();
            resCode = con.getResponseCode();

            if (resCode == HttpURLConnection.HTTP_OK) {
                stream = con.getInputStream();


                bufferedReader = new BufferedReader(new InputStreamReader(stream));

                String line = "";
                sb = new StringBuffer();
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                dbres = sb.toString();
                json_data=new JSONObject(dbres);
                //Log.d("Parser Task",jArray.toString());
                //json_data=jArray.getJSONObject(0);
                if(json_data!=null)
                {
                    MainActivity.res=json_data.getInt("success");
                    if(MainActivity.res==0)
                        MainActivity.message=json_data.getString("message");
                }
            }
        } catch (Exception e) {
            MainActivity.res=0;
            MainActivity.message=e.toString();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        return null;
    }
}