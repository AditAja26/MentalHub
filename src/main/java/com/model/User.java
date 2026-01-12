package com.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_goals", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "goal")
    private List<String> goals = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<ForumPost> forumPosts;

    public User() {
    }

    public User(Long id, String name, Integer age, String email, String phone, String password, String role) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
    }

    public User(Long id, String name, Integer age, String email, String phone, List<String> goals) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
        this.phone = phone;
        this.goals = goals != null ? goals : new ArrayList<>();
    }

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

    public List<String> getGoals() { return goals; }
    public void setGoals(List<String> goals) { this.goals = goals; }

    public List<ForumPost> getForumPosts() { return forumPosts; }
    public void setForumPosts(List<ForumPost> forumPosts) { this.forumPosts = forumPosts; }
}
