package com.allybros.superego.api;

import com.allybros.superego.api.response.SearchResponse;
import com.allybros.superego.unit.ConstantValues;
import com.android.volley.Request;

/**
 * Class includes the function of searching user
 *
 * @author umutalacam
 */
public class SearchTask extends ApiTask<SearchResponse[]> {
    /**
     * The function sends a request for searching user.
     *
     * @param query required for searching from server
     */
    public SearchTask(String query) {
        super(Request.Method.GET, ConstantValues.SEARCH_URL + query, SearchResponse[].class);
    }
}
