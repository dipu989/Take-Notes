package com.example.takenotes.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.RecyclerView;

import com.example.takenotes.Activity.DisplayNoteActivity;
import com.example.takenotes.Model.Note;
import com.example.takenotes.R;
import com.example.takenotes.Utils.DatabaseUtil;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    Context context;
    List<Note> noteList = new ArrayList<>();
    private boolean multiSelect = false;
    private List<Note> selectedNotes = new ArrayList<Note>();
    DatabaseUtil myDb;

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

        myDb = new DatabaseUtil(context);

        Note note = noteList.get(position);
        if (selectedNotes.contains(note)) {
            holder.itemView.setAlpha(0.3f);
        } else {
            holder.itemView.setAlpha(1.0f);
        }

        holder.itemView.findViewById(R.id.cardLayout).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!multiSelect) {
                    multiSelect = true;
                    ((AppCompatActivity) v.getContext()).startSupportActionMode(actionModeCallback);
                    selectItem(holder, note);
                    return true;
                } else
                    return false;
            }
        });

        holder.itemView.findViewById(R.id.cardLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (multiSelect) {
                    selectItem(holder, note);
                } else {
                    displayNotes(holder);
                }
            }
        });

        holder.title.setText(noteList.get(position).getNoteTitle());
        holder.body.setText(noteList.get(position).getNoteBody());
        holder.id.setText(Integer.toString(noteList.get(position).getId()));

        unselectData(holder);

    }

    public void displayNotes(NoteViewHolder holder) {
        Intent intent = new Intent(context, DisplayNoteActivity.class);
        intent.putExtra("Title", holder.title.getText().toString());
        intent.putExtra("Body", holder.body.getText().toString());
        intent.putExtra("ID", holder.id.getText().toString());
        context.startActivity(intent);
    }

    private void selectItem(NoteViewHolder holder, Note note) {
        if (selectedNotes.contains(note)) {
            selectedNotes.remove(note);
            holder.itemView.findViewById(R.id.cardLayout).setAlpha(1.0f);
        } else if (!selectedNotes.contains(note)) {
            selectedNotes.add(note);
            holder.itemView.findViewById(R.id.cardLayout).setAlpha(0.3f);
        } else if (selectedNotes.size() == 0 && multiSelect == true) {
            displayNotes(holder);
        }
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu, menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.action_delete) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                if (selectedNotes.size() == 1) {
                    builder.setTitle("Delete: " + selectedNotes.get(0).getNoteTitle());
                    builder.setMessage("Are you sure you want to delete this note?");
                } else {
                    builder.setTitle("Delete " + selectedNotes.size() + " notes?");
                    builder.setMessage("Are you sure you want to delete these notes?");
                }

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myDb.deleteRecord(selectedNotes);
                        noteList.removeAll(selectedNotes);
                        notifyDataSetChanged();
                        Toast.makeText(context, "Notes Deleted", Toast.LENGTH_SHORT).show();
                        mode.finish();
                    }
                })
                        .setNegativeButton("Cancel", null);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                Button cancelBtn = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                cancelBtn.setTextColor(Color.rgb(40,161,247));
                Button okBtn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                okBtn.setTextColor(Color.rgb(40,161,247));
                okBtn.setTextColor(Color.rgb(40,161,247));
                
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

            multiSelect = false;
            selectedNotes.clear();
            notifyDataSetChanged();

        }
    };

    public void unselectData(NoteViewHolder holder) {
        holder.itemView.findViewById(R.id.cardLayout).setAlpha(1.0f);
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView id, title, body;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.rvnoteId);
            title = itemView.findViewById(R.id.rvnoteTitle);
            body = itemView.findViewById(R.id.rvnoteText);
        }
    }

}

