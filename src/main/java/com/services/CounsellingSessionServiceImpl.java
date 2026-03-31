package com.services;

import com.dao.CounsellingSessionDao;
import com.model.CounsellingSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class CounsellingSessionServiceImpl implements CounsellingSessionService {

    @Autowired
    private CounsellingSessionDao counsellingSessionDao;

    @Override
    public CounsellingSession addSession(CounsellingSession session) {
        return counsellingSessionDao.save(session);
    }

    @Override
    public List<CounsellingSession> getAllSessions() {
        return counsellingSessionDao.findAll();
    }

    @Override
    public CounsellingSession getSessionById(Long id) {
        return counsellingSessionDao.findById(id);
    }

    @Override
    public List<CounsellingSession> getSessionsByDate(String dateString) {
        return counsellingSessionDao.findByDate(dateString);
    }

    @Override
    public void deleteSession(Long id) {
        counsellingSessionDao.deleteById(id);
    }
    
}
