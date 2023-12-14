package com.allybros.superego.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.R;
import com.allybros.superego.api.LoadProfileTask;
import com.allybros.superego.api.LoginTask;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.ErrorCodes;
import com.allybros.superego.unit.Trait;
import com.allybros.superego.util.SessionManager;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.recaptcha.Recaptcha;
import com.google.android.recaptcha.RecaptchaAction;
import com.google.android.recaptcha.RecaptchaTasksClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.allybros.superego.unit.ConstantValues.IS_SHOWN;


public class SplashActivity extends AppCompatActivity {

    private BroadcastReceiver loadProfileRegister;
    private BroadcastReceiver loginReceiver;
    private RecaptchaTasksClient recaptchaTasksClient;
    boolean loadTaskLock = false;
    boolean getTraitsLock = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initializeRecaptchaClient();

        if(!isGuideShown()){
            showGuide();
        } else {
            // Check internet connection
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            if (isConnected) {
                getAllTraits(getApplicationContext());
                setupReceivers();
                SessionManager.getInstance().readInfo(getApplicationContext());
                if (!SessionManager.getInstance().getSessionToken().isEmpty()) {
                    // User signed in before
                    LoadProfileTask.loadProfileTask(getApplicationContext(),
                            SessionManager.getInstance().getSessionToken(), ConstantValues.ACTION_LOAD_PROFILE);

                } else {
                    returnLoginActivity();
                }
            }
            else {
                new AlertDialog.Builder(SplashActivity.this, R.style.SegoAlertDialog)
                        .setTitle("insightof.me")
                        .setMessage(R.string.error_no_connection)
                        .setPositiveButton(getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SplashActivity.this.finish();
                                System.exit(0);
                            }
                        })
                        .show();
            }
        }
    }

    /**
     * Initializes recaptcha client for securing the login call
     */
    private void initializeRecaptchaClient() {
        final String recaptchaClientKey = this.getString(R.string.recaptcha_client_key);
        Recaptcha.getTasksClient(this.getApplication(), recaptchaClientKey, 5000)
            .addOnSuccessListener(this, client -> {
                Log.i("Recaptcha", "Created recaptcha client with client key " + recaptchaClientKey);
                this.recaptchaTasksClient = client;
            })
            .addOnFailureListener(this, e ->
                    Log.e("Recaptcha", "Error occurred on recaptcha client " + e.getMessage())
            );
    }

    /**
     * Executes the login action with recaptcha
     */
    private void executeLoginTask(String uid, String password) {
        assert recaptchaTasksClient != null;
        recaptchaTasksClient
            .executeTask(RecaptchaAction.LOGIN)
            .addOnSuccessListener(this, token -> LoginTask.loginTask(this, uid, password, token))
            .addOnFailureListener(this,
                    e -> {
                        Log.e("Recaptcha", "Token generation failure");
                    });
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
                switch (status) {
                    case ErrorCodes.SUCCESS:
                        // Profile loaded successfully, current user must be set on SessionManager
                        loadTaskLock = true;
                        notifyTaskComplete();
                        break;

                    case ErrorCodes.SESSION_EXPIRED:
                        // Session is not valid, or expired. Try to login again.
                        String uid = SessionManager.getInstance().getUserId();
                        String password = SessionManager.getInstance().getPassword();
                        // Check if user login data present
                        if (uid.isEmpty() || password.isEmpty()) {
                            returnLoginActivity();
                        } else {
                            // Execute login task
                            SplashActivity.this.executeLoginTask(uid, password);
                        }
                        break;

                    default:
                        Log.w("Splash", "Can not load profile, routing to login page");
                }
            }
        };

        loginReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, Intent intent) {
                int status = intent.getIntExtra("status", 0);
                switch (status){
                    case ErrorCodes.SUCCESS:
                        // Login succeeded, start load profile task
                        LoadProfileTask.loadProfileTask(getApplicationContext(),
                                SessionManager.getInstance().getSessionToken(),
                                ConstantValues.ACTION_LOAD_PROFILE);
                        break;

                    case ErrorCodes.CONNECTION_ERROR:
                        new AlertDialog.Builder(SplashActivity.this, R.style.SegoAlertDialog)
                                .setTitle("insightof.me")
                                .setMessage(R.string.error_no_connection)
                                .setPositiveButton(getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SplashActivity.this.finish();
                                        System.exit(0);
                                    }
                                })
                                .show();
                        break;

                    default:
                        Snackbar.make(getWindow().getDecorView(), R.string.error_login_again, 2000).show();
                        returnLoginActivity();
                        break;
                }
            }
        };

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(loginReceiver, new IntentFilter(ConstantValues.ACTION_LOGIN));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(loadProfileRegister, new IntentFilter(ConstantValues.ACTION_LOAD_PROFILE));
    }

    /**
     * Checks if both task are completed. If both of them completed, starts UserPageActivity
     */
    private synchronized void notifyTaskComplete(){
        if (loadTaskLock && getTraitsLock) {
            // Both task are completed
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(loadProfileRegister);
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(loginReceiver);

            Intent i = new Intent(SplashActivity.this, UserPageActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }
    }

    /**
     * Starts login activity and finished this activity
     */
    private void returnLoginActivity(){
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(loadProfileRegister);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(loginReceiver);

        Intent i = new Intent(SplashActivity.this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

    /**
     * Sets all trait list
     * @param context    require for sending request
     */
    public void getAllTraits(final Context context){
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
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                getTraitsLock = true;
                notifyTaskComplete();
            }
        });
        queue.add(jsonRequest);
    }
}

