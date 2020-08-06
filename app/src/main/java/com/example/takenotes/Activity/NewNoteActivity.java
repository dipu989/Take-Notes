package com.example.takenotes.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.takenotes.R;
import com.example.takenotes.Utils.DatabaseUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewNoteActivity extends AppCompatActivity {

    private DatabaseUtil myDb;
    private EditText noteTitle, noteBody;
    private Toolbar toolbar;
    private Window window;
     /*@BindView(R.id.note_title)
    EditText noteTitle;

    @BindView(R.id.note_body)
    EditText noteBody;

    @BindView(R.id.save_note)
    Button saveNote;
   */

    private FloatingActionButton saveBtn;

    @Override
    protected void onStop() {
        super.onStop();
        myDb.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);
        ButterKnife.bind(this);

        toolbar = (Toolbar) findViewById(R.id.new_note_toolbar);
        setSupportActionBar(toolbar);

        window = NewNoteActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(NewNoteActivity.this, R.color.statusBarColor));

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

        String title = noteTitle.getText().toString();
        String body = noteBody.getText().toString();

        if(title.matches("") && body.matches("")){
            Toast.makeText(this,R.string.empty_note, Toast.LENGTH_SHORT).show();
        } else {
            boolean isInserted = myDb.insertData(title.trim(), body.trim());
            if (isInserted == true) {
                Toast.makeText(this, R.string.note_add_success, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.note_add_failed, Toast.LENGTH_SHORT).show();
            }
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        noteTitle.setText("");
        noteBody.setText("");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        String title = noteTitle.getText().toString();
        String body = noteBody.getText().toString();

        if (!(title.matches("") && body.matches(""))) {
            saveNote(title.trim(), body.trim());
        }
    }

    public void saveNote(String title, String body) {
        boolean isInserted = myDb.insertData(title, body);
        if (isInserted == true) {
            Toast.makeText(this, R.string.note_add_success, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, R.string.note_add_failed, Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteNotes(View view) {
        myDb.deleteAll();
        Toast.makeText(this, "Notes Deleted", Toast.LENGTH_SHORT).show();
    }

    public void viewData(View view) {
        Cursor res = myDb.getAllData();
        if (res.getCount() == 0) {
            showMessage("Error", "Nothing found");
            return;
        }
        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            buffer.append("ID :" + res.getString(0) + "\n");
            buffer.append("Title :" + res.getString(1) + "\n");
            buffer.append("Body :" + res.getString(2) + "\n\n");
        }
        showMessage("Data", buffer.toString());
    }

    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.share_note:
                shareNote();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void shareNote() {

        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(250, 400, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        TextPaint textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(4 * getResources().getDisplayMetrics().density);
        textPaint.setColor(Color.BLACK);

        StaticLayout staticLayout = StaticLayout.Builder.obtain(noteBody.getText().toString(), 0, noteBody.getText().toString().length(), textPaint, 240)
                .build();
        staticLayout.draw(canvas);

        pdfDocument.finishPage(page);
        sharePdf(pdfDocument);
    }
    public void sharePdf(PdfDocument pdfDocument) {

        String fileDirectory = this.getExternalFilesDir(null).getAbsolutePath() + "/share";

        String targetPdf = fileDirectory + noteTitle.getText().toString().trim() + ".pdf";
        File filePath = new File(targetPdf);

        try {

            pdfDocument.writeTo(new FileOutputStream(filePath));
            Toast.makeText(this, "File Written", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "File Writing Failed", Toast.LENGTH_LONG).show();

        }
        pdfDocument.close();

        Intent intentShareFile = new Intent(Intent.ACTION_SEND);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri = Uri.fromFile(filePath);
        intentShareFile.setType("application/pdf");

        intentShareFile.putExtra(Intent.EXTRA_STREAM, uri);
        intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing Files...");
        setPermission(intentShareFile, uri);
        startActivity(Intent.createChooser(intentShareFile, "Share File"));
    }

    private void setPermission(Intent resultIntent, Uri fileUri) {
        List<ResolveInfo> resInfoList = getApplicationContext().getPackageManager().queryIntentActivities(resultIntent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            getApplicationContext().grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

}
