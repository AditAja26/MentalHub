package com.dao;

import com.model.User;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDAOHibernate implements UserDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(User user) {
        sessionFactory.getCurrentSession().saveOrUpdate(user);
    }

    @Override
    public User getById(Long id) {
        return sessionFactory.getCurrentSession().get(User.class, id);
    }

    /**
     * Optimized method to fetch User with Goals and MoodLogs.
     * This prevents N+1 query problem and lazy loading issues.
     * Uses two queries to avoid MultipleBagFetchException.
     */
    @Override
    public User getByIdWithDetails(Long id) {
        // First, fetch user with goals
        String hql1 = "SELECT DISTINCT u FROM User u " +
                      "LEFT JOIN FETCH u.goals " +
                      "WHERE u.id = :id";
        
        Query<User> query1 = sessionFactory.getCurrentSession()
                .createQuery(hql1, User.class);
        query1.setParameter("id", id);
        User user = query1.uniqueResult();
        
        if (user != null) {
            // Then, fetch mood logs for the same user
            String hql2 = "SELECT DISTINCT u FROM User u " +
                          "LEFT JOIN FETCH u.moodLogs " +
                          "WHERE u.id = :id";
            
            Query<User> query2 = sessionFactory.getCurrentSession()
                    .createQuery(hql2, User.class);
            query2.setParameter("id", id);
            query2.uniqueResult(); // This updates the user in the session with moodLogs
        }
        
        return user;
    }

    @Override
    public User getByEmail(String email) {
        Query<User> query = sessionFactory.getCurrentSession()
                .createQuery("FROM User WHERE email = :email", User.class);
        query.setParameter("email", email);
        return query.uniqueResult();
    }

    @Override
    public List<User> getAll() {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM User", User.class).list();
    }

    @Override
    public List<User> getByRole(String role) {
        Query<User> query = sessionFactory.getCurrentSession()
                .createQuery("FROM User WHERE role = :role", User.class);
        query.setParameter("role", role);
        return query.list();
    }

    @Override
    public void update(User user) {
        sessionFactory.getCurrentSession().update(user);
    }

    @Override
    public void delete(Long id) {
        User user = getById(id);
        if (user != null) {
            sessionFactory.getCurrentSession().delete(user);
        }
    }
}