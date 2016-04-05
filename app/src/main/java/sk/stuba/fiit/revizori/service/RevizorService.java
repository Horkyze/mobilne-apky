package sk.stuba.fiit.revizori.service;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import sk.stuba.fiit.revizori.Revizori;
import sk.stuba.fiit.revizori.VolleySingleton;
import sk.stuba.fiit.revizori.backendless.BackendlessCoreRequest;
import sk.stuba.fiit.revizori.backendless.BackendlessRequest;
import sk.stuba.fiit.revizori.model.Revizor;


public class RevizorService {

    private static RevizorService ourInstance = new RevizorService();

    public static RevizorService getInstance() {
        return ourInstance;
    }

    String url = "/revizor";

    public ArrayList<Revizor> getRevizori() {
        return revizori;
    }

    ArrayList<Revizor> revizori = new ArrayList<>();

    public void createRevizor(Revizor r){
        BackendlessRequest br = new BackendlessRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        BackendlessCoreRequest request = br.getRequest();
        request.setBody(r.getPOSTjson());
        System.out.println(r.getPOSTjson());
        VolleySingleton.getInstance(Revizori.getAppContext()).getRequestQueue().add(request);

    }

    public void getAll(){
        BackendlessRequest br = new BackendlessRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonReader = new JSONObject(response);
                            JSONArray submissions = jsonReader.getJSONArray("data");
                            ArrayList<String> lineNumbers = new ArrayList<String>();
                            for(int i = 0; i < submissions.length(); i++)
                            {
                                JSONObject submission = submissions.getJSONObject(i);
                                Revizor r = new Revizor(
                                    submission.getString("line_number"),
                                    submission.getDouble("latitude"),
                                    submission.getDouble("longitude"),
                                    submission.getString("photo_url"),
                                    submission.getString("comment"));

                                r.setCreated(new Date(submission.getLong("created")));
                                r.setUpdated(new Date(submission.getLong("updated")));
                                r.setObjectId(submission.getString("objectId"));
                                r.setOwnerId(submission.getString("ownerId"));
                                revizori.add(r);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            // stop refreshing (spinning icon)
                            // how ??

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                } );
        VolleySingleton.getInstance(Revizori.getAppContext()).getRequestQueue().add(br.getRequest());

    }
}
