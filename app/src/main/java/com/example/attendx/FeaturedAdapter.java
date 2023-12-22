package com.example.attendx;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FeaturedAdapter extends RecyclerView.Adapter<FeaturedAdapter.FeaturedViewHolder> {

    ArrayList<FeaturedHelper> FeaturedLocation ;

    public FeaturedAdapter(ArrayList<FeaturedHelper> featuredLocation) {
        FeaturedLocation = featuredLocation;
    }

    @NonNull
    @Override
    public FeaturedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.featured_card_design, parent, false);
        FeaturedViewHolder featuredViewHolder = new FeaturedViewHolder(view);
        return featuredViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedViewHolder holder, int position) {

        FeaturedHelper featuredHelper = FeaturedLocation.get(position);
        holder.image.setImageResource(featuredHelper.getImage());
        holder.title.setText(featuredHelper.getTitle());
        holder.sub_name.setText(featuredHelper.getSub_name());
        holder.cls_code.setText(featuredHelper.getCls_code());
        holder.admin.setText(featuredHelper.getAdmin());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(),ClassroomDashboard.class);
                i.putExtra("ROOM_ID",featuredHelper.getRoom_id());
                Log.d("FEATURED", "I M PASSING ROOM ID = "+featuredHelper.getRoom_id());
                v.getContext().startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return FeaturedLocation.size();
    }

    public static class FeaturedViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView title,admin,sub_name,cls_code;


        public FeaturedViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.featured_image);
            title = itemView.findViewById(R.id.featured_title);
            sub_name = itemView.findViewById(R.id.sub_name);
            cls_code = itemView.findViewById(R.id.cls_code);
            admin = itemView.findViewById(R.id.featured_admin);
        }
    }
}
