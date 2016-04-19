package sk.stuba.fiit.revizori;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.ListView;

import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import sk.stuba.fiit.revizori.backendless.BackendlessRequest;
import sk.stuba.fiit.revizori.data.RevizorContract;
import sk.stuba.fiit.revizori.data.RevizorDbHelper;
import sk.stuba.fiit.revizori.data.RevizorProvider;
import sk.stuba.fiit.revizori.model.Revizor;
import sk.stuba.fiit.revizori.service.RevizorService;
import sk.stuba.fiit.revizori.sync.SyncAdapter;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        SwipeRefreshLayout.OnRefreshListener {

    private Context context;
    private boolean mySubmissions;              //TRUE = only my, FLASE = all
    private RevizorCursorAdapter revizorCursorAdapter;
    // For the forecast view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] REVIZOR_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            RevizorContract.RevizorEntry.TABLE_NAME + "." + RevizorContract.RevizorEntry._ID,
            RevizorContract.RevizorEntry.COLUMN_LINE_NUMBER,
            RevizorContract.RevizorEntry.COLUMN_LONGITUDE,
            RevizorContract.RevizorEntry.COLUMN_LATITUDE,
            RevizorContract.RevizorEntry.COLUMN_PHOTO_URL,
            RevizorContract.RevizorEntry.COLUMN_COMMENT,
            RevizorContract.RevizorEntry.COLUMN_CREATED
    };

    // These indices are tied to REVIZOR_COLUMNS.
    // must math
    static final int COL_REVIZOR_ID = 0;
    static final int COL_REVIZOR_LINE_NUMBER = 1;
    static final int COL_REVIZOR_LONGITUDE = 2;
    static final int COL_REVIZOR_LANTITUDE = 3;
    static final int COL_REVIZOR_PHOTO_URL = 4;
    static final int COL_LOCATION_COMMENT = 5;
    static final int COL_WEATHER_CREATED = 6;



    private LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            /*
             * Takes action based on the ID of the Loader that's being created
             */
            Log.d("onCreateLoader", "Loader");
            switch (id) {
                case URL_LOADER:
                    // Returns a new CursorLoader
                    return new CursorLoader(
                            Revizori.getAppContext(),   // Parent activity context
                            RevizorContract.RevizorEntry.CONTENT_URI,        // Table to query
                            REVIZOR_COLUMNS,     // Projection to return
                            null,            // No selection clause
                            null,            // No selection arguments
                            null             // Default sort order
                    );
                default:
                    // An invalid id was passed in
                    return null;
            }
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            revizorCursorAdapter.changeCursor(data);
            revizorCursorAdapter.notifyDataSetChanged();
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            revizorCursorAdapter.changeCursor(null);
        }
    };

    private static final int URL_LOADER = 0;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //setContentView(R.layout.activity_create_post);
                Intent intent = new Intent(MainActivity.this, CreateRevizorActivity.class);
                startActivity(intent);

//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.all_submissions);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);


        getLoaderManager().initLoader(URL_LOADER, null, loaderCallbacks);
        revizorCursorAdapter = new RevizorCursorAdapter(getContext(), null, 0);

        SyncAdapter.initializeSyncAdapter(Revizori.getAppContext());

        final ListView listview = (ListView) findViewById(R.id.listView);
        listview.setAdapter(revizorCursorAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                if(mySubmissions){
                    intent = new Intent(MainActivity.this, EditSubmissionActivity.class);
                }
                else{
                    intent = new Intent(MainActivity.this, SubmissionDetailActivity.class);
                }
                Cursor cur = (Cursor) revizorCursorAdapter.getItem(position);
           
                cur.moveToPosition(position);
                intent.putExtra("objectId", revizorCursorAdapter.getObjectId(cur));
                intent.putExtra("lineNumber", revizorCursorAdapter.getLineNumber(cur));
                intent.putExtra("time", revizorCursorAdapter.getTime(cur));
                intent.putExtra("distance", revizorCursorAdapter.getDistance(cur));
                intent.putExtra("latitude", revizorCursorAdapter.getLatitude(cur));
                intent.putExtra("longitude", revizorCursorAdapter.getLongitude(cur));
                intent.putExtra("comment", revizorCursorAdapter.getComment(cur));
                intent.putExtra("photoUrl", revizorCursorAdapter.getPhotoUrl(cur));
                startActivity(intent);
            }
        });

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, final long id) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                //getContentResolver().delete(RevizorContract.RevizorEntry.buildRevizorUri(id), "_id = " + id, null);
                                RevizorService.getInstance().delete( (int) id );

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(listview.getContext());
                builder.setCancelable(true);

                builder.setMessage("Delete where _id = " + listview.getItemIdAtPosition(pos) + "?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

                Log.v("long clicked", "pos: " + pos + "  id: " + listview.getItemIdAtPosition(pos));

                return true;
            }
        });


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.my_submissions){
            mySubmissions = true;
        }

        if (id == R.id.all_submissions){
            mySubmissions = false;
        }

        if (id == R.id.logout) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        } else if (id == R.id.delete_local_db) {
        getContentResolver().delete(RevizorContract.RevizorEntry.CONTENT_URI, null, null);
    }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRefresh(){
        ListView listview = (ListView) findViewById(R.id.listView);
        SyncAdapter.syncImmediately(Revizori.getAppContext());

        swipeRefreshLayout.setRefreshing(false);

    }

    public Context getContext(){
        return this.context;
    }


}
