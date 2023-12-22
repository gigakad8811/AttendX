package com.example.attendx;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class view_att_adapter extends RecyclerView.Adapter<view_att_adapter.view_att_view_holder> {

    ArrayList<view_att_helper> view_att_data;
    Context context;

    public view_att_adapter(ArrayList<view_att_helper> view_att_data, Context context) {
        this.view_att_data = view_att_data;
        this.context = context;
    }

    @NonNull
    @Override
    public view_att_view_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_att_card,parent,false);
        view_att_adapter.view_att_view_holder view_att_view_holder = new view_att_adapter.view_att_view_holder(view);
        context = parent.getContext();
        return view_att_view_holder;
    }

    @Override
    public void onBindViewHolder(@NonNull view_att_view_holder holder, int position) {
        view_att_helper view_att_helper = view_att_data.get(position);

        Glide.with(this.context).load(view_att_helper.getMem_image()).into(holder.userdp);
        holder.name.setText(view_att_helper.getMem_name());
        holder.phn.setText(view_att_helper.getMem_phone());
        holder.email.setText(view_att_helper.getMem_email());


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context,view_att_of.class);
            intent.putExtra("ROOM_ID",view_att_helper.getMem_roomid());
            intent.putExtra("UID",view_att_helper.getMem_uid());
            intent.putExtra("USR_IMG",view_att_helper.getMem_image());
            intent.putExtra("USR_NAME",view_att_helper.getMem_name());
            intent.putExtra("USR_PHN",view_att_helper.getMem_phone());
            context.startActivity(intent);
        });

    }


    @Override
    public int getItemCount() {
        return view_att_data.size();
    }

    public static class view_att_view_holder extends RecyclerView.ViewHolder{

        ImageView userdp;
        TextView name,phn,email;

        public view_att_view_holder(@NonNull View itemView) {
            super(itemView);

            userdp = itemView.findViewById(R.id.user_dp);
            name = itemView.findViewById(R.id.stu_name);
            phn = itemView.findViewById(R.id.phone);
            email = itemView.findViewById(R.id.email);

        }
    }

}
