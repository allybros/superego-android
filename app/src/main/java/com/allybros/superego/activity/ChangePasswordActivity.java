package com.allybros.superego.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.R;
import com.allybros.superego.api.PasswordChangeTask;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.ErrorCodes;
import com.allybros.superego.util.SessionManager;

public class ChangePasswordActivity extends AppCompatActivity {

    Button btChangePassword;
    EditText etoldPassword, etnewPassword, etnewPasswordAgain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initializeComponents();

        btChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( etnewPassword.getText().toString().isEmpty() || etoldPassword.getText().toString().isEmpty() ||
                    etnewPasswordAgain.getText().toString().isEmpty()){ // Are fields null?

                    Log.d("ChangePasswordText","gerekli alanlar dolu değil");
                }else if( !(etnewPassword.getText().toString().equals(etnewPasswordAgain.getText().toString())) ){  // Are New pass and new pass again equal?
                    Log.d("ChangePasswordText","new pass and new pass again mismatch");
                }else{                                                                          //Send request to change password
                    Log.d("ChangePasswordText","İstek atıldı");
                    PasswordChangeTask.passwordChangeTask(getApplicationContext(),
                            SessionManager.getInstance().getSessionToken(),
                            etoldPassword.getText().toString(),
                            etnewPassword.getText().toString());
                }

            }
        });

    }

    private void initializeComponents(){
        btChangePassword = (Button) findViewById(R.id.btChangePassword);
        etoldPassword = (EditText)  findViewById(R.id.etOldPassword);
        etnewPassword = (EditText) findViewById(R.id.etNewPassword);
        etnewPasswordAgain = (EditText) findViewById(R.id.etNewPasswordAgain);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(changePasswordReceiver, new IntentFilter(ConstantValues.ACTION_PASSWORD_CHANGE));


    }

    /*
    *   Receives password process result
    *
    * */
    private BroadcastReceiver changePasswordReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra("status",0);
            Log.d("receiver", "Got message: " + status);

            //Check status
            switch (status){
                case ErrorCodes.SUCCESS:
                    Log.d("ChangePassword: ",""+ status);
                    break;
                case ErrorCodes.SYSFAIL:
                    Log.d("ChangePassword: ",""+status);
                    break;
                case ErrorCodes.PASSWORD_EMPTY:
                    Log.d("ChangePassword: ",""+status);
                    break;
                case ErrorCodes.PASSWORD_MISMATCH:
                    Log.d("ChangePassword: ",""+status);
                    break;
                case  ErrorCodes.PASSWORD_NOT_LEGAL:
                    Log.d("ChangePassword: ",""+status);
                    break;
                case  ErrorCodes.UNAUTHORIZED:
                    Log.d("ChangePassword: ",""+status);
                    break;
            }
        }
    };
}
