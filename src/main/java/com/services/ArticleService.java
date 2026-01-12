package com.services;

import com.model.Article;
import java.util.List;

public interface ArticleService {
    Article addArticle(Article article);
    List<Article> getAllArticles();
    Article getArticleById(Long id);
    List<Article> getArticlesByCategory(String category);
    Article updateArticle(Long id, Article updatedArticle);
    boolean deleteArticle(Long id);
}