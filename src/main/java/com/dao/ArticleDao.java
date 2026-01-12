package com.dao;

import com.model.Article;
import java.util.List;

public interface ArticleDao {
    Article save(Article article);           // Create or Update
    List<Article> findAll();                 // Read All
    Article findById(Long id);               // Read One
    List<Article> findByCategory(String category); // Custom Search
    void deleteById(Long id);                // Delete
    long count();                            // Count total (for init check)
}