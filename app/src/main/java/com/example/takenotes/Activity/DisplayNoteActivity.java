package com.example.takenotes.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.takenotes.R;
import com.example.takenotes.Utils.DatabaseUtil;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class DisplayNoteActivity extends AppCompatActivity {

    private EditText displayTitle;
    private EditText displayBody;
    private TextView displayId;
    private Toolbar toolbar;
    private Window window;
    private DatabaseUtil myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_note);
        displayTitle = findViewById(R.id.display_title);
        displayBody = findViewById(R.id.display_body);
        displayId = findViewById(R.id.display_id);
        toolbar = findViewById(R.id.edit_note_toolbar);
        toolbar.setTitle("Note");
        toolbar.setTitleTextColor(Color.WHITE);
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        window = DisplayNoteActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(DisplayNoteActivity.this, R.color.statusBarColor));

        myDb = new DatabaseUtil(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getIntentData();
        clearFiles();
    }

    public void clearFiles() {

        String fileDirectory = this.getExternalFilesDir(null).getAbsolutePath() + "/share";
        File file = new File(fileDirectory);
        file.mkdir();
       // Log.i("File's path is ", file.getPath());

        if (file.exists()) {
            File[] list = file.listFiles();

            if (list == null) {
                //  Log.i("List is ", "null");
            } else if (list.length == 0) {
                //  Log.i("Directory is ","empty");
            } else {
                for (File individualFiles : list) {
                     Log.i("File deleted is ",individualFiles.getName());
                    individualFiles.delete();
                }
            }

        }

    }

    private void getIntentData() {
        if (getIntent().hasExtra("Title") && getIntent().hasExtra("Body")) {
            String title = getIntent().getStringExtra("Title");
            String body = getIntent().getStringExtra("Body");
            String id = getIntent().getStringExtra("ID");

            setData(id, title, body);
        }
    }

    public void setData(String id, String title, String body) {

        if (title.matches("") && !body.matches("")) {
            displayTitle.setHint("Title");
            displayBody.setText(body);
            displayId.setText(id);
        } else if (body.matches("") && !title.matches("")) {
            displayBody.setHint("Note");
            displayTitle.setText(title);
            displayId.setText(id);
        } else {
            displayBody.setText(body);
            displayTitle.setText(title);
            displayId.setText(id);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void editNote(View view) {

        String id = displayId.getText().toString();
        String title = displayTitle.getText().toString().trim();
        String body = displayBody.getText().toString().trim();

        boolean isUpdated = myDb.update(id, title, body);

        if (isUpdated == true) {
            Toast.makeText(this, "Edited!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_note:
                shareNote();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void shareNote() {

        String docName = displayTitle.getText().toString().trim().replace(" ", "_");
        Document document;
        try {
            String fileDirectory = this.getExternalFilesDir(null).getAbsolutePath() + "/share";
            File file = new File(fileDirectory);
            if (!file.exists()) {
                file.mkdir();
            }
            
            if(displayTitle.getText().toString().matches("")){
                docName = "Untitled";
            }

            String targetPdf = fileDirectory + docName + ".pdf";

            File filePath = new File(targetPdf);
            // Log.i("File path path is",filePath.getPath());
            File share_file = new File(fileDirectory, displayTitle.getText().toString().replace(" ", "_") + ".pdf");
            //  Log.i("share file name is ",share_file.getPath());
            if (file.exists()) {
            //    Log.i("File path existing at ", filePath.getPath());
                filePath.renameTo(share_file);
             //   Log.i("File path after rename ", filePath.getName());
            } else {
             //   Log.i("File path is ", filePath.getPath());
            }
            filePath.delete();

            OutputStream outputStream = new FileOutputStream(share_file);

            document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();
            document.add(new Paragraph(displayTitle.getText().toString()));
            document.add(new Paragraph(displayBody.getText().toString()));
           // Toast.makeText(this, "File written", Toast.LENGTH_SHORT).show();
            document.close();

            Intent docPdf = new Intent(Intent.ACTION_SEND);
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

            Uri uri = Uri.parse(share_file.getAbsolutePath());
            docPdf.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            docPdf.setType("application/pdf");
            docPdf.putExtra(Intent.EXTRA_STREAM,uri);

            docPdf.putExtra(Intent.EXTRA_TEXT, "");
            setPermission(docPdf, uri);
            startActivity(Intent.createChooser(docPdf, "Share File"));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }

    private void setPermission(Intent resultIntent, Uri fileUri) {
        List<ResolveInfo> resInfoList = getApplicationContext().getPackageManager().queryIntentActivities(resultIntent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            getApplicationContext().grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

}


