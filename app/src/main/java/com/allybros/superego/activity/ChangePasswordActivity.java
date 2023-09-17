package com.allybros.superego.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class ChangePasswordActivity extends AppCompatActivity {

    private ConstraintLayout rootView;
    private MaterialProgressBar progressChangePassword;
    private Button btChangePassword;
    private EditText etOldPassword, etNewPassword, etNewPasswordAgain;
    private ConstraintLayout cardFormChangePassword;
    private ImageView ivBack, ivOldPassToggle, ivNewPassToggle, ivNewPassAgainToggle;

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
                        setError(etNewPassword, getString(R.string.error_password_not_legal));
                        clearError(etNewPasswordAgain);
                        break;

                    case  ErrorCodes.UNAUTHORIZED:
                        setError(etOldPassword, getString(R.string.error_current_password_wrong));
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

        initListener();
    }

    private void initListener() {

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        etOldPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //this method is empty
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //this method is empty
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().isEmpty()){
                    setError(etOldPassword, getString(R.string.error_password_empty));
                } else {
                    clearError(etOldPassword);
                }

            }
        });


        etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //this method is empty
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //this method is empty
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().isEmpty()){
                    setError(etNewPassword, getString(R.string.input_error_enter_new_pass));
                } else {
                    clearError(etNewPassword);
                }
            }
        });


        etNewPasswordAgain.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //this method is empty
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //this method is empty
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().isEmpty()){
                    setError(etNewPasswordAgain, getString(R.string.input_error_password_mismatch));
                } else {
                    clearError(etNewPasswordAgain);
                }

            }
        });

        btChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptChangePassword();
            }
        });

        ivOldPassToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePasswordVisibility(ivOldPassToggle, etOldPassword);
            }
        });

        ivNewPassToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePasswordVisibility(ivNewPassToggle, etNewPassword);
            }
        });

        ivNewPassAgainToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePasswordVisibility(ivNewPassAgainToggle, etNewPasswordAgain);
            }
        });
    }

    private void changePasswordVisibility(ImageView imageView, EditText editText) {
        if(editText.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
            imageView.setImageResource(R.drawable.ic_visibility_off);
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else{
            imageView.setImageResource(R.drawable.ic_visibility);
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }


    private void initializeComponents(){
        btChangePassword = findViewById(R.id.btChangePassword);
        progressChangePassword = findViewById(R.id.progressChangePassword);
        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etNewPasswordAgain = findViewById(R.id.etNewPasswordAgain);
        cardFormChangePassword = findViewById(R.id.cardFormChangePassword);
        ivBack = findViewById(R.id.ivBack);
        ivOldPassToggle = findViewById(R.id.ivOldPassToggle);
        ivNewPassToggle = findViewById(R.id.ivNewPassToggle);
        ivNewPassAgainToggle = findViewById(R.id.ivNewPassAgainToggle);
    }

    private void attemptChangePassword(){
        // Show progress indicator.
        clearError(etOldPassword);
        clearError(etNewPassword);
        clearError(etNewPasswordAgain);

        // Get user inputs.
        String oldPass = etOldPassword.getText().toString();
        String newPass = etNewPassword.getText().toString();
        String newPassAgain = etNewPasswordAgain.getText().toString();

        // Check required fields.
        if (oldPass.isEmpty()){
            setError(etOldPassword, getString(R.string.input_error_enter_current_password));
        }
        if (newPass.isEmpty()){
            setError(etNewPassword, getString(R.string.input_error_enter_new_pass));
        }

        // Old pass and new pass are given, check if new password fields are matching.
        else if (!oldPass.isEmpty() && !newPass.equals(newPassAgain)) {
            setError(etNewPassword, getString(R.string.input_error_password_mismatch));
            setError(etNewPasswordAgain, getString(R.string.input_error_password_mismatch));
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

    private void setError(EditText editText, String errorMessage) {
        editText.setHint(errorMessage);
        editText.setBackground(getDrawable(R.drawable.et_error_background));
    }

    private void clearError(EditText editText) {
        editText.setBackground(getDrawable(R.drawable.et_background));
    }

    /**
     * Sets progress view visibility.
     * @param visible Set true when progress indicator needs to be shown.
     */
    private void setProgressVisibility(boolean visible) {
        if (visible) {
            progressChangePassword.setVisibility(View.VISIBLE);
            etOldPassword.setEnabled(false);
            etNewPassword.setEnabled(false);
            etNewPasswordAgain.setEnabled(false);
            btChangePassword.setEnabled(false);
        } else {
            progressChangePassword.setVisibility(View.INVISIBLE);
            etOldPassword.setEnabled(true);
            etNewPassword.setEnabled(true);
            etNewPasswordAgain.setEnabled(true);
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

    public void onBackPressed(View view) {
        onBackPressed();
    }
}
