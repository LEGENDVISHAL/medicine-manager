package com.example.medmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AlarmActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_alarm);

    TextView userName, medName, medTime, medQty;
    Button tookMed, snooze;

    userName = findViewById(R.id.alarm_user_name);
    medName = findViewById(R.id.alarm_med_name);
    medTime = findViewById(R.id.alarm_med_time);
    medQty = findViewById(R.id.alarm_med_quantity);

    tookMed = findViewById(R.id.alarm_took);
    snooze = findViewById(R.id.alarm_snooze);

    Intent intent = getIntent();
    medName.setText(intent.getStringExtra("medName"));
    medTime.setText("Time: "+intent.getStringExtra("medTime"));
    medQty.setText("Qty: "+intent.getStringExtra("medQty"));
    userName.setText(intent.getStringExtra("userName"));

    tookMed.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });

    snooze.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Toast.makeText(getApplicationContext(),"Reminder set after 10 minutes.",Toast.LENGTH_LONG).show();
        finish();
      }
    });


  }
}