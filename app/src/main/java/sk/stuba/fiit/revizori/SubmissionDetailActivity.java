package sk.stuba.fiit.revizori;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class SubmissionDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private String photoUrl;
    private LatLng submissionPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.submission_detail);

        TextView lineNumber = (TextView) findViewById(R.id.line_number_detail);
        lineNumber.setText(getIntent().getStringExtra("lineNumber"));
        TextView timePostion = (TextView) findViewById(R.id.time_position);
        timePostion.setText(getIntent().getStringExtra("time") + " " + getIntent().getStringExtra("distance"));
        TextView comment = (TextView) findViewById(R.id.comment);
        comment.setText(getIntent().getStringExtra("comment"));
       // String a = getIntent().getStringExtra("latitude");
        //submissionPosition = new LatLng(Double.parseDouble(getIntent().getStringExtra("latitude")), Double.parseDouble(getIntent().getStringExtra("longitude")));
        photoUrl = getIntent().getStringExtra("photoUrl");




        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.submission_position_map);
        mapFragment.getMapAsync(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ImageView revizorPhoto = (ImageView) findViewById(R.id.revizor_photo);

        new ImageLoadTask(photoUrl, revizorPhoto).execute();

        revizorPhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Intent intent = new Intent(SubmissionDetailActivity.this, PhotoDetail.class);
                intent.putExtra("photo_url", photoUrl);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        submissionPosition = new LatLng(-34, 151);
        map.addMarker(new MarkerOptions().position(submissionPosition).title(""));
        map.moveCamera(CameraUpdateFactory.newLatLng(submissionPosition));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
