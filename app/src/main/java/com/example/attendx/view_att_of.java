package com.example.attendx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import sun.bob.mcalendarview.MarkStyle;
import sun.bob.mcalendarview.vo.DateData;

public class view_att_of extends AppCompatActivity {


    TextView mem_name_tv,mem_phn_tv,class_code_tv,percent_tv;
    sun.bob.mcalendarview.MCalendarView calendarView;
    String mem_name_str,mem_phn_str,cls_code_str,usr_img_url = "";
    String uid;
    ImageView usr_dp_view;
    String room_id = "0";
    final String TAG = "VIEW_ATT_OF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_att_of);
        hide_act_bar();

        mem_name_str = getIntent().getStringExtra("USR_NAME");
        mem_phn_str = getIntent().getStringExtra("USR_PHN");
        cls_code_str = getIntent().getStringExtra("ROOM_ID");
        usr_img_url = getIntent().getStringExtra("USR_IMG");

        room_id = cls_code_str;


        uid = getIntent().getStringExtra("UID");

        mem_name_tv=findViewById(R.id.mem_name);
        mem_phn_tv=findViewById(R.id.mem_phone);
        class_code_tv=findViewById(R.id.mem_class);
        usr_dp_view = findViewById(R.id.mem_dp);
        calendarView = findViewById(R.id.calendar);
        percent_tv = findViewById(R.id.att_percent);

        mem_name_tv.setText(mem_name_str);
        mem_phn_tv.setText(mem_phn_str);
        class_code_tv.setText(cls_code_str);
        Glide.with(getApplicationContext()).load(usr_img_url).into(usr_dp_view);

        get_dates_from_server();

    }

    public void get_dates_from_server(){
        // here we will get presant and absant dates from server and plot it in calender.. so we need uid and room_id

        DatabaseReference root_ref,my_att_data_ref;
        root_ref = FirebaseDatabase.getInstance().getReference();
        my_att_data_ref = root_ref.child(room_id).child("attedance_data").child(uid);

        my_att_data_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int total_cls_counter = 0;
                    int total_present_counter = 0;

                    for(DataSnapshot snapshot1 : snapshot.getChildren()){
                        // getting 11-09-21 absent
                        if(snapshot1.getValue().toString().equals("ABSENT"))
                        {
                            //mark its key mane date in red else mark the date in green
                            String date_data = snapshot1.getKey();
                            String[] date_arr = date_data.split("-",3);
                            calendarView.markDate(new DateData(
                                    Integer.valueOf(date_arr[2]),
                                    Integer.valueOf(date_arr[1]),
                                    Integer.parseInt(date_arr[0])).setMarkStyle(MarkStyle.BACKGROUND, Color.parseColor("#B50309")));
                        }
                        else {
                            String date_data = snapshot1.getKey();
                            Log.d(TAG, "value are present :"+date_data+snapshot1.getValue().toString());
                            String[] date_arr = date_data.split("-",3);
                            calendarView.markDate(new DateData(
                                    Integer.valueOf(date_arr[2]),
                                    Integer.valueOf(date_arr[1]),
                                    Integer.parseInt(date_arr[0])).setMarkStyle(MarkStyle.BACKGROUND, Color.parseColor("#5FBF80")));
                            total_present_counter = total_present_counter + 1;
                        }
                        total_cls_counter = total_cls_counter + 1;
                    }
                    float temp = (float)total_present_counter/(float) total_cls_counter;
                    float percentage = temp *100;
                    Log.d(TAG, "total values = "+total_present_counter+total_cls_counter);
                    percent_tv.setText(String.valueOf(percentage));

                }
                else {
                    Log.d(TAG, "empty snap of att data : "+snapshot.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "unable to read att data :: "+error.toString());
                Toast.makeText(getApplicationContext(), "Unable to get attadence data.", Toast.LENGTH_SHORT).show();
            }
        });

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
}