package com.model;

import javax.persistence.*;

@Entity
@Table(name = "articles")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 500) 
    private String description;

    @Column(nullable = false, name = "source_url", columnDefinition = "TEXT")
    private String sourceUrl; 

    @Column(name = "image_url", columnDefinition = "TEXT") 
    private String imageUrl; 

    // CONSTRUCTORS
    public Article() {
    }

    // Constructor with 4 parameters (useful for local creation)
    public Article(String title, String description, String sourceUrl, String imageUrl) {
        this.title = title;
        this.description = description;
        this.sourceUrl = sourceUrl;
        this.imageUrl = imageUrl;
    }

    // ADDED: Constructor with 5 parameters to match AdminController
    public Article(Long id, String title, String description, String sourceUrl, String imageUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.sourceUrl = sourceUrl;
        this.imageUrl = imageUrl;
    }

    // GETTERS AND SETTERS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}