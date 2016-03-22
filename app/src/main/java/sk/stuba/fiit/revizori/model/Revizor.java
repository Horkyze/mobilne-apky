package sk.stuba.fiit.revizori.model;

public class Revizor extends ModelBase {
    private String lineNumber;
    private double latitude;
    private double longitude;
    private String photo_url;
    private String comment;

    public Revizor(String lineNumber, double latitude, double longitude, String photo_url, String comment) {
        this.lineNumber = lineNumber;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photo_url = photo_url;
        this.comment = comment;
    }

    public Revizor(String lineNumber, double latitude, double longitude) {
        this.lineNumber = lineNumber;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photo_url = "";
        this.comment = "";
    }

    public String getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
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
