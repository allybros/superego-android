package com.allybros.superego.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.R;
import com.allybros.superego.api.ApiTask;
import com.allybros.superego.api.ChangeInfoTask;
import com.allybros.superego.api.ImageChangeTask;
import com.allybros.superego.api.response.ApiStatusResponse;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.ErrorCodes;
import com.allybros.superego.util.InputMethodWatcher;
import com.allybros.superego.util.SessionManager;
import com.allybros.superego.widget.SegoEditText;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

import static com.allybros.superego.util.HelperMethods.imageToString;

public class EditProfileActivity extends AppCompatActivity {
    private MaterialProgressBar progressEditProfile;
    private SegoEditText etUsername;
    private SegoEditText etEmail;
    private EditText etBio;
    private ImageView ivChangeAvatar;
    private CircleImageView ivSettings;
    private ConstraintLayout editProfileLayout;
    public static Uri newImagePath = null;
    private BroadcastReceiver updateImageReceiver;
    private Button btnSaveProfile;
    private static final int IMG_REQUEST = 1; //Needs for image selection from local storage

    private InputMethodWatcher inputMethodWatcher;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initializeComponents();
        setupReceivers();
        setupUi();
        setupTextWatchers();
        initInputMethodWatcher();
    }

    private void initializeComponents() {
        editProfileLayout = findViewById(R.id.editProfileLayout);
        progressEditProfile = findViewById(R.id.progressEditProfile);
        findViewById(R.id.cardFormEditProfile);
        ivChangeAvatar = findViewById(R.id.ivChangeAvatar);
        findViewById(R.id.ivBack);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etBio = findViewById(R.id.etInformation);
        ivSettings = findViewById(R.id.ivUserAvatarEditProfile);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
    }

    private void setupReceivers() {
        /**
         * Catches broadcasts of api/ImageChangeTask class
         */
        updateImageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra("status", 0);
                Log.d("receiver", "Got message: " + status);
                setProgressVisibility(false);
                ivSettings.setVisibility(View.VISIBLE);
                //Check status
                switch (status) {
                    case ErrorCodes.SUCCESS:
                        Snackbar.make(editProfileLayout, getApplicationContext().getString(R.string.message_process_succeed), Snackbar.LENGTH_LONG).show();
                        break;

                    case ErrorCodes.SYSFAIL:
                    case ErrorCodes.FILE_WRITE_ERROR:
                        Snackbar.make(editProfileLayout, getApplicationContext().getString(R.string.error_no_connection), Snackbar.LENGTH_LONG).show();
                        break;

                    case ErrorCodes.INVALID_FILE_EXTENSION:
                    case ErrorCodes.INVALID_FILE_TYPE:
                        Snackbar.make(editProfileLayout, getApplicationContext().getString(R.string.error_invalid_file_type), Snackbar.LENGTH_LONG).show();
                        break;

                    case ErrorCodes.INVALID_FILE_SIZE:
                        Snackbar.make(editProfileLayout, getApplicationContext().getString(R.string.error_invalid_file_size), Snackbar.LENGTH_LONG).show();
                        break;

                    default:
                        break;
                }
            }
        };
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(updateImageReceiver, new IntentFilter(ConstantValues.ACTION_UPDATE_IMAGE));
    }

    private void setupUi() {

        //Set view components
        etEmail.setText(SessionManager.getInstance().getUser().getEmail());
        etUsername.setText(SessionManager.getInstance().getUser().getUsername());
        etBio.setText(SessionManager.getInstance().getUser().getUserBio());


        // Check internet connection
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        //Load image
        String URL = SessionManager.getInstance().getUser().getImage();
        Picasso.get().load(URL).error(R.drawable.default_avatar).into(ivSettings);

        if (!isConnected)
            Snackbar.make(editProfileLayout, R.string.error_no_connection, BaseTransientBottomBar.LENGTH_LONG).show();

        ivChangeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check internet connection
                ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                if (isConnected) {
                    selectImage();
                } else {
                    Log.d("CONNECTION", String.valueOf(isConnected));
                    Snackbar.make(editProfileLayout, R.string.error_no_connection, BaseTransientBottomBar.LENGTH_LONG).show();
                }
            }
        });

        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputMethodWatcher.isKeyboardShown()) {
                    Log.d("Page changed", "Hide soft keyboard");
                    inputMethodWatcher.hideSoftKeyboard();
                }

                // Check internet connection
                ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                if (isConnected) {
                    saveProfile();
                } else {
                    Log.d("CONNECTION", String.valueOf(isConnected));
                    Snackbar.make(editProfileLayout, R.string.error_no_connection, BaseTransientBottomBar.LENGTH_LONG).show();
                }
            }
        });
    }


    private void setupTextWatchers() {
        etUsername.addTextChangedListener(new TextWatcher() {
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
                if (s.toString().isEmpty()) {
                    etUsername.setError(getString(R.string.error_username_empty));
                } else {
                    etUsername.clearError();
                }
            }
        });

        etEmail.addTextChangedListener(new TextWatcher() {
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
                if (s.toString().isEmpty()) {
                    etEmail.setError(getString(R.string.error_email_empty));
                } else {
                    etEmail.clearError();
                }
            }
        });

    }

    //Opens intent that provide selecting image from local storage
    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMG_REQUEST);
    }

    //Provides that cacth the results that come back from selectImage() function
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null) {
            newImagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), newImagePath);
                int fileSize = bitmap.getByteCount();
                Log.d("SIZE:  ", "" + bitmap.getByteCount());
                if (fileSize < ConstantValues.MAX_FILE_SIZE) {
                    SessionManager.getInstance().getUser().setAvatar(bitmap);
                    ivSettings.setImageBitmap(SessionManager.getInstance().getUser().getAvatar());
                    ivSettings.setVisibility(View.INVISIBLE);
                    setProgressVisibility(true);
                    ImageChangeTask.imageChangeTask(imageToString(SessionManager.getInstance().getUser().getAvatar()), getApplicationContext());
                } else {
                    Snackbar.make(editProfileLayout, "Seçilen dosya boyutu çok büyük", Snackbar.LENGTH_LONG).show();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Shows material progress bar and disables form.
     *
     * @param visible set true when progress view needs to be shown.
     */
    private void setProgressVisibility(boolean visible) {
        if (visible) {
            progressEditProfile.setVisibility(View.VISIBLE);
            etUsername.setEnabled(false);
            etEmail.setEnabled(false);
            etBio.setEnabled(false);
            btnSaveProfile.setEnabled(false);
        } else {
            progressEditProfile.setVisibility(View.INVISIBLE);
            etUsername.setEnabled(true);
            etEmail.setEnabled(true);
            etBio.setEnabled(true);
            btnSaveProfile.setEnabled(true);
        }
    }

    /**
     * Validate user information and send request to the API
     */
    private void saveProfile() {
        etUsername.clearError();
        etEmail.clearError();

        if (etUsername.getText().isEmpty()) {
            etUsername.setError(getString(R.string.error_username_empty));
        }
        if (etEmail.getText().isEmpty()) {
            etEmail.setError(getString(R.string.error_email_empty));
        }

        if (!etUsername.getText().isEmpty() && !etEmail.getText().isEmpty()) {
            setProgressVisibility(true);
            ChangeInfoTask changeInfoTask = new ChangeInfoTask(
                    SessionManager.getInstance().getSessionToken(),
                    etUsername.getText(),
                    etEmail.getText(),
                    etBio.getText().toString());
            changeInfoTask.setOnResponseListener(new ApiTask.ApiCallback<ApiStatusResponse>() {
                @Override
                public void onApiResponse(ApiStatusResponse response) {
                    handleChangeInfoTaskResponse(response,
                            etUsername.getText(),
                            etEmail.getText(),
                            etBio.getText().toString());
                }
            });
            changeInfoTask.execute(this);
        }
    }

    private void handleChangeInfoTaskResponse(ApiStatusResponse response, String uid, String email, String bio) {
        setProgressVisibility(false);
        //Check status
        String text;
        switch (response.getStatus()) {
            case ErrorCodes.SESSION_EXPIRED:
                text = getString(R.string.error_session_expired);
                break;

            case ErrorCodes.USERNAME_NOT_LEGAL:
                text = getString(R.string.error_username_not_legal);
                break;

            case ErrorCodes.USERNAME_ALREADY_EXIST:
                text = getString(R.string.error_username_taken);
                break;

            case ErrorCodes.EMAIL_NOT_LEGAL:
                text = getString(R.string.error_email_not_legal);
                break;

            case ErrorCodes.EMAIL_ALREADY_EXIST:
                text = getString(R.string.error_email_already_exist);
                break;

            case ErrorCodes.SUCCESS:
                text = getString(R.string.message_process_succeed);
                break;

            default:
                text = getString(R.string.error_no_connection);
                break;
        }
        Snackbar.make(editProfileLayout, text, BaseTransientBottomBar.LENGTH_LONG).show();
        SessionManager.getInstance().updateLocalVariables(uid, email, bio);
    }

    @Override
    public void onDestroy() {
        //Delete receivers
        LocalBroadcastManager.getInstance(EditProfileActivity.this).unregisterReceiver(updateImageReceiver);
        super.onDestroy();
    }

    public void onBackButtonPressed(View view) {
        getOnBackPressedDispatcher().onBackPressed();
    }

    /**
     * Initializes input method watcher for detecting virtual keyboard.
     */
    private void initInputMethodWatcher() {
        View contentRoot = ((ViewGroup) findViewById(R.id.editProfileLayout)).getChildAt(0);
        inputMethodWatcher = new InputMethodWatcher(contentRoot);
    }
}
