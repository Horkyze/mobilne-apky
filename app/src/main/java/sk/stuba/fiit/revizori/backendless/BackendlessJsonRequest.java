package sk.stuba.fiit.revizori.backendless;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Simon on 14.4.2016.
 */
public class BackendlessJsonRequest extends JsonObjectRequest {

    /**
     * Holds the last part of url
     * eg. "/revizor"
     */
    private String entity;
    private Map<String, String> params;
    private byte [] body;

    private String url = "http://api.backendless.com/v1/data";
    private String appId = "FAE4BDAB-E4A4-654C-FFFA-A005AD5E5D00";
    private String contentType = "application/json";


    private boolean hasContentType = false;
    private String secret = "B2C47E23-06D1-5232-FF77-07E29B78AA00";

    public BackendlessJsonRequest(String url, JSONObject object, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super( "",object, listener, errorListener);
        this.entity = url;
    }

    @Override
    public String getUrl() {
        return this.url + this.entity;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public void setBody(String body) {
        this.body = body.getBytes();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map params = new HashMap<String, String>();
        params.put("application-id", this.appId);
        params.put("secret-key", this.secret);
        if(hasContentType){
            params.put("Content-Type", this.contentType);
        }
        return params;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return super.getParams();
    }


}
