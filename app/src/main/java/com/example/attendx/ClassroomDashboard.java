package com.example.attendx;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.fonts.Font;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.SnapshotHolder;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class ClassroomDashboard extends AppCompatActivity {

    final static String TAG = "CLS_DASH";
    final static String TAG2 = TAG + "_PDF";
    final static String TAG3 = TAG2 + "_DATA";
    final static String TAG4 = TAG3 + "_DATA_FINAL";
    ImageView home_btn, gen_report_btn;
    CardView take_att_CV, view_att_CV, join_req_CV, view_mem_CV;
    TextView cls_name, sub_name, sub_code, created_by;
    String room_id = "";
    String cls_name_str, created_by_str, sub_name_str, sub_code_str = "empty";
    String total_cls_taken;

    int serial_no = 1;

    float[] col_width = {1, 3, 2, 1, 1, 1};
    Table body_table = new Table(col_width);


    String pdf_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();

    File file = new File(pdf_path, "attadence_AttandX.pdf");
    OutputStream outputStream = new FileOutputStream(file);

    PdfWriter writer = new PdfWriter(file);
    PdfDocument pdfDocument = new PdfDocument(writer);
    Document document = new Document(pdfDocument);

    public ClassroomDashboard() throws FileNotFoundException {
    }

    private static void SetWindowFlag(Activity activity, final int Bits, Boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams Winparams = win.getAttributes();
        if (on) {
            Winparams.flags |= Bits;
        } else {
            Winparams.flags &= ~Bits;
        }
        win.setAttributes(Winparams);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom_dashboard);

        hide_act_bar();
        take_att_CV = findViewById(R.id.take_att);
        view_att_CV = findViewById(R.id.view_att);
        join_req_CV = findViewById(R.id.join_req);
        view_mem_CV = findViewById(R.id.view_mem_CV);

        cls_name = findViewById(R.id.classroom_name);
        sub_name = findViewById(R.id.sub_name);
        sub_code = findViewById(R.id.sub_code);
        created_by = findViewById(R.id.created_by_view);

        room_id = getIntent().getStringExtra("ROOM_ID");

        home_btn = findViewById(R.id.home_btn);
        home_btn.setOnClickListener(v -> send_to("usr_dash"));

        gen_report_btn = findViewById(R.id.generate_report_btn);
        body_table.setWidth(UnitValue.createPercentValue(100));
        try {
            body_table.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA));
        } catch (IOException e) {
            e.printStackTrace();
        }

        gen_report_btn.setOnClickListener(v -> {
            try {
                designpdf();
            } catch (IOException e) {
                e.printStackTrace();
            }
            int x = -1;
            try {

                x = generate_pdf((name, phone, row_no) -> {

                    if (serial_no % 2 == 0) {
                        body_table.addCell(new Cell().add(new Paragraph(String.valueOf(serial_no)))
                                .setBorder(Border.NO_BORDER)
                                .setPadding(5)
                                .setBackgroundColor(new DeviceRgb(174,216,255))
                                .setFontColor(ColorConstants.BLACK)
                                .setTextAlignment(TextAlignment.CENTER)
                        );

                        body_table.addCell(new Cell().add(new Paragraph(String.valueOf(name)))
                                .setBorder(Border.NO_BORDER)
                                .setPadding(5)
                                .setBackgroundColor(new DeviceRgb(174,216,255))
                                .setFontColor(ColorConstants.BLACK)
                                .setTextAlignment(TextAlignment.CENTER)
                        );
                        body_table.addCell(new Cell().add(new Paragraph(String.valueOf(phone)))
                                .setBorder(Border.NO_BORDER)
                                .setPadding(5)
                                .setBackgroundColor(new DeviceRgb(174,216,255))
                                .setFontColor(ColorConstants.BLACK)
                                .setTextAlignment(TextAlignment.CENTER)
                        );
                        Log.d(TAG3, "VAL1(NAME)" + name + " VAL2(PHN)" + phone);
                    }
                    else
                        {
                        body_table.addCell(new Cell().add(new Paragraph(String.valueOf(serial_no)))
                                .setBorder(Border.NO_BORDER)
                                .setPadding(5)
                                .setBackgroundColor(ColorConstants.WHITE)
                                .setFontColor(ColorConstants.BLACK)
                                .setTextAlignment(TextAlignment.CENTER)
                        );

                        body_table.addCell(new Cell().add(new Paragraph(String.valueOf(name)))
                                .setBorder(Border.NO_BORDER)
                                .setPadding(5)
                                .setBackgroundColor(ColorConstants.WHITE)
                                .setFontColor(ColorConstants.BLACK)
                                .setTextAlignment(TextAlignment.CENTER)
                        );
                        body_table.addCell(new Cell().add(new Paragraph(String.valueOf(phone)))
                                .setBorder(Border.NO_BORDER)
                                .setPadding(5)
                                .setBackgroundColor(ColorConstants.WHITE)
                                .setFontColor(ColorConstants.BLACK)
                                .setTextAlignment(TextAlignment.CENTER)
                        );
                        Log.d(TAG3, "VAL1(NAME)" + name + " VAL2(PHN)" + phone);
                    }

                }, (presant, row_no,total_members) -> {

                    Log.d(TAG3, "(VAL3)TOTAL"+presant+" VAL4 row no"+row_no+"VAL 7 TOTAL MEM"+total_members);

                    if (serial_no % 2 == 0) {

                        body_table.addCell(new Cell().add(new Paragraph(String.valueOf(presant)))
                                .setBorder(Border.NO_BORDER)
                                .setPadding(5)
                                .setBackgroundColor(new DeviceRgb(174,216,255))
                                .setFontColor(ColorConstants.BLACK).setTextAlignment(TextAlignment.CENTER)
                        );

                        body_table.addCell(new Cell().add(new Paragraph(String.valueOf(total_cls_taken)))
                                .setBorder(Border.NO_BORDER)
                                .setPadding(5)
                                .setBackgroundColor(new DeviceRgb(174,216,255))
                                .setFontColor(ColorConstants.BLACK).setTextAlignment(TextAlignment.CENTER)
                        );
                        float temp = (float) presant / Integer.parseInt(total_cls_taken);
                        float percentage = temp * 100;
                        body_table.addCell(new Cell().add(new Paragraph(String.valueOf(percentage)))
                                .setBorder(Border.NO_BORDER)
                                .setPadding(5)
                                .setBackgroundColor(new DeviceRgb(174,216,255))
                                .setFontColor(ColorConstants.BLACK).setTextAlignment(TextAlignment.CENTER)
                        );
                        Log.d(TAG3, "VAL5 PERCENT"+percentage+"VAL6 TOTAL CLS TAKEN "+total_cls_taken+"  MY ROW "+row_no);
                    }
                    else
                        {
                        body_table.addCell(new Cell().add(new Paragraph(String.valueOf(presant)))
                                .setBorder(Border.NO_BORDER)
                                .setPadding(5)
                                .setBackgroundColor(ColorConstants.WHITE)
                                .setFontColor(ColorConstants.BLACK).setTextAlignment(TextAlignment.CENTER)
                        );

                        body_table.addCell(new Cell().add(new Paragraph(String.valueOf(total_cls_taken)))
                                .setBorder(Border.NO_BORDER)
                                .setPadding(5)
                                .setBackgroundColor(ColorConstants.WHITE)
                                .setFontColor(ColorConstants.BLACK).setTextAlignment(TextAlignment.CENTER)
                        );
                        float temp = (float) presant / Integer.parseInt(total_cls_taken);
                        float percentage = temp * 100;
                        body_table.addCell(new Cell().add(new Paragraph(String.valueOf(percentage)))
                                .setBorder(Border.NO_BORDER)
                                .setPadding(5)
                                .setBackgroundColor(ColorConstants.WHITE)
                                .setFontColor(ColorConstants.BLACK).setTextAlignment(TextAlignment.CENTER)
                        );
                        Log.d(TAG3, "VAL5 PERCENT"+percentage+"VAL6 TOTAL CLS TAKEN "+total_cls_taken+"  MY ROW "+row_no);
                    }


                    if (serial_no == total_members) {
                        document.add(body_table);
                        document.close();
                        Log.d(TAG3, "DOCUMENT CLOSED");
                    }
                    serial_no++;
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (x == 1) {
                Toast.makeText(ClassroomDashboard.this, "Pdf Generate Successful", Toast.LENGTH_SHORT).show();
            } else if (x == 0) {
                Toast.makeText(ClassroomDashboard.this, "Pdf Generate fail", Toast.LENGTH_SHORT).show();
            } else if (x == -1) {
                Toast.makeText(ClassroomDashboard.this, "Error in generate_pdf()", Toast.LENGTH_LONG).show();
            }
        });

        fetch_cls_details();

        take_att_CV.setOnClickListener(v -> send_to("take_att"));
        view_att_CV.setOnClickListener(v -> send_to("view_att"));
        join_req_CV.setOnClickListener(v -> send_to("join_req"));
        view_mem_CV.setOnClickListener(v -> send_to("view_mem"));

    }

    private void designpdf() throws IOException {

        // here we will set the constant part of the pdf like the back and foreground image table header and all

        document.setMargins(0, 0, 0, 0);

        Drawable bg_pdf = getDrawable(R.drawable.bg);
        Bitmap bg_pdf_bitmap = ((BitmapDrawable) bg_pdf).getBitmap();
        ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
        bg_pdf_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream1);
        byte[] bg_pdf_bitmap_data = stream1.toByteArray();
        ImageData imageData1 = ImageDataFactory.create(bg_pdf_bitmap_data);

        Image bg_pdf_img = new Image(imageData1);

        Drawable header_pdf = getDrawable(R.drawable.headerpdf);
        Bitmap header_bitmap = ((BitmapDrawable) header_pdf).getBitmap();
        ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
        header_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream2);
        byte[] header_pdf_bitmap_data = stream2.toByteArray();
        ImageData imageData2 = ImageDataFactory.create(header_pdf_bitmap_data);

        Image header_pdf_img = new Image(imageData2);

        // class details table

        // we have to make 3 functions 1 for class details from header. 1 for attadence data 1 for calculation (not sure

        List l1 = new List();
        l1.add(cls_name_str);
        l1.add(sub_name_str);
        l1.add(sub_code_str);


        Table header_table = new Table(2);
        header_table.setMarginTop(30F);
        header_table.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA));

        header_table.addCell(new Cell().add(l1)
                .setPaddingLeft(30F)
                .setTextAlignment(TextAlignment.LEFT)
                .setBorder(Border.NO_BORDER)
                .setFontSize(18)
        );

        header_table.addCell(new Cell().add(new Paragraph(created_by_str))
                .setBorder(Border.NO_BORDER)
                .setPaddingLeft(270F)
                .setFontSize(30)
        );
        String[] body_header_val = {"#", "Name", "Phone", "Present", "Total", "Percentage"};


        for (int i = 0; i <= body_header_val.length - 1; i++) {
            Text t = new Text(body_header_val[i]).setBold();
            body_table.addHeaderCell(new Cell().add(new Paragraph(t))
                    .setBorder(Border.NO_BORDER)
                    .setPadding(5)
                    .setBackgroundColor(new DeviceRgb(118, 179, 236))
                    .setFontColor(ColorConstants.WHITE)
                    .setTextAlignment(TextAlignment.CENTER)
            );
            Log.d("TABLE_VALUE", body_header_val[i]);
        }
        document.add(bg_pdf_img.setFixedPosition(0, 0));
        document.add(header_pdf_img);
        document.add(header_table);

    }


    public int generate_pdf(name_phn_call_back namePhnCallBack, total_presant_call_back totalPresantCallBack) throws IOException {

        fetch_cls_details();

        DatabaseReference root_ref, this_cls_data_ref, this_cls_header_ref;

        root_ref = FirebaseDatabase.getInstance().getReference();
        this_cls_header_ref = root_ref.child("classroom_header").child(room_id);
        this_cls_data_ref = root_ref.child(room_id).child("attedance_data");


        this_cls_data_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d(TAG + "_PDF", "snapshot " + snapshot.toString());
                    long no_of_child = snapshot.getChildrenCount();
                    Log.d(TAG + "_PDF", "NO of child =  " + no_of_child);
                    DatabaseReference user_profile_ref;
                    user_profile_ref = root_ref.child("userprofile");
                    //main pt
                    final String[] u_name = new String[1];
                    final String[] u_phn = new String[1];

                    int total_members = 0;
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String UID;
                        total_members = (int) snapshot.getChildrenCount();

                        Log.d(TAG3, "ROW" + serial_no + "#" + serial_no);
                        UID = snapshot1.getKey();

                        int finalSerial_no = serial_no;

                        user_profile_ref.child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (snapshot.exists()) {
                                    u_name[0] = (String) snapshot.child("name").getValue();
                                    u_phn[0] = (String) snapshot.child("phone").getValue();
                                    namePhnCallBack.onCallback((String) snapshot.child("name").getValue(),
                                            (String) snapshot.child("phone").getValue(),
                                            Integer.valueOf(finalSerial_no));
                                    Log.d(TAG2, "USERNAME " + u_name[0] + "\tPHONE " + u_phn[0] + "of " + UID);

                                    Log.d(TAG3, "ROW" + finalSerial_no + "Name" + u_name[0]);
                                    Log.d(TAG3, "ROW" + finalSerial_no + "PHONE" + u_phn[0]);
                                    // one call back here DONE
                                } else {
                                    Toast.makeText(getApplicationContext(), "USER_DETAILS FOR PDF NOT FOUND", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Query q1 = this_cls_data_ref.child(UID).orderByValue().equalTo("PRESENT");
                        final int[] finalSerial_no2 = {serial_no};

                        int finalTotal_members = total_members;
                        q1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                totalPresantCallBack.onCallback((int) snapshot.getChildrenCount(), finalSerial_no2[0], finalTotal_members);


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        //end of for loop
                        //serial_no = serial_no+1;
                    }
                } else {
                    Toast.makeText(ClassroomDashboard.this, "attadance data is empty for this classroom " + room_id, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ClassroomDashboard.this, "unable to read attadance data node", Toast.LENGTH_SHORT).show();
            }
        });

        // body_table.addCell(new Cell().add(new Paragraph("FUCKKKKK")).setBorder(Border.NO_BORDER).setBackgroundColor(ColorConstants.WHITE).setFontColor(ColorConstants.BLACK).setTextAlignment(TextAlignment.CENTER));


        Toast.makeText(this, "PDF created successfully in " + pdf_path, Toast.LENGTH_SHORT).show();
        return 1;
    }

    public void hide_act_bar() {
        //code
        getSupportActionBar().hide(); // its hide actionbar

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            SetWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //makes fully transparent status bar
        if (Build.VERSION.SDK_INT >= 21) {
            SetWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public void send_to(@NonNull String location) {
        Log.d("CLASS_DASH", "send_to: " + location);
        switch (location) {
            case "join_req":
                Intent j1 = new Intent(getApplicationContext(), Join_request.class);
                j1.putExtra("ROOM_ID", room_id);
                startActivity(j1);
                finish();
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
                finish();
                break;
            case "take_att":
                Intent k = new Intent(getApplicationContext(), act_take_atten.class);
                k.putExtra("ROOM_ID", room_id);
                startActivity(k);
                finish();
                break;
            case "view_att":
                Intent L = new Intent(getApplicationContext(), act_view_atten.class);
                L.putExtra("ROOM_ID", room_id);
                startActivity(L);
                finish();
                break;
            default:
                Toast.makeText(getApplicationContext(), "check loc in code", Toast.LENGTH_SHORT).show();
        }
    }

    public void fetch_cls_details() {

        DatabaseReference root_ref, this_cls_ref;

        root_ref = FirebaseDatabase.getInstance().getReference();
        this_cls_ref = root_ref.child("classroom_header").child(room_id);

        this_cls_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    cls_name_str = snapshot.child("ClassName").getValue().toString();
                    sub_name_str = snapshot.child("SubName").getValue().toString();
                    sub_code_str = snapshot.child("SubCode").getValue().toString();
                    created_by_str = snapshot.child("TeacherName").getValue().toString();
                    total_cls_taken = snapshot.child("TotalClassTaken").getValue().toString();

                    cls_name.setText(cls_name_str);
                    sub_name.setText(sub_name_str);
                    sub_code.setText(sub_code_str);
                    created_by.setText(created_by_str);

                    Log.d(TAG, "clsdetails ::" + cls_name_str + sub_name_str + sub_code_str + created_by_str);
                } else {
                    Log.d(TAG, "snapshot problem :: " + snapshot);
                    Toast.makeText(getApplicationContext(), "snapshot doesnot exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "unable to read cls header :: " + error);
                Toast.makeText(getApplicationContext(), "unable to read header in cls_dash", Toast.LENGTH_SHORT).show();

            }
        });

    }

    public interface name_phn_call_back {
        void onCallback(String name, String phone, int row_no);
    }


    public interface total_presant_call_back {
        void onCallback(int presant, int row_no,int total_members);
    }

}