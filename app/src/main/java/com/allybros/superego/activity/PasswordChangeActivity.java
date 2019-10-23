package com.allybros.superego.activity;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.allybros.superego.R;
import com.allybros.superego.api.ChangePasswordTask;

public class PasswordChangeActivity extends AppCompatActivity {
    final String USER_INFORMATION_PREF="USER_INFORMATION_PREF";
    Button btChange, btCancel;
    static EditText oldPassword;
    static EditText newPassword;
    static EditText newPasswordAgain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);

        btChange=findViewById(R.id.bt_change_password);
        btCancel=findViewById(R.id.bt_password_change_cancel);

        oldPassword=findViewById(R.id.old_password);
        newPassword=findViewById(R.id.new_password);
        newPasswordAgain=findViewById(R.id.new_password_again);
        btChange.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SharedPreferences pref = getSharedPreferences(USER_INFORMATION_PREF, MODE_PRIVATE);
                String sessionToken= pref.getString("session_token", "");
                String oldPass= oldPassword.getText().toString();
                String newPass=newPassword.getText().toString();
                String newPassAgain=newPasswordAgain.getText().toString();
                ChangePasswordTask.changePasswordTask(getApplicationContext(),oldPass,sessionToken,newPass,newPassAgain);

            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
}
