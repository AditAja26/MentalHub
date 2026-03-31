package com.dao;

import com.model.Goal;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GoalDAOHibernate implements GoalDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(Goal goal) {
        sessionFactory.getCurrentSession().saveOrUpdate(goal);
    }

    @Override
    public Goal getById(Long id) {
        return sessionFactory.getCurrentSession().get(Goal.class, id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Goal> getByUserId(Long userId) {
        // We use HQL (Hibernate Query Language) to find goals belonging to a specific user ID
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Goal g WHERE g.user.id = :uid")
                .setParameter("uid", userId)
                .getResultList();
    }

    @Override
    public void update(Goal goal) {
        sessionFactory.getCurrentSession().update(goal);
    }

    @Override
    public void delete(Long id) {
        Goal goal = getById(id);
        if (goal != null) {
            sessionFactory.getCurrentSession().delete(goal);
        }
    }
}