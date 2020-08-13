package com.example.takenotes.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.takenotes.Fragment.NotesFragment;
import com.example.takenotes.R;
import com.google.android.material.navigation.NavigationView;


import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private long back_pressed;
    //@BindView(R.id.new_note)
    //Button btnNewNote;

    private DrawerLayout drawerLayout;
    private Button newNote;
    private Toolbar toolbar;
    private Window window;
    NavigationView navigationView;

    @Override
    protected void onPostResume() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NotesFragment()).commit();
        super.onPostResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ButterKnife.setDebug(true);
        //ButterKnife.bind(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.home_page_title));
        }
        drawerLayout = findViewById(R.id.drawer_layout);
        newNote = findViewById(R.id.new_note);
       // toolbar.setTitleTextColor(getResources().getColor(android.R.color.black));
        toolbar.setTitle(getResources().getString(R.string.home_page_title));
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        window = MainActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.statusBarColor));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
     //   if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            toggle.getDrawerArrowDrawable().setColor(Color.WHITE);
//        }else {
//            toggle.getDrawerArrowDrawable().setColor(Color.BLACK);
//        }
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new NotesFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_notes);
        }
        initDirectory();
        isDarkModeOn();
    }

    private void isDarkModeOn(){
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs",MODE_PRIVATE);
        final boolean isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn",false);
        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public void initDirectory() {
        String fileDirectory = this.getExternalFilesDir(null).getAbsolutePath() + getResources().getString(R.string.share_directory);
        // Log.i("Clear files method ",fileDirectory);
        File file = new File(fileDirectory);
        if(!file.exists()){
            file.mkdir();
        }

        String backupDir = this.getExternalFilesDir(null).getAbsolutePath() + getResources().getString(R.string.backup_directory);
        File file1 = new File(backupDir);
        if(!file1.exists()){
            file1.mkdir();
        }
    }

    public void addNote(View view) {
        Intent intent = new Intent(this, NewNoteActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (back_pressed + 2000 > System.currentTimeMillis())
            super.onBackPressed();
        else {
            Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.app_exit_toast), Toast.LENGTH_SHORT);
            toast.show();
            back_pressed = System.currentTimeMillis();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_notes:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NotesFragment()).commit();
                break;
//            case R.id.nav_trash:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TrashFragment()).commit();
//                break;
            case R.id.nav_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_rateUs:
                Toast.makeText(this, getResources().getString(R.string.under_development_toast), Toast.LENGTH_SHORT).show();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
       // Log.i("On Resume called ","here");
        navigationView.setCheckedItem(R.id.nav_notes);
    }
}
