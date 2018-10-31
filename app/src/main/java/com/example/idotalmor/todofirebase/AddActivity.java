package com.example.idotalmor.todofirebase;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.idotalmor.todofirebase.Models.Note;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AddActivity extends AppCompatActivity {

    FirebaseUser currentUser;
    EditText note_title,note_description;
    TextView date_textview;
    Date note_date;
    SwitchDateTimeDialogFragment dateTimeDialogFragment;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_note_layout);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child(currentUser.getUid());

        note_title = findViewById(R.id.add_note_title);
        note_description = findViewById(R.id.add_note_description);
        date_textview = findViewById(R.id.add_date_textview);

        Toolbar toolbar = findViewById(R.id.toolbar_add_note);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add a note");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initDatePicker();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case android.R.id.home:
                this.finish();

                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    public void saveNote(View view){
        String note_title_str = note_title.getText().toString();
        String note_description_str = note_description.getText().toString();
        String key = mDatabase.push().getKey();
        Note note;

        if(note_date != null){ note = new Note(key,note_title_str,note_description_str,note_date,false);}
        else{note = new Note(key,note_title_str,note_description_str,false);}
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key,note);
        mDatabase.updateChildren(childUpdates);
        finish();

    }

    public void showDatePicker(View view){

        dateTimeDialogFragment.show(getSupportFragmentManager(), "dialog_time");

    }

    public void initDatePicker(){

        dateTimeDialogFragment = SwitchDateTimeDialogFragment.newInstance(
                "Pick Note Time",
                "OK",
                "Cancel"
        );

        dateTimeDialogFragment.startAtCalendarView();
        dateTimeDialogFragment.set24HoursMode(true);
        dateTimeDialogFragment.setMinimumDateTime(new GregorianCalendar(2015, Calendar.JANUARY, 1).getTime());
        dateTimeDialogFragment.setMaximumDateTime(new GregorianCalendar(2025, Calendar.DECEMBER, 31).getTime());
        dateTimeDialogFragment.setDefaultDateTime(new Date());


        try {
            dateTimeDialogFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("dd MMMM", Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
            Log.e("date error", e.getMessage());
        }

        dateTimeDialogFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {

                note_date = date;
                String date_n = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date);
                date_textview.setText(date_n);
            }

            @Override
            public void onNegativeButtonClick(Date date) {
                // Date is get on negative button click
            }
        });

    }


}
