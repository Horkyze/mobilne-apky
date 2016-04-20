package sk.stuba.fiit.revizori;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import sk.stuba.fiit.revizori.imgur.UploadService;
import sk.stuba.fiit.revizori.imgur.model.ImageResponse;
import sk.stuba.fiit.revizori.imgur.model.Upload;
import sk.stuba.fiit.revizori.model.Revizor;
import sk.stuba.fiit.revizori.service.RevizorService;

public class EditSubmissionActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;

    private String photoPath;
    private ImageView revizorPhoto;
    static final String PHOTO_PATH = "photoPath";
    private String photoUrl;
    private LatLng submissionPosition;

    TextView comment;
    TextView lineNumber;

    String objectId;
    final int REQUEST_IMAGE_CAPTURE = 1;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_submission);

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            photoPath = savedInstanceState.getString(PHOTO_PATH);
        }
        objectId = getIntent().getStringExtra("objectId");
        lineNumber = (TextView) findViewById(R.id.line_number_edit);
        lineNumber.setText(getIntent().getStringExtra("lineNumber"));
        comment = (TextView) findViewById(R.id.comment_edit);
        comment.setText(getIntent().getStringExtra("comment"));

        submissionPosition = new LatLng(getIntent().getExtras().getDouble("latitude"), getIntent().getExtras().getDouble("longitude"));

        photoUrl = getIntent().getStringExtra("photoUrl");
        revizorPhoto = (ImageView) findViewById(R.id.edit_new_revizor_photo);
        new ImageLoadTask(photoUrl, revizorPhoto).execute();

        Button editBtn = (Button) findViewById(R.id.editSubmissionButton);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEditPostClick();
            }
        });

        Button takePhotoBtn = (Button) findViewById(R.id.takePhotoAgainBtn);
        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTakePhotoAgainClick();
            }
        });
        /*ImageButton deleteBtn = (ImageButton) findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteClick();
            }
        });*/

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.edit_submission_position_map);
        mapFragment.getMapAsync(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        revizorPhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(EditSubmissionActivity.this, PhotoDetail.class);
                intent.putExtra("photo_url", photoUrl);
                startActivity(intent);
            }
        });

    }

    public void onEditPostClick(){
        //ulozia sa zmeny
        Revizor r = new Revizor(getIntent().getStringExtra("lineNumber"),
                getIntent().getDoubleExtra("latitude", 0),
                getIntent().getDoubleExtra("longitude", 0),
                photoUrl,
                comment.getText().toString()
                );
        r.set_id(getIntent().getLongExtra("_id", 0));
        r.setObjectId(getIntent().getStringExtra("objectId"));
        RevizorService.getInstance().update(r);
        onBackPressed();
    }

    public void onDeleteClick(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        //getContentResolver().delete(RevizorContract.RevizorEntry.buildRevizorUri(id), "_id = " + id, null);
                        //RevizorService.getInstance().delete( (int) id );

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(EditSubmissionActivity.this);
        builder.setCancelable(true);

        builder.setMessage("Naozaj chcete hlasenie zmazat?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

        onBackPressed();
    }

    public void onTakePhotoAgainClick() {
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
        revizorPhoto.setImageBitmap(bitmap);
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
            photoUrl = imageResponse.data.link;
            new ImageLoadTask(photoUrl, revizorPhoto).execute();
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
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(PHOTO_PATH, photoPath);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete, menu);
        return true;
    }
}
