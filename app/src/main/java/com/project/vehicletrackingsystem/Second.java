package com.project.vehicletrackingsystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Second extends AppCompatActivity {
    TextView t;
    SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        t=(TextView)findViewById(R.id.blank);
        pref=getSharedPreferences("Registration",0);
        t.setText(pref.getString("Phone",null));
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
