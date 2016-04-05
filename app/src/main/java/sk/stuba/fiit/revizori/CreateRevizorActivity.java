package sk.stuba.fiit.revizori;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import sk.stuba.fiit.revizori.model.Revizor;
import sk.stuba.fiit.revizori.service.RevizorService;

public class CreateRevizorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
    }

    public void onCreatePostClick(){
        TextView line = (TextView) findViewById(R.id.line_number);
        TextView comment = (TextView) findViewById(R.id.comment);
        Revizor r = new Revizor(line.getText().toString(), Math.random(), Math.random(), "photourl", comment.getText().toString());
        RevizorService.getInstance().createRevizor(r);

    }

}
