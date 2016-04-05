package sk.stuba.fiit.revizori.model;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
    }

    public Revizor(String line_number, double latitude, double longitude) {
        this.line_number = line_number;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photo_url = "";
        this.comment = "";
    }

    public String getPOSTjson(){
        Gson gson = new Gson();
        gson.toJson(this);
        JsonObject jo = new JsonObject();
        jo.addProperty("line_number", this.line_number);
        jo.addProperty("latitude", this.latitude);
        jo.addProperty("longitude", this.longitude);
        jo.addProperty("photo_url", this.photo_url);
        jo.addProperty("comment", this.comment);
        jo.addProperty("___class", "revizor");
        jo.addProperty("__meta", "{\"relationRemovalIds\":{},\"selectedProperties\":[\"line_number\",\"created\",\"___saved\",\"latitude\",\"___class\",\"comment\",\"photo_url\",\"ownerId\",\"updated\",\"objectId\",\"longitude\"],\"relatedObjects\":{}}");
        return jo.getAsString();
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
