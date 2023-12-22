package com.example.attendx;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class take_att_adapter extends RecyclerView.Adapter<take_att_adapter.view_take_att_holder> {

    ArrayList<take_att_helper> take_att_data;
    Context context;

    public take_att_adapter(ArrayList<take_att_helper> take_att_data, Context context){
        this.take_att_data=take_att_data;
        this.context=context;
    }

    @NonNull
    @Override
    public view_take_att_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.take_att_card,parent,false);
        take_att_adapter.view_take_att_holder take_att_holder = new take_att_adapter.view_take_att_holder(view);
        context = parent.getContext();
        return take_att_holder;
    }

    @Override
    public void onBindViewHolder(@NonNull view_take_att_holder holder, int position) {

        take_att_helper take_att_helper = take_att_data.get(position);

        Glide.with(this.context).load(take_att_helper.getImg()).into(holder.userdp);
        holder.name.setText(take_att_helper.getName());
        holder.phn.setText(take_att_helper.getPhn());
        holder.email.setText(take_att_helper.getEmail());

    }

    @Override
    public int getItemCount() {
        return take_att_data.size();
    }


    public static class view_take_att_holder extends RecyclerView.ViewHolder{

        ImageView userdp;
        TextView name,phn,email;
        Button submit_att_btn;

        public view_take_att_holder(@NonNull View itemView) {
            super(itemView);

            userdp = itemView.findViewById(R.id.user_dp);
            name = itemView.findViewById(R.id.stu_name);
            phn = itemView.findViewById(R.id.phone);
            email = itemView.findViewById(R.id.email);
            submit_att_btn = itemView.findViewById(R.id.submit_att_btn);

        }
    }
}
