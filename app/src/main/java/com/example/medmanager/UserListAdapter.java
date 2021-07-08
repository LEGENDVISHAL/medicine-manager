package com.example.medmanager;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medmanager.mydatabase.MedicalDB;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter {
    private Cursor user_list;
    public Context context;
    public MedicalDB helper;

    public UserListAdapter(Context context, MedicalDB helper) {
        this.context = context;
        this.helper = helper;
    }

    public void setUserData(Cursor cursor){
        this.user_list = cursor;
        if(user_list!=null)
        {
            user_list.moveToFirst();
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_card,parent,false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(user_list!=null) {
            ((MyViewHolder) holder).tv.setText(user_list.getString(1));
            ((MyViewHolder) holder).id = user_list.getInt(0);

            ((MyViewHolder) holder).deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    helper.deleteUser(helper.getWritableDatabase(),""+(((MyViewHolder) holder).id));
                    user_list= helper.getUserList(helper.getWritableDatabase());
                    setUserData(user_list);
                    notifyDataSetChanged();
                    if(user_list.getCount() ==0){
                        MainActivity.empty_view.setText(R.string.empty_users);
                    }
                }
            });
            ((MyViewHolder) holder).tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,MedicineActivity.class);
                    intent.putExtra("userId",((MyViewHolder) holder).id);
                    context.startActivity(intent);
                }
            });
            user_list.moveToNext();
        }

    }

    @Override
    public int getItemCount() {
        return user_list.getCount();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv;
        ImageButton deleteBtn;
        int id;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.user_name);
            deleteBtn = (ImageButton) itemView.findViewById(R.id.deleteUser);
        }
    }

}
