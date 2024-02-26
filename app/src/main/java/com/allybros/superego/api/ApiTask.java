package com.allybros.superego.api;

import android.content.Context;
import android.util.Log;

import com.allybros.superego.api.response.ApiStatusResponse;
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
    private final int requestMethod;
    private final Map<String, String> params;
    private final Map<String, String> headers;
    private final Class<T> responseClass;
    private String url;
    private ApiCallback<T> onResponseListener;
    private ApiCallback<ApiStatusResponse> onErrorListener;
    private static final String API_TASK = "ApiTask";
    private RequestQueue requestQueue;

    /**
     * Constructs an instance of ApiTask with the specified request method, URL, and response class.
     *
     * @param requestMethod The HTTP request method (e.g., Request.Method.GET, Request.Method.POST).
     * @param url           The URL to send the API request to.
     * @param responseClass The class type of the expected API response.
     */
    public ApiTask(int requestMethod, String url, Class<T> responseClass) {
        this(requestMethod, responseClass);
        this.url = url;
    }

    /**
     * Constructs an instance of ApiTask with the specified request method and response class.
     *
     * @param requestMethod The HTTP request method (e.g., Request.Method.GET, Request.Method.POST).
     * @param responseClass The class type of the expected API response.
     */
    public ApiTask(int requestMethod, Class<T> responseClass) {
        this.requestMethod = requestMethod;
        this.responseClass = responseClass;
        this.headers = new HashMap<>();
        this.params = new HashMap<>();
        // Default listeners won't do anything
        onErrorListener = response -> {
        };
        onResponseListener = response -> {
        };
    }

    /**
     * Maps response string to desired response type
     *
     * @param responseString Api response as plain string
     * @return object in type of T
     */
    protected T mapApiResponse(String responseString) {
        Gson gson = new Gson();
        try {
            return gson.fromJson(responseString, this.responseClass);
        } catch (JsonSyntaxException exception) {
            Log.e(API_TASK, "Json syntax is invalid");
            return null;
        }
    }

    /**
     * Maps the VolleyError object to an ApiErrorResponse object.
     *
     * @param failure The VolleyError object representing the API request failure.
     * @return The ApiErrorResponse object.
     */
    protected ApiStatusResponse mapApiError(VolleyError failure) {
        // Create a fake api response
        ApiStatusResponse errorResponse = new ApiStatusResponse();
        errorResponse.setStatus(ErrorCodes.CONNECTION_ERROR);
        errorResponse.setMessage("Api response is not received");
        return errorResponse;
    }

    /**
     * Sets the listener for the successful API response.
     *
     * @param onResponseListener The listener for the successful API response.
     */
    public void setOnResponseListener(ApiCallback<T> onResponseListener) {
        this.onResponseListener = onResponseListener;
    }

    /**
     * Sets the listener for the API request failure.
     *
     * @param onErrorListener The listener for the API request failure.
     */
    public void setOnErrorListener(ApiCallback<ApiStatusResponse> onErrorListener) {
        this.onErrorListener = onErrorListener;
    }

    /**
     * Sets a parameter for the API request.
     *
     * @param key   The parameter key.
     * @param value The parameter value.
     */
    public void setParam(String key, String value) {
        this.params.put(key, value);
    }

    /**
     * Retrieves the value of a parameter.
     *
     * @param key The parameter key.
     * @return The parameter value.
     */
    public String getParam(String key) {
        return this.params.get(key);
    }

    /**
     * Sets a header for the API request.
     *
     * @param key   The header key.
     * @param value The header value.
     */
    public void setHeader(String key, String value) {
        this.headers.put(key, value);
    }

    /**
     * Retrieves the value of a header.
     *
     * @param key The header key.
     * @return The header value.
     */
    public String getHeader(String key) {
        return this.headers.get(key);
    }

    /**
     * Sets the URL for the API request.
     *
     * @param url The URL for the API request.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Retrieves the URL of the API request.
     *
     * @return The URL of the API request.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Creates a StringRequest object for the API request.
     *
     * @return The StringRequest object.
     */
    private StringRequest createRequest() {
        // Create request object
        return new StringRequest(this.requestMethod, this.url,
                response -> {
                    Log.d(API_TASK, "Received response: " + response);
                    T apiResponse = mapApiResponse(response);
                    onResponseListener.onApiResponse(apiResponse);
                },
                error -> {
                    String errorMessage = error.getMessage();
                    Log.e(API_TASK, "Error on request: " + errorMessage);
                    ApiStatusResponse errorResponse = mapApiError(error);
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

    /**
     * Executes the API request
     *
     * @param context The application context.
     */
    public void execute(Context context) {
        // Add context information to header
        this.setHeader("Accept-Language", ClientContextUtil.getApplicationLocale(context));
        this.setHeader("Http-Client-Version", ClientContextUtil.getVersionName(context));

        // Send request
        RequestQueue queue = getRequestQueue(context);
        StringRequest request = createRequest();
        String requestUrl = request.getUrl();
        Log.d(API_TASK, "Send request: " + requestUrl);
        queue.add(request);
    }

    /**
     * Returns a request queue instance.
     *
     * @param context The application context.
     * @return the request queue instance
     */
    public RequestQueue getRequestQueue(Context context) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }

        return requestQueue;
    }

    /**
     * An interface for defining the callback for API responses.
     *
     * @param <T> The type of the API response.
     */
    public interface ApiCallback<T> {
        void onApiResponse(T response);
    }

}
