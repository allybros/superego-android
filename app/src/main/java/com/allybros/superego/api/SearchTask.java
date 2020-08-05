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
/**
 * Class includes the function of searching user
 * @author umutalacam
 */
public class SearchTask {
    /**
     * The function sends a request for searching user. After that broadcasts the response.
     * @param context   required to build request and send Broadcast
     * @param query     required for searching from server
     */
    public static void searchTask(final Context context, final String query) {
        RequestQueue queue = Volley.newRequestQueue(context);
        final Intent intent = new Intent(ConstantValues.SEARCH_URL);

        //Build request url
        final String requestUrl = ConstantValues.SEARCH_URL+query;
        StringRequest jsonRequest=new StringRequest(Request.Method.GET, requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("query-Response",response);
                intent.putExtra("query", query);
                intent.putExtra("result", response);
                intent.setAction(ConstantValues.ACTION_SEARCH);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsonRequest);

    }

}
