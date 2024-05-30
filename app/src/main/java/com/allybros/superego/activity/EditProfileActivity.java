package com.allybros.superego.activity;

import android.content.Context;
import android.content.Intent;
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

import com.allybros.superego.R;
import com.allybros.superego.api.ChangeInfoTask;
import com.allybros.superego.api.ImageChangeTask;
import com.allybros.superego.api.response.ApiStatusResponse;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.util.ClientContextUtil;
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
    private ImageView ivChangeAvatarIcon;
    private CircleImageView ivAvatar;
    private ConstraintLayout editProfileLayout;
    private Button btnSaveProfile;
    private static final int IMG_REQUEST = 1; //Needs for image selection from local storage

    private InputMethodWatcher inputMethodWatcher;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initializeComponents();
        setupUi();
        setupTextWatchers();
        initInputMethodWatcher();
    }

    private void initializeComponents() {
        editProfileLayout = findViewById(R.id.editProfileLayout);
        progressEditProfile = findViewById(R.id.progressEditProfile);
        findViewById(R.id.cardFormEditProfile);
        ivChangeAvatarIcon = findViewById(R.id.ivChangeAvatar);
        findViewById(R.id.ivBack);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etBio = findViewById(R.id.etInformation);
        ivAvatar = findViewById(R.id.ivUserAvatarEditProfile);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
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
        Picasso.get().load(URL).error(R.drawable.default_avatar).into(ivAvatar);

        if (!isConnected)
            Snackbar.make(editProfileLayout, R.string.error_no_connection, BaseTransientBottomBar.LENGTH_LONG).show();

        ivChangeAvatarIcon.setOnClickListener(this::onChangeAvatarClick);
        ivAvatar.setOnClickListener(this::onChangeAvatarClick);
        btnSaveProfile.setOnClickListener(view -> {
            if (inputMethodWatcher.isKeyboardShown()) {
                Log.d("Page changed", "Hide soft keyboard");
                inputMethodWatcher.hideSoftKeyboard();
            }

            // Check internet connection
            if (ClientContextUtil.isNetworkConnected(getApplicationContext())) {
                saveProfile();
            } else {
                Snackbar.make(editProfileLayout, R.string.error_no_connection, BaseTransientBottomBar.LENGTH_LONG).show();
            }
        });
    }

    public void onChangeAvatarClick(View view) {
        if (ClientContextUtil.isNetworkConnected(this)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, IMG_REQUEST);
        } else {
            Snackbar.make(editProfileLayout, R.string.error_no_connection, BaseTransientBottomBar.LENGTH_LONG).show();
        }
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

    //Provides that cacth the results that come back from selectImage() function
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                int fileSize = bitmap.getByteCount();
                Log.d("SIZE:  ", "" + bitmap.getByteCount());
                if (fileSize < ConstantValues.MAX_FILE_SIZE) {
                    SessionManager.getInstance().getUser().setAvatar(bitmap);
                    ivAvatar.setImageBitmap(SessionManager.getInstance().getUser().getAvatar());
                    ivAvatar.setVisibility(View.INVISIBLE);
                    setProgressVisibility(true);
                    ImageChangeTask imageChangeTask = new ImageChangeTask(SessionManager.getInstance().getSessionToken(), imageToString(SessionManager.getInstance().getUser().getAvatar()));
                    imageChangeTask.setOnResponseListener(this::handleImageChangeTaskResponse);
                    imageChangeTask.execute(this);
                } else {
                    Snackbar.make(editProfileLayout, getString(R.string.error_invalid_file_size), BaseTransientBottomBar.LENGTH_LONG).show();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleImageChangeTaskResponse(ApiStatusResponse response) {
        setProgressVisibility(false);
        ivAvatar.setVisibility(View.VISIBLE);
        Snackbar.make(editProfileLayout, response.getMessage(), BaseTransientBottomBar.LENGTH_LONG).show();
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
            changeInfoTask.setOnResponseListener(response -> handleChangeInfoTaskResponse(response,
                    etUsername.getText(),
                    etEmail.getText(),
                    etBio.getText().toString()));
            changeInfoTask.execute(this);
        }
    }

    private void handleChangeInfoTaskResponse(ApiStatusResponse response, String uid, String email, String bio) {
        setProgressVisibility(false);
        //Check status
        String text = response.getMessage();
        Snackbar.make(editProfileLayout, text, BaseTransientBottomBar.LENGTH_LONG).show();
        SessionManager.getInstance().updateLocalVariables(uid, email, bio);
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
