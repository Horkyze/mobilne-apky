package sk.stuba.fiit.revizori.backendless;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;


public class BackendlessRequest {

    public BackendlessCoreRequest getRequest() {
        return request;
    }

    public BackendlessCoreRequest request;

    public BackendlessRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        this.request = new BackendlessCoreRequest(method, url, listener, errorListener);

    }



}
