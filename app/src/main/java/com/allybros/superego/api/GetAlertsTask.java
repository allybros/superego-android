package com.allybros.superego.api;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

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
import java.util.Map;

public class GetAlertsTask {

    private GetAlertsTask() {
    }

    public static void getAlerts(Context context) {
        RequestQueue queue = Volley.newRequestQueue(context);
        Intent intent = new Intent(ConstantValues.ACTION_GET_ALERTS);
        StringRequest request = new StringRequest(Request.Method.GET,
                ConstantValues.ALERTS_URL + "?channel=android&version=2.0.0",
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
        };

        queue.add(request);


    }
}
