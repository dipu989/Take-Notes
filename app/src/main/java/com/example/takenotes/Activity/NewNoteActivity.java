package com.example.takenotes.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.takenotes.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewNoteActivity extends AppCompatActivity {


    @BindView(R.id.note_title)
    EditText noteTitle;

    @BindView(R.id.note_body)
    EditText noteBody;

    @BindView(R.id.save_note)
    Button saveNote;

    String fileName = "Note1.txt";

    File notesStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initializeDirectory();
    }

    @OnClick(R.id.note_title)
    public void setTitle(View view){
        noteTitle.setText("");
    }

    public void initializeDirectory() {
        notesStorage = new File(Environment.getExternalStorageDirectory(),"Take-Notes");
        if(!notesStorage.exists()) {
            if(!notesStorage.mkdirs()) {
                Log.d("Failed"," to create a directory");
            }
        }

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void saveNote(View view) {

        File file = new File(notesStorage,noteTitle.toString());
        FileOutputStream stream;
        try{
            stream = new FileOutputStream(file);
            stream.write(noteBody.getText().toString().getBytes());
            stream.close();
            /*OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(fileName,0));
            outputStreamWriter.write(noteBody.getText().toString());
            outputStreamWriter.close();
             */
            Toast.makeText(this, "Note Saved", Toast.LENGTH_SHORT).show();
        } catch (Throwable t) {
            Toast.makeText(this, "Exception: " + toString(), Toast.LENGTH_SHORT).show();
        }
        finally {
           // stream.close();
        }

    }

}
