package com.example.LogGuard.Repository;

import com.example.LogGuard.Model.Note;
import org.springframework.data.repository.CrudRepository;

public interface NoteDao extends CrudRepository<Note, String> {
}
