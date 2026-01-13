package com.dao;

import com.model.Goal;
import java.util.List;

public interface GoalDAO {
    void save(Goal goal);
    Goal getById(Long id);
    List<Goal> getByUserId(Long userId);
    void update(Goal goal);
    void delete(Long id);
}