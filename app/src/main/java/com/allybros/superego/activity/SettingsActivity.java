package com.allybros.superego.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.R;
import com.allybros.superego.ui.LicensesAdapter;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.ErrorCodes;
import com.allybros.superego.util.SessionManager;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;

import java.util.ArrayList;
import java.util.Arrays;

public class SettingsActivity extends AppCompatActivity {

    private ConstraintLayout optionEditProfile;
    private ConstraintLayout optionChangePassword;
    private ConstraintLayout optionAbout;
    private ConstraintLayout optionLicenses;
    private ConstraintLayout optionSignOut;
    private SlidrInterface slidr;
    private LoginButton logoutFacebook;
    private BroadcastReceiver logoutReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ActionBar appBar = getSupportActionBar();
        if (appBar != null) appBar.setDisplayHomeAsUpEnabled(true);
        slidr = Slidr.attach(this);
        slidr.unlock();
        setOptions();
    }

    private void setOptions(){
        optionEditProfile = findViewById(R.id.cardBtnEditProfile);
        optionChangePassword = findViewById(R.id.cardBtnPassword);
        optionAbout = findViewById(R.id.cardBtnAbout);
        optionLicenses = findViewById(R.id.cardBtnLicenses);
        optionSignOut = findViewById(R.id.cardBtnSingOut);
        logoutFacebook = findViewById(R.id.logoutFacebook); //Only trigger logout for Facebook
        setupReceivers();

        optionEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), EditProfileActivity.class);
                startActivity(i);
            }
        });

        optionSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        optionChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ChangePasswordActivity.class);
                startActivity(i);
            }
        });

        optionAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAboutDialog();
            }
        });

        optionLicenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLicensesDialog();
            }
        });



    }

    private void setupReceivers(){
        logoutReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra("status",0);
                Log.d("receiver", "Got message: " + status);
                switch (status){
                    case ErrorCodes.SYSFAIL:
                        Log.d("Logout-status",""+status);
                        break;
                    case ErrorCodes.SUCCESS:
                        Log.d("Logout-status",""+status);
                        SessionManager.getInstance().clearSession(getApplicationContext()); //Clear local variables that use login

                        Intent intent1=new Intent(getApplicationContext(), SplashActivity.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        getApplicationContext().startActivity(intent1);
                        finish();
                        break;
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(logoutReceiver, new IntentFilter(ConstantValues.ACTION_LOGOUT));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout(){
            AlertDialog.Builder builder =new AlertDialog.Builder(SettingsActivity.this, R.style.SegoAlertDialog);
            builder.setTitle(R.string.alert_title_end_session)
                    .setMessage(R.string.alert_context_end_session)
                    .setPositiveButton(R.string.action_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Sign Out Google
                            if (SessionManager.getInstance().getUser().getUserType() == 1){ //Google Sign out
                                GoogleSignInClient mGoogleSignInClient;
                                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                        .requestIdToken(getString(R.string.GOOGLE_CLIENT_ID))
                                        .requestEmail()
                                        .build();
                                mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);
                                mGoogleSignInClient.signOut()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Log.d("Google-Logout","Google-Logout");
                                               /*Ally Bros Logout
                                               String session_token = SessionManager.getInstance().getSessionToken();
                                                LogoutTask.logoutTask(getApplicationContext(), session_token);*/

                                                //TODO: Bizim logout çalışınca silinmeli
                                                SessionManager.getInstance().clearSession(getApplicationContext()); //Clear local variables that use login
                                                Intent intent1=new Intent(getApplicationContext(), SplashActivity.class);
                                                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                getApplicationContext().startActivity(intent1);
                                                finish();
                                            }
                                        });
                            }else if(SessionManager.getInstance().getUser().getUserType() == 2){  //Sign Out Facebook
                                //Facebook logout requirements
                                CallbackManager callbackManager = CallbackManager.Factory.create();
                                logoutFacebook.setPermissions(Arrays.asList("email","public_profile"));
                                callbackManager = CallbackManager.Factory.create();
                                LoginManager.getInstance().logOut();        //Trigger logout process


                               /*Ally Bros Logout
                               String session_token = SessionManager.getInstance().getSessionToken();
                                        LogoutTask.logoutTask(getApplicationContext(), session_token);*/
                                //TODO: Bizim logout çalışınca silinmeli
                                SessionManager.getInstance().clearSession(getApplicationContext()); //Clear local variables that use login
                                Intent intent1=new Intent(getApplicationContext(), SplashActivity.class);
                                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                getApplicationContext().startActivity(intent1);
                                finish();
                            }else{//Sign out Ally Bros

                               /*Ally Bros Logout
                               String session_token = SessionManager.getInstance().getSessionToken();
                                LogoutTask.logoutTask(getApplicationContext(), session_token);*/

                                //TODO: Bizim logout çalışınca silinmeli
                                SessionManager.getInstance().clearSession(getApplicationContext()); //Clear local variables that use login
                                Intent intent1=new Intent(getApplicationContext(), SplashActivity.class);
                                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                getApplicationContext().startActivity(intent1);
                                finish();
                            }
                        }
                    })
                    .setNegativeButton(R.string.action_no, null)
                    .setCancelable(true).show();


    }

    private void showAboutDialog(){
        //Inflate dialog layout
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_about, null);
        TextView tvWebSite = dialogView.findViewById(R.id.tvWebSite);
        tvWebSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://" + getString(R.string.web_root);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        //Show dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, R.style.SegoAlertDialog);
        builder.setView(dialogView).show();
    }

    private void showLicensesDialog(){

        //Infalate dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_licenses, null);
        ListView listViewLicenses = dialogView.findViewById(R.id.listviewLicenses);

        //Get licenses
        String[] licenses = getResources().getStringArray(R.array.licenses);
        ArrayList<String> licenseList = new ArrayList<>(Arrays.asList(licenses));
        LicensesAdapter adapter = new LicensesAdapter(getApplicationContext(), licenseList, listViewLicenses);

        //Show
        listViewLicenses.setAdapter(adapter);

        //Show dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, R.style.SegoAlertDialog);
        builder.setTitle(R.string.option_licenses);
        builder.setView(dialogView).show();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(logoutReceiver);

    }
}
