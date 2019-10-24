package com.allybros.superego.activity;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.allybros.superego.R;

public class SettingsActivity extends AppCompatActivity {

    Button btChangePassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        btChangePassword=findViewById(R.id.changePassword);


        btChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SettingsActivity.this,PasswordChangeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
