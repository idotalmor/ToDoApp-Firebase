package com.example.idotalmor.todofirebase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.idotalmor.todofirebase.Models.Note;

import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableArrayMap;

public class ListAdapter extends BaseAdapter {

    Context context;
    ObservableArrayMap<String,Note> notes;

    public ListAdapter(Context context, ObservableArrayMap notes){

        this.context = context;
        this.notes = notes;

    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public Note getItem(int position) {
        return notes.valueAt(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Note note = getItem(position);

        if(convertView == null){

            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.note_layout, null);

        }

        TextView title = convertView.findViewById(R.id.note_adapter_title);
        TextView description = convertView.findViewById(R.id.note_adapter_description);

        title.setText(note.title);
        description.setText(note.description);

        return convertView;
    }
}
