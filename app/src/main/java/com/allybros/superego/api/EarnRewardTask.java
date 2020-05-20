package com.allybros.superego.api;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.R;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.ErrorCodes;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
/**
 * @author 0rcun
 */
public class EarnRewardTask {

    /**
     * Function sends request to accept reward.
     *
     * @param context        required to build request and send Broadcast
     * @param session_token  required to verify the user
     */
    public static void EarnRewardTask(final Context context, final String session_token){

        final Intent intent = new Intent(ConstantValues.ACTION_EARNED_REWARD);

        RequestQueue queue = Volley.newRequestQueue(context);

        final StringRequest jsonRequest=new StringRequest(Request.Method.POST, ConstantValues.URL_EARN_REWARD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    int status=jsonObject.getInt("status");
                    Log.d("EarnedRewardTaskRespons",""+response.toString());

                    switch (status){

                        case ErrorCodes.SYSFAIL:
                            intent.putExtra("status", status);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                            break;

                        case ErrorCodes.SUCCESS:
                            intent.putExtra("status", status);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                            break;
                        case ErrorCodes.SESSION_EXPIRED:
                            intent.putExtra("status", status);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,context.getString(R.string.connection_error), Toast.LENGTH_SHORT);

            }
        }) {
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("session-token", session_token);
                return params;
            }
        };


        queue.add(jsonRequest);

    }
}
