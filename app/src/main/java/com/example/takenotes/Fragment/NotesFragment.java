package com.example.takenotes.Fragment;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.takenotes.Activity.SettingsActivity;
import com.example.takenotes.Adapter.NoteAdapter;
import com.example.takenotes.Model.Note;
import com.example.takenotes.R;
import com.example.takenotes.Utils.DatabaseUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotesFragment extends Fragment { //implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView.Adapter adapter;
    private List<Note> notes;
    androidx.appcompat.widget.Toolbar toolbar;
    RecyclerView recyclerView;
    private View rootView;
    private DatabaseUtil db;
    private androidx.appcompat.widget.SearchView searchView = null;
    private androidx.appcompat.widget.SearchView.OnQueryTextListener queryTextListener;

    public NotesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotesFragment newInstance(String param1, String param2) {
        NotesFragment fragment = new NotesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_notes, container, false);

        recyclerView = rootView.findViewById(R.id.recycler_view_notes);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        db = new DatabaseUtil(rootView.getContext());
        notes = db.getAllNotes();
        setHasOptionsMenu(true);

        if (notes.size() != 0) {
            adapter = new NoteAdapter(rootView.getContext(), notes);
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
        }
        return rootView;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search_menu_toolbar);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (androidx.appcompat.widget.SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            queryTextListener = new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.i("On query text change ", "reached");

                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    if (newText == null || newText.trim().isEmpty()) {
                        adapter = new NoteAdapter(rootView.getContext(), notes);
                        adapter.notifyDataSetChanged();
                        recyclerView.setAdapter(adapter);
                        Log.i("Query Text is ", "empty");
                        return true;
                    }
                    List<Note> foundNotes = db.search(newText);
                    if (foundNotes != null) {
                        adapter = new NoteAdapter(rootView.getContext(), foundNotes);
                        adapter.notifyDataSetChanged();
                        recyclerView.setAdapter(adapter);
                        Log.i("Query Text is ", " not empty");
                        return true;
                    }
                    return false;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.restore_menu_toolbar:
                loadOfflineData();
                break;
            case R.id.settings_menu_toolbar:
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadOfflineData() {
        String fileDirectory = getContext().getExternalFilesDir(null).getAbsolutePath() + "/backup";
        // Log.i("File directory is ", fileDirectory);

        final String endLocation = getContext().getDatabasePath("student.db").toString();
        // Log.i("Database Location is ", endLocation);

        try {
            File beginLocation = new File(fileDirectory, "note_backup.db");
            File endLoc = new File(endLocation);

            //Log.i("It reached here yay!", "Yo");
            if (beginLocation.exists()) {
                if (endLoc.exists()) {
                    endLoc.delete();
                }
                @SuppressWarnings("resource")
                FileChannel src = new FileInputStream(beginLocation).getChannel();
                @SuppressWarnings("resource")
                FileChannel dst = new FileOutputStream(endLoc).getChannel();
                dst.transferFrom(src, 0, src.size());
                Toast.makeText(getContext(), "Backup Restored", Toast.LENGTH_SHORT).show();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                if (Build.VERSION.SDK_INT >= 26) {
                    fragmentTransaction.setReorderingAllowed(false);
                }
                fragmentTransaction.detach(this).attach(this).commit();
                src.close();
                dst.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
