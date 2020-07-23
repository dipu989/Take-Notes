package com.example.takenotes.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.takenotes.R;
import com.example.takenotes.Utils.DatabaseUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewNoteActivity extends AppCompatActivity {


    DatabaseUtil myDb;

    EditText noteTitle, noteBody;
     /*@BindView(R.id.note_title)
    EditText noteTitle;

    @BindView(R.id.note_body)
    EditText noteBody;

    @BindView(R.id.save_note)
    Button saveNote;
   */

    private Button saveBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        saveBtn = findViewById(R.id.save_note);
        noteTitle = findViewById(R.id.note_title);
        noteBody = findViewById(R.id.note_body);
        myDb = new DatabaseUtil(this);

    }

    /*@OnClick(R.id.note_title)
    public void setTitle(View view){
        noteTitle.setText("");
    }

     */

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void saveNote(View view) {

        boolean isInserted = myDb.insertData(noteTitle.getText().toString(), noteBody.getText().toString());
        if(isInserted == true){
            Toast.makeText(this, "Note added", Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(this,"Failed", Toast.LENGTH_SHORT).show();
        }
        noteTitle.setText("");
        noteBody.setText("");
    }

    public void viewData(View view) {
        Cursor res = myDb.getAllData();
        if(res.getCount() == 0) {
            showMessage("Error","Nothing found");
            return;
        }
        StringBuffer buffer = new StringBuffer();
        while(res.moveToNext()) {
            buffer.append("ID :" + res.getString(0) + "\n");
            buffer.append("Title :" + res.getString(1) + "\n");
            buffer.append("Body :" + res.getString(2) + "\n\n");
        }
        showMessage("Data",buffer.toString());
    }

    public void showMessage(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();

    }

}
