package com.allybros.superego.activity;


import static com.allybros.superego.unit.ConstantValues.IS_SHOWN;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.allybros.superego.R;
import com.allybros.superego.api.GetAlertsTask;
import com.allybros.superego.api.LoadProfileTask;
import com.allybros.superego.api.mapper.UserMapper;
import com.allybros.superego.unit.Alert;
import com.allybros.superego.unit.ErrorCodes;
import com.allybros.superego.unit.User;
import com.allybros.superego.util.ClientContextUtil;
import com.allybros.superego.util.SessionManager;
import com.google.gson.Gson;


@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    private static boolean alertsShown = false; // Do not show alerts multiple times to user
    private ActivityResultLauncher<Intent> alertActivityLauncher;
    private ActivityResultLauncher<Intent> guideActivityLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        registerAlertActivityLauncher();
        registerGuideActivityLauncher();

        if (!checkNetworkConnection()) {
            // No internet connection
            showNetworkErrorDialog();
        } else if (!isGuideShown()) {
            // Show guide activity
            startGuideActivity();
        } else {
            // Ask for alerts
            onReadyForAlerts();
        }
    }

    /**
     * Performs check on internet connection
     * @return false if no network connectivity
     */
    private boolean checkNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    /**
     * Shows network error dialog
     */
    private void showNetworkErrorDialog() {
        new AlertDialog.Builder(this, R.style.SegoAlertDialog)
                .setTitle("insightof.me")
                .setMessage(R.string.error_no_connection)
                .setPositiveButton(getString(R.string.action_ok), (dialog, which) -> {
                    SplashActivity.this.finish();
                    System.exit(0);
                })
                .show();
    }

    /**
     * Initializes alert activity launcher
     */
    private void registerAlertActivityLauncher() {
        // Set ready for login as the callback
        this.alertActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                o -> onReadyForLogin()
        );
    }

    /**
     * Initializes guide activity launcher
     */
    private void registerGuideActivityLauncher() {
        // Set ready for alerts as the callback
        this.guideActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                o -> onReadyForAlerts()
        );
    }


    /**
     * Send requests for alerts
     */
    private void onReadyForAlerts() {
        if (alertsShown) {
            Log.i("ReadyForAlerts", "Alerts already shown to the user");
            onReadyForLogin();

        } else {
            Log.i("ReadyForAlerts", "Alerts not shown before");
            executeGetAlertsTask();
        }
    }

    /**
     * Send requests and launch the applications
     */
    private void onReadyForLogin() {
        Log.i(getClass().getSimpleName(), "Ready for starting application");
        // Read session data
        SessionManager.getInstance().readInfo(getApplicationContext());
        if (!SessionManager.getInstance().getSessionToken().isEmpty()) {
            // User signed in before
            Log.i("ReadyForStart", "Session token found, starting load profile task");
            executeLoadProfileTask(SessionManager.getInstance().getSessionToken());
        } else {
            // No session, go to login
            Log.i("ReadyForStart", "No session data found, redirecting to login");
            startLoginActivity();
        }
    }

    private void executeLoadProfileTask(String sessionToken) {
        LoadProfileTask loadProfileTask = new LoadProfileTask(sessionToken);
        loadProfileTask.setOnResponseListener(response -> {
            // Map to session user
            if (response.getStatus() == ErrorCodes.SUCCESS) {
                // Successfully response
                User sessionUser = UserMapper.fromProfileResponse(response);
                SessionManager.getInstance().setUser(sessionUser);
                // Start user page activity
                startUserPageActivity();
            } else {
                Log.w("Splash", "Can not load profile, routing to login page");
                startLoginActivity();
            }

        });
        loadProfileTask.execute(this);
    }

    private void startGuideActivity() {
        Intent intent = new Intent(getApplicationContext(), GuideActivity.class);
        this.guideActivityLauncher.launch(intent);
    }

    private boolean isGuideShown() {
        SharedPreferences pref = getSharedPreferences(IS_SHOWN, MODE_PRIVATE);
        return pref.getBoolean("isShown", false);
    }

    /**
     * Checks if both task are completed. If both of them completed, starts UserPageActivity
     */
    private synchronized void startUserPageActivity(){
        // Both task are completed
        Intent i = new Intent(SplashActivity.this, UserPageActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();

    }

    /**
     * Starts login activity and finished this activity
     */
    private void startLoginActivity(){
        Intent i = new Intent(SplashActivity.this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

    /**
     * Starts get alerts task
     */
    private void executeGetAlertsTask() {
        String versionName = ClientContextUtil.getVersionName(this);
        GetAlertsTask alertsApiTask = new GetAlertsTask(versionName);
        alertsApiTask.setOnResponseListener(response -> {
            alertsShown = true;
            if (response.getAlerts().isEmpty()) {
                // No alerts received, launch app
                Log.i("SplashScreen", "No alerts received, start app");
                onReadyForLogin();
                return;
            }
            // Start alert activity for each alert
            for (Alert alert: response.getAlerts()) {
                Intent alertIntent = new Intent(SplashActivity.this, AlertActivity.class);
                Gson gson = new Gson();
                String alertJson = gson.toJson(alert);
                alertIntent.putExtra("alert", alertJson);
                alertActivityLauncher.launch(alertIntent);
            }
        });
        alertsApiTask.execute(this);
    }

}

