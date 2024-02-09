package com.allybros.superego.api;

import com.allybros.superego.api.response.ApiStatusResponse;
import com.allybros.superego.unit.ConstantValues;
import com.android.volley.Request;

/**
 * Class includes the function of changing users profile information
 *
 * @author 0rcun
 */
public class ChangeInfoTask extends ApiTask<ApiStatusResponse> {
    /**
     * User informations change request to API
     *
     * @param sessionToken   required to verify the user
     * @param newUid         required to change old username
     * @param newEmail       required to change old mail
     * @param newInformation required to change old information
     */
    public ChangeInfoTask(String sessionToken, String newUid, String newEmail, String newInformation) {
        super(Request.Method.POST, ConstantValues.UPDATE_INFORMATION, ApiStatusResponse.class);
        // set params
        setParam("session-token", sessionToken);
        setParam("new-username", newUid);
        setParam("new-email", newEmail);
        setParam("new-user-bio", newInformation);
    }
}




