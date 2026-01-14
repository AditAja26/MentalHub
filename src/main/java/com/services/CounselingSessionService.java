package com.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dao.CounselingSessionDAO;
import com.model.CounselingSession;
import com.model.User;

@Service
public class CounselingSessionService {

    @Autowired
    private CounselingSessionDAO sessionDAO;

    @Transactional
    public void createSession(CounselingSession session) {
        sessionDAO.addSession(session);
    }
    
    @Transactional
    public void updateSession(CounselingSession session) {
        sessionDAO.updateSession(session);
    }

    @Transactional
    public void deleteSession(Long id) {
        sessionDAO.deleteSession(id);
    }

    @Transactional(readOnly = true)
    public CounselingSession getSessionById(Long id) {
        return sessionDAO.getSessionById(id);
    }

    @Transactional(readOnly = true)
    public List<CounselingSession> getAllSessions() {
        return sessionDAO.getAllSessions();
    }

    @Transactional(readOnly = true)
    public List<CounselingSession> getSessionsByAdvisor(User advisor) {
        return sessionDAO.getSessionsByAdvisor(advisor);
    }

    /**
     * Logic for a student joining a session
     * Returns true if successful, false if full or already joined
     */
    @Transactional
    public boolean enrollStudent(Long sessionId, User student) {
        CounselingSession session = sessionDAO.getSessionById(sessionId);
        
        if (session != null) {
            // Check logic: Is it full?
            if (session.isFull()) {
                return false;
            }
            // Check logic: Is student already in?
            // (Uses User.equals, ensure User entity has equals/hashCode or check IDs)
            for (User attendee : session.getAttendees()) {
                if (attendee.getId().equals(student.getId())) {
                    return false; 
                }
            }

            session.addAttendee(student);
            sessionDAO.updateSession(session); // Update DB
            return true;
        }
        return false;
    }
}