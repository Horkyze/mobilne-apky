package sk.stuba.fiit.revizori.model;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

public class Revizor extends ModelBase {
    private String line_number;
    private double latitude;
    private double longitude;
    private String photo_url;
    private String comment;

    public Revizor(String line_number, double latitude, double longitude, String photo_url, String comment) {
        this.line_number = line_number;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photo_url = photo_url;
        this.comment = comment;
        this.setCreated(new Date());
        this.setUpdated(new Date());
        this.setObjectId(UUID.randomUUID().toString());
    }

    public Revizor(String line_number, double latitude, double longitude) {
        this.line_number = line_number;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photo_url = "";
        this.comment = "";
        this.setCreated(new Date());
        this.setUpdated(new Date());
        this.setObjectId(UUID.randomUUID().toString());
    }

    public JSONObject getPUTJson(){

        JSONObject jo = new JSONObject();
        char QUOTE = '"';
        String meta = "{"+QUOTE+"relationRemovalIds"+QUOTE+":{},"+QUOTE+"selectedProperties"+QUOTE+":["+QUOTE+"line_number"+QUOTE+","+QUOTE+"created"+QUOTE+","+QUOTE+"___saved"+QUOTE+","+QUOTE+"latitude"+QUOTE+","+QUOTE+"___class"+QUOTE+","+QUOTE+"comment"+QUOTE+","+QUOTE+"photo_url"+QUOTE+","+QUOTE+"ownerId"+QUOTE+","+QUOTE+"updated"+QUOTE+","+QUOTE+"objectId"+QUOTE+","+QUOTE+"longitude"+QUOTE+"],"+QUOTE+"relatedObjects"+QUOTE+":{}}";
        try {
            jo.put("line_number", this.line_number);
            jo.put("latitude", this.latitude);
            jo.put("longitude", this.longitude);
            if (photo_url.isEmpty()){
                jo.put(photo_url, null);
            } else {
                jo.put("photo_url", this.photo_url);
            }
            jo.put("comment", this.comment);
            jo.put("___class", "revizor");
            jo.put("__meta", meta);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jo;
    }

    public JSONObject getPOSTjson(){

        JSONObject jo = new JSONObject();
        //char QUOTE = '"';
        //String meta = "{"+QUOTE+"relationRemovalIds"+QUOTE+":{},"+QUOTE+"selectedProperties"+QUOTE+":["+QUOTE+"line_number"+QUOTE+","+QUOTE+"created"+QUOTE+","+QUOTE+"___saved"+QUOTE+","+QUOTE+"latitude"+QUOTE+","+QUOTE+"___class"+QUOTE+","+QUOTE+"comment"+QUOTE+","+QUOTE+"photo_url"+QUOTE+","+QUOTE+"ownerId"+QUOTE+","+QUOTE+"updated"+QUOTE+","+QUOTE+"objectId"+QUOTE+","+QUOTE+"longitude"+QUOTE+"],"+QUOTE+"relatedObjects"+QUOTE+":{}}";
        try {
            jo.put("line_number", this.line_number);
            jo.put("latitude", this.latitude);
            jo.put("longitude", this.longitude);
            if (photo_url.isEmpty()){
                jo.put("photo_url", "http://i.imgur.com/1muyJ.jpg");
            } else {
                jo.put("photo_url", this.photo_url);
            }
            jo.put("comment", this.comment);
            jo.put("objectId", this.getObjectId());
            //jo.put("ownerId", this.getOwnerId());
            jo.put("created", this.getCreated());
            jo.put("updated", this.getUpdated());
            //jo.put("___class", "revizor");
            //jo.put("__meta", meta);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jo;
    }

    public String getLine_number() {
        return line_number;
    }

    public void setLine_number(String line_number) {
        this.line_number = line_number;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
