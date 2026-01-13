package com.services;

import com.model.Goal;
import com.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class AnalysisService {

    @Autowired
    private GoalService goalService;

    @Autowired
    private UserService userService; // Added to fetch user-specific baseline data

    /**
     * @Transactional(readOnly = true) is best practice for analysis 
     * as it optimizes database performance for reading.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> analyzeUserProgress(Long userId) {
        // 1. Fetch real goals from the database via GoalService
        List<Goal> allGoals = goalService.getGoalsByUserId(userId);
        
        int total = allGoals.size();
        long completed = allGoals.stream().filter(Goal::isCompleted).count();
        long pending = total - completed; // Goals that are not completed yet
        
        // 2. Calculate Progress Percentage
        double percentage = total == 0 ? 0 : ((double) completed / total) * 100;
        long roundedPercentage = Math.round(percentage);
        
        // 3. Interpret the status for the report text
        String status = interpretProgress(percentage);

        // 4. Create the report map with keys that match our HTML
        Map<String, Object> report = new HashMap<>();
        report.put("total", total);
        report.put("completed", completed);
        report.put("pending", pending);              // Goals not yet completed
        report.put("incomplete", pending);           // Alias for pending
        report.put("percentage", roundedPercentage); // Used for progress bars
        report.put("status", status);                // Used for the "Summary" section
        
        // 5. Add detailed goal lists for the report
        List<Goal> completedGoals = allGoals.stream().filter(Goal::isCompleted).toList();
        List<Goal> pendingGoals = allGoals.stream().filter(g -> !g.isCompleted()).toList();
        
        report.put("completedGoals", completedGoals);
        report.put("pendingGoals", pendingGoals);
        report.put("allGoals", allGoals);
        
        // 6. Add Mood Logs Data for Chart (CRITICAL for cross-platform compatibility)
        User user = userService.getUserByIdWithDetails(userId);
        if (user != null && user.getMoodLogs() != null && !user.getMoodLogs().isEmpty()) {
            // Extract mood scores in order for the trend chart
            List<Double> moodScores = user.getMoodLogs().stream()
                .map(moodLog -> moodLog.getScore())
                .collect(java.util.stream.Collectors.toList());
            report.put("weeklyMoods", moodScores);
        } else {
            // Provide default data if no mood logs exist (prevents null errors)
            report.put("weeklyMoods", java.util.Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
        }
        
        // 7. Add Mood & Stress (Logic can be expanded later)
        // If progress is high, we can assume lower stress for now
        report.put("moodAvg", (roundedPercentage / 20.0)); // e.g., 80% = 4.0/5.0
        report.put("stressLevel", percentage >= 70 ? "Low" : "Moderate");
        
        return report;
    }

    private String interpretProgress(double percentage) {
        if (percentage >= 80) return "High Mental Health Literacy - Flourishing";
        if (percentage >= 50) return "Moderate Literacy - On the Right Track";
        return "Low Literacy - Consider Booking an Appointment";
    }
}