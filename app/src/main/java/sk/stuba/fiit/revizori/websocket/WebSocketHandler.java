package sk.stuba.fiit.revizori.websocket;

import android.content.Context;
import android.util.Log;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by hork on 12/05/16.
 */
public class WebSocketHandler {

    private static WebSocketHandler instance = null;
    private Socket socket;

    public WebSocketHandler(){
        Log.d("WebSocketHandler", "WebSocketHandler()");
        IO.Options opts = new IO.Options();
        opts.secure = false;
        opts.port = 1341;
        opts.reconnection = true;
        opts.forceNew = true;
        opts.timeout = 5000;

        try {
            socket = IO.socket("http://sandbox.touch4it.com:1341/?__sails_io_sdk_version=0.12.1", opts);
            socket.connect();
            Log.d("WebSocketHandler", "create OK");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Socket getSocket(){
        return WebSocketHandler.getInstance().socket;
    }


    public static WebSocketHandler getInstance() {
        if (instance == null) {
            instance = new WebSocketHandler();
        }
        return instance;
    }


}
