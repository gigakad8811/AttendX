package com.example.attendx;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CreationSucess extends AppCompatActivity {

    public String line1 = "This is your code to join Classroom";
    public String line2 = "Classroom id : ";
    public String line3 = "Classroom name :";
    public String line4 = "Created by :";
    public String line5 = "Enter ";
    public String line6 = "to join";


    TextView editText;
    ImageView whatsapp, copy;
    ClipData myClip;
    ClipboardManager myClipboard;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_sucess);

        editText = findViewById(R.id.crtsuccess);
        whatsapp = findViewById(R.id.whatsapp_share);
        copy = findViewById(R.id.copy);
        hide_act_bar();
        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        String cls_id, cls_name, created_by;
        cls_id = getIntent().getStringExtra("CLASS_ID");
        cls_name = getIntent().getStringExtra("CLASS_NAME");
        created_by = getIntent().getStringExtra("CREATED_BY");

        String s = line1+"\n"+line2+" "+cls_id+"\n"+line3+" "+cls_name+"\n"+line4+" "+created_by+"\n"+line5+cls_id+" "+line6;
        editText.setText(s);

        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                whatsappIntent.setType("text/plain");
                whatsappIntent.setPackage("com.whatsapp");
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, s.toString());
                try {
                    startActivity(whatsappIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(CreationSucess.this, "WhatsApp not Installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myClip = ClipData.newPlainText("text", s.toString());
                myClipboard.setPrimaryClip(myClip);

                Toast.makeText(getApplicationContext(), "Text Copied",
                        Toast.LENGTH_SHORT).show();

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