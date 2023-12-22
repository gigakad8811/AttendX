package com.example.attendx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.gauravk.bubblenavigation.BubbleNavigationLinearView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class act_view_atten extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;

    ImageView back_btn, home_btn;
    ArrayList<view_att_helper> view_att_data_arr = new ArrayList<>();

    String room_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_view_atten);

        room_id = getIntent().getStringExtra("ROOM_ID");
        hide_act_bar();

        back_btn = findViewById(R.id.back_btn);
        recyclerView = findViewById(R.id.join_req_rv);

        home_btn = findViewById(R.id.home_btn);
        home_btn.setOnClickListener(v->send_to("usr_dash"));

        back_btn.setOnClickListener(v->{send_to("cls_dash");});

        recyclerView_handler();

    }

    public void hide_act_bar(){
        //code
        getSupportActionBar().hide(); // its hide actionbar

        if(Build.VERSION.SDK_INT>=19 && Build.VERSION.SDK_INT <21)
        {
            SetWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >=19)
        {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //makes fully transparent status bar
        if (Build.VERSION.SDK_INT >= 21)
        {
            SetWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private static void SetWindowFlag(Activity activity, final int Bits, Boolean on)
    {
        Window win = activity.getWindow();
        WindowManager.LayoutParams Winparams = win.getAttributes();
        if(on){
            Winparams.flags |=Bits;
        }
        else{
            Winparams.flags &= ~Bits;
        }
        win.setAttributes(Winparams);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        send_to("cls_dash");
    }

    public  void recyclerView_handler(){
        Log.d("RV_CALLED", "rv_called");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        // get full member list from DB

        DatabaseReference root_ref, this_cls_ref, profile_ref;

        root_ref = FirebaseDatabase.getInstance().getReference();
        this_cls_ref = root_ref.child(room_id).child("members");
        profile_ref = root_ref.child("userprofile");


        // now we have to get all children key to get the member uid

        this_cls_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.getChildrenCount() > 0) {
                        Log.d("VIEW_ALL_MEM", "cls_snap : " + snapshot.toString());
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            String uid = snapshot1.getKey();
                            Log.d("VIEW_ALL_MEM", "got " + uid);
                            assert uid != null;
                            profile_ref.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        Log.d("VIEW_ALL_MEM", "pro_snap of " + uid + " = " + snapshot.toString());
                                        String name, phn, email, img;
                                        name = (String) snapshot.child("name").getValue();
                                        phn = (String) snapshot.child("phone").getValue();
                                        email = (String) snapshot.child("email").getValue();
                                        img = (String) snapshot.child("photoUrl").getValue();

                                        view_att_data_arr.add(new view_att_helper(name, phn, email, img, room_id,uid));
                                        adapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.d("VIEW_ALL_MEM", "Unable to fetch profile of " + uid);
                                    Toast.makeText(getApplicationContext(), "unable to fetch profile", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    } else {
                        Log.d("VIEW_ALL_MEM", "This cls " + room_id + " doesnot have any member yet");
                        Toast.makeText(getApplicationContext(), "This class doesnot have any members yet " + snapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Log.d("VIEW_ALL_MEM", "unable to find cls" + room_id);
                    Toast.makeText(getApplicationContext(), "unable to find class snap " + room_id, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("VIEW_ALL_MEM", "unable to fetch member of " + room_id);
                Toast.makeText(getApplicationContext(), "unable to fetch members of class : " + room_id, Toast.LENGTH_SHORT).show();
            }
        });

        adapter = new view_att_adapter(view_att_data_arr, getApplicationContext());
        recyclerView.setAdapter(adapter);

    }

    public void send_to(@NonNull String location) {
        switch (location) {
            case "join_req":
                Intent j1 = new Intent(getApplicationContext(), Join_request.class);
                j1.putExtra("ROOM_ID", room_id);
                startActivity(j1);
                break;
            case "usr_dash":
                Intent i = new Intent(getApplicationContext(), dashboard.class);
                i.putExtra("ROOM_ID", room_id);
                startActivity(i);
                finish();
                break;
            case "cls_dash":
                Intent j5 = new Intent(getApplicationContext(), ClassroomDashboard.class);
                j5.putExtra("ROOM_ID", room_id);
                startActivity(j5);
                finish();
                break;
            case "view_mem":
                Intent j = new Intent(getApplicationContext(), act_view_member.class);
                j.putExtra("ROOM_ID", room_id);
                startActivity(j);
                break;
            case "take_att":
                Intent k = new Intent(getApplicationContext(), act_take_atten.class);
                k.putExtra("ROOM_ID", room_id);
                startActivity(k);
                break;
            case "view_att":
                Intent L = new Intent(getApplicationContext(), act_view_atten.class);
                L.putExtra("ROOM_ID", room_id);
                startActivity(L);
                break;
            default:
                Toast.makeText(getApplicationContext(), "check loc in code", Toast.LENGTH_SHORT).show();
        }
    }
}