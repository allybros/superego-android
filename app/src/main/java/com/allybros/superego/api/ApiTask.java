package com.allybros.superego.api;

import android.content.Context;
import android.util.Log;

import com.allybros.superego.api.response.ApiErrorResponse;
import com.allybros.superego.unit.ErrorCodes;
import com.allybros.superego.util.ClientContextUtil;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.HashMap;
import java.util.Map;

public class ApiTask<T> {

    private final StringRequest request;
    private final Map<String,String> params;
    private final Map<String,String> headers;
    private final int requestMethod;
    private final String url;
    private final Class<T> responseClass;
    private ApiCallback<T> onResponseListener;
    private ApiCallback<ApiErrorResponse> onErrorListener;

    public ApiTask(int requestMethod, String url, Class<T> responseClass) {
        this.requestMethod = requestMethod;
        this.url = url;
        this.responseClass = responseClass;
        this.headers = new HashMap<>();
        this.params = new HashMap<>();
        this.request = createRequest();
    }

    private StringRequest createRequest() {
        // Create request object
        return new StringRequest(requestMethod, url,
                response -> {
                    Log.d("ApiResponse", response);
                    T apiResponse = mapApiResponse(response);
                    onResponseListener.onApiResponse(apiResponse);
                },
                error -> {
                    ApiErrorResponse errorResponse = mapApiError(error);
                    onErrorListener.onApiResponse(errorResponse);
                }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    return ApiTask.this.params;
                }
                @Override
                public Map<String, String> getHeaders() {
                    return ApiTask.this.headers;
                }
            };
        }

    protected T mapApiResponse(String responseString) {
        Gson gson = new Gson();
        try {
            return gson.fromJson(responseString, this.responseClass);
        } catch (JsonSyntaxException exception) {
            Log.e("ApiTask", "Json syntax is invalid");
            return null;
        }
    }

    protected ApiErrorResponse mapApiError(VolleyError failure) {
        // Create a fake api response
        ApiErrorResponse errorResponse = new ApiErrorResponse();
        errorResponse.setStatus(ErrorCodes.CONNECTION_ERROR);
        errorResponse.setMessage("Api response is not received");
        return errorResponse;
    }

    public void setOnResponseListener(ApiCallback<T> onResponseListener) {
        this.onResponseListener = onResponseListener;
    }

    public void setOnErrorListener(ApiCallback<ApiErrorResponse> onErrorListener) {
        this.onErrorListener = onErrorListener;
    }

    public void setParam(String key, String value) {
        this.params.put(key, value);
    }

    public String getParam(String key) {
        return this.params.get(key);
    }

    public void setHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public String getHeader(String key) {
        return this.headers.get(key);
    }

    public void execute(Context context) {
        // Add context information
        this.setHeader("Accept-Language", ClientContextUtil.getApplicationLocale(context));
        this.setHeader("Http-Client-Version", ClientContextUtil.getVersionName(context));
        // Send request
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(this.request);
    }

    public interface ApiCallback<T> {
        void onApiResponse(T response);
    }

}
