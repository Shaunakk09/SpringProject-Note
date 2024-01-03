package com.example.LogGuard.Controller;

import com.example.LogGuard.LogGuardApplication;
import com.example.LogGuard.Model.Note;
import com.example.LogGuard.Model.User;
import com.example.LogGuard.RateLimiting.RateLimitingControllerAdvice;
import com.example.LogGuard.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(LogGuardApplication.class);

    public Map<String, String> tokenMap = new HashMap<>();

    @Autowired
    private UserService userService;

    @Autowired
    private RateLimitingControllerAdvice rateLimitingControllerAdvice;

    @PostMapping("/api/auth/signup")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        rateLimitingControllerAdvice.checkRateLimit("createUser");
        userService.addUser(user);
        log.info("User " + user.getFirstName() + " added to the database.");
        return ResponseEntity.ok("User " + user.getFirstName() + " added successfully.");
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<String> loginUser(@RequestBody User user) {
        rateLimitingControllerAdvice.checkRateLimit("loginUser");
        if (userService.userExist(user.getFirstName())) {
            String accessToken = generateAccessToken(user.getFirstName());
            tokenMap.put(accessToken, user.getFirstName());
            return ResponseEntity.ok("Access token-> " + accessToken);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User does not exist.");
        }
    }

    @GetMapping("/api/notes")
    public ResponseEntity<List<Note>> getAllNotesForAuthenticatedUser(@RequestHeader("accessToken") String accessToken) {
        rateLimitingControllerAdvice.checkRateLimit("getAllNotesForAuthenticatedUser");
        if (isValidToken(accessToken)) {
            List<Note> userNotes = userService.getUserNotes(tokenMap.get(accessToken));
            return ResponseEntity.ok(userNotes);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/api/notes/{id}")
    public ResponseEntity<Note> getNoteForAuthenticatedUserById(@RequestHeader("accessToken") String accessToken, @PathVariable("id") int id) {
        rateLimitingControllerAdvice.checkRateLimit("getNoteForAuthenticatedUserById");
        if (isValidToken(accessToken)) {
            Note userNote = userService.getUserNotesById(tokenMap.get(accessToken), id);
            if (userNote != null) {
                return ResponseEntity.ok(userNote);
            }
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/api/notes")
    public ResponseEntity<String> createNote(@RequestBody Note note, @RequestHeader("accessToken") String accessToken) {
        rateLimitingControllerAdvice.checkRateLimit("createNote");
        if (isValidToken(accessToken)) {
            userService.addNote(note);
            log.info("Note added to the database.");
            return ResponseEntity.ok("Note added successfully.");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized.");
    }

    @PutMapping("/api/notes/{id}")
    public ResponseEntity<String> updateNote(@RequestBody Note note, @RequestHeader("accessToken") String accessToken, @PathVariable("id") int id) {
        rateLimitingControllerAdvice.checkRateLimit("updateNote");
        if (isValidToken(accessToken)) {
            Note existingNote = userService.getUserNotesById(tokenMap.get(accessToken), id);
            if (existingNote != null) {
                userService.addNote(note);
                log.info("Note updated in the database.");
                return ResponseEntity.ok("Note updated successfully.");
            }
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized.");
    }

    @DeleteMapping("/api/notes/{id}")
    public ResponseEntity<String> deleteNoteForAuthenticatedUserById(@RequestHeader("accessToken") String accessToken, @PathVariable("id") int id) {
        rateLimitingControllerAdvice.checkRateLimit("deleteNoteForAuthenticatedUserById");
        if (isValidToken(accessToken)) {
            Note existingNote = userService.getUserNotesById(tokenMap.get(accessToken), id);
            if (existingNote != null) {
                userService.deleteNoteById(tokenMap.get(accessToken), id);
                log.info("Note deleted from the database.");
                return ResponseEntity.ok("Note deleted successfully.");
            }
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized.");
    }

    @GetMapping("/api/search")
    public ResponseEntity<List<Note>> searchNotes(@RequestHeader("accessToken") String accessToken, @RequestParam("query") String query) {
        rateLimitingControllerAdvice.checkRateLimit("searchNotes");
        if (isValidToken(accessToken)) {
            List<Note> searchedNotes = userService.searchUserNotes(tokenMap.get(accessToken), query);
            return ResponseEntity.ok(searchedNotes);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    private boolean isValidToken(String accessToken) {
        return tokenMap.containsKey(accessToken);
    }

    private String generateAccessToken(String username) {
        return username + UUID.randomUUID().toString();
    }
}
