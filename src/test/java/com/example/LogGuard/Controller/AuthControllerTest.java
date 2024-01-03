package com.example.LogGuard.Controller;

import com.example.LogGuard.Model.Note;
import com.example.LogGuard.Model.User;
import com.example.LogGuard.RateLimiting.RateLimitingControllerAdvice;
import com.example.LogGuard.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private RateLimitingControllerAdvice rateLimitingControllerAdvice;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setUp() {
        String validToken = "dummyToken";
        String username = "John";
        authController.tokenMap.put(validToken, username);
    }


    @Test
    public void testCreateUser() {
        User user = new User();
        user.setFirstName("John");

        ResponseEntity<String> response = authController.createUser(user);

        verify(userService, times(1)).addUser(user);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testLoginUser_UserExists() {
        User user = new User();
        user.setFirstName("John");

        when(userService.userExist(anyString())).thenReturn(true);

        ResponseEntity<String> response = authController.loginUser(user);

        verify(userService, times(1)).userExist(user.getFirstName());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testLoginUser_UserDoesNotExist() {
        User user = new User();
        user.setFirstName("John");

        when(userService.userExist(anyString())).thenReturn(false);

        ResponseEntity<String> response = authController.loginUser(user);

        verify(userService, times(1)).userExist(user.getFirstName());
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testGetAllNotesForAuthenticatedUser() {
        String accessToken = "dummyToken";
        List<Note> notes = Collections.singletonList(new Note());

        when(userService.getUserNotes(anyString())).thenReturn(notes);

        ResponseEntity<List<Note>> response = authController.getAllNotesForAuthenticatedUser(accessToken);

        verify(userService, times(1)).getUserNotes(anyString());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(notes, response.getBody());
    }
    @Test
    public void testGetNoteForAuthenticatedUserById_NoteExists() {
        String accessToken = "dummyToken";
        int noteId = 1;
        Note note = new Note();
        note.setId(noteId);

        when(userService.getUserNotesById(anyString(), anyInt())).thenReturn(note);

        ResponseEntity<Note> response = authController.getNoteForAuthenticatedUserById(accessToken, noteId);

        verify(userService, times(1)).getUserNotesById(anyString(), anyInt());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(note, response.getBody());
    }

    @Test
    public void testGetNoteForAuthenticatedUserById_NoteDoesNotExist() {
        String accessToken = "dummyToken";
        int noteId = 1;
        when(userService.getUserNotesById(anyString(), anyInt())).thenReturn(null);
        ResponseEntity<Note> response = authController.getNoteForAuthenticatedUserById(accessToken, noteId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testCreateNote_ValidToken() {
        String accessToken = "dummyToken";
        Note note = new Note();
        ResponseEntity<String> response = authController.createNote(note, accessToken);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testCreateNote_InvalidToken() {
        String invalidAccessToken = "invalidToken";
        Note note = new Note();

        ResponseEntity<String> response = authController.createNote(note, invalidAccessToken);

        verifyNoInteractions(userService);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
    @Test
    public void testUpdateNote_NoteExists() {
        String accessToken = "dummyToken";
        int noteId = 1;
        Note note = new Note();
        note.setId(noteId);

        when(userService.getUserNotesById(anyString(), anyInt())).thenReturn(note);

        ResponseEntity<String> response = authController.updateNote(note, accessToken, noteId);

        verify(userService, times(1)).getUserNotesById(anyString(), anyInt());
        verify(userService, times(1)).addNote(note);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testUpdateNote_NoteDoesNotExist() {
        String accessToken = "dummyToken";
        int noteId = 1;

        when(userService.getUserNotesById(anyString(), anyInt())).thenReturn(null);

        ResponseEntity<String> response = authController.updateNote(new Note(), accessToken, noteId);

        verify(userService, times(1)).getUserNotesById(anyString(), anyInt());
        verifyNoMoreInteractions(userService);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testDeleteNoteForAuthenticatedUserById_NoteExists() {
        String accessToken = "dummyToken";
        int noteId = 1;
        Note note = new Note();
        note.setId(noteId);

        when(userService.getUserNotesById(anyString(), anyInt())).thenReturn(note);

        ResponseEntity<String> response = authController.deleteNoteForAuthenticatedUserById(accessToken, noteId);

        verify(userService, times(1)).getUserNotesById(anyString(), anyInt());
        verify(userService, times(1)).deleteNoteById(anyString(), anyInt());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testDeleteNoteForAuthenticatedUserById_NoteDoesNotExist() {
        String accessToken = "dummyToken";
        int noteId = 1;

        when(userService.getUserNotesById(anyString(), anyInt())).thenReturn(null);

        ResponseEntity<String> response = authController.deleteNoteForAuthenticatedUserById(accessToken, noteId);

        verify(userService, times(1)).getUserNotesById(anyString(), anyInt());
        verifyNoMoreInteractions(userService);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testSearchNotes_ValidToken() {
        String accessToken = "dummyToken";
        String query = "test";

        List<Note> searchedNotes = Collections.singletonList(new Note());
        when(userService.searchUserNotes(anyString(), anyString())).thenReturn(searchedNotes);

        ResponseEntity<List<Note>> response = authController.searchNotes(accessToken, query);

        verify(userService, times(1)).searchUserNotes(anyString(), anyString());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(searchedNotes, response.getBody());
    }

    @Test
    public void testSearchNotes_InvalidToken() {
        String invalidAccessToken = "invalidToken";
        String query = "test";

        ResponseEntity<List<Note>> response = authController.searchNotes(invalidAccessToken, query);

        verifyNoInteractions(userService);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
