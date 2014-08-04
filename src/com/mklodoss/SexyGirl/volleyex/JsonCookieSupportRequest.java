package com.mklodoss.SexyGirl.volleyex;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mklodoss.SexyGirl.AppConfig;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dingdj on 2014/7/22.
 */
public class JsonCookieSupportRequest extends JsonObjectRequest {

    /**
     * Creates a new request.
     *
     * @param method        the HTTP method to use
     * @param url           URL to fetch the JSON from
     * @param jsonRequest   A {@link org.json.JSONObject} to post with the request. Null is allowed and
     *                      indicates no parameters will be posted along with request.
     * @param listener      Listener to receive the JSON response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public JsonCookieSupportRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    /**
     * Constructor which defaults to <code>GET</code> if <code>jsonRequest</code> is
     * <code>null</code>, <code>POST</code> otherwise.
     *
     * @param url
     * @param jsonRequest
     * @param listener
     * @param errorListener
     */
    public JsonCookieSupportRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        AppConfig.getInstance().checkSessionCookie(response.headers);
        try {
            String jsonString = new String(response.data, "UTF-8");

            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    /**
     * Returns a list of extra HTTP headers to go along with this request. Can
     * throw {@link com.android.volley.AuthFailureError} as authentication may be required to
     * provide these values.
     *
     * @throws com.android.volley.AuthFailureError In the event of auth failure
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();
        if (headers == null
                || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<String, String>();
        }
        AppConfig.getInstance().setCookieIfHave(headers);
        return headers;
    }
}
