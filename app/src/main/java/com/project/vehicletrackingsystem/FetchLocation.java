package com.project.vehicletrackingsystem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by Ammu on 02-02-2017.
 */

public class FetchLocation {
    private double speed;

    public String getLocation(String device_id) throws IOException {
        int resCode = -1;
        InputStream stream;
        HttpURLConnection con=null;
        String dbres,return_text = null;
        JSONObject json_data = null;
        JSONObject location=null;
        JSONArray jarray;
        double longitude=16,latitude=80;
        BufferedReader bufferedReader=null;
        StringBuffer sb;
        try {
            List nameValuePairs = null;
            String urls = "http://vts.store/VTS/loc/get_location.php?device_id='"+device_id+"'";
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
                if(!dbres.equals("0 results")) {
                    //jArray = new JSONArray(dbres);
                    //Log.d("ParserTask", jArray.toString());
                    json_data = new JSONObject(dbres);
                    if (json_data != null) {
                        //this.bus_no = bus_no;
                        jarray=json_data.getJSONArray("location");
                        location = jarray.getJSONObject(0);
                        latitude=location.getDouble("Latitude");
                        longitude=location.getDouble("Longitude");
                        speed=location.getDouble("Speed");
                        return "" + latitude + ":" + longitude + ":" + speed;
                    }
                }
            }
        } catch (Exception e) {
            return "0:"+e.toString();

        } finally {

            if (con != null) {
                con.disconnect();
                bufferedReader.close();
            }
            // return_text= "@@@@@@@@@@"+return_text;
        }
        return_text="0:fail";
        return return_text;
    }
}