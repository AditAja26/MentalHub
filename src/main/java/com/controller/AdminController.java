package com.controller;

import com.model.Article;
import com.model.User;
import com.model.Goal;
import com.services.ArticleService;
import com.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional; // NEW IMPORT

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ArticleService articleService;

    @GetMapping(value = {"", "/"})
    public String showAdminLandingPage(Model model) {
        model.addAttribute("adminName", "Hakimi");
        return "mainPages/adminLandingPage";
    }

    @GetMapping("/users")
    public String showUsersList(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "adminModule/usersList";
    }

    // ADDED @Transactional TO PREVENT 500 ERROR
    @Transactional(readOnly = true)
    @GetMapping("/users/{id}")
    public String showUserDetail(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        if (user == null) {
            return "redirect:/admin/users";
        }

        // This line triggered the error; @Transactional fixes it
        if (user.getGoals() != null) {
            user.getGoals().size();
        }

        model.addAttribute("user", user);
        return "adminModule/userDetail";
    }

    @GetMapping("/users/{id}/edit")
    public String showEditUser(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        if (user == null) return "redirect:/admin/users";
        model.addAttribute("user", user);
        return "adminModule/editUser";
    }

    @PostMapping("/users/{id}/update")
    public String updateUser(@PathVariable Long id,
                            @RequestParam("name") String name,
                            @RequestParam("email") String email,
                            @RequestParam("phone") String phone,
                            @RequestParam(value = "age", required = false) Integer age) {
        User user = userService.getUserById(id);
        if (user != null) {
            user.setName(name);
            user.setEmail(email);
            user.setPhone(phone);
            if (age != null) user.setAge(age);
            userService.updateUser(id, user);
        }
        return "redirect:/admin/users/" + id;
    }

    @GetMapping("/users/{id}/remove")
    public String removeUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/content")
    public String showContentList(Model model) {
        List<Article> articles = articleService.getAllArticles();
        model.addAttribute("articles", articles);
        return "adminModule/contentList";
    }

    @GetMapping("/content/{id}")
    public String showArticleView(@PathVariable Long id, Model model) {
        Article article = articleService.getArticleById(id);
        if (article == null) return "redirect:/admin/content";
        return "redirect:" + article.getSourceUrl();
    }

    @GetMapping("/content/{id}/edit")
    public String showEditArticle(@PathVariable Long id, Model model) {
        Article article = articleService.getArticleById(id);
        if (article == null) return "redirect:/admin/content";
        model.addAttribute("article", article);
        return "adminModule/editArticle";
    }

    @PostMapping("/content/{id}/update")
    public String updateArticle(@PathVariable Long id,
                                @RequestParam("title") String title,
                                @RequestParam("description") String description,
                                @RequestParam("sourceUrl") String sourceUrl, 
                                @RequestParam("imageUrl") String imageUrl) { 
        Article article = articleService.getArticleById(id);
        if (article != null) {
            article.setTitle(title);
            article.setDescription(description);
            article.setSourceUrl(sourceUrl); 
            article.setImageUrl(imageUrl);   
            articleService.updateArticle(id, article);
        }
        return "redirect:/admin/content";
    }

    @GetMapping("/content/{id}/delete")
    public String deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return "redirect:/admin/content";
    }

    @GetMapping("/content/add")
    public String showAddArticle(Model model) {
        model.addAttribute("article", new Article());
        return "adminModule/addArticle";
    }

    @PostMapping("/content/add")
    public String addArticle(@RequestParam("title") String title,
                             @RequestParam("description") String description,
                             @RequestParam("sourceUrl") String sourceUrl, 
                             @RequestParam("imageUrl") String imageUrl) {
        Article article = new Article(null, title, description, sourceUrl, imageUrl);
        articleService.addArticle(article);
        return "redirect:/admin/content";
    }
}