package com.example.attendx;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class JoinClassroom extends AppCompatActivity {
    // Completedy done NOthing to add more

    EditText editText;
    Button joinButton;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),dashboard.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_classroom);

        editText = findViewById(R.id.classId);
        joinButton = findViewById(R.id.joinBtn);
        hide_act_bar();


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String uid = user.getUid();

        DatabaseReference Root_Ref, UserProfile_Ref, classheader_Ref;

        Root_Ref = FirebaseDatabase.getInstance().getReference();
        UserProfile_Ref = Root_Ref.child("userprofile").child(uid);
        classheader_Ref = Root_Ref.child("classroom_header");


        joinButton.setOnClickListener(v -> {
            if (joinButton.isEnabled()) {

                joinButton.setEnabled(false);

                String given_room_id = editText.getText().toString();
                if (!TextUtils.isEmpty(given_room_id)) {
                    if (given_room_id.length() == 6) {

                        //Reading room header
                        classheader_Ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (snapshot.exists()) {

                                    Log.d("MEMBER_OF", given_room_id);

                                    Log.d("MEMBER__HEADER_SNAP", snapshot.toString());

                                    if (snapshot.hasChild(given_room_id)) {
                                        //Room Found
                                        // good now push it under member_of uid
                                        // have to check twice
                                        DatabaseReference root_ref, my_ref, member_of_ref;
                                        root_ref = FirebaseDatabase.getInstance().getReference();
                                        my_ref = root_ref.child("userprofile").child(uid);
                                        member_of_ref = my_ref.child("member_of");

                                        Query query1 = member_of_ref.orderByValue().equalTo(given_room_id);

                                        Query query2 = my_ref.child("Sir_ji").orderByValue().equalTo(given_room_id);

                                        query2.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    Log.d("CLS_EXISTS", "You are the creator of " + given_room_id);
                                                    Toast.makeText(getApplicationContext(), "You can not join the class that you have created. ", Toast.LENGTH_LONG).show();
                                                    Intent i = new Intent(getApplicationContext(), dashboard.class);
                                                    startActivity(i);
                                                    finish();
                                                } else {
                                                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (snapshot.exists()) {
                                                                Log.d("CLS_EXISTS", given_room_id + " Already exists");
                                                                Toast.makeText(getApplicationContext(), "You already sent join request to " + given_room_id + " earlier", Toast.LENGTH_LONG).show();
                                                                Intent i = new Intent(getApplicationContext(), dashboard.class);
                                                                startActivity(i);
                                                                finish();
                                                            } else {
                                                                Log.d("CLS_EXISTS", given_room_id + " is unique");
                                                                UserProfile_Ref.child("member_of").push().setValue(given_room_id).addOnSuccessListener(unused -> {
                                                                    send_join_request(given_room_id);
                                                                    Log.d("CLS_EXISTS", "request sen successfully");
                                                                    Toast.makeText(getApplicationContext(), "Request sent successfully.", Toast.LENGTH_LONG).show();
                                                                    Intent i = new Intent(getApplicationContext(), dashboard.class);
                                                                    startActivity(i);
                                                                    finish();
                                                                });
                                                            }

                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            Toast.makeText(JoinClassroom.this, "unable to run QUERY1 " + error.toString(), Toast.LENGTH_SHORT).show();

                                                        }
                                                    });

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(getApplicationContext(), "unable to run QUERY 2 " + error.toString(), Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    } else {
                                        Toast.makeText(JoinClassroom.this, "Incorrect room id.", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    Toast.makeText(JoinClassroom.this, "Empty header.", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                                Toast.makeText(JoinClassroom.this, "Unable to read header." + error, Toast.LENGTH_SHORT).show();

                            }
                        });

                    } else {
                        Toast.makeText(JoinClassroom.this, "Room id should be of 6 character!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("CLS_EXISTS", "field empty");
                    Toast.makeText(JoinClassroom.this, "Room id cannot be empty!", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), dashboard.class);
                    startActivity(i);
                    finish();
                }
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


    private void send_join_request(String given_room_id) {

        //THIS WILL ADD JOIN REQUEST BY CREATING NEW CLASS NODE IN ROOT NODE AND PUT UID UNDER JOIN REQUEST

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String uid = user.getUid();

        DatabaseReference Root_Ref;

        Root_Ref = FirebaseDatabase.getInstance().getReference();

        Root_Ref.child(given_room_id).child("join_request").push().setValue(uid);

        Toast.makeText(JoinClassroom.this, "JOIN REQUEST SENT SUCCESSFULLY.", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(getApplicationContext(), dashboard.class);
        startActivity(i);
        finish();

    }


}