package com.services;

import com.model.Article;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ArticleService {
    
    private final ConcurrentHashMap<Long, Article> articles = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private boolean initialized = false;

    private void initIfNeeded() {
        if (initialized) return;
        initialized = true;
        addArticle(new Article(null, 
            "The Student's Guide to Taming Test Anxiety",
            "Do your palms sweat just thinking about an exam? You're not alone. This guide breaks down why test anxiety happens",
            "Test anxiety is a specific type of performance anxiety, a feeling of unease and worry centered on the high stakes of an exam. It happens because your brain perceives the test as a significant threat. This \"threat\" isn't physical, like facing a predator, but it's psychological—it could be the fear of failure, the pressure to get a certain grade, the worry about disappointing your parents or teachers, or the deep-seated belief that your self-worth is tied to your score. When your brain flags this \"threat,\" it triggers the body's ancient, automatic \"fight-or-flight\" response, just as it would for a real physical danger. Your sympathetic nervous system floods your body with stress hormones like adrenaline and cortisol. This is what causes the physical symptoms: your heart pounds to pump more blood, your breathing quickens to take in more oxygen, and yes, your palms sweat (a primitive mechanism to improve grip). This biological surge is meant to prepare you to run or fight, but it's terrible for sitting still and thinking.",
            "awareness"));
        
        addArticle(new Article(null,
            "That Feeling You're a 'Fraud'? It's Called Imposter Syndrome",
            "Ever feel like you don't deserve your accomplishments and that you'll be 'found out'? This is a classic sign of imposter syndrome. Learn to recognize these thoughts and challenge them.",
            "Imposter syndrome is a psychological pattern where individuals doubt their accomplishments and have a persistent, often internalized fear of being exposed as a \"fraud.\" Despite external evidence of their competence, those experiencing this phenomenon remain convinced that they are frauds and do not deserve the success they have achieved. This article explores the common signs, causes, and strategies to overcome imposter syndrome.",
            "awareness"));
        
        addArticle(new Article(null,
            "\"So Much to Do!\": How to Handle Academic Overwhelm",
            "This article helps you sort \"urgent\" from \"important,\" break down big projects, and create a study plan that actually works.",
            "Academic overwhelm is a common experience for students facing multiple deadlines, assignments, and responsibilities. This article provides practical strategies for managing your workload, prioritizing tasks, and maintaining your mental health while navigating the demands of academic life.",
            "help-seeking"));
        
        addArticle(new Article(null,
            "Your \"Good Enough\" Guide to Perfectionism",
            "Chasing perfection is exhausting and often leads to burnout. This article helps you set realistic goals and celebrate your progress.",
            "Perfectionism can be both a driving force and a source of significant stress. While striving for excellence is admirable, an unhealthy pursuit of perfection can lead to anxiety, procrastination, and burnout. This guide helps you recognize perfectionist tendencies and develop a healthier approach to your goals.",
            "support-services"));
    }

    public Article addArticle(Article article) {
        initIfNeeded();
        if (article.getId() == null) {
            article.setId(idGenerator.getAndIncrement());
        }
        articles.put(article.getId(), article);
        return article;
    }

    public List<Article> getAllArticles() {
        initIfNeeded();
        return new ArrayList<>(articles.values());
    }

    public List<Article> getArticlesByCategory(String category) {
        List<Article> result = new ArrayList<>();
        for (Article article : articles.values()) {
            if (category.equals(article.getCategory())) {
                result.add(article);
            }
        }
        return result;
    }

    public Article getArticleById(Long id) {
        return articles.get(id);
    }

    public Article updateArticle(Long id, Article updatedArticle) {
        Article existing = articles.get(id);
        if (existing != null) {
            if (updatedArticle.getTitle() != null) existing.setTitle(updatedArticle.getTitle());
            if (updatedArticle.getDescription() != null) existing.setDescription(updatedArticle.getDescription());
            if (updatedArticle.getContent() != null) existing.setContent(updatedArticle.getContent());
            if (updatedArticle.getCategory() != null) existing.setCategory(updatedArticle.getCategory());
            return existing;
        }
        return null;
    }

    public boolean deleteArticle(Long id) {
        return articles.remove(id) != null;
    }

    public int getArticleCount() {
        return articles.size();
    }
}
