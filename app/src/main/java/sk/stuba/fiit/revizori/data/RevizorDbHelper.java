/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sk.stuba.fiit.revizori.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages a local database for weather data.
 */
public class RevizorDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "revizori.db";

    public RevizorDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold locations.  A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude

        final String SQL_CREATE_REVIZOR_TABLE = "CREATE TABLE " + RevizorContract.RevizorEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                RevizorContract.RevizorEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                RevizorContract.RevizorEntry.COLUMN_OBJECT_ID + " TEXT NOT NULL, " +
                RevizorContract.RevizorEntry.COLUMN_OWNER_ID  + " TEXT, " +
                RevizorContract.RevizorEntry.COLUMN_CREATED   + " INTEGER  NOT NULL, " +
                RevizorContract.RevizorEntry.COLUMN_UPDATED   + " INTEGER ," +

                RevizorContract.RevizorEntry.COLUMN_LINE_NUMBER + " TEXT NOT NULL, " +
                RevizorContract.RevizorEntry.COLUMN_LONGITUDE   + " REAL NOT NULL, " +
                RevizorContract.RevizorEntry.COLUMN_LATITUDE    + " REAL NOT NULL, " +
                RevizorContract.RevizorEntry.COLUMN_PHOTO_URL   + " TEXT, " +
                RevizorContract.RevizorEntry.COLUMN_COMMENT     + " TEXT, " +


                // To assure the application have just one weather entry per day
                // per location, it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + RevizorContract.RevizorEntry.COLUMN_OBJECT_ID  + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_REVIZOR_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RevizorContract.RevizorEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
