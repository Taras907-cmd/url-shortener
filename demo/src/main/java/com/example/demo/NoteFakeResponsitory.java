package com.example.demo;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class NoteFakeResponsitory {

    private List<Note> notes = new ArrayList<>();

    public Note createNote(Note note) {
        note.setId(generateUniqueId());
        this.notes.add(note);
        return note;
    }

    public List<Note> findAllNotes() {
        return this.notes;
    }

    public void noteDelete(long id) {
        boolean removed = notes.removeIf(note -> note.getId() == id);
        if (!removed) {
            throw new NoSuchElementException("Note with id = " + id + " not found");
        }
    }

    public void noteUpdate(Note note) {
        Note existing = notes.stream().filter(n -> n.getId() == note.getId()).findFirst().orElseThrow(() -> new NoSuchElementException("Note with id = " + note.getId() + " not found"));
        existing.setTitle(note.getTitle());
        existing.setContent(note.getContent());
    }

    public Note noteGetById(long id) {
        return notes.stream().filter(note -> note.getId() == id).findFirst().orElseThrow(() -> new NoSuchElementException("Note with id = " + id + " not found"));
    }

    private long generateUniqueId() {
        long candidate;
        boolean exists;
        do {
            candidate = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
            long idToCheck = candidate;
            exists = notes.stream().anyMatch(n -> n.getId() == idToCheck);
        } while (exists);
        return candidate;
    }
}