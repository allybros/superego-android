package com.allybros.superego.activity;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.R;
import com.allybros.superego.api.RegisterTask;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.ErrorCodes;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {


    TextInputEditText etRegisterUsername,etRegisterMail,etRegisterPassword;
    TextInputLayout username_text_input_register,email_text_input_register,password_text_input_register;
    Button btSignUp;
    TextView tvAggrementRegister;
    CheckBox checkBoxAggrement;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        btSignUp=(MaterialButton) findViewById(R.id.btSignUp);
        etRegisterPassword=(TextInputEditText)findViewById(R.id.etRegisterPassword);
        etRegisterMail=(TextInputEditText)findViewById(R.id.etRegisterMail);
        etRegisterUsername=(TextInputEditText)findViewById(R.id.etRegisterUsername);
        //TODO:Policy için siteye linke yönlendirme olması lazım o eksik
        tvAggrementRegister=(TextView) findViewById(R.id.tvAggrementRegister);
        username_text_input_register=(TextInputLayout) findViewById(R.id.username_text_input_register);
        email_text_input_register=(TextInputLayout) findViewById(R.id.email_text_input_register);
        password_text_input_register=(TextInputLayout) findViewById(R.id.password_text_input);
        checkBoxAggrement=(CheckBox) findViewById(R.id.checkboxAggrement);


        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(ConstantValues.ACTION_REGISTER));

        tvAggrementRegister.setMovementMethod(LinkMovementMethod.getInstance());

        checkBoxAggrement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(checkBoxAggrement.isChecked()){
                    btSignUp.setBackgroundTintList(getApplicationContext().getColorStateList(R.color.buttonLoginScreen));
                }else{
                    btSignUp.setBackgroundTintList(getApplicationContext().getColorStateList(R.color.grey));
                }
            }
        });

        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username_text_input_register.setErrorEnabled(false);
                email_text_input_register.setErrorEnabled(false);
                password_text_input_register.setErrorEnabled(false);
                if(checkBoxAggrement.isChecked()){
                    if(!etRegisterUsername.getText().toString().equals("") && !etRegisterMail.getText().toString().equals("") && !etRegisterPassword.getText().toString().equals("")) {
                        Log.d("Register request send","Register request send");
                        RegisterTask.registerTask(getApplicationContext(), etRegisterUsername.getText().toString(), etRegisterMail.getText().toString(), etRegisterPassword.getText().toString(), true);
                    } else{
                        if(etRegisterUsername.getText().toString().equals("")) {
                            username_text_input_register.setError("Kullanıcı adı doldurulmalıdır");
                        }
                        if(etRegisterPassword.getText().toString().equals("")) {
                            password_text_input_register.setError("Parola alanı doldurulmalıdır");
                        }
                        if(etRegisterMail.getText().toString().equals("")) {
                            email_text_input_register.setError("E posta alanı doldurulmalıdır");
                        }
                    }
                }else{
                    if(etRegisterUsername.getText().toString().equals("")) {
                        username_text_input_register.setError("Kullanıcı adı doldurulmalıdır");
                    }
                    if(etRegisterPassword.getText().toString().equals("")) {
                        password_text_input_register.setError("Parola alanı doldurulmalıdır");
                    }
                    if(etRegisterMail.getText().toString().equals("")) {
                        email_text_input_register.setError("E posta alanı doldurulmalıdır");
                    }
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(findViewById(R.id.layoutAggrement));
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra("status",0);
            Log.d("receiver", "Got message: " + status);
            switch (status) {
                case ErrorCodes.SYSFAIL:
                    username_text_input_register.setError("Bir şeyler yanlış gitti");
                    email_text_input_register.setError("Lütfen tekrar deneyin.");
                    password_text_input_register.setError("Bir sonrakinde başaracağım söz");
                    break;

                case ErrorCodes.USERNAME_NOT_LEGAL:
                    username_text_input_register.setError("Kullanıcı adı kurallara uymamaktadır.");
                    break;

                case ErrorCodes.USERNAME_ALREADY_EXIST:
                    username_text_input_register.setError("Kullanıcı adı başkası tarafından alınmış");
                    break;

                case ErrorCodes.EMAIL_ALREADY_EXIST:
                    email_text_input_register.setError("Bu e posta adresi zaten kayıtlı");
                    break;

                case ErrorCodes.EMAIL_NOT_LEGAL:
                    email_text_input_register.setError("E posta kurallara uymamaktadır.");
                    break;

                case ErrorCodes.PASSWORD_NOT_LEGAL:
                    password_text_input_register.setError("Parola kurallara uymamaktadır.");
                    break;
            }

        }
    };
}