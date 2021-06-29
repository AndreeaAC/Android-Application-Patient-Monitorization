package com.example.patientmonitorization;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PlotActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot);
    }


    public void onBackPressed() {
        Intent intent = new Intent(PlotActivity.this, PatientPageActivity.class);
        startActivity(intent);
    }
}
