package com.services;

import com.dao.MoodLogDAO;
import com.model.MoodLog;
import com.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class MoodLogService {

    @Autowired
    private MoodLogDAO moodLogDAO;

    /**
     * Logs the user's mood after login
     * @param user The user logging the mood
     * @param moodType The mood type: "happy", "average", "sad", "depressed"
     */
    @Transactional
    public MoodLog logMood(User user, String moodType) {
        // Map mood types to scores for chart compatibility
        Double score = getMoodScore(moodType);
        
        MoodLog moodLog = new MoodLog(user, moodType, score);
        moodLogDAO.save(moodLog);
        
        System.out.println(">>> MOOD LOG: User " + user.getName() + " logged mood: " + moodType + " (score: " + score + ")");
        return moodLog;
    }
    
    /**
     * Logs the user's mood based on quiz responses
     * @param user The user logging the mood
     * @param q1 Question 1: Overall feeling (1-5)
     * @param q2 Question 2: Sleep quality (1-5)
     * @param q3 Question 3: Stress level (1-5, inverted)
     * @param q4 Question 4: Focus ability (1-5)
     * @param q5 Question 5: Social connection (1-5)
     */
    @Transactional
    public MoodLog logMoodFromQuiz(User user, Integer q1, Integer q2, Integer q3, Integer q4, Integer q5) {
        System.out.println(">>> MoodLogService.logMoodFromQuiz called");
        System.out.println(">>> User: " + (user != null ? user.getName() + " (ID: " + user.getId() + ")" : "NULL"));
        System.out.println(">>> User object hashcode: " + (user != null ? user.hashCode() : "NULL"));
        System.out.println(">>> Raw values - Q1:" + q1 + " Q2:" + q2 + " Q3:" + q3 + " Q4:" + q4 + " Q5:" + q5);
        
        if (user == null || user.getId() == null) {
            System.out.println(">>> ERROR: User or User ID is null! Cannot save mood log.");
            throw new IllegalArgumentException("User cannot be null");
        }
        
        // Invert stress level (high stress = low mood score)
        Integer q3Inverted = 6 - q3;
        System.out.println(">>> Q3 inverted from " + q3 + " to " + q3Inverted);
        
        MoodLog moodLog = new MoodLog(user, q1, q2, q3Inverted, q4, q5);
        System.out.println(">>> MoodLog created - Type: " + moodLog.getMoodType() + ", Score: " + moodLog.getScore());
        System.out.println(">>> MoodLog.user: " + (moodLog.getUser() != null ? moodLog.getUser().getName() + " (ID: " + moodLog.getUser().getId() + ")" : "NULL"));
        System.out.println(">>> MoodLog values - Q1:" + moodLog.getQ1OverallFeeling() + 
                          " Q2:" + moodLog.getQ2SleepQuality() + 
                          " Q3:" + moodLog.getQ3StressLevel() + 
                          " Q4:" + moodLog.getQ4FocusAbility() + 
                          " Q5:" + moodLog.getQ5SocialConnection());
        
        System.out.println(">>> Calling DAO.save()...");
        moodLogDAO.save(moodLog);
        System.out.println(">>> DAO.save() completed. MoodLog ID: " + moodLog.getId());
        System.out.println(">>> After save - MoodLog.user: " + (moodLog.getUser() != null ? moodLog.getUser().getId() : "NULL"));
        
        System.out.println(">>> MOOD LOG (QUIZ): User " + user.getName() + " logged mood: " + moodLog.getMoodType() + 
                          " (score: " + moodLog.getScore() + ") [Q1:" + q1 + " Q2:" + q2 + " Q3:" + q3 + " Q4:" + q4 + " Q5:" + q5 + "]");
        return moodLog;
    }

    /**
     * Checks if the user has already logged their mood today
     */
    @Transactional(readOnly = true)
    public boolean hasLoggedMoodToday(Long userId) {
        return moodLogDAO.hasLoggedMoodToday(userId);
    }

    /**
     * Gets all mood logs for a user
     */
    @Transactional(readOnly = true)
    public List<MoodLog> getUserMoodLogs(Long userId) {
        return moodLogDAO.getByUserId(userId);
    }

    /**
     * Gets mood logs within a date range
     */
    @Transactional(readOnly = true)
    public List<MoodLog> getUserMoodLogsByDateRange(Long userId, Date startDate, Date endDate) {
        return moodLogDAO.getByUserIdAndDateRange(userId, startDate, endDate);
    }

    /**
     * Gets the latest mood log for a user
     */
    @Transactional(readOnly = true)
    public MoodLog getLatestMoodLog(Long userId) {
        return moodLogDAO.getLatestByUserId(userId);
    }

    /**
     * Maps mood type to a numeric score for chart display
     * happy = 5.0, average = 3.0, sad = 2.0, depressed = 1.0
     */
    private Double getMoodScore(String moodType) {
        switch (moodType.toLowerCase()) {
            case "happy":
                return 5.0;
            case "average":
                return 3.0;
            case "sad":
                return 2.0;
            case "depressed":
                return 1.0;
            default:
                return 3.0; // Default to average
        }
    }

    /**
     * Get mood description from score
     */
    public String getMoodDescription(Double score) {
        if (score >= 4.5) return "happy";
        if (score >= 2.5) return "average";
        if (score >= 1.5) return "sad";
        return "depressed";
    }
}
