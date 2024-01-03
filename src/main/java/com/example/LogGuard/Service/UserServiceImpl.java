package com.example.LogGuard.Service;

import com.example.LogGuard.LogGuardApplication;
import com.example.LogGuard.Model.Note;
import com.example.LogGuard.Model.User;
import com.example.LogGuard.Repository.NoteDao;
import com.example.LogGuard.Repository.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService{
    private static final Logger log = LoggerFactory.getLogger(LogGuardApplication.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserDao userDao;

    @Autowired
    private NoteDao noteDao;

    @Override
    public void addUser(User user) {
        userDao.save(user);
    }

    @Override
    public Boolean userExist(String firstName) {
        return userDao.existsById(firstName);
    }

    @Override
    public void deleteById(User user) {
        String firstName = user.getFirstName();
        log.info("User " + firstName + " Deleted from Db.");
        userDao.deleteById(user.getFirstName());
    }

    @Override
    public Boolean userExistInCache(int mid) {
        return redisTemplate.hasKey("user::" + String.valueOf(mid));
    }

    private void logResponseTime(Long startTime, User user, Boolean check) {
        Long dbResponseTime = System.currentTimeMillis() - startTime;
        log.info("User " + user.getFirstName() + " fetched from database in time " + dbResponseTime + "ms");
        if (check.equals(true)) log.info("Increased latency of Database operation.");
    }

    @Override
    public List<Note> getUserNotes(String firstName) {
        List<Note> res = new ArrayList<>();
        List<Note> notes = (List<Note>) noteDao.findAll();
        for (Note n : notes) {
            if (n.getFirstName().toLowerCase().equals(firstName.toLowerCase())) res.add(n);
        }
        return res;
    }

    @Override
    public Note getUserNotesById(String firstName, int id) {
        List<Note> notes = getUserNotes(firstName);
        for (Note n : notes) {
            if (n.getId() == id) return n;
        }
        return null;
    }

    @Override
    public void addNote(Note note) {
        noteDao.save(note);
    }

    @Override
    public void deleteNoteById(String firstName, int id) {
        log.info("Note " + id + " deleted for user " + firstName + " from Db.");
        noteDao.deleteById(firstName);
    }

    @Override
    public List<Note> searchUserNotes(String firstName, String query){
        List<Note> userNotes = getUserNotes(firstName); // Assuming this method retrieves all user notes
        List<Note> searchedNotes = new ArrayList<>();
        for (Note note : userNotes) {
            if (note.getNote().toLowerCase().contains(query.toLowerCase())) {
                searchedNotes.add(note);
            }
        }
        return searchedNotes;
    }
}
