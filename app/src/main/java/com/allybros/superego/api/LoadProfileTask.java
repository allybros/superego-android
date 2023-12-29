package com.allybros.superego.api;

import com.allybros.superego.api.response.ProfileResponse;
import com.allybros.superego.unit.ConstantValues;
import com.android.volley.Request;

public class LoadProfileTask extends ApiTask<ProfileResponse> {

    /**
     * Loads user data from API
     * @param sessionToken Session token of the user
     */
    public LoadProfileTask(String sessionToken) {
        super(Request.Method.POST, ConstantValues.LOAD_PROFILE, ProfileResponse.class);
        // set params
        setParam("session-token", sessionToken);
    }
}
