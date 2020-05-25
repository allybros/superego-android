package com.allybros.superego.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.R;
import com.allybros.superego.api.ChangeInfoTask;
import com.allybros.superego.api.ImageChangeTask;
import com.allybros.superego.ui.CircledNetworkImageView;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.ErrorCodes;
import com.allybros.superego.util.RequestForGetImage;
import com.allybros.superego.util.SessionManager;
import com.android.volley.toolbox.ImageLoader;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;

import java.io.IOException;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

import static com.allybros.superego.util.HelperMethods.imageToString;

public class EditProfileActivity extends AppCompatActivity {
    private MaterialProgressBar progressEditProfile;
    private ConstraintLayout cardFormEditProfile;
    private TextInputEditText username,email,information;
    private TextInputLayout etUsername_text_input,etEmail_text_input,etInformation_text_input;
    private SlidrInterface slidr;
    private Button btChangePhoto;
    private CircledNetworkImageView settingsImage;
    private ConstraintLayout editProfileLayout;
    public static Uri newImagePath=null;

    private Button btnSaveProfile;

    private final int IMG_REQUEST=1;    //Needs for image selection from local storage


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initializeComponents();
        setupReceivers();
        setupUi();
    }

    public void initializeComponents(){
        editProfileLayout = findViewById(R.id.editProfileLayout);
        progressEditProfile = findViewById(R.id.progressEditProfile);
        cardFormEditProfile = findViewById(R.id.cardFormEditProfile);
        btChangePhoto = findViewById(R.id.btChangePhoto);
        username = findViewById(R.id.etUsername);
        email = findViewById(R.id.etEmail);
        information = findViewById(R.id.etInformation);
        etUsername_text_input = findViewById(R.id.textInputUsername);
        etEmail_text_input = findViewById(R.id.textInputEmail);
        etInformation_text_input = findViewById(R.id.etInformation_text_input);
        settingsImage = findViewById(R.id.imageSettings);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
    }

    public void setupReceivers(){
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(updateInformationReceiver, new IntentFilter(ConstantValues.ACTION_UPDATE_INFORMATION));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(updateImageReceiver, new IntentFilter(ConstantValues.ACTION_UPDATE_IMAGE));
    }

    public void setupUi(){

        btChangePhoto.bringToFront();
        btChangePhoto.invalidate();

        //Set view components
        email.setText(SessionManager.getInstance().getUser().getEmail());
        username.setText(SessionManager.getInstance().getUser().getUsername());
        information.setText(SessionManager.getInstance().getUser().getUserBio());

        //Load image
        String URL= ConstantValues.AVATAR_URL+SessionManager.getInstance().getUser().getImage();
        ImageLoader mImageLoader;
        mImageLoader = RequestForGetImage.getInstance(getApplicationContext()).getImageLoader();
        mImageLoader.get(URL, ImageLoader.getImageListener(settingsImage, R.drawable.img_avatar, android.R.drawable.ic_dialog_alert));
        settingsImage.setImageUrl(URL, mImageLoader);

        //Require for slide behaviour
        slidr= Slidr.attach(this);
        slidr.unlock();

        btChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProfile();
            }
        });
    }

    //Opens intent that provide selecting image from local storage
    private void selectImage(){
       Intent intent = new Intent();
       intent.setType("image/*");
       intent.setAction(Intent.ACTION_GET_CONTENT);
       startActivityForResult(intent,IMG_REQUEST);
    }

    //Provides that cacth the results that come back from selectImage() function
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null){
            newImagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),newImagePath);
                int fileSize = bitmap.getByteCount();
                Log.d("SIZE:  ",""+ bitmap.getByteCount());
                if(fileSize < ConstantValues.MAX_FILE_SIZE){
                    SessionManager.getInstance().getUser().setAvatar(bitmap);
                    settingsImage.setImageBitmap(SessionManager.getInstance().getUser().getAvatar());
                    settingsImage.setVisibility(View.INVISIBLE);
                    setProgressVisibility(true);
                    ImageChangeTask.imageChangeTask(imageToString(SessionManager.getInstance().getUser().getAvatar()),getApplicationContext());
                }else{
                    Snackbar.make(editProfileLayout,"Seçilen dosya boyutu çok büyük",Snackbar.LENGTH_LONG).show();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Shows material progress bar and disables form.
     * @param visible set true when progress view needs to be shown.
     */
    private void setProgressVisibility(boolean visible){
        if (visible) {
            progressEditProfile.setVisibility(View.VISIBLE);
            cardFormEditProfile.setAlpha(0.5f);
            btnSaveProfile.setEnabled(false);
        } else {
            progressEditProfile.setVisibility(View.INVISIBLE);
            cardFormEditProfile.setAlpha(1f);
            btnSaveProfile.setEnabled(true);;
        }
    }

    /**
     * Validate user information and send request to the API
     */
    private void saveProfile(){
        // Moved menu item option here
        etEmail_text_input.setErrorEnabled(false);
        etInformation_text_input.setErrorEnabled(false);
        etUsername_text_input.setErrorEnabled(false);

        if(username.getText().toString().isEmpty()) etUsername_text_input.setError(getString(R.string.error_field_required));
        if(email.getText().toString().isEmpty()) etEmail_text_input.setError(getString(R.string.error_field_required));

        if(!username.getText().toString().isEmpty()
                && !email.getText().toString().isEmpty() ){
            setProgressVisibility(true);
            ChangeInfoTask.changeInfoTask(getApplicationContext(),username.getText().toString(),email.getText().toString(),information.getText().toString(), SessionManager.getInstance().getSessionToken());
        }
    }

    /**
     * Catches broadcasts of api/ChangeInfoTask class
     */
    private BroadcastReceiver updateInformationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int status = intent.getIntExtra("status",0);
            Log.d("receiver", "Got message: " + status);
            setProgressVisibility(false);

            //Check status
            switch (status){

                case ErrorCodes.SESSION_EXPIRED:
                    Snackbar.make(editProfileLayout,getApplicationContext().getString(R.string.session_expired),Snackbar.LENGTH_LONG).show();
                    break;

                case ErrorCodes.USERNAME_NOT_LEGAL:
                    Snackbar.make(editProfileLayout,getApplicationContext().getString(R.string.usernameNotLegal),Snackbar.LENGTH_LONG).show();
                    break;

                case ErrorCodes.USERNAME_ALREADY_EXIST:
                    Snackbar.make(editProfileLayout,getApplicationContext().getString(R.string.usernameAlreadyExist),Snackbar.LENGTH_LONG).show();
                    break;

                case ErrorCodes.EMAIL_NOT_LEGAL:
                    Snackbar.make(editProfileLayout,getApplicationContext().getString(R.string.emailNotLegal),Snackbar.LENGTH_LONG).show();
                    break;

                case ErrorCodes.EMAIL_ALREADY_EXIST:
                    Snackbar.make(editProfileLayout,getApplicationContext().getString(R.string.emailAlreadyExist),Snackbar.LENGTH_LONG).show();
                    break;

                case ErrorCodes.SUCCESS:
                    Snackbar.make(editProfileLayout,getApplicationContext().getString(R.string.processComplated),Snackbar.LENGTH_LONG).show();
                    break;

                case ErrorCodes.SYSFAIL:
                    Snackbar.make(editProfileLayout,getApplicationContext().getString(R.string.connection_error),Snackbar.LENGTH_LONG).show();
                    break;

            }
        }
    };
    /**
     * Catches broadcasts of api/ImageChangeTask class
     */
    private BroadcastReceiver updateImageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra("status",0);
            Log.d("receiver", "Got message: " + status);
            setProgressVisibility(false);
            settingsImage.setVisibility(View.VISIBLE);
            //Check status
            switch (status){
                case ErrorCodes.SUCCESS:
                    Snackbar.make(editProfileLayout,getApplicationContext().getString(R.string.processComplated),Snackbar.LENGTH_LONG).show();
                    break;

                case ErrorCodes.SYSFAIL:
                    Snackbar.make(editProfileLayout,getApplicationContext().getString(R.string.connection_error),Snackbar.LENGTH_LONG).show();
                    break;

                case ErrorCodes.INVALID_FILE_EXTENSION:
                    Snackbar.make(editProfileLayout,getApplicationContext().getString(R.string.invalid_file_extension),Snackbar.LENGTH_LONG).show();
                    break;

                case ErrorCodes.INVALID_FILE_TYPE:
                    Snackbar.make(editProfileLayout,getApplicationContext().getString(R.string.invalid_file_type),Snackbar.LENGTH_LONG).show();
                    break;

                case ErrorCodes.INVALID_FILE_SIZE:
                    Snackbar.make(editProfileLayout,getApplicationContext().getString(R.string.invalid_file_size),Snackbar.LENGTH_LONG).show();
                    break;

                case ErrorCodes.FILE_WRITE_ERROR:
                    Snackbar.make(editProfileLayout,getApplicationContext().getString(R.string.connection_error),Snackbar.LENGTH_LONG).show();
                    break;
            }
        }
    };
    @Override
    public void onDestroy() {
        //Delete receivers
        LocalBroadcastManager.getInstance(EditProfileActivity.this).unregisterReceiver(updateImageReceiver);
        LocalBroadcastManager.getInstance(EditProfileActivity.this).unregisterReceiver(updateInformationReceiver);
        Log.d("SettingsDestroy","RUN");
        super.onDestroy();
    }
}
