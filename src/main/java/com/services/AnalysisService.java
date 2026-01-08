package com.services;

import com.model.Goal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class AnalysisService {

    @Autowired
    private GoalService goalService;

    public Map<String, Object> analyzeUserProgress(Long userId) {
        List<Goal> allGoals = goalService.getGoalsByUserId(userId);
        int total = allGoals.size();
        long completed = allGoals.stream().filter(Goal::isCompleted).count();
        
        // Analysis Logic
        double percentage = total == 0 ? 0 : ((double) completed / total) * 100;
        String status = interpretProgress(percentage);

        Map<String, Object> report = new HashMap<>();
        report.put("total", total);
        report.put("completed", completed);
        report.put("percentage", Math.round(percentage));
        report.put("status", status);
        
        return report;
    }

    private String interpretProgress(double percentage) {
        if (percentage >= 80) return "High Mental Health Literacy - Flourishing";
        if (percentage >= 50) return "Moderate Literacy - On the Right Track";
        return "Low Literacy - Consider Booking an Appointment";
    }
}