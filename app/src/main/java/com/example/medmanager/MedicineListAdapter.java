package com.example.medmanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medmanager.mydatabase.MedicalDB;

import java.util.Calendar;



public class MedicineListAdapter extends RecyclerView.Adapter{
    private Cursor med_list;
    public Context context;
    public MedicalDB helper;
    public int user_id;

    public MedicineListAdapter(Context context, MedicalDB helper, int user_id){
        this.context = context;
        this.helper = helper;
        this.user_id = user_id;
    }

    public void setUserData(Cursor cursor){
        this.med_list = cursor;
        if(med_list!=null)
        {
            med_list.moveToFirst();
        }
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public MedicineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.medicine_card,parent,false);
        MedicineHolder vh = new MedicineHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(med_list!=null){
            ((MedicineHolder) holder).medName.setText(med_list.getString(1));


            ((MedicineHolder) holder).qty.setText("Qty: "+med_list.getInt(2));
            ((MedicineHolder) holder).id = med_list.getInt(0);
            ((MedicineHolder) holder).time.setText(med_list.getString(3));

            boolean isChecked = med_list.getInt(6)==1?true:false;
            ((MedicineHolder) holder).toggleSwitch.setChecked(isChecked);
            ((MedicineHolder) holder).toggleSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(((MedicineHolder) holder).toggleSwitch.isChecked()){
                        helper.setEnable(helper.getWritableDatabase(),((MedicineHolder) holder).id,1);
                    }else{
                        helper.setEnable(helper.getWritableDatabase(),((MedicineHolder) holder).id,0);
                    }

                    Cursor c = helper.getMedicine(helper.getWritableDatabase(),((MedicineHolder) holder).id);
                    c.moveToFirst();
                    String[] raw_time = c.getString(3).split(":",2);
                    int hour = Integer.parseInt(raw_time[0]);
                    int min = Integer.parseInt(raw_time[1]);

                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.HOUR_OF_DAY, hour);
                    cal.set(Calendar.MINUTE, min);
                    cal.set(Calendar.SECOND,0);

                    Calendar now = Calendar.getInstance();
                    now.set(Calendar.SECOND, 0);
                    now.set(Calendar.MILLISECOND, 0);



                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
                    Intent intent = new Intent(context,AlarmBroadcastReceiver.class);
                    intent.putExtra("medName",c.getString(1));
                    intent.putExtra("medQty",c.getString(2));
                    intent.putExtra("medTime",c.getString(3));
                    intent.putExtra("userName",helper.getUserName(helper.getWritableDatabase(),user_id));

                    if(((MedicineHolder) holder).toggleSwitch.isChecked()){
                        //set alarm

                        String days = c.getString(4);
                        if(days.equals("0000000")){
                            if(cal.before(now)){
                                cal.add(Calendar.DATE, 1);
                            }
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,Integer.parseInt(user_id+""+((MedicineHolder) holder).id),intent,0);
                            alarmManager.set(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),pendingIntent);

                            Toast.makeText(context, "Reminder set for "+c.getString(1)+" on "+cal.get(Calendar.HOUR)+":"+cal.get(Calendar.MINUTE) + ", "+cal.get(Calendar.DATE)+"/"+cal.get(Calendar.MONTH)+"/"+cal.get(Calendar.YEAR), Toast.LENGTH_LONG).show();
                        }
                        else{
                            int ct=1;
                            for(char d : days.toCharArray()){

                                if(d == '1'){
                                    cal.set(Calendar.DAY_OF_WEEK,ct);
                                    if(cal.before(now)){
                                        cal.add(Calendar.DATE, 7);
                                    }

                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context,Integer.parseInt(user_id+""+((MedicineHolder) holder).id + ""+ct),intent,0);
                                    System.out.println(cal.get(Calendar.DAY_OF_WEEK));
                                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),AlarmManager.INTERVAL_DAY * 7,pendingIntent);
                                }
                                ct++;
                            }
                            Toast.makeText(context, "Reminder set for "+c.getString(1)+" on "+cal.get(Calendar.HOUR)+":"+cal.get(Calendar.MINUTE), Toast.LENGTH_SHORT).show();
                        }

                    }
                    else{

                        String days = c.getString(4);
                        if(days.equals("0000000")){
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,Integer.parseInt(user_id+""+((MedicineHolder) holder).id),intent,0);
                            alarmManager.cancel(pendingIntent);
                        }
                        else{
                            int ct=1;
                            for(char d : days.toCharArray()){

                                if(d == '1'){
                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context,Integer.parseInt(user_id+""+((MedicineHolder) holder).id + ""+ct),intent,0);
                                    alarmManager.cancel(pendingIntent);
                                }
                                ct++;
                            }
                        }

                    }
                }
            });


            ((MedicineHolder) holder).deleteMed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    helper.deleteMedicine(helper.getWritableDatabase(),((MedicineHolder) holder).id);
                    setUserData(helper.getMedicineListById(helper.getWritableDatabase(),user_id));
                }
            });
            med_list.moveToNext();


        }
    }

    @Override
    public int getItemCount() {
        return med_list.getCount();
    }




    public class MedicineHolder extends RecyclerView.ViewHolder{
        TextView medName, time, qty;
        ImageButton deleteMed;
        int id;
        Switch toggleSwitch;


        public MedicineHolder(@NonNull View itemView) {
            super(itemView);
            medName = (TextView) itemView.findViewById(R.id.med_name);
            time = (TextView) itemView.findViewById(R.id.med_time);
            qty = (TextView) itemView.findViewById(R.id.med_quantity);
            deleteMed = (ImageButton) itemView.findViewById(R.id.delete_med);
            toggleSwitch = (Switch) itemView.findViewById(R.id.toggle_switch);
        }
    }


}
