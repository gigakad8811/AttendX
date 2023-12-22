package com.example.attendx;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder>{

    ArrayList<CategoriesHelper> categories;

    public CategoriesAdapter(ArrayList<CategoriesHelper> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_card_design,parent,false);
        CategoriesViewHolder categoriesViewHolder = new CategoriesViewHolder(view);
        return categoriesViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesViewHolder holder, int position) {
        CategoriesHelper categoriesHelper = categories.get(position);
        holder.image.setImageResource(categoriesHelper.getImage()); //NEW VIEW HOOK WILL BE ADDED HERE
        holder.title.setText(categoriesHelper.getTitle().toString());

        holder.sub_name.setText(categoriesHelper.getSub_name());
        holder.sub_code.setText(categoriesHelper.getSub_code());
        holder.ins_name.setText(categoriesHelper.getIns_name());
        holder.admin.setText(categoriesHelper.getAdmin());

        holder.relativeLayout.setBackground(categoriesHelper.getGradient());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(),View_my_Attadence.class);
                i.putExtra("ROOM_ID",categoriesHelper.getRoom_id());
                v.getContext().startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CategoriesViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView title; //ADD MORE VIEWS HERE
        TextView sub_name,sub_code,ins_name,admin;
        RelativeLayout relativeLayout;


        public CategoriesViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.card_bg);
            title = itemView.findViewById(R.id.class_name);
            relativeLayout = itemView.findViewById(R.id.relative_layout);

            sub_name = itemView.findViewById(R.id.sub_name);
            sub_code = itemView.findViewById(R.id.sub_code);
            ins_name = itemView.findViewById(R.id.ins_name);
            admin = itemView.findViewById(R.id.created_by);

        }
    }

}
