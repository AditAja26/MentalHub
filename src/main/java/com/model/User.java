package com.model;

import javax.persistence.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
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

    // 1. Goals (Students)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Goal> goals = new ArrayList<>();

    // 2. Mood Logs (Students) - Using SUBSELECT to prevent N+1 queries for the chart
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @OrderBy("id ASC")
    private List<MoodLog> moodLogs = new ArrayList<>();

    // 3. Forum Posts (Peer Support)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ForumPost> forumPosts = new ArrayList<>();

    // 4. Forum Comments (Peer Support)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ForumComment> forumComments = new ArrayList<>();

    // 5. Advised Sessions (Advisors)
    @OneToMany(mappedBy = "advisor", cascade = CascadeType.ALL)
    private List<CounselingSession> advisedSessions = new ArrayList<>();

    // 6. Joined Sessions (Students)
    @ManyToMany(mappedBy = "attendees")
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

    // --- GETTERS & SETTERS ---

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

    public List<ForumComment> getForumComments() { return forumComments; }
    public void setForumComments(List<ForumComment> forumComments) { this.forumComments = forumComments; }

    public List<CounselingSession> getAdvisedSessions() { return advisedSessions; }
    public void setAdvisedSessions(List<CounselingSession> advisedSessions) { this.advisedSessions = advisedSessions; }

    public List<CounselingSession> getJoinedSessions() { return joinedSessions; }
    public void setJoinedSessions(List<CounselingSession> joinedSessions) { this.joinedSessions = joinedSessions; }

    // --- EQUALS & HASHCODE (Important for Set/List comparisons) ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // --- TOSTRING (Excluding lists to prevent Infinite Loops) ---
    @Override
    public String toString() {
        return "User{" + "id=" + id + ", name='" + name + '\'' + ", email='" + email + '\'' + ", role='" + role + '\'' + '}';
    }
}