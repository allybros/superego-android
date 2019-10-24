package com.allybros.superego.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.allybros.superego.R;
import com.allybros.superego.api.LoadProfileTask;
import com.allybros.superego.fragments.ResultsFragment;
import com.allybros.superego.unit.Trait;

import java.util.ArrayList;


public class SplashActivity extends AppCompatActivity {
    public static final String USER_INFORMATION_PREF="USER_INFORMATION_PREF";
    private String session_token;
    private SharedPreferences pref;
    public static ArrayList<Trait> allTraits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        pref= getSharedPreferences(USER_INFORMATION_PREF,MODE_PRIVATE);
        session_token=pref.getString("session_token","");
        allTraits= LoadProfileTask.getAllTraits(getApplicationContext());

        if(!session_token.isEmpty()){
            LoadProfileTask.loadProfileTask(getBaseContext(),session_token);
        }else{
            Intent intent=new Intent(this,LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

}

