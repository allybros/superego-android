package com.allybros.superego.api;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.allybros.superego.util.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class LoginTask extends AsyncTask<String, String, String> {

    private Context currentContext;
    private int status = 0;
    private String uid,password;
    private JSONParser jsonParser = new JSONParser();

    private static String loginProcessUrl = "http://192.168.1.106:80/api.allybros.com/superego/login.php";

    public LoginTask(Context currentContext, String uid, String password) {
        this.currentContext = currentContext;
        this.uid = uid;
        this.password = password;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected String doInBackground(String... args) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("login-submit", "login-submit"));
        params.add(new BasicNameValuePair("uid", this.uid));
        params.add(new BasicNameValuePair("password", this.password));

        JSONObject json = jsonParser.makeHttpRequest(loginProcessUrl,"POST", params);
        try {
            status = json.getInt("status");
            Log.d("respo",json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
            Intent intent=new Intent();
            intent.putExtra("status",status);
            intent.setAction("status");
        LocalBroadcastManager.getInstance(currentContext).sendBroadcast(intent);
    }
}