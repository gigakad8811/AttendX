package com.example.attendx;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gauravk.bubblenavigation.BubbleNavigationLinearView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class act_take_atten extends AppCompatActivity {

    public static final String TAG = "TAKE_ATT";
    TextView todays_date_val,total_stu_val,present_val,absent_val,last_cls_taken_val;

    Button submit_btn;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;

    ImageView back_btn,home_btn;

    ArrayList<take_att_helper> take_att_arr = new ArrayList<>();

    ArrayList<String> present_uid_arr = new ArrayList<>();
    ArrayList<String> absent_uid_arr = new ArrayList<>();

    String room_id = "0";


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_take_atten);

        hide_act_bar();

        //hooks

        recyclerView = findViewById(R.id.take_att_RV);
        submit_btn = findViewById(R.id.submit_att_btn);
        back_btn = findViewById(R.id.back_btn);
        home_btn = findViewById(R.id.home_btn);
        todays_date_val = findViewById(R.id.date_value);
        total_stu_val = findViewById(R.id.total_stu_value);
        present_val = findViewById(R.id.total_present_value);
        absent_val = findViewById(R.id.total_absent_value);
        last_cls_taken_val = findViewById(R.id.last_cls_date_value);
        //hooks

        //init value of Test views starts
        todays_date_val.setText(get_today_date());
        present_val.setText(String.valueOf(present_uid_arr.size()));
        absent_val.setText(String.valueOf(absent_uid_arr.size()));
        //init value ends

        room_id = getIntent().getStringExtra("ROOM_ID");

        back_btn.setOnClickListener(v->{send_to("cls_dash");});
        home_btn.setOnClickListener(v->send_to("usr_dash"));

        DatabaseReference root_ref,this_cls_header_ref;
        root_ref = FirebaseDatabase.getInstance().getReference();
        this_cls_header_ref = root_ref.child("classroom_header").child(room_id);

        this_cls_header_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChild("last_class_taken_on")){
                    last_cls_taken_val.setText(snapshot.child("last_class_taken_on").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        take_att_RV_handler();

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

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            take_att_helper buffer;
            Log.d("TAKE_SWIPE", "adapter pos :: " + position + " and size : " + take_att_arr.size());


            switch (direction) {
                case ItemTouchHelper.RIGHT:

                    if (take_att_arr.size() == 1) {
                        // i m swiping the last card
                        Log.d(TAG, "swiping the last card ");
                        present_uid_arr.add(take_att_arr.get(position).getUid());
                        present_val.setText(String.valueOf(present_uid_arr.size()));

                        buffer = take_att_arr.get(position);

                        take_att_arr.remove(position);
                        adapter.notifyItemRemoved(position);


                        Snackbar.make(recyclerView, buffer.getName() + " is marked present", BaseTransientBottomBar.LENGTH_LONG)
                                .setAction("undo present", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        recyclerView.setVisibility(View.VISIBLE);
                                        submit_btn.setVisibility(View.GONE);

                                        present_uid_arr.remove(buffer.getUid());
                                        present_val.setText(String.valueOf(present_uid_arr.size()));

                                        take_att_arr.add(position, buffer);

                                        adapter.notifyItemInserted(position);

                                    }
                                }).show();
                        //check if test_buffer is empty or not..if its empty then it means that undo btn is not clicked and if its not empty then we have deleted the last card too so we will show submit btn

                        if (take_att_arr.size() == 0) {
                            recyclerView.setVisibility(View.GONE);
                            att_submit_handler();
                        }
                    } else {
                        Log.d(TAG, "swipping the " + take_att_arr.size());
                        // i m not swping the last card
                        present_uid_arr.add(take_att_arr.get(position).getUid());
                        present_val.setText(String.valueOf(present_uid_arr.size()));

                        buffer = take_att_arr.get(position);

                        take_att_arr.remove(position);
                        adapter.notifyItemRemoved(position);


                        Snackbar.make(recyclerView, buffer.getName() + " is marked present", BaseTransientBottomBar.LENGTH_LONG)
                                .setAction("undo present", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        present_uid_arr.remove(buffer.getUid());
                                        present_val.setText(String.valueOf(present_uid_arr.size()));

                                        take_att_arr.add(position, buffer);

                                        adapter.notifyItemInserted(position);
                                    }
                                }).show();
                    }

                    break;

                case ItemTouchHelper.LEFT:

                    if (take_att_arr.size() == 1) {

                        absent_uid_arr.add(take_att_arr.get(position).getUid());
                        absent_val.setText(String.valueOf(absent_uid_arr.size()));

                        buffer = take_att_arr.get(position);

                        take_att_arr.remove(position);
                        adapter.notifyItemRemoved(position);

                        Snackbar.make(recyclerView, buffer.getName() + " is marked absent", BaseTransientBottomBar.LENGTH_LONG)
                                .setAction("undo absent", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        recyclerView.setVisibility(View.VISIBLE);
                                        submit_btn.setVisibility(View.GONE);

                                        absent_uid_arr.remove(buffer.getUid());
                                        absent_val.setText(String.valueOf(absent_uid_arr.size()));
                                        take_att_arr.remove(buffer.getUid());
                                        take_att_arr.add(position, buffer);
                                        adapter.notifyItemInserted(position);
                                    }
                                }).show();

                        if (take_att_arr.size() == 0) {
                            recyclerView.setVisibility(View.GONE);
                            att_submit_handler();
                        }

                    } else {
                        absent_uid_arr.add(take_att_arr.get(position).getUid());
                        absent_val.setText(String.valueOf(absent_uid_arr.size()));

                        buffer = take_att_arr.get(position);

                        take_att_arr.remove(position);
                        adapter.notifyItemRemoved(position);

                        Snackbar.make(recyclerView, buffer.getName() + " is marked absent", BaseTransientBottomBar.LENGTH_LONG)
                                .setAction("undo absent", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        absent_uid_arr.remove(buffer.getUid());
                                        absent_val.setText(String.valueOf(absent_uid_arr.size()));
                                        take_att_arr.remove(buffer.getUid());
                                        take_att_arr.add(position, buffer);
                                        adapter.notifyItemInserted(position);
                                    }
                                }).show();

                    }
                    break;
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String get_today_date(){
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        String date = day + "-" + (month + 1) + "-" + year;
        return date;
    }

    public void att_submit_handler() {
        Log.d(TAG, "BUTTON HANDLER IS CALLED");
        submit_btn.setVisibility(View.VISIBLE);

        //on clicking we will upload it to server probably with a progress bar
        //in nxt version on click we will create a workmanager and do uploading there

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (submit_btn.isEnabled()) {
                    submit_btn.setEnabled(false);
                    Intent intent = new Intent(getApplicationContext(), upload_att.class);
                    intent.putExtra("PRESENT_ARRAY", present_uid_arr);
                    intent.putExtra("ABSENT_ARRAY", absent_uid_arr);
                    intent.putExtra("ROOM_ID", room_id);
                    startActivity(intent);
                    finish();
                }
            }
        });


    }

    public void take_att_RV_handler() {
        Log.d(TAG, "take_att_RV_handler called");


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(act_take_atten.this, LinearLayoutManager.VERTICAL, false));


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
                        Log.d(TAG, "cls_snap : " + snapshot.toString());
                        total_stu_val.setText(String.valueOf(snapshot.getChildrenCount()));
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            String uid = snapshot1.getKey();
                            Log.d(TAG, "got " + uid);
                            assert uid != null;
                            profile_ref.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        Log.d(TAG, "pro_snap of " + uid + " = " + snapshot.toString());
                                        String name, phn, email, img;
                                        name = (String) snapshot.child("name").getValue();
                                        phn = (String) snapshot.child("phone").getValue();
                                        email = (String) snapshot.child("email").getValue();
                                        img = (String) snapshot.child("photoUrl").getValue();

                                        take_att_arr.add(new take_att_helper(uid, name, phn, email, img));
                                        adapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.d(TAG, "Unable to fetch profile of " + uid);
                                    Toast.makeText(getApplicationContext(), "unable to fetch profile", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    } else {
                        Log.d(TAG, "This cls " + room_id + " doesnot have any member yet");
                        Toast.makeText(getApplicationContext(), "This class doesnot have any members yet " + snapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Log.d(TAG, "unable to find cls" + room_id);
                    Toast.makeText(getApplicationContext(), "unable to find class snap " + room_id, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "unable to fetch member of " + room_id);
                Toast.makeText(getApplicationContext(), "unable to fetch members of class : " + room_id, Toast.LENGTH_SHORT).show();
            }
        });


        adapter = new take_att_adapter(take_att_arr, getApplicationContext());
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(act_take_atten.this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);


        ItemTouchHelper itemswipe = new ItemTouchHelper(simpleCallback);
        itemswipe.attachToRecyclerView(recyclerView);

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