package com.services;

import com.dao.GoalDAO;
import com.model.Goal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoalService {
    
    @Autowired
    private GoalDAO goalDao;

    @Transactional
    public Goal addGoal(Goal goal) {
        // Hibernate handles ID generation automatically now
        goalDao.save(goal);
        return goal;
    }

    @Transactional(readOnly = true)
    public List<Goal> getGoalsByUserId(Long userId) {
        return goalDao.getByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Goal> getActiveGoalsByUserId(Long userId) {
        return goalDao.getByUserId(userId).stream()
            .filter(goal -> !goal.isCompleted())
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Goal> getCompletedGoalsByUserId(Long userId) {
        return goalDao.getByUserId(userId).stream()
            .filter(Goal::isCompleted)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Goal getGoalById(Long id) {
        return goalDao.getById(id);
    }

    @Transactional
    public Goal updateGoal(Long id, Goal updatedGoal) {
        Goal existing = goalDao.getById(id);
        if (existing != null) {
            if (updatedGoal.getDescription() != null) {
                existing.setDescription(updatedGoal.getDescription());
            }
            existing.setCompleted(updatedGoal.isCompleted());
            goalDao.update(existing);
            return existing;
        }
        return null;
    }

    @Transactional
    public Goal completeGoal(Long id) {
        Goal existing = goalDao.getById(id);
        if (existing != null) {
            existing.setCompleted(true);
            goalDao.update(existing);
            return existing;
        }
        return null;
    }

    @Transactional
    public boolean deleteGoal(Long id) {
        if (goalDao.getById(id) != null) {
            goalDao.delete(id);
            return true;
        }
        return false;
    }
}