package com.example.attendx;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.icu.util.Calendar;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;


public class JoinReqAdapter extends RecyclerView.Adapter<JoinReqAdapter.JoinReqViewholder> {

    ArrayList<JoinReqHelper> join_requests;
    Context context;
    String user_id;
    String this_room_id;
    String key;


    public JoinReqAdapter(ArrayList<JoinReqHelper> join_requests, Context context) {
        this.join_requests = join_requests;
        this.context = context;
    }

    @NonNull
    @Override
    public JoinReqViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.join_req_view, parent, false);
        JoinReqViewholder joinReqViewholder = new JoinReqViewholder(view);
        context = parent.getContext();
        return joinReqViewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull JoinReqViewholder holder, int position) {
        JoinReqHelper joinReqHelper = join_requests.get(position);

        holder.stu_name.setText(joinReqHelper.getStu_name());
        holder.phn_no.setText(joinReqHelper.getPhn_no());
        holder.email.setText(joinReqHelper.getEmail());
        user_id = joinReqHelper.getUser_id();
        this_room_id = joinReqHelper.getRoom_id();
        key = joinReqHelper.getKey();

        Glide.with(this.context).load(joinReqHelper.getImage_url()).into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return join_requests.size();
    }

    public class JoinReqViewholder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView stu_name, phn_no, email;
        ImageView approve, deny;

        public JoinReqViewholder(@NonNull View itemView) {
            super(itemView);

            stu_name = itemView.findViewById(R.id.stu_name);
            phn_no = itemView.findViewById(R.id.phone);
            email = itemView.findViewById(R.id.email);
            imageView = itemView.findViewById(R.id.user_dp);

            approve = itemView.findViewById(R.id.approve);
            deny = itemView.findViewById(R.id.deny);


            approve.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //approve request by first delete this entry from B node then create a new node with user_id inside member node of this class
                    //send notification to user_id
                    // we will need rand_key of this entry to delete
                    //we will need room_id for ref
                    // we will need user_id of the request to send notification and create a node under member

                    Log.d("APPROVE", "approved clicked");


                    // Create Alert using Builder
                    CFAlertDialog.Builder builder = new CFAlertDialog.Builder(v.getContext())
                            .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                            .setTitle("APPROVE REQUEST!")
                            .setMessage("Do you want to approve the join request??")
                            .addButton("Approve", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                                Toast.makeText(v.getContext(), "approve tapped", Toast.LENGTH_SHORT).show();
                                send_accept_req_notification(this_room_id,user_id);
                                join_requests.remove(getAdapterPosition());
                                notifyItemRemoved(getAdapterPosition());
                                notifyDataSetChanged();
                                dialog.dismiss();
                            })
                            .addButton("cancel",-1,-1, CFAlertDialog.CFAlertActionStyle.DEFAULT,CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, ((dialog, which) -> {
                                Toast.makeText(v.getContext(), "Cancel tapped", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }));

                    // Show the alert
                    builder.show();



                }
            });

            deny.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // we will delete the request for this we will need rand_key of this entry then we will need user_id to push notification only

                    CFAlertDialog.Builder builder = new CFAlertDialog.Builder(v.getContext());
                    builder.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT);
                    builder.setTitle("Select reason for deny request");
                    builder.setSingleChoiceItems(new String[]{"Wrong Profile picture given", "Wrong Name given", "Wrong Phone number given", "Wrong Email Given", "You are not a student of this class", "Others/spam"}, 3, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int index) {

                            Toast.makeText(context, "Selected:"+index, Toast.LENGTH_SHORT).show();
                            join_requests.remove(getAdapterPosition());
                            notifyItemRemoved(getAdapterPosition());
                            notifyDataSetChanged();

                        switch (index)
                            {
                                case 0:{
                                    String reason = "Wrong Profile picture given";
                                    send_deny_request_notification(user_id,reason,this_room_id);
                                    break;
                                }
                                case 1:
                                    {
                                        String reason = "Wrong Name given";
                                        send_deny_request_notification(user_id,reason,this_room_id);
                                        break;
                                    }
                                case 2:
                                    {
                                        String reason = "Wrong Phone number given";
                                        send_deny_request_notification(user_id,reason,this_room_id);
                                        break;
                                    }
                                case 3:
                                {
                                    String reason = "Wrong Email Given";
                                    send_deny_request_notification(user_id,reason,this_room_id);
                                    break;
                                }
                                case 4:
                                {
                                    String reason = "You are not a student of this class";
                                    send_deny_request_notification(user_id,reason,this_room_id);
                                    break;
                                }
                                case 5:
                                {
                                    String reason = "Others/spam";
                                    send_deny_request_notification(user_id,reason,this_room_id);
                                    break;
                                }
                            }

                        }
                    });
                    builder.addButton("Deny Request", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.d("DENY", String.valueOf(i));
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();
                   // builder.setItems(new String[]{"Wrong Profile picture given", "Wrong Name given", "Wrong Phnoe number given", "Wrong Email Given", "You are not a student of this class", "Others/spam"}, new DialogInterface.OnClickListener() {

                }
            });

        }
    }

    public void send_accept_req_notification(String room_id,String usr_id)
    {
        DatabaseReference root_ref,user_noti_ref,join_ref,new_notiref,room_ref;

        root_ref = FirebaseDatabase.getInstance().getReference();
        user_noti_ref=root_ref.child("userprofile").child(usr_id).child("notification");
        join_ref = root_ref.child(room_id).child("join_request");
        room_ref = root_ref.child(room_id);
        new_notiref = user_noti_ref.child("new_noti_exists");

        // send noti start
        Noti_upload_helper noti_upload_helper = new Noti_upload_helper("Your Join request to class code "+this_room_id.toString()+" has been ACCEPTED .",
                "0",
                "0");
        user_noti_ref.push().setValue(noti_upload_helper);
        // send noti stop

        //set new notification received to true starts
        new_notiref.setValue("TRUE");
        //set new notification received to true stops

        //delete join req and make a new node to store attandance data under it... its a old idea
        // now new idea is to delete the join req then create  a node called members to store members uid
        join_ref.child(key).removeValue(new DatabaseReference.CompletionListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                room_ref.child("members").child(user_id).setValue(upload_att.date);
            }
        });
        //delete join req ends.
    }

    public void send_deny_request_notification(String user_id, String reason, String this_room_id)
    {
        Log.d("DENY_NOTI", "send_deny_request_notification: Called");
        DatabaseReference root_ref,user_noti_ref,join_ref,new_notiref,room_ref;

        root_ref = FirebaseDatabase.getInstance().getReference();
        user_noti_ref=root_ref.child("userprofile").child(user_id).child("notification");
        join_ref = root_ref.child(this_room_id).child("join_request");
        room_ref = root_ref.child(this_room_id);
        new_notiref = user_noti_ref.child("new_noti_exists");

        //send notification starts
        Noti_upload_helper noti_upload_helper = new Noti_upload_helper("Your Join request to class code "+this_room_id.toString()+" has been REJECTED to "+reason.toString(),
                "0",
                "0");
        user_noti_ref.push().setValue(noti_upload_helper);
        //send notification ends

        //set new notification received to true starts
        new_notiref.setValue("TRUE");
        //set new notification received to true stops

        //delete request starts


        join_ref.child(key).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
            }
        });

        //delete req ends

    }

}
