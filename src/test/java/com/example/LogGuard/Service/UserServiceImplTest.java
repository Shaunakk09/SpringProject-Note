package com.example.LogGuard.Service;

import com.example.LogGuard.Model.Note;
import com.example.LogGuard.Model.User;
import com.example.LogGuard.Repository.NoteDao;
import com.example.LogGuard.Repository.UserDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private NoteDao noteDao;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void testAddUser() {
        User user = new User();
        userService.addUser(user);
        verify(userDao, times(1)).save(user);
    }

    @Test
    public void testUserExist() {
        String firstName = "John";
        when(userDao.existsById(firstName)).thenReturn(true);

        boolean result = userService.userExist(firstName);

        assertTrue(result);
        verify(userDao, times(1)).existsById(firstName);
    }

    @Test
    public void testDeleteById() {
        User user = new User();
        user.setFirstName("John");

        userService.deleteById(user);

        verify(userDao, times(1)).deleteById(user.getFirstName());
    }

    @Test
    public void testUserExistInCache() {
        int mid = 123;
        when(redisTemplate.hasKey("user::" + String.valueOf(mid))).thenReturn(true);

        boolean result = userService.userExistInCache(mid);

        assertTrue(result);
        verify(redisTemplate, times(1)).hasKey("user::" + String.valueOf(mid));
    }

    @Test
    public void testGetUserNotes() {
        String firstName = "John";
        List<Note> notes = new ArrayList<>();
        Note note1 = new Note();
        note1.setFirstName("John");
        Note note2 = new Note();
        note2.setFirstName("Alice");
        notes.add(note1);
        notes.add(note2);
        when(noteDao.findAll()).thenReturn(notes);

        List<Note> result = userService.getUserNotes(firstName);

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
        verify(noteDao, times(1)).findAll();
    }

    @Test
    public void testGetUserNotesById() {
        String firstName = "John";
        int id = 1;
        List<Note> notes = new ArrayList<>();
        Note note1 = new Note();
        note1.setId(1);
        note1.setFirstName("John");
        Note note2 = new Note();
        note2.setId(2);
        note2.setFirstName("John");
        notes.add(note1);
        notes.add(note2);
        when(noteDao.findAll()).thenReturn(notes);

        Note result = userService.getUserNotesById(firstName, id);

        assertEquals(1, result.getId());
        assertEquals("John", result.getFirstName());
        verify(noteDao, times(1)).findAll();
    }

    @Test
    public void testAddNote() {
        Note note = new Note();
        userService.addNote(note);
        verify(noteDao, times(1)).save(note);
    }

    @Test
    public void testDeleteNoteById() {
        String firstName = "John";
        int id = 1;
        Note note = new Note();
        note.setFirstName(firstName);
        note.setId(id);
        when(noteDao.existsById("id")).thenReturn(true);

        userService.deleteNoteById(firstName, id);

        verify(noteDao, times(1)).deleteById(any());
    }

    @Test
    public void testSearchUserNotes() {
        String firstName = "John";
        String query = "search";

        List<Note> notes = new ArrayList<>();
        Note note1 = new Note();
        note1.setId(1);
        note1.setFirstName("John");
        note1.setNote("Searching the web");
        Note note2 = new Note();
        note2.setId(2);
        note2.setFirstName("John");
        note2.setNote("Java search algorithm");

        notes.add(note1);
        notes.add(note2);
        when(noteDao.findAll()).thenReturn(notes);

        List<Note> result = userService.searchUserNotes(firstName, query);

        assertEquals(2, result.size());
        verify(noteDao, times(1)).findAll();
    }
}

