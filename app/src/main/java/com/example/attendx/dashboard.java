package com.example.attendx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.fonts.Font;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;
import com.google.firebase.messaging.FirebaseMessaging;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfTrueTypeFont;
import com.itextpdf.kernel.pdf.PdfCatalog;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Tab;
import com.itextpdf.layout.element.TabStop;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TabAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.nex3z.notificationbadge.NotificationBadge;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;


public class dashboard extends AppCompatActivity {


    FirebaseAuth mAuth;
    ImageView imageView, createClassroomBtn, joinClassroomBtn;
    TextView textView, logout;

    RecyclerView featuredrecycler, categoriesrecycler;
    RecyclerView.Adapter adapter;
    RecyclerView.Adapter adapter0;

    ArrayList<FeaturedHelper> admin_cls = new ArrayList<>();
    ArrayList<CategoriesHelper> categories = new ArrayList<>();

    NotificationBadge notificationBadge;
    ImageView attendx_dashboard, notification;

    int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        mAuth = FirebaseAuth.getInstance();

        hide_act_bar();

        joinClassroomBtn = findViewById(R.id.joinClassroomBtn);
        createClassroomBtn = findViewById(R.id.createClassroomBtn);
        logout = findViewById(R.id.logoutbtn);

        imageView = findViewById(R.id.user_dp);
        textView = findViewById(R.id.user_view);

        //Hooks Recycler view
        featuredrecycler = findViewById(R.id.featured_recycler);
        categoriesrecycler = findViewById(R.id.categories_rv);
        categoriesrecyclerView();

        //notification badge
        notificationBadge = findViewById(R.id.badge);

        attendx_dashboard = findViewById(R.id.attendx_dashboard);
        notification = findViewById(R.id.notification);

        attendx_dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationBadge.setNumber(count++);

            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationBadge.setNumber(0);
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        Log.d("DASH_UID_F", uid);
        //Storing fcm token
        get_fcm_token();

        DatabaseReference Root_Ref, UserProfile_Ref;
        Root_Ref = FirebaseDatabase.getInstance().getReference();
        UserProfile_Ref = Root_Ref.child("userprofile").child(uid);

        DatabaseReference User_Sir_ji_Ref;
        User_Sir_ji_Ref = UserProfile_Ref.child("Sir_ji");

        //method created for recycler view
        featuredrecycler(User_Sir_ji_Ref, Root_Ref);


        UserProfile_Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("FUCKER_SNAP",snapshot.toString());
                if (snapshot.exists()) {
                    String name = "";
                    String url = "";
                    Log.d("DASBOARD_UID", uid);
                    name = snapshot.child("name").getValue().toString();
                    url = snapshot.child("photoUrl").getValue().toString();
                    textView.setText(name.toString());
                    Glide.with(getApplicationContext()).load(url.toString()).into(imageView);
                }
                else{
                    Toast.makeText(getApplicationContext(), "YOU ARE NOT LOGGED IN", Toast.LENGTH_SHORT).show();
                    
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth.signOut();
                GoogleSignIn.getClient(getApplicationContext(),new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()).signOut();
                Intent i = new Intent(dashboard.this, login.class);
                startActivity(i);
                finish();

            }
        });
        


        createClassroomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(dashboard.this, CreateClassroom.class);
                startActivity(i);
                finish();
            }
        });

        joinClassroomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(dashboard.this, JoinClassroom.class);
                startActivity(i);
                finish();
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

    private void featuredrecycler(DatabaseReference user_Sir_ji_Ref, DatabaseReference root_Ref) {

        featuredrecycler.setHasFixedSize(true);
        featuredrecycler.setLayoutManager(new LinearLayoutManager(dashboard.this, LinearLayoutManager.HORIZONTAL, false));


        //ArrayList<FeaturedHelper> admin_cls = new ArrayList<>();

        //admin_cls.add(new FeaturedHelper(R.drawable.icon, "test0", "test2"));


        user_Sir_ji_Ref = user_Sir_ji_Ref;
        user_Sir_ji_Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    DatabaseReference ClassHeader;
                    ClassHeader = root_Ref.child("classroom_header");
                    for(DataSnapshot snapshot1 : snapshot.getChildren()){

                        Log.d("SNAPSHOT", snapshot1.getValue().toString());


                        ClassHeader.addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot3) {
                                if (snapshot3.exists()) {
                                    Log.d("SNAPSHOT", snapshot3.toString());
                                    String Cls_name = "";
                                    String createdBy, sub_name, cls_code;

                                    String room_id = snapshot1.getValue().toString();
                                   // Log.d("SNAPSHOT_cls_name", snapshot3.child(room_id).child("ClassName").getValue().toString());
                                    Cls_name = snapshot3.child(room_id).child("ClassName").getValue().toString();
                                    createdBy = snapshot3.child(room_id).child("TeacherName").getValue().toString();
                                    sub_name = snapshot3.child(room_id).child("SubName").getValue().toString();
                                    cls_code = snapshot1.getValue().toString();


                                    admin_cls.add(new FeaturedHelper(R.drawable.icon, Cls_name, room_id, createdBy, sub_name, cls_code));
                                    adapter.notifyDataSetChanged(); //baba tumi main

                                    Log.d("SNAPSHOT", Cls_name + "\n" + createdBy);
                                }else{
                                    Toast.makeText(getApplicationContext(), "Header of "+snapshot1.getValue().toString()+" not Found", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                                Toast.makeText(getApplicationContext(), "Recycler View Error.", Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getApplicationContext(), "UNABLE_TO_READ_SIR_JI", Toast.LENGTH_SHORT).show();
            }
        });


        Log.d("SNAPSHOT", admin_cls.toString());
        adapter = new FeaturedAdapter(admin_cls);
        featuredrecycler.setAdapter(adapter);

    }

    public void categoriesrecyclerView(){


        categoriesrecycler.setHasFixedSize(true);
        categoriesrecycler.setLayoutManager(new LinearLayoutManager(dashboard.this, LinearLayoutManager.HORIZONTAL, false));

        // db stuff here

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        DatabaseReference root_ref,user_ref;
        root_ref = FirebaseDatabase.getInstance().getReference();
        user_ref = root_ref.child("userprofile").child(uid);

        user_ref.child("member_of").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    DatabaseReference cls_header;
                    cls_header = root_ref.child("classroom_header");
                    for (DataSnapshot snapshot1 : snapshot.getChildren())
                    {

                        Log.d("MEMBER_OF_VALUES", snapshot1.getValue().toString());
                        String temp_id = snapshot1.getValue().toString();
                        //get data from header using roomid present inside member_of
                        cls_header.child(snapshot1.getValue().toString()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    String cls_name = snapshot.child("ClassName").getValue().toString();
                                    String sub_name = snapshot.child("SubName").getValue().toString();
                                    String sub_code,ins_name = "empty";
                                    sub_code = snapshot.child("SubCode").getValue().toString();
                                    ins_name = snapshot.child("InstituteName").getValue().toString();
                                    String created_by = snapshot.child("TeacherName").getValue().toString();

                                    Log.d("MEMBER_OF_FINAL", "\n"+cls_name+"\n"+sub_name+"\n"+sub_code+"\n"+ins_name+"\n"+created_by+"\n is fetched to array");

                                    categories.add(new CategoriesHelper(getrandom_grediant(), getrandom_bg_for_category(),cls_name,sub_name,sub_code,ins_name,created_by,temp_id));
                                    adapter0.notifyDataSetChanged();
                                }else{
                                    Toast.makeText(getApplicationContext(), "Header of "+temp_id+" not found", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }

                    adapter0 = new CategoriesAdapter(categories);
                    categoriesrecycler.setAdapter(adapter0);
                }else{
                    Toast.makeText(getApplicationContext(), "unable to find member_of", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



//        categories.add(new CategoriesHelper(getrandom_grediant(), getrandom_bg_for_category(),"Classroom","physics","PH 201","Gurucharan College","Dr. Abhijit Saha","adsget"));
  //      categories.add(new CategoriesHelper(getrandom_grediant(), getrandom_bg_for_category(),"Classroom 2","chemistry","CHE 202","National Institute of Technology","DR Souvik Dey","gdyejd"));
    //    categories.add(new CategoriesHelper(getrandom_grediant(), getrandom_bg_for_category(),"Classroom 3","maths","MC 14","FATOK BAZAR","Sunny Di","bdhwos"));
      //  categories.add(new CategoriesHelper(getrandom_grediant(), getrandom_bg_for_category(),"Classroom 4","Computer Science","LOVE CS","IIT Kharagpur","DR X","huwehh"));


    }

    public int getrandom_bg_for_category(){
        int[] img_array = {R.drawable.recent1,R.drawable.recent2,R.drawable.recent3,R.drawable.recent4,R.drawable.recent5};

        Random rm = new Random();
        int x = rm.nextInt(4)+1;
        Log.d("RAND_BG", String.valueOf(x));
        return img_array[x];
    }

    public GradientDrawable getrandom_grediant(){
        GradientDrawable[] gradient_array = {new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xffd4cbe5, 0xff9A3F2C}),
                new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xff7adccf, 0xff0075FF}),
                new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xfff7c59f, 0xFFFF4D00}),
                new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xffb8d7f5, 0xff00FFA3}),
                new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xff55EA76, 0xFFE0D92C}),
                new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0x18ffffff, 0x00ffffff})};
        Random rm = new Random();
        int x = rm.nextInt(5)+1;
        Log.d("RAND_GRADENT", String.valueOf(x));
        return gradient_array[x];
    }

    public void get_fcm_token(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                String my_fcm_token = "1";
                if(task.isSuccessful()){
                    String token = task.getResult();
                    my_fcm_token = token;
                    Log.d("FCM", "Token Generated : "+my_fcm_token);
                    upload_fcm_token(my_fcm_token);
                }else{
                    Log.d("FCM", "Token generation Fail : "+my_fcm_token);
                    upload_fcm_token(my_fcm_token);
                }
            }
        });
    }

    public void upload_fcm_token(String token){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        DatabaseReference Root_Ref;
        Root_Ref = FirebaseDatabase.getInstance().getReference();
        Root_Ref.child("userprofile").child(uid).child("fcm_token").setValue(token).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("FCM", "Token Upload success");
                } else {
                    Log.d("FCM", "Token upload Fail");
                }
            }});

    }



}