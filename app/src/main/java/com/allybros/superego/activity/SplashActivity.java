package com.allybros.superego.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.allybros.superego.R;
import com.allybros.superego.api.LoadProfileTask;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.Trait;

import java.util.ArrayList;


public class SplashActivity extends AppCompatActivity {
    public static String session_token;
    private SharedPreferences pref;
    public static ArrayList<Trait> allTraits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        pref= getSharedPreferences(ConstantValues.getUserInformationPref(),MODE_PRIVATE);
        session_token=pref.getString("session_token","");
        Log.d("sessionTokenSplash",session_token);

        allTraits= LoadProfileTask.getAllTraits(getApplicationContext());
        Trait.setAllTraits(allTraits);
        if(!session_token.isEmpty()){
            LoadProfileTask.loadProfileTask(getApplicationContext(),session_token);
        }else{
            Intent intent=new Intent(this,LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

}

