package com.services;

import com.dao.ArticleDao;
import com.model.Article;
import com.services.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@Transactional // Ensures database transactions are managed automatically
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleDao articleDao;

    // Initialize DB with sample data if empty
    @PostConstruct
    public void init() {
        if (articleDao.count() == 0) {
            articleDao.save(new Article(
                "The Student's Guide to Taming Test Anxiety",
                "Do your palms sweat just thinking about an exam? This guide breaks down why test anxiety happens.",
                "https://www.healthline.com/health/test-anxiety", // sourceUrl
                "https://images.unsplash.com/photo-1434030216411-0b793f4b4173" // imageUrl
            ));

            articleDao.save(new Article(
                "That Feeling You're a 'Fraud'? It's Called Imposter Syndrome",
                "Ever feel like you don't deserve your accomplishments? Learn to recognize these thoughts.",
                "https://hbr.org/2008/05/overcoming-imposter-syndrome",
                "https://images.unsplash.com/photo-1453847668862-487637052f8a"
            ));
        }
    }

    @Override
    public Article addArticle(Article article) {
        return articleDao.save(article);
    }

    @Override
    public List<Article> getAllArticles() {
        return articleDao.findAll();
    }

    @Override
    public Article getArticleById(Long id) {
        return articleDao.findById(id);
    }

    @Override
    public List<Article> getArticlesByCategory(String category) {
        return articleDao.findByCategory(category);
    }

    @Override
    public Article updateArticle(Long id, Article updatedArticle) {
        Article existing = articleDao.findById(id);
        
        if (existing != null) {
            if (updatedArticle.getTitle() != null) 
                existing.setTitle(updatedArticle.getTitle());
            
            if (updatedArticle.getDescription() != null) 
                existing.setDescription(updatedArticle.getDescription());
            
            if (updatedArticle.getSourceUrl() != null) 
                existing.setSourceUrl(updatedArticle.getSourceUrl());
            
            if (updatedArticle.getImageUrl() != null) 
                existing.setImageUrl(updatedArticle.getImageUrl());

            return articleDao.save(existing);
        }
        return null;
    }

    @Override
    public boolean deleteArticle(Long id) {
        Article existing = articleDao.findById(id);
        if (existing != null) {
            articleDao.deleteById(id);
            return true;
        }
        return false;
    }
}