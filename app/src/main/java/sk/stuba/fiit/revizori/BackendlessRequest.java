package sk.stuba.fiit.revizori;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;


public class BackendlessRequest extends StringRequest {

    String url = "https://api.backendless.com/v1/data/revizor";
    String appId = "FAE4BDAB-E4A4-654C-FFFA-A005AD5E5D00";
    String secret = "B2C47E23-06D1-5232-FF77-07E29B78AA00";

    public BackendlessRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String>  params = new HashMap<String, String>();
        params.put("application-id", this.appId);
        params.put("secret-key", this.secret);
        return params;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return super.getParams();
    }
}
