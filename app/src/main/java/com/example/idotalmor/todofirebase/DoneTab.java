package com.example.idotalmor.todofirebase;

import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.idotalmor.todofirebase.Models.Note;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableMap;
import androidx.fragment.app.Fragment;


public class DoneTab extends Fragment {

    ListView listView;
    Context context;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_list_layout,container,false);
        listView = view.findViewById(R.id.notesListView);

        context = view.getContext();

        final ListAdapter listAdapter = new ListAdapter(context,MainTask.donelist);
        listView.setAdapter(listAdapter);

        MainTask.donelist.addOnMapChangedCallback(new ObservableMap.OnMapChangedCallback<ObservableMap<String, Note>, String, Note>() {
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
