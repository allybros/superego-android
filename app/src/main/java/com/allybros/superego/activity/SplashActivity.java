package com.allybros.superego.activity;


import static com.allybros.superego.unit.ConstantValues.IS_SHOWN;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.R;
import com.allybros.superego.api.GetAlertsTask;
import com.allybros.superego.api.LoadProfileTask;
import com.allybros.superego.unit.Alert;
import com.allybros.superego.unit.AlertResponse;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.ErrorCodes;
import com.allybros.superego.unit.Trait;
import com.allybros.superego.util.SessionManager;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private BroadcastReceiver loadProfileRegister;
    private BroadcastReceiver alertsReceiver;
    boolean loadTaskLock = false;
    boolean getTraitsLock = false;
    private ActivityResultLauncher<Intent> alertActivityLauncher;
    private static boolean alertsShown = false; // Do not show alerts multiple times to user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        registerAlertActivityLauncher();
        setupReceivers();

        if (!isGuideShown()) {
            // Show introduction guide
            showGuide();
        } else if (!checkNetworkConnection()) {
            // No internet connection
            showNetworkErrorDialog();
        } else if (!alertsShown){
            // Get alerts
            executeGetAlertsTask();
        } else {
            onReadyForStart();
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
        this.alertActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                o -> onReadyForStart()
        );
    }

    /**
     * Send requests and launch the applications
     */
    private void onReadyForStart() {
        Log.i(getClass().getSimpleName(), "Ready for starting application");
        executeGetTraitsTask(getApplicationContext());
        // Read session data
        SessionManager.getInstance().readInfo(getApplicationContext());
        if (!SessionManager.getInstance().getSessionToken().isEmpty()) {
            // User signed in before
            Log.i("ReadyForStart", "Session token found, starting load profile task");
            LoadProfileTask.loadProfileTask(getApplicationContext(),
                    SessionManager.getInstance().getSessionToken(), ConstantValues.ACTION_LOAD_PROFILE);
        } else {
            // No session, go to login
            Log.i("ReadyForStart", "No session data found, redirecting to login");
            startLoginActivity();
        }
    }

    private void showGuide() {
        Intent intent = new Intent(getApplicationContext(), GuideActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean isGuideShown() {
        SharedPreferences pref = getSharedPreferences(IS_SHOWN, MODE_PRIVATE);
        return pref.getBoolean("isShown", false);
    }

    private void setupReceivers() {
        loadProfileRegister = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra("status", ErrorCodes.SYSFAIL);
                if (status == ErrorCodes.SUCCESS) {
                    // Profile loaded successfully, current user must be set on SessionManager
                    loadTaskLock = true;
                    notifyTaskComplete();
                } else {
                    Log.w("Splash", "Can not load profile, routing to login page");
                    startLoginActivity();
                }
            }
        };

        // Receive from alerts
        alertsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Extract information, if we can deserialize, set alerts
                int status = intent.getIntExtra("status", ErrorCodes.SYSFAIL);
                if (status == ErrorCodes.SYSFAIL) {
                    Log.e("AlertsReceiver", "Unable to get alerts, skipping");
                    onReadyForStart();
                    return;
                }
                String alertsJson = intent.getStringExtra("response");
                Gson gson = new Gson();
                AlertResponse alertResponse = gson.fromJson(alertsJson, AlertResponse.class);
                if (alertResponse.getAlerts().isEmpty()) {
                    // No alerts received, launch app
                    Log.i("AlertsReceiver", "No alerts received, start app");
                    onReadyForStart();
                    return;
                }
                // Start alert activity for each alert
                for (Alert alert: alertResponse.getAlerts()) {
                    Intent alertIntent = new Intent(SplashActivity.this, AlertActivity.class);
                    String alertJson = gson.toJson(alert);
                    alertIntent.putExtra("alert", alertJson);
                    alertActivityLauncher.launch(alertIntent);
                }
                alertsShown = true;
            }
        };

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(alertsReceiver, new IntentFilter(ConstantValues.ACTION_GET_ALERTS));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(loadProfileRegister, new IntentFilter(ConstantValues.ACTION_LOAD_PROFILE));
    }

    /**
     * Checks if both task are completed. If both of them completed, starts UserPageActivity
     */
    private synchronized void notifyTaskComplete(){
        if (loadTaskLock && getTraitsLock) {
            // Both task are completed
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(loadProfileRegister);
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(alertsReceiver);

            Intent i = new Intent(SplashActivity.this, UserPageActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }
    }

    /**
     * Starts login activity and finished this activity
     */
    private void startLoginActivity(){
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(loadProfileRegister);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(alertsReceiver);

        Intent i = new Intent(SplashActivity.this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

    /**
     * Starts get alerts task
     */
    private void executeGetAlertsTask() {
        Log.i("SplashScreen", "Starting get alerts task");
        GetAlertsTask.getAlerts(this);
    }

    /**
     * Sets all trait list
     * @param context require for sending request
     */
    public void executeGetTraitsTask(final Context context){
        RequestQueue queue = Volley.newRequestQueue(context);
        final ArrayList<Trait> traits=new ArrayList<>();

        final StringRequest jsonRequest = new StringRequest(Request.Method.GET, ConstantValues.ALL_TRAITS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Log.d("getAllTraits",response.toString());
                try {
                    JSONObject jsonObject=new JSONObject(response);

                    for(int i = 0; i < jsonObject.getJSONArray("o").length(); i++) {
                        JSONObject iter= (JSONObject) jsonObject.getJSONArray("o").get(i);
                        int traitNo;
                        String positiveName,negativeName,positiveIcon,negativeIcon;

                        traitNo=iter.getInt("traitNo");
                        positiveName=iter.getString("positive");
                        negativeName=iter.getString("negative");
                        positiveIcon=iter.getString("positive_icon");
                        negativeIcon=iter.getString("negative_icon");
                        traits.add(new Trait(traitNo,positiveName,negativeName,positiveIcon,negativeIcon));
                    }
                    for(int i = 0; i < jsonObject.getJSONArray("c").length(); i++) {
                        JSONObject iter= (JSONObject) jsonObject.getJSONArray("c").get(i);
                        int traitNo;
                        String positiveName,negativeName,positiveIcon,negativeIcon;

                        traitNo=iter.getInt("traitNo");
                        positiveName=iter.getString("positive");
                        negativeName=iter.getString("negative");
                        positiveIcon=iter.getString("positive_icon");
                        negativeIcon=iter.getString("negative_icon");
                        traits.add(new Trait(traitNo,positiveName,negativeName,positiveIcon,negativeIcon));
                    }
                    for(int i = 0; i < jsonObject.getJSONArray("e").length(); i++) {
                        JSONObject iter= (JSONObject) jsonObject.getJSONArray("e").get(i);
                        int traitNo;
                        String positiveName,negativeName,positiveIcon,negativeIcon;

                        traitNo=iter.getInt("traitNo");
                        positiveName=iter.getString("positive");
                        negativeName=iter.getString("negative");
                        positiveIcon=iter.getString("positive_icon");
                        negativeIcon=iter.getString("negative_icon");
                        traits.add(new Trait(traitNo,positiveName,negativeName,positiveIcon,negativeIcon));
                    }
                    for(int i = 0; i < jsonObject.getJSONArray("a").length(); i++) {
                        JSONObject iter= (JSONObject) jsonObject.getJSONArray("a").get(i);
                        int traitNo;
                        String positiveName,negativeName,positiveIcon,negativeIcon;

                        traitNo=iter.getInt("traitNo");
                        positiveName=iter.getString("positive");
                        negativeName=iter.getString("negative");
                        positiveIcon=iter.getString("positive_icon");
                        negativeIcon=iter.getString("negative_icon");
                        traits.add(new Trait(traitNo,positiveName,negativeName,positiveIcon,negativeIcon));
                    }
                    for(int i = 0; i < jsonObject.getJSONArray("n").length(); i++) {
                        JSONObject iter= (JSONObject) jsonObject.getJSONArray("n").get(i);
                        int traitNo;
                        String positiveName,negativeName,positiveIcon,negativeIcon;

                        traitNo=iter.getInt("traitNo");
                        positiveName=iter.getString("positive");
                        negativeName=iter.getString("negative");
                        positiveIcon=iter.getString("positive_icon");
                        negativeIcon=iter.getString("negative_icon");
                        traits.add(new Trait(traitNo,positiveName,negativeName,positiveIcon,negativeIcon));
                    }
                    getTraitsLock = true;
                    notifyTaskComplete();
                    Trait.setAllTraits(traits);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> {
            getTraitsLock = true;
            notifyTaskComplete();
        });
        queue.add(jsonRequest);
    }

}

