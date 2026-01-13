package com.dao;

import java.util.List;
import com.model.CounselingSession;
import com.model.User;

public interface CounselingSessionDAO {
    void addSession(CounselingSession session);
    void updateSession(CounselingSession session);
    void deleteSession(Long id);
    CounselingSession getSessionById(Long id);
    List<CounselingSession> getAllSessions();
    List<CounselingSession> getSessionsByAdvisor(User advisor);
}