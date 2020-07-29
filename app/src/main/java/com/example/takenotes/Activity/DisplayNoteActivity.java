package com.example.takenotes.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.takenotes.R;
import com.example.takenotes.Utils.DatabaseUtil;

public class DisplayNoteActivity extends AppCompatActivity {

    EditText displayTitle;
    EditText displayBody;
    TextView displayId;
    Toolbar toolbar;
    Window window;
    DatabaseUtil myDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_note);

        displayTitle = findViewById(R.id.display_title);
        displayBody = findViewById(R.id.display_body);
        displayId = findViewById(R.id.display_id);
        toolbar = findViewById(R.id.edit_note_toolbar);
        setSupportActionBar(toolbar);

        window = DisplayNoteActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(DisplayNoteActivity.this, R.color.buttonColor));

        myDb = new DatabaseUtil(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getIntentData();
    }

    private void getIntentData() {
        if(getIntent().hasExtra("Title") && getIntent().hasExtra("Body")){
            String title = getIntent().getStringExtra("Title");
            String body = getIntent().getStringExtra("Body");
            String id = getIntent().getStringExtra("ID");

            setData(id, title, body);
        }
    }

    public void setData(String id, String title, String body) {
        displayId.setText(id);
        displayTitle.setText(title);
        displayBody.setText(body);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void editNote(View view){


        String id = displayId.getText().toString();
        String title = displayTitle.getText().toString();
        String body = displayBody.getText().toString();

        boolean isUpdated = myDb.update(id, title, body);

        if( isUpdated == true) {
            Toast.makeText(this, "Edited!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else{
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        }

    }


}
