package com.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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

    /**
     * FIX 1: Use mappedBy instead of @JoinTable.
     * Your 'goals' table already has a 'user_id' column.
     * This stops Hibernate from creating that broken 'user_goals' table.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private List<Goal> goals = new ArrayList<>();

    /**
     * FIX 2: Keep SUBSELECT and EAGER.
     * This ensures 'moodLogs' data is ready for the trend chart.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @OrderBy("id ASC") 
    private List<MoodLog> moodLogs = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<ForumPost> forumPosts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Notification> notifications = new ArrayList<>();

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

    // Getters and Setters (Keep existing ones)
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
}