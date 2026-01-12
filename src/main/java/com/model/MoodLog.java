package com.model;

import javax.persistence.*;

@Entity
@Table(name = "mood_logs")
public class MoodLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Double score; // This provides the value for the chart (e.g., 4.5)
    private String label; // This provides the X-axis label (e.g., "Week 1")

    public MoodLog() {}

    public MoodLog(User user, Double score, String label) {
        this.user = user;
        this.score = score;
        this.label = label;
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
}