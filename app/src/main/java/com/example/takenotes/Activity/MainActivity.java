package com.example.takenotes.Activity;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.takenotes.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    
    private long back_pressed;

    //@BindView(R.id.new_note)
    //Button btnNewNote;

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.setDebug(true);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

  //  @OnClick(R.id.new_note)
   // public void addNote(View view) {
     //   Intent intent = new Intent(this, NewNoteActivity.class);
      //  startActivity(intent);
   // }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if(back_pressed + 2000 > System.currentTimeMillis())
            super.onBackPressed();
        else {
            Toast toast = Toast.makeText(getApplicationContext(), "Double tap to exit", Toast.LENGTH_SHORT);
            toast.show();
            back_pressed = System.currentTimeMillis();
        }
    }
}
