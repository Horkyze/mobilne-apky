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
import sk.stuba.fiit.revizori.websocket.WebSocketHandler;


public class RevizorService {

    private static RevizorService ourInstance = new RevizorService();

    public static RevizorService getInstance() {
        return ourInstance;
    }


    String url = "/revizor";

    // app generated uuidv4
    String user = "l337beef1";

    public ArrayList<Revizor> getRevizori() {
        return revizori;
    }

    ArrayList<Revizor> revizori = new ArrayList<>();

    public void createRevizor(Revizor r){

        Socket socket = WebSocketHandler.getSocket();

        JSONObject obj = new JSONObject();
        try {
            obj.put("url", "/data/" + this.user);
            obj.put("data", new JSONObject().put("data", r.getPOSTjson()));
        } catch (Exception e){

        }


        socket.emit("post", obj, new Ack() {
            @Override
            public void call(Object... args) {
                Log.d("WEBSOCKET", "GOT RESPONSE");
                JSONObject o = (JSONObject)args[0];
                Log.d("WEBSICKET", o.toString());
            }
        });

    }

    public void getAll(){

        Socket socket = WebSocketHandler.getSocket();

        JSONObject obj = new JSONObject();
        try {
            obj.put("url", "/data/" + this.user);
        } catch (Exception e){

        }

        socket.emit("get", obj, new Ack() {
            @Override
            public void call(Object... args) {
                Log.d("WEBSOCKET", "GOT RESPONSE GET ALL");
                // put eveything into db
                try {
                    JSONObject o = (JSONObject) args[0];
                    Log.d("WEBSOCKET", o.toString());
                    JSONObject body = o.getJSONObject("body");
                    JSONArray array = body.getJSONArray("data");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject entry = array.getJSONObject(i);
                        JSONObject data = entry.getJSONObject("data");

                        ContentValues cv = new ContentValues();
                        cv.put(RevizorContract.RevizorEntry.COLUMN_LINE_NUMBER, data.getString("line_number"));
                        cv.put(RevizorContract.RevizorEntry.COLUMN_LATITUDE, data.getString("latitude"));
                        cv.put(RevizorContract.RevizorEntry.COLUMN_LONGITUDE, data.getString("longitude"));
                        cv.put(RevizorContract.RevizorEntry.COLUMN_PHOTO_URL, data.getString("photo_url"));
                        cv.put(RevizorContract.RevizorEntry.COLUMN_COMMENT, data.getString("comment"));
                        cv.put(RevizorContract.RevizorEntry.COLUMN_OBJECT_ID,  entry.getString("id"));
                        cv.put(RevizorContract.RevizorEntry.COLUMN_OWNER_ID,  entry.getString("id"));
                        cv.put(RevizorContract.RevizorEntry.COLUMN_CREATED, data.getString("created"));
                        cv.put(RevizorContract.RevizorEntry.COLUMN_UPDATED, data.getString("updated"));

                        ContentResolver cr = Revizori.getAppContext().getContentResolver();
                        Uri u = cr.insert(RevizorContract.RevizorEntry.CONTENT_URI, cv);
                        Log.i("content provider", "insert ");
                        cr.query(RevizorContract.RevizorEntry.CONTENT_URI, null, null, null, null);

                    }
                } catch (Exception e){
                    Log.e("WEBSOCKET", "get all uppaa" + e.getMessage());
                    e.printStackTrace();
                }

            }
        });

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

        Socket socket = WebSocketHandler.getSocket();

        JSONObject obj = new JSONObject();
        try {
            obj.put("url", "/data/" + this.user + "/" + objectId);
        } catch (Exception e){

        }

        socket.emit("delete", obj, new Ack() {
            @Override
            public void call(Object... args) {
                Log.d("WEBSOCKET", "DELETE");
            }
        });

    }

    public void update(Revizor r){
        Uri uri = RevizorContract.RevizorEntry.buildRevizorUri(r.get_id());
        Cursor c = Revizori.getAppContext().getContentResolver().query(uri, null, null, null, null);

        try {
            c.moveToFirst();
            r.setObjectId( c.getString(1) );
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        c.close();

        Socket socket = WebSocketHandler.getSocket();

        JSONObject obj = new JSONObject();
        try {
            obj.put("url", "/data/" + this.user + "/" + r.getObjectId());
            obj.put("data", new JSONObject().put("data", r.getPOSTjson()));
        } catch (Exception e){

        }

        socket.emit("put", obj, new Ack() {
            @Override
            public void call(Object... args) {
                Log.d("WEBSOCKET", "UPDATE");
            }
        });

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
            socket = IO.socket("http://sandbox.touch4it.com:1341", opts);
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
        socket.connect();

    }

    public void create_webSocket(){


    }
}
