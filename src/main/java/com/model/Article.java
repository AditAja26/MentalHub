package com.model;

public class Article {
    private Long id;
    private String title;
    private String description;
    private String content;
    private String category;

    public Article() {}

    public Article(Long id, String title, String description, String content) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.content = content;
    }

    public Article(Long id, String title, String description, String content, String category) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.content = content;
        this.category = category;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
