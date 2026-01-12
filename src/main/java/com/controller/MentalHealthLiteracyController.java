package com.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.services.ArticleService;
import com.services.DailyQuizService;
import com.model.Article;
import com.model.DailyQuiz;


@Controller
@RequestMapping("/literacy")

public class MentalHealthLiteracyController{

    @Autowired
    private ArticleService articleService;

    @Autowired
    private DailyQuizService dailyQuizService;

    @GetMapping
    public String showLiteracyPage(Model model) {
        // 1. Articles
        List<Article> articles = articleService.getAllArticles();
        model.addAttribute("articles", articles);

        // 2. Quizzes 
        List<DailyQuiz> todayQuizzes = dailyQuizService.getQuizForToday();
        model.addAttribute("quizzes", todayQuizzes);

        // --- DEBUG LOGS (Check your IntelliJ/Eclipse Console) ---
        System.out.println("=======================================");
        System.out.println("Loading Literacy Page...");
        System.out.println("Found Articles: " + articles.size());
        System.out.println("Found Quizzes for Today: " + todayQuizzes.size());
        if (todayQuizzes.isEmpty()) {
            System.out.println("WARNING: No quizzes found. Check if the date in DB matches today.");
        }
        System.out.println("=======================================");
        // --------------------------------------------------------

        return "mentalHealthLiteracyModule/literacyPage";
    }
         
    @GetMapping("/content/{id}")
    public String showArticleView(@PathVariable Long id, Model model) {
        Article article = articleService.getArticleById(id);
        if (article == null || article.getSourceUrl() == null) {
            return "redirect:/literacy"; 
        }
        return "redirect:" + article.getSourceUrl();
    }
}