package com.allybros.superego.api;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.unit.ConstantValues;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

public class SearchTask {

    public static void searchTask(final Context context, final String query) {
        RequestQueue queue = Volley.newRequestQueue(context);
        final Intent intent = new Intent(ConstantValues.getSearchUrl());

        //Build request url
        final String requestUrl = ConstantValues.getSearchUrl()+query;
        StringRequest jsonRequest=new StringRequest(Request.Method.GET, requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                int status;

                try {
                    JSONArray jsonObject=new JSONArray(response);
                    Log.d("query-Response",response.toString());
                    intent.putExtra("result",response.toString());
                    intent.setAction(ConstantValues.getActionSearch());
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);


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

    }

}
