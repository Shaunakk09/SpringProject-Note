package com.example.LogGuard.Service;

import com.example.LogGuard.Model.Note;
import com.example.LogGuard.Model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UserService {
    void addUser(User user);
    Boolean userExist(String firstName);
    void deleteById(User user);
    Boolean userExistInCache(int mid);
    List<Note> getUserNotes(String firstName);
    Note getUserNotesById(String firstName, int id);
    void deleteNoteById(String firstName, int id);
    void addNote(Note note);
    List<Note> searchUserNotes(String firstName, String query);

}
