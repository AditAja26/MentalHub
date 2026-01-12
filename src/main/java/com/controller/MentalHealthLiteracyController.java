package com.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.services.ArticleService;
import com.model.Article;


@Controller
@RequestMapping("/literacy")

public class MentalHealthLiteracyController{

    @Autowired
    private ArticleService articleService;

    @GetMapping
    public String showLiteracyPage(Model model) {
        List<Article> articles = articleService.getAllArticles();
        model.addAttribute("articles", articles);
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