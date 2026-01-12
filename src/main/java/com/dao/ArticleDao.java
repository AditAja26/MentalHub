package com.dao;

import com.model.Article;
import java.util.List;

public interface ArticleDao {
    Article save(Article article);           
    List<Article> findAll();                 
    Article findById(Long id);               
    List<Article> findByCategory(String category); 
    void deleteById(Long id);                
    long count();                            
}