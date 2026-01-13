package com.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private Integer age;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String role;

    // --- RELATIONSHIPS ---

    // Changed to LAZY to prevent the "Infinite Loop" crash
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Goal> goals = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("id ASC") 
    private List<MoodLog> moodLogs = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<ForumPost> forumPosts = new ArrayList<>();

    // NEW: For Advisors to see sessions they created
    @OneToMany(mappedBy = "advisor", fetch = FetchType.LAZY)
    private List<CounselingSession> advisedSessions = new ArrayList<>();

    // NEW: For Students to see sessions they joined
    @ManyToMany(mappedBy = "attendees", fetch = FetchType.LAZY)
    private List<CounselingSession> joinedSessions = new ArrayList<>();

    // --- CONSTRUCTORS ---
    public User() {}

    public User(Long id, String name, Integer age, String email, String phone, String password, String role) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
    }

    // --- GETTERS AND SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public List<Goal> getGoals() { return goals; }
    public void setGoals(List<Goal> goals) { this.goals = goals; }
    public List<MoodLog> getMoodLogs() { return moodLogs; }
    public void setMoodLogs(List<MoodLog> moodLogs) { this.moodLogs = moodLogs; }
    public List<ForumPost> getForumPosts() { return forumPosts; }
    public void setForumPosts(List<ForumPost> forumPosts) { this.forumPosts = forumPosts; }
    public List<CounselingSession> getAdvisedSessions() { return advisedSessions; }
    public void setAdvisedSessions(List<CounselingSession> advisedSessions) { this.advisedSessions = advisedSessions; }
    public List<CounselingSession> getJoinedSessions() { return joinedSessions; }
    public void setJoinedSessions(List<CounselingSession> joinedSessions) { this.joinedSessions = joinedSessions; }

    // --- SAFETY FIXES: PROXY-SAFE METHODS ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        // Use instanceof to handle Hibernate Proxies correctly
        if (!(o instanceof User)) return false; 
        User user = (User) o;
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        // Constant hashcode is safest for entities that move in and out of Sets/Lists
        return 31; 
    }

    @Override
    public String toString() {
        // NEVER include Lists (goals, moodLogs, sessions) in toString
        // This is the #1 cause of StackOverflow errors
        return "User{id=" + id + ", name='" + name + "', role='" + role + "'}";
    }
}