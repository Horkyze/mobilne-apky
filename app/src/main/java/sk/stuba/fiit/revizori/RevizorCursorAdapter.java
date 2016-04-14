package sk.stuba.fiit.revizori;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import sk.stuba.fiit.revizori.data.RevizorContract;
import sk.stuba.fiit.revizori.service.LocationService;


public class RevizorCursorAdapter extends CursorAdapter {

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
        Date date = new Date(unixSeconds);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+4"));
        String formattedDate = sdf.format(date);
        time.setText(formattedDate);

        double latitude = cursor.getDouble(
                cursor.getColumnIndex(RevizorContract.RevizorEntry.COLUMN_LATITUDE)
        );
        double longitude = cursor.getDouble(
                                cursor.getColumnIndex(RevizorContract.RevizorEntry.COLUMN_LONGITUDE)
                            );

        distance.setText(computeDistance(latitude, longitude) + " km od vás");
    }

    private float computeDistance(double latitude, double longitude){
        Location submissionLocation = new Location("submission");
        submissionLocation.setLatitude(latitude);
        submissionLocation.setLongitude(longitude);
        Location myLocation = getMyLocation();
        float distance = submissionLocation.distanceTo(myLocation);
        if(distance > 100){
            distance = (float)(Math.round(distance / 1000 * 10d) / 10d);
        }
        return distance;
    }

    private Location getMyLocation(){
        LocationService ls = LocationService.getInstance();
        Location myLocation = ls.getBestLocation();
        return myLocation;
    }
}
