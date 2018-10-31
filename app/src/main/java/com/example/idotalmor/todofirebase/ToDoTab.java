package com.example.idotalmor.todofirebase;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.idotalmor.todofirebase.Models.Note;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableMap;
import androidx.fragment.app.Fragment;


public class ToDoTab extends Fragment {

    ListView listView;
    Context context;
    FirebaseUser currentuser;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.main_list_layout,container,false);
        currentuser= FirebaseAuth.getInstance().getCurrentUser();

        listView = view.findViewById(R.id.notesListView);

        context = view.getContext();
        final ListAdapter listAdapter = new ListAdapter(context,MainTask.todolist);
        listView.setAdapter(listAdapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle("Edit Note");
                alertDialog.setMessage("Do you want to mark the note as done?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "mark as Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Note note = MainTask.todolist.valueAt(position);
                        DatabaseReference noteRef = FirebaseDatabase.getInstance().getReference().child(currentuser.getUid()).child(note.note_uid).child("done");
                        noteRef.setValue(true);
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();

                return true;
            }
        });

        MainTask.todolist.addOnMapChangedCallback(new ObservableMap.OnMapChangedCallback<ObservableMap<String, Note>, String, Note>() {
            @Override
            public void onMapChanged(ObservableMap<String, Note> sender, String key) {
                listAdapter.notifyDataSetChanged();
                listView.invalidateViews();
                listView.refreshDrawableState();
            }
        });

        return view;
    }

}
