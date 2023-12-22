package com.example.attendx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;



public class login extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "LOGIN_ACTIVITY";
    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    Dialog dialog;
    Button signInbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        hide_act_bar();
        signInbtn = findViewById(R.id.google_signIn);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            Log.d(TAG, "USER ALREADY FOUND LOGED IN SENDING HIM TO DASHBOARD"+user.getDisplayName()+user.getUid());
            Intent i = new Intent(login.this,dashboard.class);
            startActivity(i);
            finish();
        }

        // Configuring google sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        dialog = new Dialog(login.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_wait1);
        dialog.setCanceledOnTouchOutside(false);

        //getting the button Click
        signInbtn.setOnClickListener(v -> signIn());
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

    //popup window appear
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            dialog.show();

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                dialog.dismiss();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "FIREBASE AUTH SUCCESS");
                        FirebaseUser user = mAuth.getCurrentUser();
                        getuserdatafromgmail(user);

                        dialog.dismiss();

                    } else {
                        // If sign in fails, display a message to the user.
                        dialog.dismiss();
                        Toast.makeText(login.this, "FIREBASE AUTH Failed", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "FIREBASE AUTH FAILED");

                    }
                });
    }
    private void getuserdatafromgmail(FirebaseUser user) {

        if (user != null) {

            Log.d(TAG, "getuserdatafromgmail: CALLED");
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
            String uid = user.getUid();

            Log.d("USER_DETAILS", "Name :" + name);
            Log.d("USER_DETAILS", "Email :" + email);
            Log.d("USER_DETAILS", "Userid :" + uid);

            Intent i = new Intent(login.this, DetailsFromUser.class);
            i.putExtra("EMAIL",email);
            i.putExtra("NAME", name);
            i.putExtra("UID", uid);
            i.putExtra("PHOTOURL", photoUrl.toString());
            Log.d("PHOTOURL_LOG", photoUrl.toString());
            startActivity(i);
            finish();
        }
    }

}