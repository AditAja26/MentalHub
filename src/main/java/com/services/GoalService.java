package com.services;

import com.model.Goal;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class GoalService {
    
    private final ConcurrentHashMap<Long, Goal> goals = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private boolean initialized = false;

    private void initIfNeeded() {
        if (initialized) return;
        initialized = true;
        Long hakimiUserId = 9L;
        
        addGoal(new Goal(null, hakimiUserId, "Read 2 articles on stress management this month.", false));
        addGoal(new Goal(null, hakimiUserId, "Book a counseling appointment by the end of the week.", false));
        
        addGoal(new Goal(null, hakimiUserId, "Read 2 articles on self reflection", true));
        addGoal(new Goal(null, hakimiUserId, "Watch 3 videos on 'Understanding Anxiety'.", true));
        addGoal(new Goal(null, hakimiUserId, "Complete 10 quiz", true));
        addGoal(new Goal(null, hakimiUserId, "Book my first counseling appointment.", true));
    }

    public Goal addGoal(Goal goal) {
        initIfNeeded();
        if (goal.getId() == null) {
            goal.setId(idGenerator.getAndIncrement());
        }
        goals.put(goal.getId(), goal);
        return goal;
    }

    public List<Goal> getAllGoals() {
        initIfNeeded();
        return new ArrayList<>(goals.values());
    }

    public List<Goal> getGoalsByUserId(Long userId) {
        return goals.values().stream()
            .filter(goal -> userId.equals(goal.getUserId()))
            .collect(Collectors.toList());
    }

    public List<Goal> getActiveGoalsByUserId(Long userId) {
        return goals.values().stream()
            .filter(goal -> userId.equals(goal.getUserId()) && !goal.isCompleted())
            .collect(Collectors.toList());
    }

    public List<Goal> getCompletedGoalsByUserId(Long userId) {
        return goals.values().stream()
            .filter(goal -> userId.equals(goal.getUserId()) && goal.isCompleted())
            .collect(Collectors.toList());
    }

    public Goal getGoalById(Long id) {
        return goals.get(id);
    }

    public Goal updateGoal(Long id, Goal updatedGoal) {
        Goal existing = goals.get(id);
        if (existing != null) {
            if (updatedGoal.getDescription() != null) existing.setDescription(updatedGoal.getDescription());
            existing.setCompleted(updatedGoal.isCompleted());
            return existing;
        }
        return null;
    }

    public Goal completeGoal(Long id) {
        Goal existing = goals.get(id);
        if (existing != null) {
            existing.setCompleted(true);
            return existing;
        }
        return null;
    }

    public boolean deleteGoal(Long id) {
        return goals.remove(id) != null;
    }

    public int getGoalCount() {
        return goals.size();
    }
}
