package sk.stuba.fiit.revizori.backendless;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Simon on 14.4.2016.
 */
public class BackendlessJsonRequest extends JsonObjectRequest {


    public BackendlessJsonRequest(int method, String url, JSONObject object, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url,object, listener, errorListener);
    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return Backendless.getHeaders();
    }


}
