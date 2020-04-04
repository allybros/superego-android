package com.allybros.superego.api;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.R;
import com.allybros.superego.unit.ErrorCodes;
import com.allybros.superego.unit.Trait;
import com.allybros.superego.unit.User;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoadTestTask{

    final private static String LOAD_TEST_URL ="https://api.allybros.com/superego/load-test.php";

    public static void LoadTestTask(final Context currentContext){

        RequestQueue queue = Volley.newRequestQueue(currentContext);

        final StringRequest jsonRequest=new StringRequest(Request.Method.POST, LOAD_TEST_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response_Load_Test",response.toString());
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    int status=jsonObject.getInt("status");
                    switch (status){

                        case ErrorCodes.SYSFAIL:
                            Toast.makeText(currentContext, currentContext.getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                            break;

                        case ErrorCodes.SUCCESS:
                            ArrayList<Trait> test=new ArrayList<>();

                            if(!jsonObject.isNull("test_items")){
                                for (int i = 0; i < jsonObject.getJSONArray("test_items").length(); i++) {
                                    JSONObject iter= (JSONObject) jsonObject.getJSONArray("test_items").get(i);
                                    int traitNo;
                                    String positive, negative;

                                    traitNo=iter.getInt("traitNo");
                                    positive=iter.getString("positive");
                                    negative=iter.getString("negative");

                                    test.add(new Trait(traitNo,positive,negative));
                                }
                            }
                            User.setTest(test);
                            Intent intent = new Intent("load-test-status-share");
                            LocalBroadcastManager.getInstance(currentContext).sendBroadcast(intent);

                            break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(currentContext,currentContext.getString(R.string.connection_error), Toast.LENGTH_SHORT);

            }
        }) {
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("test_id", User.getTestId());
                return params;
            }
        };


        queue.add(jsonRequest);

    }
}