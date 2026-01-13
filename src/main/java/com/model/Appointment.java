package com.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "appointments") // SQL table name
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String date;
    private String time;
    private String counselor;
    private String reason;

    // Constructor
    public Appointment() {}

    // Getters and Setters (Important for Hibernate to access data)
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
}