package com.example.takenotes.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.takenotes.R;
import com.example.takenotes.Utils.DatabaseUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class SettingsActivity extends AppCompatActivity {

    private Switch btn_toggle;
    private Toolbar toolbar;
    private Window window;
    private RelativeLayout relativeLayout;
    private DatabaseUtil myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        myDb = new DatabaseUtil(this);

        relativeLayout = findViewById(R.id.dark_mode_layout);

        toolbar = findViewById(R.id.settings_activity_toolbar);
        toolbar.setTitle("Settings");
        toolbar.setTitleTextColor(Color.WHITE);
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_24dp);
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

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDarkModeOn) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    btn_toggle.setChecked(false);
                    editor.putBoolean("isDarkModeOn", false);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    btn_toggle.setChecked(true);
                    editor.putBoolean("isDarkModeOn", true);
                }
                editor.apply();
            }
        });
    }

    public void createOfflineBackup(View view) {

        if (myDb.getAllNotes().isEmpty()) {
            Toast.makeText(this, "Create a note first", Toast.LENGTH_SHORT).show();
        } else {

            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_TITLE, "note_backup.db");
            startActivityForResult(intent, 1);

        }
    }

    public void loadOfflineData(View view) {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, 2);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {

            Uri uri = data.getData();
            Log.i("Uri may be null", "!");

            try {
                String outputFileName = getDatabasePath("student.db").toString();
         //       Log.i("OutputFileName will be ", outputFileName);
         //       Log.i("Backup AP will be", dbFile.getAbsolutePath());
         //       Log.i("Backup name will be", dbFile.getName());
                InputStream inputStream = getContentResolver().openInputStream(uri);
                OutputStream outputStream = new FileOutputStream(outputFileName);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();
                Toast.makeText(this, "Backup Complete", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Failed to load backup", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else if (requestCode == 1) {
            try {
                String databaseFilePath = getDatabasePath("student.db").toString();
                File dbFile = new File(databaseFilePath);

                /*   Perfectly working   */
                BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(dbFile.getAbsoluteFile()));
                OutputStream outputStream = this.getContentResolver().openOutputStream(data.getData());
                int length = 0;
                byte[] buffer = new byte[1024];
                while ((length = bufferedInputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.flush();
                outputStream.close();
                bufferedInputStream.close();
                Toast.makeText(this, "Backup Created", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void loadOnlineBackup(View view) {
        Toast.makeText(this, "Coming soon...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
