package com.allybros.superego.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.R;
import com.allybros.superego.api.ChangeInfoTask;
import com.allybros.superego.api.ImageChangeTask;
import com.allybros.superego.api.LogoutTask;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.ErrorCodes;
import com.allybros.superego.unit.User;
import com.allybros.superego.util.CircledNetworkImageView;
import com.allybros.superego.util.CustomVolleyRequestQueue;
import com.allybros.superego.util.HelperMethods;
import com.android.volley.toolbox.ImageLoader;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;
import com.vlad1m1r.lemniscate.funny.HeartProgressView;

import java.io.IOException;

import static com.allybros.superego.util.HelperMethods.imageToString;

public class SettingsActivity extends AppCompatActivity {
    //TODO:USER_INFORMATION_PREF variable must move in unit/api
    final String USER_INFORMATION_PREF="USER_INFORMATION_PREF";
    TextInputEditText username,email,information;
    TextInputLayout etUsername_text_input,etEmail_text_input,etInformation_text_input;
    MaterialButton btChangePhoto, btLogout;   //TODO: Fotoğraf değiştirme işlemi yapılacak.
    private SlidrInterface slidr;
    CircledNetworkImageView settingsImage;
    HeartProgressView heartAnimation;
    private ActionBar toolbar;

    public static Uri newImagePath=null;

    private final int IMG_REQUEST=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        toolbar = getSupportActionBar();

        btChangePhoto=(MaterialButton) findViewById(R.id.btChangePhoto);
        btLogout= (MaterialButton) findViewById(R.id.btLogout);
        username=(TextInputEditText) findViewById(R.id.etUsername);
        email=(TextInputEditText) findViewById(R.id.etEmail);
        information=(TextInputEditText) findViewById(R.id.etInformation);
        etUsername_text_input=(TextInputLayout) findViewById(R.id.etUsername_text_input);
        etEmail_text_input=(TextInputLayout) findViewById(R.id.etEmail_text_input);
        etInformation_text_input=(TextInputLayout) findViewById(R.id.etInformation_text_input);
        settingsImage = (CircledNetworkImageView) findViewById(R.id.imageSettings);
        heartAnimation = (HeartProgressView) findViewById(R.id.hearthAnimation);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(updateInformationReceiver, new IntentFilter(ConstantValues.getActionUpdateInformation()));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(updateImageReceiver, new IntentFilter(ConstantValues.getActionUpdateImage()));

        email.setText(User.getEmail());
        username.setText(User.getUsername());
        information.setText(User.getUserBio());

        if(User.getAvatar()!=null){
            Log.d("OnCreate-1","Run");
            settingsImage.setImageBitmap(User.getAvatar());
        }else{
            Log.d("OnCreate-2","Run");
            HelperMethods.imageLoadFromUrl(getApplicationContext(), ConstantValues.getAvatarUrl()+User.getImage(),settingsImage);
        }

        String URL= ConstantValues.getAvatarUrl()+User.getImage();
        ImageLoader mImageLoader;
        mImageLoader = CustomVolleyRequestQueue.getInstance(getApplicationContext()).getImageLoader();
        mImageLoader.get(URL, ImageLoader.getImageListener(settingsImage, R.drawable.simple_profile_photo, android.R.drawable.ic_dialog_alert));
        settingsImage.setImageUrl(URL, mImageLoader);


        toolbar.setTitle("Settings");

        slidr= Slidr.attach(this);
        slidr.unlock();
        btChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String session_token;
                SharedPreferences pref = getApplicationContext().getSharedPreferences(USER_INFORMATION_PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                session_token=pref.getString("session_token","");
                editor.clear();
                editor.commit();
                LogoutTask.logoutTask(getApplicationContext(),session_token);

                
                LoginActivity.mGoogleSignInClient.signOut();

                Intent intent=new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getApplicationContext().startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.aciton_save:
                etEmail_text_input.setErrorEnabled(false);
                etInformation_text_input.setErrorEnabled(false);
                etUsername_text_input.setErrorEnabled(false);
                if(username.getText().toString().isEmpty()) etUsername_text_input.setError("Lütfen değer giriniz");
                if(email.getText().toString().isEmpty()) etEmail_text_input.setError("Lütfen değer giriniz");
                if(information.getText().toString().isEmpty()) etInformation_text_input.setError("Lütfen değer giriniz");

                if(!username.getText().toString().isEmpty() && !email.getText().toString().isEmpty() && !information.getText().toString().isEmpty()){
                    ChangeInfoTask.changeInfoTask(getApplicationContext(),username.getText().toString(),email.getText().toString(),information.getText().toString(),SplashActivity.session_token);
                }
              break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void selectImage(){
       Intent intent = new Intent();
       intent.setType("image/*");
       intent.setAction(Intent.ACTION_GET_CONTENT);
       startActivityForResult(intent,IMG_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null){
            newImagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),newImagePath);
                User.setAvatar(bitmap);
                settingsImage.setImageBitmap(User.getAvatar());
                settingsImage.setVisibility(View.INVISIBLE);
                heartAnimation.setVisibility(View.VISIBLE);
                ImageChangeTask.imageChangeTask(imageToString(User.getAvatar()),getApplicationContext());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private BroadcastReceiver updateInformationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int status = intent.getIntExtra("status",0);
            Log.d("receiver", "Got message: " + status);

            //Check status
            switch (status){

                case ErrorCodes.SESSION_EXPIRED:

                     new MaterialAlertDialogBuilder(SettingsActivity.this)
                            .setTitle("insightof.me")
                            .setMessage(getApplicationContext().getString(R.string.session_expired))
                            .setPositiveButton(getString(R.string.okey), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    getApplicationContext().startActivity(intent);
                                }
                            })
                            .show();
                    break;

                case ErrorCodes.USERNAME_NOT_LEGAL:

                    new MaterialAlertDialogBuilder(SettingsActivity.this)
                            .setTitle("insightof.me")
                            .setMessage(getApplicationContext().getString(R.string.usernameNotLegal))
                            .setPositiveButton(getString(R.string.okey), null)
                            .show();
                    break;

                case ErrorCodes.USERNAME_ALREADY_EXIST:

                    new MaterialAlertDialogBuilder(SettingsActivity.this)
                            .setTitle("insightof.me")
                            .setMessage(getApplicationContext().getString(R.string.usernameAlreadyExist))
                            .setPositiveButton(getString(R.string.okey), null)
                            .show();
                    break;

                case ErrorCodes.EMAIL_NOT_LEGAL:

                    new MaterialAlertDialogBuilder(SettingsActivity.this)
                            .setTitle("insightof.me")
                            .setMessage(getApplicationContext().getString(R.string.emailNotLegal))
                            .setPositiveButton(getString(R.string.okey), null)
                            .show();
                    break;

                case ErrorCodes.EMAIL_ALREADY_EXIST:

                    new MaterialAlertDialogBuilder(SettingsActivity.this)
                            .setTitle("insightof.me")
                            .setMessage(getApplicationContext().getString(R.string.emailAlreadyExist))
                            .setPositiveButton(getString(R.string.okey), null)
                            .show();
                    break;

                case ErrorCodes.SUCCESS:

                    new MaterialAlertDialogBuilder(SettingsActivity.this)
                            .setTitle("insightof.me")
                            .setMessage(getApplicationContext().getString(R.string.processComplated))
                            .setPositiveButton(getString(R.string.okey), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d("Success","İşlem tamam patron");
                                }
                            }).show();
                    break;

                case ErrorCodes.SYSFAIL:

                    new MaterialAlertDialogBuilder(SettingsActivity.this)
                            .setTitle("insightof.me")
                            .setMessage(getApplicationContext().getString(R.string.connection_error))
                            .setPositiveButton(getString(R.string.okey), null)
                            .show();
                    break;

            }
        }
    };

    private BroadcastReceiver updateImageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra("status",0);
            Log.d("receiver", "Got message: " + status);

            settingsImage.setVisibility(View.VISIBLE);
            heartAnimation.setVisibility(View.GONE);
            //Check status
            switch (status){
                case ErrorCodes.SUCCESS:

                    new MaterialAlertDialogBuilder(SettingsActivity.this)
                            .setTitle("insightof.me")
                            .setMessage(getApplicationContext().getString(R.string.processComplated))
                            .setPositiveButton(getString(R.string.okey), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                    break;

                case ErrorCodes.SYSFAIL:

                    new MaterialAlertDialogBuilder(SettingsActivity.this)
                            .setTitle("insightof.me")
                            .setMessage(getApplicationContext().getString(R.string.connection_error))
                            .setPositiveButton(getString(R.string.okey), null)
                            .show();
                    break;

                case ErrorCodes.INVALID_FILE_EXTENSION:

                    new MaterialAlertDialogBuilder(SettingsActivity.this)
                            .setTitle("insightof.me")
                            .setMessage(getApplicationContext().getString(R.string.invalid_file_extension)+" "+getApplicationContext().getString(R.string.connection_error)+status)
                            .setPositiveButton(getString(R.string.okey), null)
                            .show();
                    break;

                case ErrorCodes.INVALID_FILE_TYPE:

                    new MaterialAlertDialogBuilder(SettingsActivity.this)
                            .setTitle("insightof.me")
                            .setMessage(getApplicationContext().getString(R.string.invalid_file_type)+" "+getApplicationContext().getString(R.string.connection_error)+status)
                            .setPositiveButton(getString(R.string.okey), null)
                            .show();
                    break;

                case ErrorCodes.INVALID_FILE_SIZE:

                    new MaterialAlertDialogBuilder(SettingsActivity.this)
                            .setTitle("insightof.me")
                            .setMessage(getApplicationContext().getString(R.string.invalid_file_size)+" "+getApplicationContext().getString(R.string.connection_error)+status)
                            .setPositiveButton(getString(R.string.okey), null)
                            .show();
                    break;

                case ErrorCodes.FILE_WRITE_ERROR:

                    new MaterialAlertDialogBuilder(SettingsActivity.this)
                            .setTitle("insightof.me")
                            .setMessage(getApplicationContext().getString(R.string.connection_error)+" "+getApplicationContext().getString(R.string.connection_error)+status)
                            .setPositiveButton(getString(R.string.okey), null)
                            .show();
                    break;

            }
        }
    };


    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(SettingsActivity.this).unregisterReceiver(updateImageReceiver);

        LocalBroadcastManager.getInstance(SettingsActivity.this).unregisterReceiver(updateInformationReceiver);
        Log.d("SettingsDestroy","RUN");
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(User.getAvatar()!=null){
            settingsImage.setImageBitmap(User.getAvatar());
        }else{
            HelperMethods.imageLoadFromUrl(getApplicationContext(), ConstantValues.getAvatarUrl()+User.getImage(),settingsImage);
        }
        Log.d("SettingsResume","RUN");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("SettingsPause","RUN");

    }
}
