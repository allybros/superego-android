package com.allybros.superego.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.R;
import com.allybros.superego.api.PasswordChangeTask;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.ErrorCodes;
import com.allybros.superego.util.SessionManager;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class ChangePasswordActivity extends AppCompatActivity {

    private ScrollView rootView;
    private MaterialProgressBar progressChangePassword;
    private Button btChangePassword;
    private EditText etOldPassword, etNewPassword, etNewPasswordAgain;
    private TextInputLayout tilOldPassword, tilNewPassword, tilNewPasswordAgain;
    private ConstraintLayout cardFormChangePassword;
    private SlidrInterface slidr;

    private BroadcastReceiver changePasswordReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        ActionBar ab = getSupportActionBar();
        if (ab != null) ab.setDisplayHomeAsUpEnabled(true);

        rootView = findViewById(R.id.contentRootChangePassword);
        initializeComponents();

        changePasswordReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra("status",0);
                setProgressVisibility(false);
                //Check status
                switch (status){
                    case ErrorCodes.SUCCESS:
                        Snackbar.make(rootView, R.string.message_process_succeed, BaseTransientBottomBar.LENGTH_LONG).show();
                        break;

                    case  ErrorCodes.PASSWORD_NOT_LEGAL:
                        tilNewPassword.setError(getString(R.string.error_password_not_legal));
                        tilNewPasswordAgain.setError("");
                        break;
                    case  ErrorCodes.UNAUTHORIZED:
                        tilOldPassword.setError(getString(R.string.error_current_password_wrong));
                        break;

                    default:
                        Snackbar.make(rootView, R.string.error_no_connection, BaseTransientBottomBar.LENGTH_LONG)
                                .setAction(R.string.action_try_again, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        attemptChangePassword();
                                    }
                                }).show();
                }
            }
        };
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(changePasswordReceiver, new IntentFilter(ConstantValues.ACTION_PASSWORD_CHANGE));

        slidr = Slidr.attach(this);
        slidr.unlock();
    }

    private void initializeComponents(){
        btChangePassword = findViewById(R.id.btChangePassword);
        progressChangePassword = findViewById(R.id.progressChangePassword);
        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etNewPasswordAgain = findViewById(R.id.etNewPasswordAgain);
        tilOldPassword = findViewById(R.id.layoutOldPassword);
        tilNewPassword = findViewById(R.id.layoutNewPassword);
        tilNewPasswordAgain = findViewById(R.id.layoutNewPasswordAgain);
        cardFormChangePassword = findViewById(R.id.cardFormChangePassword);

        btChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptChangePassword();
            }
        });
    }

    private void attemptChangePassword(){
        // Show progress indicator.
        tilOldPassword.setErrorEnabled(false);
        tilNewPassword.setErrorEnabled(false);
        tilNewPasswordAgain.setErrorEnabled(false);

        // Get user inputs.
        String oldPass = etOldPassword.getText().toString();
        String newPass = etNewPassword.getText().toString();
        String newPassAgain = etNewPasswordAgain.getText().toString();

        // Check required fields.
        if (oldPass.isEmpty()) tilOldPassword.setError(getString(R.string.input_error_enter_current_password));
        if (newPass.isEmpty()) tilNewPassword.setError(getString(R.string.input_error_enter_new_pass));

        // Old pass and new pass are given, check if new password fields are matching.
        else if (!oldPass.isEmpty() && !newPass.equals(newPassAgain)) {
            tilNewPassword.setErrorEnabled(true);
            tilNewPasswordAgain.setError(getString(R.string.input_error_password_mismatch));
        }
        // All requirements are satisfied, proceed for creating new password.
        else if (!oldPass.isEmpty()) {
            // Check internet connection
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            if(isConnected) {
                setProgressVisibility(true);
                PasswordChangeTask.passwordChangeTask(getApplicationContext(),
                        SessionManager.getInstance().getSessionToken(),
                        oldPass,
                        newPass);
            }
            else {
                Log.d("CONNECTION", String.valueOf(isConnected));
                Snackbar.make(rootView, R.string.error_no_connection, BaseTransientBottomBar.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Sets progress view visibility.
     * @param visible Set true when progress indicator needs to be shown.
     */
    private void setProgressVisibility(boolean visible) {
        if (visible) {
            progressChangePassword.setVisibility(View.VISIBLE);
            cardFormChangePassword.setAlpha(0.5f);
            btChangePassword.setEnabled(false);
        } else {
            progressChangePassword.setVisibility(View.INVISIBLE);
            cardFormChangePassword.setAlpha(1f);
            btChangePassword.setEnabled(true);
        }
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
