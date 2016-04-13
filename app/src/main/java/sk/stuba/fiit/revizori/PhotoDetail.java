package sk.stuba.fiit.revizori;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.google.android.gms.maps.SupportMapFragment;

public class PhotoDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_detail);

        String photoUrl = getIntent().getStringExtra("photo_url");

        WebView wv = (WebView) findViewById(R.id.photo_detail);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setUserAgentString("Chrome");
        wv.setWebViewClient(new myWebViewClient ());
        wv.getSettings().setBuiltInZoomControls(true);
        wv.loadUrl(photoUrl);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
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

    private class myWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
