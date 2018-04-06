package com.project.vehicletrackingsystem;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment { //implements OnMapReadyCallback {

    // private Spinner spinner2;
    private GoogleMap map;
    //float distance,estimatedArrivalTime;
    SharedPreferences pref;
    int res;
    String result = null;
    String message = "";
    double lat;
    double lon;
    double speed;
    //Location source, destination, currentLocation;
    private MapView mapView;
    //private onFragmentInteractionListener mListener;
    //Bundle bundle;
    //String type;
    MarkerOptions college;
    Marker currentMarker;
    private int BOUND_PADDING = 100;
    private List<LatLng> list=new ArrayList<LatLng>();
    String device_id = "dv_001";
    String phn = NavDrawer.phn;

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    /*class GetDeviceId extends  AsyncTask<Void,Void,Void>{

        int resCode = -1;
        InputStream stream;
        HttpURLConnection con=null;
        String dbres="";
        BufferedReader bufferedReader=null;
        StringBuffer sb;
        JSONArray jArray;
        JSONObject json_data = null;


        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String urls = "http://vts.store/VTS/loc/get_deviceid.php?phno='"+phn+"'";
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
                        int res=json_data.getInt("success");
                        Log.d("res",String.valueOf(res));
                            device_id=json_data.getString("deviceid");
                            Log.d("device_id",device_id);
                    }
                }
            } catch (Exception e) {
                Log.d("Error","error");
                e.toString();
            } finally {
                if (con != null) {
                    con.disconnect();
                }
            }
            return null;
        }
    }*/

    class ExecuteTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.d("Executing", "doInBackGround");
            FetchLocation fl = new FetchLocation();
            try {
                result = fl.getLocation(device_id);
                Log.e("Result", result);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            if (result != null) {
                String[] parts = result.split(":");
                Log.d("Executing", "PostExecute");
                if (parts.length == 3) {
                    lat = Double.parseDouble(parts[0]); // 004
                    lon = Double.parseDouble(parts[1]);
                    //speed = Float.parseFloat(parts[2]);
                    //brno = parts[3];
                    speed = Double.parseDouble(parts[2]); // 034556
                    onMapReady(map);
                    Log.e("Result", String.valueOf(lat));
                    //Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
                } else {
                    // Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("calling", "getDeviceId");
        //Log.d("calling","Execute Task");
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = (MapView) v.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy old = StrictMode.getThreadPolicy();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder(old)
                .permitDiskWrites()
                .build());
        addItemsOnSpinner2(v);
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap gMap) {
                map = gMap;
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                map.getUiSettings().setCompassEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
                //LatLng marker = new LatLng(lat,lon);
                //college = new MarkerOptions().position(marker).title("VVIT");
                //currentMarker = map.addMarker(college);
                //currentMarker.showInfoWindow();
                //CameraPosition cameraPosition = new CameraPosition.Builder().target(marker).zoom(14).build();
                //map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                //onMapReady(googleMap);
            }
        });

        StrictMode.setThreadPolicy(old);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }
/*
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment=(SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);
    }
*/

    public void addItemsOnSpinner2(View view) {


        final Handler handler = new Handler();
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            ExecuteTask jsonTask = new ExecuteTask();
                            jsonTask.execute();
                        } catch (Exception e) {
                            // error, do something
                        }
                    }
                });
            }
        };

        timer.schedule(task, 0, 5 * 1000);
        //Toast.makeText(getContext(),"BrNo:"+brno,Toast.LENGTH_SHORT).show();
    }


    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        //map.clear();
        LatLng point = new LatLng(lat, lon);
        list.add(point);
        drawPolyLineOnMap(list);
        MarkerOptions marker = new MarkerOptions().position(point);
        Marker m = map.addMarker(marker);
        m.setTitle("Current Location("+speed+")");
        //map.addMarker(college);
        m.showInfoWindow();
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(point,16));
    }
    //map.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 16));

    //LatLng dest=new LatLng(lat,lon);
    //LatLng origin=new LatLng(17.02,79.26);
    //map.addMarker(new MarkerOptions().position(dest));
    //map.addMarker(new MarkerOptions().position(dest).title("Bus No.50("+speed+"kmph)"));
    //map.moveCamera(CameraUpdateFactory.newLatLngZoom(dest,10));

    //String url = getDirectionsUrl(origin, dest);

    //DownloadTask downloadTask = new DownloadTask();
    // Start downloading json data from Google Directions API
    //downloadTask.execute(url);*/


    public void drawPolyLineOnMap(List<LatLng> list) {
        /*PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(Color.BLUE);
        polyOptions.width(5);
        polyOptions.addAll(list);
        map.clear();
        map.addPolyline(polyOptions);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : list) {
            builder.include(latLng);
        }
        final LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, BOUND_PADDING);
        map.animateCamera(cu);
    }*/
        int n = list.size();
        if(n>1) {
            String url = null;
            for (int i = 0; i < n - 1; i++) {
                url = getDirectionsUrl(list.get(i), list.get(i + 1));
            }

            DownloadTask downloadTask = new DownloadTask();
            //Start downloading json data from Google Directions API
            downloadTask.execute(url);
        }
    }
    class DownloadTask extends AsyncTask<String,Void,String>
    {

        @Override
        protected String doInBackground(String... url) {
            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap point = path.get(j);

                    double lat = Double.parseDouble((String) point.get("lat"));
                    double lng = Double.parseDouble((String) point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(4);
                lineOptions.color(Color.BLACK);
                lineOptions.geodesic(true);
            }

// Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng latLng : list) {
                builder.include(latLng);
            }
            final LatLngBounds bounds = builder.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, BOUND_PADDING);
            map.animateCamera(cu);
        }
    }


    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


}
/*class GetLocation extends AsyncTask<Void, Void, Void> {
    int resCode = -1;
    InputStream stream;
    HttpURLConnection con=null;
    String dbres="";
    BufferedReader bufferedReader=null;
    StringBuffer sb;
    JSONObject json_data = null;
    JSONObject location=null;
    //String device_id="dv_001";
    @Override
    protected Void doInBackground(Void... voids) {

        try {
            String urls = "http://vts.store/VTS/loc/get_location.php?device_id='dv_001'";
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

                        location=json_data.getJSONObject("location");
                        MapFragment.lat=location.getDouble("Latitude");
                        MapFragment.lon=location.getDouble("Longitude");
                        MapFragment.speed=location.getDouble("Speed");


                }
            }
        } catch (Exception e) {
            e.toString();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        return null;
    }
}*/