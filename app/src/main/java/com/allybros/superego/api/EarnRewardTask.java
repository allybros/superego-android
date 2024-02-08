package com.allybros.superego.api;

import com.allybros.superego.api.response.ApiStatusResponse;
import com.allybros.superego.unit.ConstantValues;
import com.android.volley.Request;

/**
 * Class includes the function of accepting awards
 *
 * @author 0rcun
 */

public class EarnRewardTask extends ApiTask<ApiStatusResponse> {
    /**
     * Function sends request to API. For accepting awards.
     *
     * @param sessionToken required to verify the user
     */
    public EarnRewardTask(String sessionToken) {
        super(Request.Method.POST, ConstantValues.URL_EARN_REWARD, ApiStatusResponse.class);
        // set params
        setParam("session-token", sessionToken);
    }
}
