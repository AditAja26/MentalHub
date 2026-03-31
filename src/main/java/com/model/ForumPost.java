package com.model;

import javax.persistence.*;
import java.util.Date;
import java.util.ArrayList; 
import java.util.List;

@Entity
@Table(name = "forum_posts")
public class ForumPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    // "TEXT" allows for long posts (more than 255 characters)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // --- RELATIONSHIP: Many Posts belong to One User ---
    @ManyToOne(fetch = FetchType.LAZY) // Lazy fetch improves performance
    @JoinColumn(name = "user_id", nullable = false) // Foreign key in DB
    private User user;

    @Column(name = "is_anonymous")
    private boolean isAnonymous;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC") // Oldest comments at the top (like a conversation)
    private List<ForumComment> comments = new ArrayList<>();

    // --- CONSTRUCTORS ---
    public ForumPost() {
        this.createdAt = new Date(); // Sets time to NOW automatically
    }

    public ForumPost(String title, String content, User user, boolean isAnonymous) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.isAnonymous = isAnonymous;
        this.createdAt = new Date();
    }

    // --- GETTERS AND SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public boolean isAnonymous() { return isAnonymous; }
    public void setAnonymous(boolean anonymous) { isAnonymous = anonymous; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public List<ForumComment> getComments() {return comments;}
    public void setComments(List<ForumComment> comments) {this.comments = comments;}

    // --- HELPER METHOD FOR HTML VIEW ---
    // In Thymeleaf, you can just use ${post.displayAuthor}
    public String getDisplayAuthor() {
        if (this.isAnonymous) {
            return "Anonymous";
        }
        // Returns the user's real name from the User entity
        return (user != null) ? user.getName() : "Unknown User";
    }
}