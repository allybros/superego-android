package com.allybros.superego.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.allybros.superego.R;
import com.allybros.superego.api.LogoutTask;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;

public class SettingsActivity extends AppCompatActivity {

    final String USER_INFORMATION_PREF="USER_INFORMATION_PREF";
    private ConstraintLayout optionEditProfile;
    private ConstraintLayout optionChangePassword;
    private ConstraintLayout optionAbout;
    private ConstraintLayout optionLicenses;
    private ConstraintLayout optionSignOut;
    private SlidrInterface slidr;

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
                AlertDialog.Builder builder =new AlertDialog.Builder(SettingsActivity.this, R.style.SegoAlertDialog);
                builder.setTitle("Oturumu Sonlandır")
                    .setMessage("Geçerli oturmunu sonlandırmak istediğine emin misin?")
                    .setPositiveButton(R.string.action_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Sign Out
                            String session_token;
                            SharedPreferences pref = getApplicationContext().getSharedPreferences(USER_INFORMATION_PREF, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            session_token = pref.getString("session_token","");
                            editor.clear().apply();

                            LogoutTask.logoutTask(getApplicationContext(), session_token);
                            if (LoginActivity.mGoogleSignInClient != null)
                                LoginActivity.mGoogleSignInClient.signOut();

                            Intent intent=new Intent(getApplicationContext(), SplashActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            getApplicationContext().startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.action_no, null)
                    .setCancelable(true).show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
