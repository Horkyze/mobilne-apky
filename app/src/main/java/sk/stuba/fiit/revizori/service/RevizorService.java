package sk.stuba.fiit.revizori.service;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

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

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import sk.stuba.fiit.revizori.Revizori;
import sk.stuba.fiit.revizori.VolleySingleton;
import sk.stuba.fiit.revizori.backendless.Backendless;
import sk.stuba.fiit.revizori.backendless.BackendlessCoreRequest;
import sk.stuba.fiit.revizori.backendless.BackendlessJsonRequest;
import sk.stuba.fiit.revizori.backendless.BackendlessRequest;
import sk.stuba.fiit.revizori.data.RevizorContract;
import sk.stuba.fiit.revizori.data.RevizorProvider;
import sk.stuba.fiit.revizori.model.Revizor;
import sk.stuba.fiit.revizori.sync.SyncAdapter;
import sk.stuba.fiit.revizori.sync.SyncService;


public class RevizorService {

    private static RevizorService ourInstance = new RevizorService();

    public static RevizorService getInstance() {
        return ourInstance;
    }


    String url = "/revizor";

    // app generated uuidv4
    String user = "d7ef639c-83c8-40d0-ab9d-2bd655191804";

    public ArrayList<Revizor> getRevizori() {
        return revizori;
    }

    ArrayList<Revizor> revizori = new ArrayList<>();

    public void createRevizor(Revizor r){
        BackendlessJsonRequest request = new BackendlessJsonRequest(Request.Method.POST, Backendless.url, r.getPOSTjson(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response.toString());
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Volley", error.getMessage());
                error.printStackTrace();
                Toast.makeText(Revizori.getAppContext(), "Nepodarilo sa vytvoriť nový príspevok", Toast.LENGTH_LONG).show();


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


        BackendlessJsonRequest jsonRequest = new BackendlessJsonRequest(Request.Method.DELETE, Backendless.url + "/" +  objectId, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d("delete ok", "ok");
                SyncAdapter.syncImmediately(Revizori.getAppContext());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("delete failed", "not ok");
                Toast.makeText(Revizori.getAppContext(), "Nepodarilo sa zmazať príspevok", Toast.LENGTH_LONG).show();
            }
        });
        VolleySingleton.getInstance(Revizori.getAppContext()).getRequestQueue().add(jsonRequest);

    }

    public void update(Revizor r){
        Uri uri = RevizorContract.RevizorEntry.buildRevizorUri(r.get_id());
        Cursor c = Revizori.getAppContext().getContentResolver().query(uri, null, null, null, null);

        try {
            c.moveToFirst();
            r.setObjectId(c.getString(1));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        c.close();

        BackendlessJsonRequest jsonRequest = new BackendlessJsonRequest(Request.Method.PUT, Backendless.url + "/" + r.getObjectId(), r.getPUTJson(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d("PUT ok", "ok");
                SyncAdapter.syncImmediately(Revizori.getAppContext());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("PUT failed", "not ok");
                Toast.makeText(Revizori.getAppContext(), "Nepodarilo sa upraviť príspevok", Toast.LENGTH_LONG).show();
            }
        });
        VolleySingleton.getInstance(Revizori.getAppContext()).getRequestQueue().add(jsonRequest);
    }

    public void getAll_webSocket(){
        final Socket socket;
        IO.Options opts = new IO.Options();
        opts.secure = false;
        opts.port = 1341;
        opts.reconnection = true;
        opts.forceNew = true;
        opts.timeout = 5000;
        try {
            socket = IO.socket("http://sandbox.touch4it.com:1341/?__sails_io_sdk_version=0.12.1", opts);
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        JSONObject js = new JSONObject();
        try {
            js.put("url", "/data/d7ef639c-83c8-40d0-ab9d-2bd655191804");
            js.put("data", new JSONObject().put("data", new JSONObject("     {\n" +
                    "         \\\"Employee\\\" :[\n" +
                    "         {\n" +
                    "            \\\"id\\\":\\\"01\\\",\n" +
                    "            \\\"name\\\":\\\"Gopal Varma\\\",\n" +
                    "            \\\"salary\\\":\\\"500000\\\"\n" +
                    "         },\n" +
                    "         {\n" +
                    "            \\\"id\\\":\\\"02\\\",\n" +
                    "            \\\"name\\\":\\\"Sairamkrishna\\\",\n" +
                    "            \\\"salary\\\":\\\"500000\\\"\n" +
                    "         },\n" +
                    "         {\n" +
                    "            \\\"id\\\":\\\"03\\\",\n" +
                    "            \\\"name\\\":\\\"Sathish kallakuri\\\",\n" +
                    "            \\\"salary\\\":\\\"600000\\\"\n" +
                    "         }\n" +
                    "         ] \n" +
                    "      }")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit("post", js, new Ack() {
            @Override
            public void call(Object... args) {
                System.out.println("cau");}
        });


/*        final Socket socket;
        try {
            socket = IO.socket("http://sandbox.touch4it.com:1341/data");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                socket.emit("foo", "hi");
                socket.disconnect();
            }

        }).on("event", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                JSONObject obj = (JSONObject)args[0];
            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {}

        });
        socket.connect();*/

    }
}
