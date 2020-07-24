package com.example.takenotes.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

import com.example.takenotes.R;

public class DisplayNoteActivity extends AppCompatActivity {

    EditText displayTitle;
    EditText displayBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_note);

        displayTitle = findViewById(R.id.display_title);
        displayBody = findViewById(R.id.display_body);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getIntentData();
    }

    private void getIntentData() {
        if(getIntent().hasExtra("Title") && getIntent().hasExtra("Body")){
            String title = getIntent().getStringExtra("Title");
            String body = getIntent().getStringExtra("Body");
            setData(title, body);
        }
    }

    public void setData(String title, String body) {
        displayTitle.setText(title);
        displayBody.setText(body);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
