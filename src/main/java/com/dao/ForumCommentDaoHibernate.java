package com.dao;

import com.model.ForumComment;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ForumCommentDaoHibernate implements ForumCommentDao {

    @Autowired
    private SessionFactory sessionFactory;

    private Session openSession() {
        return sessionFactory.openSession();
    }

    @Override
    public void save(ForumComment comment) {
        Session session = openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.saveOrUpdate(comment);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public ForumComment findById(Long id) {
        Session session = openSession();
        ForumComment comment = session.get(ForumComment.class, id);
        session.close();
        return comment;
    }

    @Override
    public void deleteById(Long id) {
        Session session = openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            ForumComment comment = session.get(ForumComment.class, id);
            if (comment != null) {
                session.delete(comment);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}