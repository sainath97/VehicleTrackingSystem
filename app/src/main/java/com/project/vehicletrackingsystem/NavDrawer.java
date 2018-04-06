package com.project.vehicletrackingsystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NavDrawer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap gmap;
    View v;
    int res;
    int alarm;
    String type,var;
    AlertDialog.Builder builder;
    android.support.v4.app.FragmentTransaction fragmentTransaction;
    Bundle bundle = new Bundle();
    Fragment fragment = new com.project.vehicletrackingsystem.MapFragment();
    static String phn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);

        SharedPreferences pref=getSharedPreferences("registration",0);
        phn=pref.getString("phn",null);
        String type="normal";
        bundle.putString("map_type",type);
        Intent intent = getIntent();
        intent.putExtras(bundle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Toast.makeText(this,"Map Type:"+type,Toast.LENGTH_SHORT).show();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);


        fragment.setArguments(bundle);
        if(fragment !=null){
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            FragmentTransaction replace = fragmentTransaction.replace(R.id.content_frame, new com.project.vehicletrackingsystem.MapFragment());
            fragmentTransaction.commit();
        }
//        Fragment fragment = new MapFragment();*/
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_drawer, menu);
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

    private void closeApp() {
        //((ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE)).clearApplicationUserData();
        this.finish();
        System.exit(0);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_feedback) {
            startActivity(new Intent(this,FeedbackActivity.class));
            // Handle the camera action
        } else if (id == R.id.nav_alarm) {
            startActivity(new Intent(this,AlarmActivity.class));
        }/* else if (id == R.id.nav_change_map) {
            onCreateDialog();
        } else if (id == R.id.nav_notify) {

        } */else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if(id==R.id.nav_logout){
            SharedPreferences pref=getSharedPreferences("Registration",0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("complete", false);
            editor.commit();
            new ExecuteTask().execute();
            if(res==1) {
                Toast.makeText(this, "Deregistered Successfully", Toast.LENGTH_SHORT).show();
                Intent ob = new Intent(NavDrawer.this, MainActivity.class);
                startActivity(ob);
                this.finish();
            }
        }

        if(fragment!=null){
            android.support.v4.app.FragmentTransaction ft= getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame,fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    class ExecuteTask extends AsyncTask<Void,Void,Void>
    {
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
                String urls = "http://vts.store/VTS/loc/deregister.php?phno="+phn;
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
                        res=json_data.getInt("success");

                    }
                }
            } catch (Exception e) {
                res=0;
            } finally {
                if (con != null) {
                    con.disconnect();
                }
            }
            return null;
        }
    }
}
