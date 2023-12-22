package com.example.attendx;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class view_mem_adapter extends RecyclerView.Adapter<view_mem_adapter.view_memview_holder> {

    ArrayList<view_mem_helper> view_mem_data;
    Context context;

    public view_mem_adapter(ArrayList<view_mem_helper> view_mem_data, Context context) {
        this.view_mem_data = view_mem_data;
        this.context = context;
    }

    @NonNull
    @Override
    public view_memview_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_mem_card,parent,false);
        view_memview_holder view_memview_holder = new view_memview_holder(view);
        context = parent.getContext();
        return view_memview_holder;
    }

    @Override
    public void onBindViewHolder(@NonNull view_memview_holder holder, int position) {
        view_mem_helper view_mem_helper = view_mem_data.get(position);

        Glide.with(this.context).load(view_mem_helper.getMem_image()).into(holder.userdp);
        holder.name.setText(view_mem_helper.getMem_name());
        holder.phn.setText(view_mem_helper.getMem_phone());
        holder.email.setText(view_mem_helper.getMem_email());
    }

    @Override
    public int getItemCount() {
        return view_mem_data.size();
    }

    public static class view_memview_holder extends RecyclerView.ViewHolder{

        ImageView userdp;
        TextView name,phn,email;

        public view_memview_holder(@NonNull View itemView) {
            super(itemView);

            userdp = itemView.findViewById(R.id.user_dp);
            name = itemView.findViewById(R.id.stu_name);
            phn = itemView.findViewById(R.id.phone);
            email = itemView.findViewById(R.id.email);

        }
    }



}
