package com.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "forum_posts")
public class ForumPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000) 
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate = new Date(); 

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) 
    private User user;

    // Constructors, Getters, and Setters...
    public ForumPost(){

    }

    public ForumPost(Long id, String title, String content, Date createDate, User user){
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdDate = createDate;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    
}