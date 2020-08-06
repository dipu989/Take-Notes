package com.example.takenotes.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.text.TextUtils;
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

import java.io.File;
import java.io.FileOutputStream;
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
        setSupportActionBar(toolbar);

        window = DisplayNoteActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(DisplayNoteActivity.this, R.color.statusBarColor));

        myDb = new DatabaseUtil(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getIntentData();
        initDirectory();
        clearFiles();
    }

    public void initDirectory() {

    }

    public void clearFiles() {
        String fileDirectory = this.getExternalFilesDir(null).getAbsolutePath() + "/share";
        // Log.i("Clear files method ",fileDirectory);
        File file = new File(fileDirectory);
        if (file != null) {
            File[] list = file.listFiles();
            for (File files : list) {
                files.delete();
                //    Log.i("File deleted is ",files.getName());
            }
        } else {
            //   Log.i("Files were ","not deleted!");
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
        displayId.setText(id);
        displayTitle.setText(title);
        displayBody.setText(body);
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

        StaticLayout staticLayout = StaticLayout.Builder.obtain(displayBody.getText().toString(), 0, displayBody.getText().toString().length(), textPaint, 240)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setEllipsize(TextUtils.TruncateAt.END)
                .setTextDirection(TextDirectionHeuristics.LTR)
                .build();
        staticLayout.draw(canvas);

        pdfDocument.finishPage(page);
        sharePdf(pdfDocument);
    }

    public void sharePdf(PdfDocument pdfDocument) {

        String fileDirectory = this.getExternalFilesDir(null).getAbsolutePath() + "/share";
        File file = new File(fileDirectory);
        if (!file.exists()) {
            file.mkdir();
        }

        String targetPdf = fileDirectory + displayTitle.getText().toString().trim().replace(" ", "_") + ".pdf";

        File filePath = new File(targetPdf);
        // Log.i("File path path is",filePath.getPath());
        File share_file = new File(fileDirectory, displayTitle.getText().toString().replace(" ", "_") + ".pdf");
        //  Log.i("share file name is ",share_file.getPath());
        if (file.exists()) {
            Log.i("File path existing at ", filePath.getPath());
            filePath.renameTo(share_file);
            Log.i("File path after rename ", filePath.getName());
        } else {
            Log.i("File path is ", filePath.getPath());
        }

        try {
            pdfDocument.writeTo(new FileOutputStream(share_file));
        } catch (Exception e) {
            e.printStackTrace();
        }
        pdfDocument.close();

        Intent intentShareFile = new Intent(Intent.ACTION_SEND);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri = Uri.fromFile(share_file);
        intentShareFile.setType("application/pdf");
        intentShareFile.putExtra(Intent.EXTRA_STREAM, uri);
        intentShareFile.putExtra(Intent.EXTRA_TEXT, "");
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


