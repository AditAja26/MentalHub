package com.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "counseling_sessions")
public class CounselingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    
    @Column(length = 1000)
    private String description;
    
    private String date;       // Format: YYYY-MM-DD
    private String time;       // Format: HH:MM
    private String meetingLink; 
    private int maxCapacity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "advisor_id")
    private User advisor;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "session_enrollments",
        joinColumns = @JoinColumn(name = "session_id"),
        inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private List<User> attendees = new ArrayList<>();

    // Constructors
    public CounselingSession() {}

    // --- HELPER METHODS ---

    public void addAttendee(User student) {
        if (!attendees.contains(student)) {
            attendees.add(student);
        }
    }

    // THIS IS THE METHOD YOU WERE MISSING
    public boolean isFull() {
        return this.attendees.size() >= this.maxCapacity;
    }

    // --- GETTERS & SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getMeetingLink() { return meetingLink; }
    public void setMeetingLink(String meetingLink) { this.meetingLink = meetingLink; }

    public int getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(int maxCapacity) { this.maxCapacity = maxCapacity; }

    public User getAdvisor() { return advisor; }
    public void setAdvisor(User advisor) { this.advisor = advisor; }

    public List<User> getAttendees() { return attendees; }
    public void setAttendees(List<User> attendees) { this.attendees = attendees; }
}