package sk.stuba.fiit.revizori;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import sk.stuba.fiit.revizori.model.Revizor;
import sk.stuba.fiit.revizori.service.RevizorService;

public class CreateRevizorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        Button button = (Button) findViewById(R.id.createPostBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreatePostClick();
            }
        });

    }

    public void onCreatePostClick(){
        TextView line = (TextView) findViewById(R.id.add_line_number);
        TextView comment = (TextView) findViewById(R.id.comment);
        Revizor r = new Revizor(line.getText().toString(), Math.random(), Math.random(), "photourl", comment.getText().toString());
        RevizorService.getInstance().createRevizor(r);

    }

}
