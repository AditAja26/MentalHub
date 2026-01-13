package com.dao;

import com.model.ForumPost;
import org.hibernate.Hibernate; // Import for initialization
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ForumPostDaoHibernate implements ForumPostDao {

    @Autowired
    private SessionFactory sessionFactory;

    private Session openSession() {
        return sessionFactory.openSession();
    }

    @Override
    public void save(ForumPost post) {
        Session session = openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.saveOrUpdate(post);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public List<ForumPost> findAll() {
        Session session = openSession();
        
        // HQL: Simple, readable, object-oriented
        String hql = "FROM ForumPost p ORDER BY p.createdAt DESC";
        
        List<ForumPost> list = session.createQuery(hql, ForumPost.class).list();
        
        // Initialize relationships to prevent "LazyInitializationException" in HTML
        for (ForumPost post : list) {
            // Safely initialize the User (Author)
            if (post.getUser() != null) {
                Hibernate.initialize(post.getUser());
            }
            // Safely initialize the Comments list
            Hibernate.initialize(post.getComments());
        }
        
        session.close();
        return list;
    }

    @Override
    public ForumPost findById(Long id) {
        Session session = openSession();
        
        // HQL POWER MOVE: "LEFT JOIN FETCH"
        // This query fetches the Post, the User, and the Comments in ONE go.
        // It prevents the "Session Closed" error completely for the Detail View.
        String hql = "SELECT DISTINCT p FROM ForumPost p " +
                     "LEFT JOIN FETCH p.user " +
                     "LEFT JOIN FETCH p.comments " +
                     "WHERE p.id = :id";
        
        ForumPost post = session.createQuery(hql, ForumPost.class)
                                .setParameter("id", id)
                                .uniqueResult();
        
        session.close();
        return post;
    }

    @Override
    public void deleteById(Long id) {
        Session session = openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // With HQL/Hibernate, we fetch the object first, then delete it.
            // This ensures cascade rules (deleting comments) work correctly.
            ForumPost post = session.get(ForumPost.class, id);
            if (post != null) {
                session.delete(post);
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