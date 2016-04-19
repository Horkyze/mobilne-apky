package sk.stuba.fiit.revizori;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import sk.stuba.fiit.revizori.data.RevizorContract;
import sk.stuba.fiit.revizori.service.LocationService;


public class RevizorCursorAdapter extends CursorAdapter implements Filterable {

    public RevizorCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.row_layout, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView lineNumber = (TextView) view.findViewById(R.id.row_line_number);
        TextView distance = (TextView) view.findViewById(R.id.distance);
        TextView time = (TextView) view.findViewById(R.id.time);

        lineNumber.setText(
                cursor.getString(
                        cursor.getColumnIndex(RevizorContract.RevizorEntry.COLUMN_LINE_NUMBER)
                )
        );

        long unixSeconds = cursor.getLong(
                cursor.getColumnIndex(RevizorContract.RevizorEntry.COLUMN_CREATED)
        );

        time.setText(unixTimeToDate(unixSeconds));

        double latitude = cursor.getDouble(
                cursor.getColumnIndex(RevizorContract.RevizorEntry.COLUMN_LATITUDE)
        );
        double longitude = cursor.getDouble(
                cursor.getColumnIndex(RevizorContract.RevizorEntry.COLUMN_LONGITUDE)
        );

        distance.setText(computeDistance(latitude, longitude) + " od vás");
    }

    private String computeDistance(double latitude, double longitude){
        Location submissionLocation = new Location("submission");
        submissionLocation.setLatitude(latitude);
        submissionLocation.setLongitude(longitude);
        Location myLocation = getMyLocation();
        float distance = submissionLocation.distanceTo(myLocation);
        if(distance > 100){
            distance = (float)(Math.round(distance / 1000 * 10d) / 10d);
            return distance + " km";
        }
        else{
            distance = (float)(Math.round(distance * 10d) / 10d);
            return distance + " m";
        }
    }

    private Location getMyLocation(){
        LocationService ls = LocationService.getInstance();
        Location myLocation = ls.getBestLocation();
        return myLocation;
    }

    private String unixTimeToDate(long unixSeconds){
        Date date = new Date(unixSeconds);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+2"));
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    public String getDistance(Cursor cursor){
        double latitude = cursor.getDouble(
                cursor.getColumnIndex(RevizorContract.RevizorEntry.COLUMN_LATITUDE)
        );
        double longitude = cursor.getDouble(
                cursor.getColumnIndex(RevizorContract.RevizorEntry.COLUMN_LONGITUDE)
        );
        return (computeDistance(latitude, longitude) + " od vás");
    }

    public String getPhotoUrl(Cursor cursor){
        return cursor.getString(
                cursor.getColumnIndex(RevizorContract.RevizorEntry.COLUMN_PHOTO_URL)
        );
    }

    public String getLineNumber(Cursor cursor){
        return cursor.getString(
                cursor.getColumnIndex(RevizorContract.RevizorEntry.COLUMN_LINE_NUMBER)
        );
    }

    public String getTime(Cursor cursor){
        long unixSeconds = cursor.getLong(
                cursor.getColumnIndex(RevizorContract.RevizorEntry.COLUMN_CREATED)
        );
        return (unixTimeToDate(unixSeconds));
    }

    public double getLongitude(Cursor cursor){
        return cursor.getDouble(
                cursor.getColumnIndex(RevizorContract.RevizorEntry.COLUMN_LONGITUDE)
        );
    }

    public double getLatitude(Cursor cursor){
        return cursor.getDouble(
                cursor.getColumnIndex(RevizorContract.RevizorEntry.COLUMN_LATITUDE)
        );
    }

    public String getComment(Cursor cursor){
        return cursor.getString(
                cursor.getColumnIndex(RevizorContract.RevizorEntry.COLUMN_COMMENT)
        );
    }

    public String getObjectId(Cursor cursor){
        return cursor.getString(
                1
        );
    }



}
