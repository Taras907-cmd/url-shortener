package com.example.demo.service;

import com.example.demo.model.Note;
import com.example.demo.repository.NoteFakeResponsitory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteServiceImpl implements NoteService {

    @Autowired
    private NoteFakeResponsitory noteFakeResponsitory;

    @Override
    public List<Note> listAll() {
        return noteFakeResponsitory.findAllNotes();
    }

    @Override
    public Note add(Note note) {
        return noteFakeResponsitory.createNote(note);
    }

    @Override
    public void deleteById(long id) {
        noteFakeResponsitory.noteDelete(id);
    }

    @Override
    public void update(Note note) {
        noteFakeResponsitory.noteUpdate(note);
    }

    @Override
    public Note getById(long id) {
        return noteFakeResponsitory.noteGetById(id);
    }
}