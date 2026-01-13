package com.model;

import javax.persistence.*;

@Entity
@Table(name = "appointments")
public class Appointment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String date;
    private String time;
    private String counselor; // The Advisor's name
    private String reason;

    // --- NEW FIELDS ---
    
    // 1. Status: To track Accept/Reject
    // We use a String for simplicity, or you can create an Enum
    private String status = "PENDING"; 

    // 2. Student: We MUST link this to the User entity
    // otherwise we don't know who to notify!
    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    // --- CONSTRUCTORS ---

    public Appointment() {}

    public Appointment(String date, String time, String counselor, String reason, User student) {
        this.date = date;
        this.time = time;
        this.counselor = counselor;
        this.reason = reason;
        this.student = student;
        this.status = "PENDING"; // Default to Pending
    }

    // --- GETTERS AND SETTERS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    
    public String getCounselor() { return counselor; }
    public void setCounselor(String counselor) { this.counselor = counselor; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    // New Getters/Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
}