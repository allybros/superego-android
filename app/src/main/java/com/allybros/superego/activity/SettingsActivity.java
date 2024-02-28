package com.allybros.superego.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.allybros.superego.R;
import com.allybros.superego.adapter.LicensesAdapter;
import com.allybros.superego.api.LogoutTask;
import com.allybros.superego.api.response.ApiStatusResponse;
import com.allybros.superego.unit.ErrorCodes;
import com.allybros.superego.util.ClientContextUtil;
import com.allybros.superego.util.SessionManager;
import com.allybros.superego.widget.SegoMenuButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private SegoMenuButton optionEditProfile, optionChangePassword, optionAbout, optionLicenses, optionSignOut;
    private TextView tvUsername;
    private CircleImageView ivUserAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ActionBar appBar = getSupportActionBar();
        if (appBar != null) appBar.setDisplayHomeAsUpEnabled(true);
        setViews();
    }

    private void setViews() {
        optionEditProfile = findViewById(R.id.cardBtnEditProfile);
        optionChangePassword = findViewById(R.id.cardBtnPassword);
        optionAbout = findViewById(R.id.cardBtnAbout);
        optionLicenses = findViewById(R.id.cardBtnLicenses);
        optionSignOut = findViewById(R.id.cardBtnSingOut);
        ivUserAvatar = findViewById(R.id.ivUserAvatar);
        tvUsername = findViewById(R.id.tvUsername);

        optionEditProfile.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), EditProfileActivity.class);
            startActivity(i);
        });

        optionSignOut.setOnClickListener(view -> showLogoutDialog());

        optionChangePassword.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), ChangePasswordActivity.class);
            startActivity(i);
        });

        optionAbout.setOnClickListener(view -> showAboutDialog());

        optionLicenses.setOnClickListener(view -> showLicensesDialog());

        String username = "@" + SessionManager.getInstance().getUser().getUsername();
        tvUsername.setText(username);
        Picasso.get().load(SessionManager.getInstance().getUser().getImage()).error(R.drawable.default_avatar).into(ivUserAvatar);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, R.style.SegoAlertDialog);
        builder.setTitle(R.string.alert_title_end_session)
                .setMessage(R.string.alert_context_end_session)
                .setPositiveButton(R.string.action_yes, (dialogInterface, i) -> logout())
                .setNegativeButton(R.string.action_no, null)
                .setCancelable(true).show();
    }

    private void logout() {
        if (SessionManager.getInstance().getUser().getUserType() == 1) { //Google Sign out
            GoogleSignInClient mGoogleSignInClient;
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.google_client_id))
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);
            mGoogleSignInClient.signOut().addOnCompleteListener(task -> logoutInternal());
        } else {
            logoutInternal();
        }
    }

    private void logoutInternal() {
        LogoutTask logoutTask = new LogoutTask(SessionManager.getInstance().getSessionToken());
        logoutTask.setOnResponseListener(this::handleLogoutResponse);
        logoutTask.execute(this);
    }

    private void handleLogoutResponse(ApiStatusResponse response) {
        if (response.getStatus() == ErrorCodes.SUCCESS) {
            SessionManager.getInstance().clearSession(getApplicationContext()); //Clear local variables that use login
            Intent intent1 = new Intent(getApplicationContext(), SplashActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            getApplicationContext().startActivity(intent1);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.error_check_connection),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void showAboutDialog() {
        //Inflate dialog layout
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_about, null);
        TextView tvWebSite = dialogView.findViewById(R.id.tvWebSite);
        TextView tvVersionInfo = dialogView.findViewById(R.id.tvVersionInfo);
        tvVersionInfo.setText(String.format("v%s %s", ClientContextUtil.getVersionName(this),
                getString(R.string.with_version_from)));
        tvWebSite.setOnClickListener(view -> {
            String url = "http://" + getString(R.string.web_root);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });
        //Show dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, R.style.SegoAlertDialog);
        builder.setView(dialogView).show();
    }

    private void showLicensesDialog() {

        //Infalate dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_licenses, null);
        ListView listViewLicenses = dialogView.findViewById(R.id.listviewLicenses);

        //Get licenses
        String[] licenses = getResources().getStringArray(R.array.licenses);
        ArrayList<String> licenseList = new ArrayList<>(Arrays.asList(licenses));
        LicensesAdapter adapter = new LicensesAdapter(getApplicationContext(), licenseList, listViewLicenses);

        //Show
        listViewLicenses.setAdapter(adapter);

        //Show dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, R.style.SegoAlertDialog);
        builder.setTitle(R.string.option_licenses);
        builder.setView(dialogView).show();
    }

    public void onBackButtonPressed(View view) {
        getOnBackPressedDispatcher().onBackPressed();
    }
}
