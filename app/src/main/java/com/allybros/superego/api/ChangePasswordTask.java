package com.allybros.superego.api;



import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.allybros.superego.R;
import com.allybros.superego.activity.LoginActivity;
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
//TODO: Bu işlev tam olarak bitmedi. Geri dönüş karşılaması falan yapılacak. Temel olarak çalışıyor.
public class ChangePasswordTask {
    public static void changePasswordTask (final Context currentContext, final String oldPassword, final String sessionToken, final String newPassword, final String newPasswordAgain){
        final String loginFailed= (String) currentContext.getString(R.string.loginFailed);
        final String loginSuccess= (String) currentContext.getString(R.string.loginSuccess);
        final String usernameEmpty= (String) currentContext.getString(R.string.usernameEmpty);
        final String passwordEmpty= (String) currentContext.getString(R.string.passwordEmpty);
        final String emailEmpty= (String) currentContext.getString(R.string.emailEmpty);
        final String usernameNotLegal=(String) currentContext.getString(R.string.usernameNotLegal);
        final String usernameAlreadyExist=(String) currentContext.getString(R.string.usernameAlreadyExist);
        final String emailAlreadyExist=(String) currentContext.getString(R.string.emailAlreadyExist);
        final String emailNotLegal=(String) currentContext.getString(R.string.emailNotLegal);
        final String passwordNotLegal=(String) currentContext.getString(R.string.passwordNotLegal);
        final String successRegister=(String) currentContext.getString(R.string.successRegister);
        final String REGISTER_URL ="https://api.allybros.com/superego/reset-password.php";


        RequestQueue queue = Volley.newRequestQueue(currentContext);

        final StringRequest jsonRequest=new StringRequest(Request.Method.POST, REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Log response pass chan",response.toString());
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

                params.put("session-token", sessionToken);
                params.put("old-password", oldPassword);
                params.put("new-password", newPassword);
                params.put("new-password-again", newPasswordAgain);

                return params;
            }
        };


        queue.add(jsonRequest);

    }



}


