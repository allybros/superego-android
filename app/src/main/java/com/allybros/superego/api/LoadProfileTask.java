package com.allybros.superego.api;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.allybros.superego.R;
import com.allybros.superego.activity.MainActivity;
import com.allybros.superego.activity.ProfilFragment;
import com.allybros.superego.activity.UserPageActivity;
import com.allybros.superego.util.Trait;
import com.allybros.superego.util.User;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class LoadProfileTask{

    public static final int SYSFAIL=0;
    public static final int SUCCESS=10;
    public static final int SESSION_EXPIRED=31;
    public static final String USER_INFORMATION_PREF="USER_INFORMATION_PREF";


    final private static String LOAD_PROFILE_URL ="http://192.168.0.41/api.allybros.com/superego/load-profile.php";

    public static void loadProfileTask(final Context currentContext, final String session_token){

        RequestQueue queue = Volley.newRequestQueue(currentContext);

        final StringRequest jsonRequest=new StringRequest(Request.Method.POST, LOAD_PROFILE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response_Load_Profile",response.toString());
                Intent intent;
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    int status=jsonObject.getInt("status");
                    switch (status){
                        case SYSFAIL:

                            Toast.makeText(currentContext, currentContext.getString(R.string.please_login_again), Toast.LENGTH_SHORT).show();
                            intent=new Intent(currentContext, MainActivity.class);
                            currentContext.startActivity(intent);
                            break;

                        case SUCCESS:

                            int user_type=jsonObject.getInt("user_type");
                            String username=jsonObject.getString("username");
                            String user_bio=jsonObject.getString("user_bio");
                            String email=jsonObject.getString("email");
                            ArrayList<Trait> traits=new ArrayList<>();

                            for (int i = 0; i < jsonObject.getJSONArray("scores").length(); i++) {
                                JSONObject iter= (JSONObject) jsonObject.getJSONArray("scores").get(i);
                                int traitNo;
                                float value;

                                traitNo=iter.getInt("traitNo");
                                value=iter.getInt("value");

                                traits.add(new Trait(traitNo,value));
                            }

                            Log.d("BASARIIII","BASARRR");
                            User.setUserType(user_type);
                            User.setUsername(username);
                            User.setUserBio(user_bio);
                            User.setEmail(email);
                            User.setScores(traits);
                            intent=new Intent(currentContext, UserPageActivity.class);
                            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                            currentContext.startActivity(intent);


                            break;
                        case SESSION_EXPIRED:
                            SharedPreferences pref = currentContext.getSharedPreferences(USER_INFORMATION_PREF, currentContext.MODE_PRIVATE);
                            String uid= pref.getString("uid", "");
                            String password=pref.getString("password","");
                            Log.d("RespoUID",uid);
                            Log.d("RespoPassword",password);
                            LoginTask.loginTask(currentContext,uid,password);
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
                params.put("session-token", session_token);
                return params;
            }
        };


        queue.add(jsonRequest);

    }
}