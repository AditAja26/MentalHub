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

    // Use columnDefinition="TEXT" to allow long paragraphs/stories
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String author; // User enters their name or "Anonymous"

    @Temporal(TemporalType.TIMESTAMP) // Stores Date AND Time (e.g., Jan 13, 14:30)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    // --- CONSTRUCTORS ---
    public ForumPost() {
        this.createdAt = new Date(); // Automatically set time to NOW when created
    }

    public ForumPost(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.createdAt = new Date();
    }

    // --- GETTERS AND SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}