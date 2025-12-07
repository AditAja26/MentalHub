package com.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private Long id;
    private String name;
    private Integer age;
    private String email;
    private String phone;
    private String password;
    private String role;
    private List<String> goals;

    public User() {
        this.goals = new ArrayList<>();
    }

    public User(Long id, String name, Integer age, String email, String phone, String password, String role) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
        this.goals = new ArrayList<>();
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
}
