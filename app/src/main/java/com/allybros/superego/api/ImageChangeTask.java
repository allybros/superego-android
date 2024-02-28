package com.allybros.superego.api;

import com.allybros.superego.api.response.ApiStatusResponse;
import com.allybros.superego.unit.ConstantValues;
import com.android.volley.Request;

/**
 * Class includes the function of changing image
 *
 * @author orcunkamiloglu
 */
public class ImageChangeTask extends ApiTask<ApiStatusResponse> {


    /**
     * Function sends request to API. For changing image.
     *
     * @param image        that is to be changed image
     * @param sessionToken required to verify the user
     */
    public ImageChangeTask(String sessionToken, String image) {
        super(Request.Method.POST, ConstantValues.UPLOAD_IMAGE, ApiStatusResponse.class);
        // set params
        setParam("session-token", sessionToken);
        setParam("new-avatar-base64", image);
    }
}
