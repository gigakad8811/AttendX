package com.example.attendx;


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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gauravk.bubblenavigation.BubbleNavigationLinearView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;


public class Join_request extends AppCompatActivity {

    RecyclerView join_req_rv;
    RecyclerView.Adapter adapter;

    ImageView back_btn, home_btn;

    String room_id = " ";

    BubbleNavigationLinearView bubbleNavigationLinearView;

    ArrayList<JoinReqHelper> join_req_arr = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_request);

        back_btn = findViewById(R.id.back_btn);
        home_btn = findViewById(R.id.home_btn);

        hide_act_bar();

        join_req_rv = findViewById(R.id.join_req_rv);

        room_id = getIntent().getStringExtra("ROOM_ID");
        Log.d("FEATURED", "i have received room id = " + room_id);

        join_req_recycer_view();

        back_btn.setOnClickListener(v -> send_to("cls_dash"));
        home_btn.setOnClickListener(v->send_to("usr_dash"));

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

    public void join_req_recycer_view() {
        join_req_rv.setHasFixedSize(true);
        join_req_rv.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        //Do db Staff here

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        user.getUid();

        DatabaseReference root_ref, room_ref, join_req_ref, profile_ref;
        root_ref = FirebaseDatabase.getInstance().getReference();
        room_ref = root_ref.child(room_id);
        join_req_ref = room_ref.child("join_request");
        profile_ref = root_ref.child("userprofile");


        join_req_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d("JOIN_REQ_SNAP", snapshot.toString());
                    Log.d("JOIN_REQ_SNAP_VAL", Objects.requireNonNull(snapshot.getValue()).toString());

                    String key;
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        // have to query user_profile with uid present
                        Log.d("JOIN_REQ_UID", Objects.requireNonNull(snapshot1.getValue()).toString());
                        key = snapshot1.getKey();
                        Query query1 = profile_ref.child(snapshot1.getValue().toString());

                        String finalKey = key;
                        query1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                String img_url, name, phn_no, email, user_id;

                                img_url = Objects.requireNonNull(snapshot.child("photoUrl").getValue()).toString();
                                name = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                                phn_no = Objects.requireNonNull(snapshot.child("phone").getValue()).toString();
                                email = Objects.requireNonNull(snapshot.child("email").getValue()).toString();
                                user_id = Objects.requireNonNull(snapshot.child("user_id").getValue()).toString();


                                join_req_arr.add(new JoinReqHelper(img_url, name, phn_no, email, user_id, room_id, finalKey));
                                adapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.d("JOIN_REQ", "unable to read profile of " + snapshot1.getValue().toString() + " error = " + error.toString());
                                Toast.makeText(getApplicationContext(), "unable to read profile of " + snapshot1.getValue().toString(), Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                } else {
                    Toast.makeText(getApplicationContext(), "This class doesnot have any join request yet.", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getApplicationContext(), "unable to read database " + error.toString(), Toast.LENGTH_SHORT).show();

            }
        });
        adapter = new JoinReqAdapter(join_req_arr, getApplicationContext());
        join_req_rv.setAdapter(adapter);
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