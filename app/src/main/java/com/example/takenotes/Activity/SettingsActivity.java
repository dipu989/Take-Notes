package com.example.takenotes.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.takenotes.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class SettingsActivity extends AppCompatActivity {

    private Switch btn_toggle;
    private Toolbar toolbar;
    private Window window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = findViewById(R.id.settings_activity_toolbar);
        toolbar.setTitle("Settings");
        toolbar.setTitleTextColor(Color.WHITE);
        final Drawable upArrow = ContextCompat.getDrawable(this,R.drawable.ic_arrow_back_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        window = SettingsActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(SettingsActivity.this, R.color.statusBarColor));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btn_toggle = findViewById(R.id.toggle_dark_mode);
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final boolean isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false);
        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            btn_toggle.setChecked(true);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            btn_toggle.setChecked(false);
        }
        btn_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDarkModeOn) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor.putBoolean("isDarkModeOn", false);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor.putBoolean("isDarkModeOn", true);
                }
                editor.apply();
            }
        });
    }

    public void createOfflineBackup(View view) {

        String fileDirectory = this.getExternalFilesDir(null).getAbsolutePath() + "/backup";
        //Log.i("File directory is ", fileDirectory);

        String copyDBPath = "note_backup.db";

        final String realDatabase = this.getDatabasePath("student.db").toString();
        //Log.i("Database Location is ", realDatabase);

        try {
            File currentDB = new File(realDatabase);
            File copyDB = new File(fileDirectory, copyDBPath);
            if (currentDB.exists()) {
                if (copyDB.exists()) {
                    copyDB.delete();
                }
                @SuppressWarnings("resource")
                FileChannel src = new FileInputStream(currentDB).getChannel();
                @SuppressWarnings("resource")
                FileChannel dst = new FileOutputStream(copyDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                Toast.makeText(this, "Backup Created", Toast.LENGTH_SHORT).show();
                src.close();
                dst.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadOfflineData(View view) {
        String fileDirectory = this.getExternalFilesDir(null).getAbsolutePath() + "/backup";
        // Log.i("File directory is ", fileDirectory);

        final String endLocation = this.getDatabasePath("student.db").toString();
        // Log.i("Database Location is ", endLocation);

        try {
            File beginLocation = new File(fileDirectory, "note_backup.db");
            File endLoc = new File(endLocation);

            //Log.i("It reached here yay!", "Yo");
            if (beginLocation.exists()) {
                if (endLoc.exists()) {
                    endLoc.delete();
                }
                @SuppressWarnings("resource")
                FileChannel src = new FileInputStream(beginLocation).getChannel();
                @SuppressWarnings("resource")
                FileChannel dst = new FileOutputStream(endLoc).getChannel();
                dst.transferFrom(src, 0, src.size());
                Toast.makeText(this, "Backup Restored", Toast.LENGTH_SHORT).show();
                src.close();
                dst.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Log.i("On back pressed", "not working");
        super.onBackPressed();
    }

}
