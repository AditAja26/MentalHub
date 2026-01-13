package com.dao;

import com.model.DailyQuiz;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DailyQuizDaoHibernate implements DailyQuizDao{
    
    @Autowired
    private SessionFactory sessionFactory;

    private Session openSession(){
        return sessionFactory.openSession();
    }

    @Override
    public DailyQuiz save(DailyQuiz quiz) {
        Session session = openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.saveOrUpdate(quiz);
            tx.commit();
            return quiz;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }
    }

    @Override
    public List<DailyQuiz> findAll() {
        Session session = openSession();
        String sql = "SELECT * FROM daily_quizzes ORDER BY quiz_date DESC";
        List<DailyQuiz> list = session.createNativeQuery(sql, DailyQuiz.class).list();
        session.close();
        return list;
    }

    @Override
    public DailyQuiz findById(Long id) {
        Session session = openSession();
        String sql = "SELECT * FROM daily_quizzes WHERE id = :id";
        DailyQuiz quiz = session.createNativeQuery(sql, DailyQuiz.class)
                                .setParameter("id", id)
                                .uniqueResult();
        session.close();
        return quiz;
    }

    @Override
    public List<DailyQuiz> findByDate(String dateString) {
        Session session = openSession();
        
        // SQL: "Does the text version of the date match?"
        // DATE_FORMAT(column, '%Y-%m-%d') turns the DB date into "2026-01-13"
        String sql = "SELECT * FROM daily_quizzes WHERE DATE_FORMAT(quiz_date, '%Y-%m-%d') = :dateString";
        
        List<DailyQuiz> quizzes = session.createNativeQuery(sql, DailyQuiz.class)
                                         .setParameter("dateString", dateString)
                                         .list();
        session.close();
        return quizzes;
    }

    @Override
    public void deleteById(Long id) {
        Session session = openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            
            String sql = "DELETE FROM daily_quizzes WHERE id = :id";
            session.createNativeQuery(sql)
                   .setParameter("id", id)
                   .executeUpdate();
            
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    } 
}
