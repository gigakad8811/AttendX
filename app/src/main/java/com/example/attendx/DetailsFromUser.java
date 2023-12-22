package com.example.attendx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DetailsFromUser extends AppCompatActivity {

    EditText edit_name,edit_phone;
    Button submit_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_from_user);

        edit_name = findViewById(R.id.Name);
        edit_phone = findViewById(R.id.Phone);
        submit_btn = findViewById(R.id.lets_go);
        hide_act_bar();

        submit_btn.setOnClickListener(v -> {

            String name, phone;
            name = edit_name.getText().toString();
            phone = edit_phone.getText().toString();

            if (!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(phone)){
                if (phone.length()==10){


                    // we have to read the user-profile node and search for this uid if its persent then we should not update the value insteadforward him to dashboard
                    DatabaseReference root_ref,usr_profile_ref;
                    root_ref = FirebaseDatabase.getInstance().getReference();
                    usr_profile_ref = root_ref.child("userprofile");
                    String email = getIntent().getStringExtra("EMAIL");
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = user.getUid();
                    Query query = usr_profile_ref.child(uid);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Log.d("BUG_DETAILS", "query snapshot :"+snapshot.toString());
                            if (snapshot.exists() && snapshot.getChildrenCount() >= 3){

                                    Toast.makeText(getApplicationContext(), "Welcome Back " + email, Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(DetailsFromUser.this, dashboard.class));
                                    finish();

                            }else
                            {
                                //Update data to database
                                DatabaseReference mDatabase;
                                mDatabase = FirebaseDatabase.getInstance().getReference();
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                String uid = user.getUid();
                                String photoUrl = getIntent().getStringExtra("PHOTOURL");
                                userBasicData userBasicData = new userBasicData(name, email, uid, phone, photoUrl);
                                Log.d("UPDATING_PHOTOURL", photoUrl);
                                Log.d("UPDATING_NAME", name);
                                Log.d("UPDATING_EMAIL", email);
                                Log.d("UPDATING_PHONE", phone);

                                mDatabase.child("userprofile").child(uid).setValue(userBasicData);
                                Log.d("UPDATED_DETAILS", userBasicData.toString());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getApplicationContext(), "Database error : "+error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                {
                    Toast.makeText(DetailsFromUser.this,"Phone no. must be of ten digit", Toast.LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(DetailsFromUser.this,"Name or no. cannot be empty", Toast.LENGTH_LONG).show();
            }

            Intent i = new Intent(DetailsFromUser.this, dashboard.class);
            startActivity(i);
            finish();


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