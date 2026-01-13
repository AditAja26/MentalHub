package com.services;

import java.util.List;
import com.model.CounsellingSession;

public interface CounsellingSessionService {
    CounsellingSession addSession(CounsellingSession session);
    List<CounsellingSession> getAllSessions();
    CounsellingSession getSessionById(Long id);
    List<CounsellingSession> getSessionsByDate(String dateString);
    void deleteSession(Long id);
}
