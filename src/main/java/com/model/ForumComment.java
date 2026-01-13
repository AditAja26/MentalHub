package com.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "forum_comments")
public class ForumComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // --- RELATIONSHIPS ---
    
    // Link to the Post being commented on
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private ForumPost post;

    // Link to the User who wrote the comment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // --- PRIVACY ---
    @Column(name = "is_anonymous")
    private boolean isAnonymous;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    // --- CONSTRUCTORS ---
    public ForumComment() {
        this.createdAt = new Date();
    }

    public ForumComment(String content, ForumPost post, User user, boolean isAnonymous) {
        this.content = content;
        this.post = post;
        this.user = user;
        this.isAnonymous = isAnonymous;
        this.createdAt = new Date();
    }

    // --- GETTERS & SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public ForumPost getPost() { return post; }
    public void setPost(ForumPost post) { this.post = post; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public boolean isAnonymous() { return isAnonymous; }
    public void setAnonymous(boolean anonymous) { isAnonymous = anonymous; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    // --- HELPER ---
    public String getDisplayAuthor() {
        if (this.isAnonymous) {
            return "Anonymous Student";
        }
        return (user != null) ? user.getName() : "Unknown User";
    }
}