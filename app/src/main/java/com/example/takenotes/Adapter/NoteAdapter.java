package com.example.takenotes.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.takenotes.Activity.DisplayNoteActivity;
import com.example.takenotes.Model.Note;
import com.example.takenotes.R;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    Context context;
    List<Note> noteList = new ArrayList<>();
    private boolean multiSelect = false;
    private List<Note> selectedNotes = new ArrayList<Note>();

    public NoteAdapter(Context context, List<Note> noteList) {
        this.context = context;
        this.noteList = noteList;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_note, parent, false);
        NoteViewHolder noteViewHolder = new NoteViewHolder(view);
        return noteViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.title.setText(noteList.get(position).getNoteTitle());
        holder.body.setText(noteList.get(position).getNoteBody());

        holder.body.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DisplayNoteActivity.class);
                intent.putExtra("Title", holder.title.getText().toString());
                intent.putExtra("Body", holder.body.getText().toString());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView title, body;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.rvnoteTitle);
            body = itemView.findViewById(R.id.rvnoteText);
        }
    }

    public void updateData(List<Note> note) {
        noteList.clear();
        noteList.addAll(note);
        notifyDataSetChanged();
    }
}

