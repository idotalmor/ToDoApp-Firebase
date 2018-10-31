package com.example.idotalmor.todofirebase.Models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Note {

    public String note_uid,title,description;
    public Date date;
    public boolean done;

    public Note(String note_uid,String title,String description,Date date,Boolean done){

        this.note_uid = note_uid;
        this.title = title;
        this.description = description;
        this.date = date;
        this.done = done;

    }

    public Note(String note_uid,String title,String description,Boolean done){

        this.note_uid = note_uid;
        this.title = title;
        this.description = description;
        this.done = done;

    }

    public Note(){}

    public Map<String, Object> toMap() {
        HashMap<String, Object> note_map = new HashMap<>();
        note_map.put("note_id", note_uid);
        note_map.put("title", title);
        note_map.put("description", description);
        if(date != null){
        note_map.put("date", date.getTime());}
        note_map.put("done", done);

        return note_map;
    }

}
