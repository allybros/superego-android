package com.allybros.superego.api;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.R;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.ErrorCodes;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GetAlertsTask {

    private static final String STATUS_EXTRA = "status";
    private static final String RESPONSE_EXTRA = "response";
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
        // Build request url
        String requestUrl = ConstantValues.ALERTS_URL;
        requestUrl += "?channel=android";
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
                        intent.putExtra(RESPONSE_EXTRA, response);
                        intent.putExtra(STATUS_EXTRA, ErrorCodes.SUCCESS);
                    } catch (JSONException e) {
                        intent.putExtra(STATUS_EXTRA, ErrorCodes.SYSFAIL);
                    }
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }, error -> {
                    if (error != null) {
                        Log.e("GetAlertsError", Objects.requireNonNull(error.getMessage()));
                    }
                    intent.putExtra(STATUS_EXTRA, ErrorCodes.CONNECTION_ERROR);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                // Set request headers
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept-Language", getCurrentLocale(context));
                return headers;
            }
        };

        queue.add(request);
    }

    public static String getCurrentLocale(Context c) {
        return c.getString(R.string.locale);
    }
}
