package com.allybros.superego.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SplashActivity extends AppCompatActivity {

    private BroadcastReceiver loadProfileRegister;
    private BroadcastReceiver loginReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //TODO: Fix empty traits issue
        SplashActivity.getAllTraits(getApplicationContext());
        setupReceivers();

        SessionManager.getInstance().readInfo(getApplicationContext());
        if(!SessionManager.getInstance().getSessionToken().isEmpty()){
            // User signed in before
            LoadProfileTask.loadProfileTask(getApplicationContext(),
                    SessionManager.getInstance().getSessionToken(), ConstantValues.ACTION_LOAD_PROFILE);

        }else{
            returnLoginActivity();
        }
    }

    private void setupReceivers(){
        loadProfileRegister = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra("status", ErrorCodes.SYSFAIL);
                switch (status) {
                    case ErrorCodes.SUCCESS:
                        // Profile loaded successfully, current user must be set on SessionManager
                        LocalBroadcastManager.getInstance(context).unregisterReceiver(loadProfileRegister);
                        LocalBroadcastManager.getInstance(context).unregisterReceiver(loginReceiver);

                        Intent i = new Intent(SplashActivity.this, UserPageActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();
                        break;

                    case ErrorCodes.SESSION_EXPIRED:
                        // Session is not valid, or expired. Try to login again.
                        String uid = SessionManager.getInstance().getUserId();
                        String password = SessionManager.getInstance().getPassword();
                        // Check if user login data present
                        if (uid.isEmpty() || password.isEmpty()) {
                            returnLoginActivity();
                        } else {
                            LoginTask.loginTask(SplashActivity.this, uid, password);
                        }
                        break;

                    case ErrorCodes.SYSFAIL:
                        new AlertDialog.Builder(SplashActivity.this, R.style.SegoAlertDialog)
                                .setTitle("insightof.me")
                                .setMessage(R.string.error_login_failed)
                                .setPositiveButton(getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SessionManager.getInstance().clearSession(getApplicationContext());
                                        returnLoginActivity();
                                    }
                                })
                                .show();

                    case ErrorCodes.CONNECTION_ERROR:
                        // TODO: Detect network issues
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
    public static void getAllTraits(final Context context){
        RequestQueue queue = Volley.newRequestQueue(context);
        final ArrayList<Trait> traits=new ArrayList<>();

        final StringRequest jsonRequest = new StringRequest(Request.Method.GET, ConstantValues.ALL_TRAITS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Log.d("getAllTraits",response.toString());

                try {
                    JSONObject jsonObject=new JSONObject(response);

                    for (int i = 0; i < jsonObject.getJSONArray("traits").length(); i++) {
                        JSONObject iter= (JSONObject) jsonObject.getJSONArray("traits").get(i);
                        int traitNo;
                        String positiveName,negativeName,positiveIcon,negativeIcon;

                        traitNo=iter.getInt("traitNo");
                        positiveName=iter.getString("positive");
                        negativeName=iter.getString("negative");
                        positiveIcon=iter.getString("positive_icon");
                        negativeIcon=iter.getString("negative_icon");
                        traits.add(new Trait(traitNo,positiveName,negativeName,positiveIcon,negativeIcon));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsonRequest);
        Trait.setAllTraits(traits);
    }
}

