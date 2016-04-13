package sk.stuba.fiit.revizori;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import sk.stuba.fiit.revizori.imgur.UploadService;
import sk.stuba.fiit.revizori.imgur.model.ImageResponse;
import sk.stuba.fiit.revizori.imgur.model.Upload;
import sk.stuba.fiit.revizori.model.Revizor;
import sk.stuba.fiit.revizori.service.RevizorService;

public class CreateRevizorActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private AutoCompleteTextView lineNumber;
    private String[] lineNumbers;
    static final String PHOTO_PATH = "photoPath";
    private String photoPath;
    private ImageView newRevizorPhoto;
    private String uploadedPhotoUrl;
    //localization
    private LocationManager locationManager;
    private String provider;
    private LatLng myPosition;

    final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            photoPath = savedInstanceState.getString(PHOTO_PATH);
        }

        newRevizorPhoto = (ImageView) findViewById(R.id.new_revizor_photo);

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
                .findFragmentById(R.id.my_possition_map);
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
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
               // ...
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            uploadPhoto();
            setPic();
        }
    }

    private void setPic() {
        // Get the dimensions of the View

        int targetW = (int) getResources().getDimension(R.dimen.photo_thumbnail_width);
        int targetH = (int) getResources().getDimension(R.dimen.photo_thumbnail_height);

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
        newRevizorPhoto.setImageBitmap(bitmap);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        photoPath = image.getAbsolutePath();
        return image;
    }
    private void uploadPhoto(){
        Upload upload = new Upload();
        System.out.println(photoPath);
        upload.image = new File(photoPath);
        //upload.albumId = Constants.ALBUM_ID;
        new UploadService(this).Execute(upload, new UiCallback());
    }

    private class UiCallback implements Callback<ImageResponse> {

        @Override
        public void success(ImageResponse imageResponse, Response response) {
            uploadedPhotoUrl = imageResponse.data.link;
        }

        @Override
        public void failure(RetrofitError error) {
            //Assume we have no connection, since error is null
            if (error == null) {
                Snackbar.make(findViewById(R.id.create_post_view), "No internet connection", Snackbar.LENGTH_SHORT).show();
            }
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

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), true);

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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putString(PHOTO_PATH, photoPath);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
}