package com.example.takenotes.Model;


public class Note {

    private int id;
    private String noteTitle;
    private String noteBody;

    public Note() {

    }

    public Note(int id, String title, String note) {
        this.id = id;
        this.noteTitle = title;
        this.noteBody = note;
    }

    public Note(String title, String note) {
        this.noteTitle = title;
        this.noteBody = note;
    }

    public int getId() {
        return id;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public String getNoteBody() {
        return noteBody;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNoteTitle(String title) {
        this.noteTitle = title;
    }

    public void setNoteBody(String body) {
        this.noteBody = body;
    }
}
