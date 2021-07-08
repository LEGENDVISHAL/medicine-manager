package com.example.medmanager;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medmanager.mydatabase.MedicalDB;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MedicineActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
//    medicine list for user with id = user_id;
    public RecyclerView medList;
    public TextView medUserName;
    public MedicineListAdapter medListAdapter;
    public FloatingActionButton medFab;
    public int user_id;
    Button medTime;
    EditText medName, medQty;
    Switch isRepeat;
    ChipGroup chipGroup;
    Chip sun,mon,tue,wed,thu,fri,sat;

//    database:
    public MedicalDB DbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine);
        DbHelper = MedicalDB.getInstance(getApplicationContext());
        user_id = getIntent().getIntExtra("userId",0);

        //Connect views to the activity:
        medList = findViewById(R.id.med_list);
        medUserName = findViewById(R.id.med_user_name);
        medFab = findViewById(R.id.med_fab);

        medUserName.setText(DbHelper.getUserName(DbHelper.getWritableDatabase(),user_id));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        medList.setLayoutManager(linearLayoutManager);
        medListAdapter = new MedicineListAdapter(getApplicationContext(),DbHelper,user_id);
        medListAdapter.setUserData(DbHelper.getMedicineListById(DbHelper.getWritableDatabase(),user_id));
        medList.setAdapter(medListAdapter);




        medFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medicineAdder().show();
            }
        });

        medUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myTextDialog().show();
            }
        });
    }

    private android.app.AlertDialog myTextDialog() {
        View layout = View.inflate(this, R.layout.update_user_dialog, null);
        EditText savedText = ((EditText) layout.findViewById(R.id.update_username));

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

        builder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                DbHelper.updateUser(DbHelper.getWritableDatabase(),user_id,savedText.getText().toString());
                medUserName.setText(savedText.getText().toString());

            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setView(layout);
        return builder.create();
    }

    private AlertDialog medicineAdder(){


        View layout = View.inflate(this, R.layout.add_med_dialog, null);
//        medicine details:
        medName = layout.findViewById(R.id.add_med_name);
        medQty = layout.findViewById(R.id.add_med_qty);
        medTime = layout.findViewById(R.id.add_med_time);
//        UI components:
        isRepeat = layout.findViewById(R.id.repeat_switch);
        chipGroup = layout.findViewById(R.id.chip_group);
        setChildrenEnabled(chipGroup,false);
        sun = layout.findViewById(R.id.sunday);
        mon = layout.findViewById(R.id.monday);
        tue = layout.findViewById(R.id.tuesday);
        wed = layout.findViewById(R.id.wednesday);
        thu = layout.findViewById(R.id.thursday);
        fri = layout.findViewById(R.id.friday);
        sat = layout.findViewById(R.id.saturday);

        medTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(),"time picker");
            }
        });

        isRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isRepeat.isChecked()){
                    setChildrenEnabled(chipGroup,false);
                }else{
                    setChildrenEnabled(chipGroup,true);
                }
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String temp = medQty.getText().toString();
                int qty = 0;
                if (!"". equals(temp))
                    qty = Integer. parseInt(temp);
                String days="0000000";
                if(isRepeat.isChecked()){
                    days = setDaysFormat(sun,mon,tue,wed,thu,fri,sat);
                }
                DbHelper.addMedicine(DbHelper.getWritableDatabase(),user_id,medName.getText().toString(),qty,medTime.getText().toString(),days);
                medListAdapter.setUserData(DbHelper.getMedicineListById(DbHelper.getWritableDatabase(),user_id));
                medListAdapter.notifyDataSetChanged();
                medList.setAdapter(medListAdapter);

            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setView(layout);
        return builder.create();
    }

    public String setDaysFormat(Chip sun, Chip mon, Chip tue, Chip wed, Chip thu, Chip fri, Chip sat){
        String dayString = ""+ (sun.isChecked()?"1":"0") + (mon.isChecked()?"1":"0") + (tue.isChecked()?"1":"0") + (wed.isChecked()?"1":"0") + (thu.isChecked()?"1":"0") + (fri.isChecked()?"1":"0") + (sat.isChecked()?"1":"0");
        return dayString;
    }

    public void setChildrenEnabled(ChipGroup chipGroup, Boolean enable) {
        for(int i=0; i<chipGroup.getChildCount(); i++){
            chipGroup.getChildAt(i).setEnabled(enable);
        }

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        medTime.setText(hourOfDay + ":"+minute);
    }
}