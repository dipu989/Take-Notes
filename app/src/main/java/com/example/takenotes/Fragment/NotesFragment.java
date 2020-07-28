package com.example.takenotes.Fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;

import com.example.takenotes.Adapter.NoteAdapter;
import com.example.takenotes.Model.Note;
import com.example.takenotes.R;
import com.example.takenotes.Utils.DatabaseUtil;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotesFragment extends Fragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {
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
    // NoteAdapter noteAdapter;

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

        menu.clear();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search_menu_toolbar);
        MenuItemCompat.setShowAsAction(searchItem, MenuItemCompat.SHOW_AS_ACTION_ALWAYS | MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        adapter = new NoteAdapter(rootView.getContext(), notes);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if (newText == null || newText.trim().isEmpty()) {
            adapter = new NoteAdapter(rootView.getContext(), notes);
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
            return true;
        }
        List<Note> foundNotes = db.search(newText);
        if (foundNotes != null) {
            adapter = new NoteAdapter(rootView.getContext(), foundNotes);
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
            return true;
        }
        return false;
    }

}
