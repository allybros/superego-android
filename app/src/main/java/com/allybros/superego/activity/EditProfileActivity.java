package com.allybros.superego.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.drm.DrmStore;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.R;
import com.allybros.superego.api.ChangeInfoTask;
import com.allybros.superego.api.ImageChangeTask;
import com.allybros.superego.ui.CircledNetworkImageView;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.ErrorCodes;
import com.allybros.superego.util.HelperMethods;
import com.allybros.superego.util.RequestForGetImage;
import com.allybros.superego.util.SessionManager;
import com.android.volley.toolbox.ImageLoader;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;

import java.io.IOException;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

import static com.allybros.superego.util.HelperMethods.imageToString;

public class EditProfileActivity extends AppCompatActivity {
    //TODO: Why not private?
    private MaterialProgressBar progressEditProfile;
    private ConstraintLayout cardFormEditProfile;
    TextInputEditText username,email,information;
    TextInputLayout etUsername_text_input,etEmail_text_input,etInformation_text_input;
    private SlidrInterface slidr;
    Button btChangePhoto;
    CircledNetworkImageView settingsImage;
    ConstraintLayout editProfileLayout;
    public static Uri newImagePath=null;

    private Button btnSaveProfile;

    private final int IMG_REQUEST=1; //TODO: Add description. This is not a usual thing.
    //TODO: Add free space before each method
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //TODO: Redundant casting. Place spaces when using '=' Ex: a = 5 not a= 5
        //TODO: Building view hierarchy here is a valid operation. You can keep them here.
        editProfileLayout = findViewById(R.id.editProfileLayout);
        progressEditProfile = findViewById(R.id.progressEditProfile);
        cardFormEditProfile =findViewById(R.id.cardFormEditProfile);
        btChangePhoto= findViewById(R.id.btChangePhoto);
        username= findViewById(R.id.etUsername);
        email= findViewById(R.id.etEmail);
        information= findViewById(R.id.etInformation);
        etUsername_text_input= findViewById(R.id.textInputUsername);
        etEmail_text_input= findViewById(R.id.textInputEmail);
        etInformation_text_input= findViewById(R.id.etInformation_text_input);
        settingsImage = findViewById(R.id.imageSettings);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(updateInformationReceiver, new IntentFilter(ConstantValues.ACTION_UPDATE_INFORMATION));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(updateImageReceiver, new IntentFilter(ConstantValues.ACTION_UPDATE_IMAGE));

        //TODO: Initializing Buttons, TextFields, AvatarChooser tasks are separated and there are no shared dependency.
        // There is no need to keep them together. Separate them to subroutines, in java methods, for making easier to modify.
        btChangePhoto.bringToFront();
        btChangePhoto.invalidate();

        email.setText(SessionManager.getInstance().getUser().getEmail());
        username.setText(SessionManager.getInstance().getUser().getUsername());
        information.setText(SessionManager.getInstance().getUser().getUserBio());

        if(SessionManager.getInstance().getUser().getAvatar()!=null){
            Log.d("OnCreate-1","Run");
            settingsImage.setImageBitmap(SessionManager.getInstance().getUser().getAvatar());
        }else{
            Log.d("OnCreate-2","Run");
            HelperMethods.imageLoadFromUrl(getApplicationContext(), ConstantValues.AVATAR_URL+SessionManager.getInstance().getUser().getImage(),settingsImage);
        }

        String URL= ConstantValues.AVATAR_URL+SessionManager.getInstance().getUser().getImage();
        ImageLoader mImageLoader;
        mImageLoader = RequestForGetImage.getInstance(getApplicationContext()).getImageLoader();
        //TODO: Use default avatar
        mImageLoader.get(URL, ImageLoader.getImageListener(settingsImage, R.drawable.simple_profile_photo, android.R.drawable.ic_dialog_alert));
        settingsImage.setImageUrl(URL, mImageLoader);

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

    //TODO: What about moving assignment to onCreate method?
    // Do not instantiate not primitive types outside in a method. It is confusing.
    // However there is nothing wrong about instantiating empty collections such as ArrayList
    private BroadcastReceiver updateInformationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int status = intent.getIntExtra("status",0);
            Log.d("receiver", "Got message: " + status);
            setProgressVisibility(false);

            //Check status
            switch (status){

                case ErrorCodes.SESSION_EXPIRED:

                     new MaterialAlertDialogBuilder(EditProfileActivity.this)
                            .setTitle("insightof.me")
                            .setMessage(getApplicationContext().getString(R.string.session_expired))
                            .setPositiveButton(getString(R.string.action_ok), new DialogInterface.OnClickListener() {
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

                    new MaterialAlertDialogBuilder(EditProfileActivity.this)
                            .setTitle("insightof.me")
                            .setMessage(getApplicationContext().getString(R.string.usernameNotLegal))
                            .setPositiveButton(getString(R.string.action_ok), null)
                            .show();
                    break;

                case ErrorCodes.USERNAME_ALREADY_EXIST:

                    new MaterialAlertDialogBuilder(EditProfileActivity.this)
                            .setTitle("insightof.me")
                            .setMessage(getApplicationContext().getString(R.string.usernameAlreadyExist))
                            .setPositiveButton(getString(R.string.action_ok), null)
                            .show();
                    break;

                case ErrorCodes.EMAIL_NOT_LEGAL:

                    new MaterialAlertDialogBuilder(EditProfileActivity.this)
                            .setTitle("insightof.me")
                            .setMessage(getApplicationContext().getString(R.string.emailNotLegal))
                            .setPositiveButton(getString(R.string.action_ok), null)
                            .show();
                    break;

                case ErrorCodes.EMAIL_ALREADY_EXIST:

                    new MaterialAlertDialogBuilder(EditProfileActivity.this)
                            .setTitle("insightof.me")
                            .setMessage(getApplicationContext().getString(R.string.emailAlreadyExist))
                            .setPositiveButton(getString(R.string.action_ok), null)
                            .show();
                    break;

                case ErrorCodes.SUCCESS:

                    new MaterialAlertDialogBuilder(EditProfileActivity.this)
                            .setTitle("insightof.me")
                            .setMessage(getApplicationContext().getString(R.string.processComplated))
                            .setPositiveButton(getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d("Success","İşlem tamam patron");
                                }
                            }).show();
                    break;

                case ErrorCodes.SYSFAIL:

                    new MaterialAlertDialogBuilder(EditProfileActivity.this)
                            .setTitle("insightof.me")
                            .setMessage(getApplicationContext().getString(R.string.connection_error))
                            .setPositiveButton(getString(R.string.action_ok), null)
                            .show();
                    break;

            }
        }
    };

    //TODO: Clear redundant blank spaces in order to make reading easier. Just add 1 blank space after break statement.
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

                    new MaterialAlertDialogBuilder(EditProfileActivity.this)
                            .setTitle("insightof.me")
                            .setMessage(getApplicationContext().getString(R.string.processComplated))
                            .setPositiveButton(getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                    break;

                case ErrorCodes.SYSFAIL:

                    new MaterialAlertDialogBuilder(EditProfileActivity.this)
                            .setTitle("insightof.me")
                            .setMessage(getApplicationContext().getString(R.string.connection_error))
                            .setPositiveButton(getString(R.string.action_ok), null)
                            .show();
                    break;

                case ErrorCodes.INVALID_FILE_EXTENSION:

                    new MaterialAlertDialogBuilder(EditProfileActivity.this)
                            .setTitle("insightof.me")
                            .setMessage(getApplicationContext().getString(R.string.invalid_file_extension)+" "+getApplicationContext().getString(R.string.connection_error)+status)
                            .setPositiveButton(getString(R.string.action_ok), null)
                            .show();
                    break;

                case ErrorCodes.INVALID_FILE_TYPE:

                    new MaterialAlertDialogBuilder(EditProfileActivity.this)
                            .setTitle("insightof.me")
                            .setMessage(getApplicationContext().getString(R.string.invalid_file_type)+" "+getApplicationContext().getString(R.string.connection_error)+status)
                            .setPositiveButton(getString(R.string.action_ok), null)
                            .show();
                    break;

                case ErrorCodes.INVALID_FILE_SIZE:

                    new MaterialAlertDialogBuilder(EditProfileActivity.this)
                            .setTitle("insightof.me")
                            .setMessage(getApplicationContext().getString(R.string.invalid_file_size)+" "+getApplicationContext().getString(R.string.connection_error)+status)
                            .setPositiveButton(getString(R.string.action_ok), null)
                            .show();
                    break;

                case ErrorCodes.FILE_WRITE_ERROR:

                    new MaterialAlertDialogBuilder(EditProfileActivity.this)
                            .setTitle("insightof.me")
                            .setMessage(getApplicationContext().getString(R.string.connection_error)+" "+getApplicationContext().getString(R.string.connection_error)+status)
                            .setPositiveButton(getString(R.string.action_ok), null)
                            .show();
                    break;

            }
        }
    };


    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(EditProfileActivity.this).unregisterReceiver(updateImageReceiver);

        LocalBroadcastManager.getInstance(EditProfileActivity.this).unregisterReceiver(updateInformationReceiver);
        Log.d("SettingsDestroy","RUN");
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();

        if(SessionManager.getInstance().getUser().getAvatar()!=null){
            settingsImage.setImageBitmap(SessionManager.getInstance().getUser().getAvatar());
        }else{
            HelperMethods.imageLoadFromUrl(getApplicationContext(), ConstantValues.AVATAR_URL+SessionManager.getInstance().getUser().getImage(),settingsImage);
        }
        Log.d("SettingsResume","RUN");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("SettingsPause","RUN");

    }
}
