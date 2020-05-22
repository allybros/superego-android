package com.allybros.superego.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.R;
import com.allybros.superego.api.PasswordChangeTask;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.ErrorCodes;
import com.allybros.superego.util.SessionManager;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.snackbar.Snackbar;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class ChangePasswordActivity extends AppCompatActivity {

    ScrollView rootView;
    MaterialProgressBar progressChangePassword;
    Button btChangePassword;
    EditText etoldPassword, etnewPassword, etnewPasswordAgain;
    private BroadcastReceiver changePasswordReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        rootView = findViewById(R.id.contentRootChangePassword);

        changePasswordReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra("status",0);
                setProgressVisibility(false);
                //Check status
                switch (status){
                    case ErrorCodes.SUCCESS:
                        Snackbar.make(rootView,"Başarıyla parolan değiştirildi.", 2000).show();
                        break;
                    case ErrorCodes.SYSFAIL:
                        Snackbar.make(rootView,"Sistemsel bir hata meydana geldi", 2000)
                                .setAction(getString(R.string.pleaseTryAgain), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        btChangePassword.performClick();
                                    }
                                }).show();
                        break;
                    case ErrorCodes.PASSWORD_EMPTY:
                        Snackbar.make(rootView,"Parola boş olamaz.", 2000).show();
                        break;
                    case ErrorCodes.PASSWORD_MISMATCH:
                        Snackbar.make(rootView,"Parolalar eşleşmiyor", 2000).show();
                        break;
                    case  ErrorCodes.PASSWORD_NOT_LEGAL:
                        Snackbar.make(rootView,"Belirlediğin parola şartları karşılamıyor.", 2000).show();
                        break;
                    case  ErrorCodes.UNAUTHORIZED:
                        Snackbar.make(rootView,"Geçerli parola yanlış", 2000).show();
                        break;
                }
            }
        };
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(changePasswordReceiver, new IntentFilter(ConstantValues.ACTION_PASSWORD_CHANGE));
        initializeComponents();
    }

    private void initializeComponents(){
        btChangePassword = findViewById(R.id.btChangePassword);
        progressChangePassword = findViewById(R.id.progressChangePassword);
        etoldPassword = findViewById(R.id.etOldPassword);
        etnewPassword = findViewById(R.id.etNewPassword);
        etnewPasswordAgain = findViewById(R.id.etNewPasswordAgain);

        btChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptChangePassword();

            }
        });
    }

    private void attemptChangePassword(){
        setProgressVisibility(true);
        // Get user inputs
        String oldPass = etoldPassword.getText().toString();
        String newPass = etnewPassword.getText().toString();
        String newPassAgain = etnewPasswordAgain.getText().toString();
        setProgressVisibility(true);

        PasswordChangeTask.passwordChangeTask(getApplicationContext(),
                SessionManager.getInstance().getSessionToken(),
                oldPass,
                newPass);
    }

    /**
     * Sets progress view visibility.
     * @param visible Set true when progress indicator needs to be shown.
     */
    private void setProgressVisibility(boolean visible) {
        if (visible) {
            progressChangePassword.setVisibility(View.VISIBLE);
        } else {
            progressChangePassword.setVisibility(View.INVISIBLE);
        }
    }

}
