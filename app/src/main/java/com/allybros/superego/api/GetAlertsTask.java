package com.allybros.superego.api;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.util.Log;

import androidx.core.os.ConfigurationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.ErrorCodes;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GetAlertsTask {

    private GetAlertsTask() {
    }

    public static void getAlerts(Context context) {
        RequestQueue queue = Volley.newRequestQueue(context);
        Intent intent = new Intent(ConstantValues.ACTION_GET_ALERTS);
        String packageName = context.getPackageName();
        String versionName = null;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("GetVersion", "Unable to get version name");
        }

        String requestUrl = ConstantValues.ALERTS_URL + "?channel=android";
        if (versionName != null) {
            requestUrl += "&version=" + versionName;
        }
        StringRequest request = new StringRequest(Request.Method.GET, requestUrl,
                response -> {
                    Log.d("GetAlertsResponse", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (!jsonObject.has("alerts")) {
                            Log.e("GetAlertsResponse", "No alerts field detected on the response, returning error response");
                            throw new JSONException("No alerts field");
                        }
                        intent.putExtra("response", response);
                        intent.putExtra("status", ErrorCodes.SUCCESS);
                    } catch (JSONException e) {
                        intent.putExtra("status", ErrorCodes.SYSFAIL);
                    }
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }, error -> {
                    if (error != null) {
                        Log.e("GetAlertsError", error.getMessage());
                    }
                    intent.putExtra("status", ErrorCodes.CONNECTION_ERROR);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("channel", "android");
                params.put("version", "2.0.0");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept-Language", getCurrentLocale());
                return headers;
            }
        };

        queue.add(request);
    }

    public static String getCurrentLocale() {
        Locale locale = ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).get(0);
        if (locale != null) {
            return locale.getLanguage();
        } else {
            return "en";
        }
    }
}
