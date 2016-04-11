package sk.stuba.fiit.revizori;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;

import sk.stuba.fiit.revizori.model.Revizor;
import sk.stuba.fiit.revizori.service.RevizorService;

public class CreateRevizorActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private ImageView revizorPhoto;
    private AutoCompleteTextView lineNumber;
    private String[] lineNumbers;

    //localization
    LocationManager locationManager;
    String provider;
    LatLng myPosition;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        revizorPhoto = (ImageView) findViewById(R.id.revizorPhoto);

        lineNumber = (AutoCompleteTextView) findViewById(R.id.line_number);
        lineNumbers = getResources().getStringArray(R.array.lines_numbers);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lineNumbers);
        lineNumber.setAdapter(adapter);
        lineNumber.setValidator(new AutoCompleteTextView.Validator() {
            @Override
            public boolean isValid(CharSequence text) {
                return Arrays.asList(lineNumbers).contains(text.toString());
            }

            @Override
            public CharSequence fixText(CharSequence invalidText) {
                lineNumber.setError(getString(R.string.error_bad_line_number));
                return invalidText;
            }
        });



        Button addBtn = (Button) findViewById(R.id.createPostBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreatePostClick();
            }
        });

        Button takePhotoBtn = (Button) findViewById(R.id.takePhotoBtn);
        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTakePhotoClick();
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_p);
        mapFragment.getMapAsync(this);
    }

    public void onCreatePostClick() {
        boolean lineNumberExists = Arrays.asList(lineNumbers).contains(lineNumber.getText().toString());

        if (lineNumberExists) {
            TextView comment = (TextView) findViewById(R.id.comment);
            Revizor r = new Revizor(lineNumber.getText().toString(), Math.random(), Math.random(), "photourl", comment.getText().toString());
            RevizorService.getInstance().createRevizor(r);
            onBackPressed();
        } else {
            if (lineNumber.getText().toString().equals("")) {
                lineNumber.setError(getString(R.string.error_field_required));
            }
            lineNumber.requestFocus();
        }
    }

    public void onTakePhotoClick() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            revizorPhoto.setImageBitmap(imageBitmap);
            //upload code here
        }
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        }
            map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    getMyPosition();
                    return true;
                }
            });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), true);
        getMyPosition();
    }

    void getMyPosition(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(provider);
            myPosition = new LatLng(location.getLatitude(), location.getLongitude());
            //mMap.addMarker(new MarkerOptions().position(myPosition));
            map.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
            map.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
    }
}