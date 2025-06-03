package com.example.finalproject.listeners;

import com.example.finalproject.entities.Note;

public interface NoteListener {
    void onNoteClicked(Note note, int position);
}
