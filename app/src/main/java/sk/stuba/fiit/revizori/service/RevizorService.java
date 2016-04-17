package sk.stuba.fiit.revizori.service;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.alirezaafkar.json.requester.Requester;
import com.android.volley.AuthFailureError;
import com.android.volley.ClientError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import sk.stuba.fiit.revizori.Revizori;
import sk.stuba.fiit.revizori.VolleySingleton;
import sk.stuba.fiit.revizori.backendless.Backendless;
import sk.stuba.fiit.revizori.backendless.BackendlessCoreRequest;
import sk.stuba.fiit.revizori.backendless.BackendlessJsonRequest;
import sk.stuba.fiit.revizori.backendless.BackendlessRequest;
import sk.stuba.fiit.revizori.data.RevizorContract;
import sk.stuba.fiit.revizori.data.RevizorProvider;
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
        BackendlessJsonRequest request = new BackendlessJsonRequest(Request.Method.PUT, url, r.getPOSTjson(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response.toString());
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Volley", error.getMessage());
                error.printStackTrace();

            }
        });
        VolleySingleton.getInstance(Revizori.getAppContext()).getRequestQueue().add(request);

    }

    public void getAll(){
        BackendlessRequest br = new BackendlessRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("some", "got response");
                            JSONObject jsonReader = new JSONObject(response);
                            JSONArray submissions = jsonReader.getJSONArray("data");
                            for(int i = 0; i < submissions.length(); i++)
                            {
                                JSONObject sub = submissions.getJSONObject(i);
//                                Revizor r = new Revizor(
//                                    sub.getString("line_number"),
//                                    sub.getDouble("latitude"),
//                                    sub.getDouble("longitude"),
//                                    sub.getString("photo_url"),
//                                    sub.getString("comment"));

                                if (sub.isNull("updated"))
                                    sub.put("updated", 0);
                                if (sub.isNull("ownerId"))
                                    sub.put("ownerId", 0);

//                                r.setCreated(new Date(sub.getInt("created")));
//                                r.setUpdated(new Date(sub.getInt("updated")));
//                                r.setObjectId(sub.getString("objectId"));
//                                r.setOwnerId(sub.getString("ownerId"));

//                                revizori.add(r);

                                ContentValues cv = new ContentValues();
                                cv.put(RevizorContract.RevizorEntry.COLUMN_LINE_NUMBER, sub.getString("line_number"));
                                cv.put(RevizorContract.RevizorEntry.COLUMN_LATITUDE, sub.getString("latitude"));
                                cv.put(RevizorContract.RevizorEntry.COLUMN_LONGITUDE, sub.getString("longitude"));
                                cv.put(RevizorContract.RevizorEntry.COLUMN_PHOTO_URL, sub.getString("photo_url"));
                                cv.put(RevizorContract.RevizorEntry.COLUMN_COMMENT, sub.getString("comment"));
                                cv.put(RevizorContract.RevizorEntry.COLUMN_OBJECT_ID, sub.getString("objectId"));
                                cv.put(RevizorContract.RevizorEntry.COLUMN_OWNER_ID, sub.getString("ownerId"));
                                cv.put(RevizorContract.RevizorEntry.COLUMN_CREATED, sub.getString("created"));
                                cv.put(RevizorContract.RevizorEntry.COLUMN_UPDATED, sub.getString("updated"));

                                ContentResolver cr = Revizori.getAppContext().getContentResolver();
                                Uri u = cr.insert(RevizorContract.RevizorEntry.CONTENT_URI, cv);
                                Log.i("content provider", "insert: " + u.toString());
                                cr.query(RevizorContract.RevizorEntry.CONTENT_URI, null, null, null, null);
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
    public void delete(int id){
        Uri uri = RevizorContract.RevizorEntry.buildRevizorUri(id);
        Cursor c = Revizori.getAppContext().getContentResolver().query(uri, null, null, null, null);
        String objectId;

        try {
            c.moveToFirst();
            objectId = c.getString(1);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        c.close();


        JSONObject json = new JSONObject();
        try {
            json.put("objectId", objectId);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

//        BackendlessRequest backendlessRequest = new BackendlessRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.d("delete ok", "");
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.d("delete failed ", "");
//            }
//        });
//        backendlessRequest.getRequest().setBody(json.toString().getBytes());


        BackendlessJsonRequest jsonRequest = new BackendlessJsonRequest(Request.Method.DELETE, Backendless.url, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("delete ok", "");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("delete failed ", "");
            }
        });
        VolleySingleton.getInstance(Revizori.getAppContext()).getRequestQueue().add(jsonRequest);




    }
}
