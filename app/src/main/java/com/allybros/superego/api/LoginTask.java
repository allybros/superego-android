package com.allybros.superego.api;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.allybros.superego.R;
import com.allybros.superego.util.UserTraits;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
public class LoginTask {

    private Context currentContext;
    private String uid, password;
    private static String loginProcessUrl = "https://api.allybros.com/superego/login.php";
    private JSONObject postParams;


    public LoginTask(String uid, String password, Context currentContext) {
        this.uid = uid;
        this.password = password;
        this.currentContext = currentContext;
    }

    public void loginRequest() throws JSONException {
        postParams=new JSONObject();
        this.postParams.put("login-submit", "login-submit");
        this.postParams.put("uid", this.uid);
        this.postParams.put("password", this.password);


        JsonObjectRequest jsonobj=new JsonObjectRequest(Request.Method.POST, loginProcessUrl, postParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject obj = new JSONObject((Map) response);
                ArrayList<UserTraits> userTraits =new ArrayList<UserTraits>();
                Intent intent=new Intent();
                try {
                    JSONArray userTraitsArray=new JSONArray(obj.getJSONArray("scores"));
                    for (int i = 0; i <userTraitsArray.length() ; i++) {
                        JSONObject traitObject = userTraitsArray.getJSONObject(i);
                        UserTraits tmpUserTrait=new UserTraits(traitObject.getInt("traştNo"),traitObject.getInt("value"));
                        userTraits.add(tmpUserTrait);
                    }
                    Bundle userTraitsArrayBundle = new Bundle();
                    userTraitsArrayBundle.putSerializable("userTraitsArray", (Serializable) userTraitsArray);
                    
                    intent.putExtra("userTraitsArray",userTraitsArrayBundle);
                    intent.putExtra("user_id",obj.getInt("user_id"));
                    intent.putExtra("user_type",obj.getInt("user_type"));
                    intent.putExtra("username", obj.getString("username"));
                    intent.putExtra("user_bio",obj.getString("user_bio"));
                    intent.putExtra("email",obj.getString("email"));
                    intent.putExtra("status",obj.getInt("status"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                intent.setAction("loginTaskResult");
                LocalBroadcastManager.getInstance(currentContext).sendBroadcast(intent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(currentContext,currentContext.getString(R.string.communicationError),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Context getCurrentContext() {
        return currentContext;
    }

    public String getUid() {
        return uid;
    }

    public String getPassword() {
        return password;
    }
}

