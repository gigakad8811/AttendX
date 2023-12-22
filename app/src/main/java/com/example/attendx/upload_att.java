package com.example.attendx;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.N)
public class upload_att extends AppCompatActivity {

    final static String TAG = "ATT_UPLOAD";
    static Calendar c = Calendar.getInstance();
    static int day = c.get(Calendar.DAY_OF_MONTH);
    static int month = c.get(Calendar.MONTH);
    static int year = c.get(Calendar.YEAR);
    static String date = day + "-" + (month + 1) + "-" + year;
    ArrayList<String> present_uid_arr = new ArrayList<>();
    ArrayList<String> absent_uid_arr = new ArrayList<>();
    String room_id = "";
    TextView present_val, absent_val, init_val, final_val;
    Handler main_ui_handler = new Handler();
    int initial_value = 0;
    int final_value = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_att);

        present_uid_arr = getIntent().getStringArrayListExtra("PRESENT_ARRAY");
        absent_uid_arr = getIntent().getStringArrayListExtra("ABSENT_ARRAY");
        room_id = getIntent().getStringExtra("ROOM_ID");

        Log.d(TAG, "date :: " + date);
        Log.d(TAG, "Present :: " + present_uid_arr);
        Log.d(TAG, "absent :: " + absent_uid_arr);
        Log.d(TAG, "ROOM_ID : " + room_id);

        present_val = findViewById(R.id.presant_val);
        absent_val = findViewById(R.id.absent_val);

        init_val = findViewById(R.id.init_val);
        final_val = findViewById(R.id.final_val);

        final_value = (int) (present_uid_arr.size() + absent_uid_arr.size());

        present_val.setText(String.valueOf(present_uid_arr.size()));
        absent_val.setText(String.valueOf(absent_uid_arr.size()));
        final_val.setText(String.valueOf(final_value));

        presant_runnable presant_runnable = new presant_runnable();
        presant_runnable.run();

        absent_runnable absent_runnable = new absent_runnable();
        absent_runnable.run();
    }

    class absent_runnable implements Runnable {
        @Override
        public void run() {
            // we will establish db connection here
            if (initial_value == final_value) {
                Toast.makeText(getApplicationContext(), "All record uploaded", Toast.LENGTH_LONG).show();
                Log.d(TAG, "ALL UPLOAD SUCCESS");
                Intent i = new Intent(getApplicationContext(), dashboard.class);
                startActivity(i);
                finish();
            }
            DatabaseReference root_ref, this_cls_ref;
            root_ref = FirebaseDatabase.getInstance().getReference();
            this_cls_ref = root_ref.child(room_id).child("attedance_data");
            String uid;
            for (int i = 0; i < absent_uid_arr.size(); i++) {
                uid = absent_uid_arr.get(i);
                main_ui_handler.post(() -> {
                    // update initial value
                    initial_value = initial_value + 1;
                    init_val.setText(String.valueOf(initial_value));
                    Log.d(TAG, "init val from pre run : " + initial_value);
                });
                this_cls_ref.child(uid).child(date).setValue("ABSENT").addOnSuccessListener(unused -> {
                    if (initial_value == final_value) {
                        Toast.makeText(getApplicationContext(), "All record uploaded", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "ALL UPLOAD SUCCESS");
                        //TODO  upload total cls taken and last cls taken on here
                        DatabaseReference this_cls_header_ref;

                        this_cls_header_ref = root_ref.child("classroom_header").child(room_id);

                        this_cls_header_ref.child("last_class_taken_on").setValue(date);

                        this_cls_header_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChild("TotalClassTaken")){
                                    int total_cls_taken_init_val;
                                    total_cls_taken_init_val = Integer.valueOf( snapshot.child("TotalClassTaken").getValue().toString());
                                    Log.d(TAG, "TOTAL CLS TAKEN VAL ON DB "+total_cls_taken_init_val);
                                    int finalval =total_cls_taken_init_val+1;
                                    Log.d(TAG, "TOTAL CLS TAKEN FINAL VAL"+finalval);
                                    this_cls_header_ref.child("TotalClassTaken").setValue(finalval);

                                    Intent j = new Intent(getApplicationContext(), dashboard.class);
                                    startActivity(j);
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
            }
        }
    }

    class presant_runnable implements Runnable {
        @Override
        public void run() {
            // we will establish db connection here
            DatabaseReference root_ref, this_cls_ref;
            root_ref = FirebaseDatabase.getInstance().getReference();
            this_cls_ref = root_ref.child(room_id).child("attedance_data");
            String uid;
            for (int i = 0; i < present_uid_arr.size(); i++) {
                uid = present_uid_arr.get(i);
                main_ui_handler.post(() -> {
                    // update initial value
                    initial_value = initial_value + 1;
                    init_val.setText(String.valueOf(initial_value));
                    Log.d(TAG, "init val from pre run : " + initial_value);
                });
                this_cls_ref.child(uid).child(date).setValue("PRESENT").addOnSuccessListener(unused -> {
                    if (initial_value == final_value) {
                        Toast.makeText(getApplicationContext(), "All record uploaded", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "ALL UPLOAD SUCCESS");
                        DatabaseReference this_cls_header_ref;

                        this_cls_header_ref = root_ref.child("classroom_header").child(room_id);

                        this_cls_header_ref.child("last_class_taken_on").setValue(date);

                        this_cls_header_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChild("TotalClassTaken")){
                                    int total_cls_taken_init_val;
                                    total_cls_taken_init_val = Integer.valueOf(snapshot.child("TotalClassTaken").getValue().toString());
                                    Log.d(TAG, "TOTAL CLS TAKEN VAL ON DB "+total_cls_taken_init_val);
                                    int finalval =total_cls_taken_init_val+1;
                                    Log.d(TAG, "TOTAL CLS TAKEN FINAL VAL"+finalval);
                                    this_cls_header_ref.child("TotalClassTaken").setValue(finalval);

                                    Intent j = new Intent(getApplicationContext(), dashboard.class);
                                    startActivity(j);
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
            }
        }
    }
}