package com.allybros.superego;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.test.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.ErrorCodes;
import com.allybros.superego.util.SessionManager;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {


    @Test
    public void logout(){
        final Context context = InstrumentationRegistry.getTargetContext();
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest jsonRequest=new StringRequest(Request.Method.POST, ConstantValues.LOGOUT_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int status;
                try {
                    JSONObject jsonObj=new JSONObject(response);
                    Log.d("sego-Response-logout",response.toString());
                    status = jsonObj.getInt("status");

                    Intent intent = new Intent(ConstantValues.ACTION_LOGOUT);
                    intent.putExtra("status", status);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, ErrorCodes.CONNECTION_ERROR, Toast.LENGTH_SHORT);

            }
        }) {
            //Add parameters in request
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("session-token", SessionManager.getInstance().getSessionToken());
                return params;
            }
        };


        queue.add(jsonRequest);

    }

}
