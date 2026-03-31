package com.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "mood_logs")
public class MoodLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Double score; // This provides the value for the chart (e.g., 4.5)
    private String label; // This provides the X-axis label (e.g., "Week 1")
    
    // NEW FIELDS for per-login mood tracking
    @Column(name = "mood_type")
    private String moodType; // "happy", "average", "sad", "depressed"
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "logged_at")
    private Date loggedAt; // Timestamp when mood was recorded
    
    // QUIZ FIELDS - Store individual question responses
    @Column(name = "q1_overall_feeling")
    private Integer q1OverallFeeling; // 1-5: How feeling overall
    
    @Column(name = "q2_sleep_quality")
    private Integer q2SleepQuality; // 1-5: Sleep quality
    
    @Column(name = "q3_stress_level")
    private Integer q3StressLevel; // 1-5: Stress level
    
    @Column(name = "q4_focus_ability")
    private Integer q4FocusAbility; // 1-5: Ability to focus
    
    @Column(name = "q5_social_connection")
    private Integer q5SocialConnection; // 1-5: Social connection

    public MoodLog() {}

    public MoodLog(User user, Double score, String label) {
        this.user = user;
        this.score = score;
        this.label = label;
    }
    
    // NEW Constructor for per-login mood tracking
    public MoodLog(User user, String moodType, Double score) {
        this.user = user;
        this.moodType = moodType;
        this.score = score;
        this.loggedAt = new Date();
        this.label = null; // Not used for per-login tracking
    }
    
    // Constructor for quiz-based mood tracking
    public MoodLog(User user, Integer q1, Integer q2, Integer q3, Integer q4, Integer q5) {
        this.user = user;
        this.q1OverallFeeling = q1;
        this.q2SleepQuality = q2;
        this.q3StressLevel = q3;
        this.q4FocusAbility = q4;
        this.q5SocialConnection = q5;
        this.loggedAt = new Date();
        
        // Calculate score from quiz answers (average of all questions)
        this.score = (q1 + q2 + q3 + q4 + q5) / 5.0;
        
        // Determine mood type based on average score
        if (this.score >= 4.0) {
            this.moodType = "happy";
        } else if (this.score >= 3.0) {
            this.moodType = "average";
        } else if (this.score >= 2.0) {
            this.moodType = "sad";
        } else {
            this.moodType = "depressed";
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public String getMoodType() { return moodType; }
    public void setMoodType(String moodType) { this.moodType = moodType; }
    public Date getLoggedAt() { return loggedAt; }
    public void setLoggedAt(Date loggedAt) { this.loggedAt = loggedAt; }
    
    // Quiz answer getters and setters
    public Integer getQ1OverallFeeling() { return q1OverallFeeling; }
    public void setQ1OverallFeeling(Integer q1OverallFeeling) { this.q1OverallFeeling = q1OverallFeeling; }
    public Integer getQ2SleepQuality() { return q2SleepQuality; }
    public void setQ2SleepQuality(Integer q2SleepQuality) { this.q2SleepQuality = q2SleepQuality; }
    public Integer getQ3StressLevel() { return q3StressLevel; }
    public void setQ3StressLevel(Integer q3StressLevel) { this.q3StressLevel = q3StressLevel; }
    public Integer getQ4FocusAbility() { return q4FocusAbility; }
    public void setQ4FocusAbility(Integer q4FocusAbility) { this.q4FocusAbility = q4FocusAbility; }
    public Integer getQ5SocialConnection() { return q5SocialConnection; }
    public void setQ5SocialConnection(Integer q5SocialConnection) { this.q5SocialConnection = q5SocialConnection; }
}