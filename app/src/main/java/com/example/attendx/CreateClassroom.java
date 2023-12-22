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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class CreateClassroom extends AppCompatActivity {
    // Completely done nothing to add more

    EditText classname_edit, subjectname_edit, subjectcode_edit, institutename_edit;
    ImageView submitbtn;
    String username;
    String SubCode = "null";
    String InsName = "null";
    String TotalClassTkn = "0";
    String ClassName, SubName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_classroom);
        classname_edit = findViewById(R.id.classroomName);
        subjectname_edit = findViewById(R.id.subjectName);
        subjectcode_edit = findViewById(R.id.subjectCode);
        institutename_edit = findViewById(R.id.instituteName);
        submitbtn = findViewById(R.id.crtclsbtn);
        hide_act_bar();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String uid = user.getUid();

        DatabaseReference Root_Ref, UserProfile_Ref, classheader_Ref, Sir_ji_Ref;

        Root_Ref = FirebaseDatabase.getInstance().getReference();
        UserProfile_Ref = Root_Ref.child("userprofile").child(uid);
        classheader_Ref = Root_Ref.child("classroom_header");
        Sir_ji_Ref = UserProfile_Ref.child("Sir_ji");
        //UserProfile_Ref.child("XtvVD4QztYOnNBIOUDyB8YNvC2J2").child("Sir_ji").removeValue();


        UserProfile_Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                username = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                Log.d("USERNAME", username);
                Log.d("USERID", uid);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.d("DATABASE_ERROR", error.toString());

            }
        });


        submitbtn.setOnClickListener(v -> {


            ClassName = classname_edit.getText().toString();
            SubName = subjectname_edit.getText().toString();

            if(!TextUtils.isEmpty(ClassName)&&!TextUtils.isEmpty(SubName))
            {
                String temp_subcode, temp_insname;
                temp_subcode = subjectcode_edit.getText().toString();
                temp_insname = institutename_edit.getText().toString();
                if (!TextUtils.isEmpty(temp_subcode))
                {
                    SubCode = temp_subcode;
                }

                if (!TextUtils.isEmpty(temp_insname))
                {
                    InsName = temp_insname;
                }

                String room_id = generateClassroomId();
                Classroom_headerHelper Classroom_headerHelper = new Classroom_headerHelper(username, ClassName, SubName, SubCode, InsName, TotalClassTkn);
                classheader_Ref.child(room_id).setValue(Classroom_headerHelper);


                List<String> Sir_ji_array = new ArrayList<>();
                UserProfile_Ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Log.d("Sir_ji_snapshot", snapshot.toString());

                        if(snapshot.hasChild("Sir_ji")){
                            Log.d("SIR_JI_CHILD", "EXIST");
                            Sir_ji_Ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    for(DataSnapshot snapshot1 : snapshot.getChildren()){

                                        Log.d("SIR_JI_SNAP", Objects.requireNonNull(snapshot1.getValue()).toString());
                                        Sir_ji_array.add(snapshot1.getValue().toString());
                                    }
                                    Sir_ji_array.add(room_id);
                                    Log.d("SIR_JI_ARRAY", Sir_ji_array.toString());
                                    UserProfile_Ref.child("Sir_ji").removeValue();
                                    UserProfile_Ref.child("Sir_ji").setValue(Sir_ji_array);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                    Log.d("SIR_JI", "UNABLE TO READ SIR JI"+error);
                                }
                            });
                        }
                        else{
                            Log.d("SIR_JI_CHILD", "NOT EXIST");

                            Sir_ji_array.add(room_id);

                            UserProfile_Ref.child("Sir_ji").child("0").setValue(room_id);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Log.d("SIR_JI", "UNABLE TO GET PROFILE");

                    }
                });

                Intent i = new Intent(CreateClassroom.this, CreationSucess.class);
                i.putExtra("CLASS_ID", room_id);
                i.putExtra("CLASS_NAME", ClassName);
                i.putExtra("CREATED_BY", username);


                startActivity(i);
                finish();


            }
            else
            {
                Toast.makeText(CreateClassroom.this, "Classroom and Subject names are mandatory", Toast.LENGTH_SHORT).show();
            }

        });



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),dashboard.class));
        finish();
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

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public String generateClassroomId()
    {
        int length = 6;
        Random random = new Random();
        StringBuilder builder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            builder.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }

        return builder.toString();
    }

}