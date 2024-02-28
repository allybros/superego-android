package com.allybros.superego.api;

import com.allybros.superego.api.response.AlertResponse;
import com.allybros.superego.unit.ConstantValues;
import com.android.volley.Request;

public class GetAlertsTask extends ApiTask<AlertResponse> {

    /**
     * Returns the current alerts to be shown to the user from API
     * @param versionName version name for filtering alerts
     */
    public GetAlertsTask(String versionName) {
        super(Request.Method.GET, AlertResponse.class);
        // Build request url
        String requestUrl = ConstantValues.ALERTS_URL;
        requestUrl += "?channel=android";
        requestUrl += "&version=" + versionName;
        this.setUrl(requestUrl);
    }

}
