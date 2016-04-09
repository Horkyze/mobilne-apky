package sk.stuba.fiit.revizori;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import sk.stuba.fiit.revizori.data.RevizorContract;


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
        TextView lineNumber = (TextView) view.findViewById(R.id.line_number);
        TextView distance = (TextView) view.findViewById(R.id.distance);
        TextView time = (TextView) view.findViewById(R.id.time);

        lineNumber.setText(
                cursor.getString(
                        cursor.getColumnIndex("_id")
                )
        );

        time.setText(
                cursor.getString(
                        cursor.getColumnIndex(RevizorContract.RevizorEntry.COLUMN_CREATED)
                )
        );

        distance.setText("5 km o d vas");
    }
}
