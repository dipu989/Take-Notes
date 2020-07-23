package com.example.takenotes.Utils;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class ExternalStorageUtil {

   /* File notesStorage;

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

            Toast.makeText(this, "Note Saved", Toast.LENGTH_SHORT).show();
        } catch (Throwable t) {
            Toast.makeText(this, "Exception: " + toString(), Toast.LENGTH_SHORT).show();
        }
        finally {
            // stream.close();
        }

    }

    public void initializeDirectory() {
        notesStorage = new File(Environment.getExternalStorageDirectory(),"Take-Notes");
        if(!notesStorage.exists()) {
            if(!notesStorage.mkdirs()) {
                Log.d("Failed"," to create a directory");
            }
        }

    }
    */
}
