package com.dao;

import java.util.List;

import com.model.CounsellingSession;

public interface CounsellingSessionDao {
    CounsellingSession save(CounsellingSession session);            // Create or Update
    List<CounsellingSession> findAll();                             // List all sessions (for Admin)
    CounsellingSession findById(Long id);                           // Find by ID
    List<CounsellingSession> findByDate(String dateString);         // Find session for a specific day (Student View)
    void deleteById(Long id);                                       // Delete a session by ID
}
