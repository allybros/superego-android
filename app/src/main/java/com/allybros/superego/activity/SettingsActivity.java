package com.allybros.superego.activity;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.allybros.superego.R;
import com.allybros.superego.dialogs.passwordChangeFragment;

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
                passwordChangeFragment changePassword = new passwordChangeFragment();
                FragmentManager fm = getSupportFragmentManager();

                changePassword.show(fm, "Custom Dialog Fragment");
            }
        });
    }
}
