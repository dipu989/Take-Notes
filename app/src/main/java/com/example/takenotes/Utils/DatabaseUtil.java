package com.example.takenotes.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.takenotes.Model.Note;

import java.util.ArrayList;
import java.util.List;

public class DatabaseUtil extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "student.db";
    public static final String TABLE_NAME = "student_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "TITLE";
    public static final String COL_3 = "BODY";
    private static final String[] args = {COL_1, COL_2, COL_3};

    public DatabaseUtil(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, TITLE TEXT NOT NULL, BODY TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String title, String body) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, title);
        contentValues.put(COL_3, body);
         long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1)
            return false;
        return true;
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("Select * from " + TABLE_NAME, null);
        return res;
    }

    public List<Note> getAllNotes() {
        List<Note> allNotes = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_NAME, args,null,null,null,null, null);
        if(cursor!=null && cursor.moveToFirst()) {
            do{
                Note note = new Note();
                note.setId(Integer.parseInt(cursor.getString(0)));
                note.setNoteTitle(cursor.getString(1));
                note.setNoteBody(cursor.getString(2));

                allNotes.add(note);
            }while(cursor.moveToNext());
        }
        db.close();
        return allNotes;
    }

    public void deleteRecord(List<Note> selectedNotes) {
        SQLiteDatabase db = this.getWritableDatabase();
        for(int i = 0; i<selectedNotes.size(); i++) {
            db.delete(TABLE_NAME, "ID='" + selectedNotes.get(i).getId() + "'", null);
        }
        db.close();
    }

    public List<Note> search(String keyword) {
        List<Note> foundNotes = null;
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where " + COL_3 + " like ?", new String[] { "%" + keyword + "%"});
            if(cursor.moveToFirst()) {
                foundNotes = new ArrayList<Note>();
                do{
                    Note note = new Note();
                    note.setId(cursor.getInt(0));
                    note.setNoteTitle(cursor.getString(1));
                    note.setNoteBody(cursor.getString(2));
                    foundNotes.add(note);
                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            foundNotes = null;
        }
        return foundNotes;
    }

}
