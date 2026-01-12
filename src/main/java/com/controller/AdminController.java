package com.controller;

import com.model.Article;
import com.model.User;
import com.model.DailyQuiz;
import com.model.Goal; // Added this import
import com.services.ArticleService;
import com.services.UserService;
import com.services.DailyQuizService;
import com.services.ForumPostService;
import com.services.ForumCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import javax.servlet.http.HttpSession;

import java.util.ArrayList;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private DailyQuizService dailyQuizService;

    @Autowired
    private ForumPostService forumPostService;

    @Autowired
    private ForumCommentService forumCommentService;

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

    @GetMapping("/users/{id}")
    public String showUserDetail(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        if (user == null) {
            return "redirect:/admin/users";
        }
        model.addAttribute("user", user);
        return "adminModule/userDetail";
    }

    @GetMapping("/users/{id}/edit")
    public String showEditUser(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        if (user == null) {
            return "redirect:/admin/users";
        }
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
            
            // Note: We don't touch goals here so they remain unchanged in the DB
            userService.updateUser(id, user);
        }
        return "redirect:/admin/users/" + id;
    }

    @GetMapping("/users/{id}/remove")
    public String removeUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }


    // ==========================================
    //           ARTICLE MANAGEMENT
    // ==========================================

    @GetMapping("/content")
    public String showContentList(Model model) {
        List<Article> articles = articleService.getAllArticles();
        model.addAttribute("articles", articles);
        return "adminModule/contentList";
    }

    @GetMapping("/content/{id}")
    public String showArticleView(@PathVariable Long id, Model model) {
        Article article = articleService.getArticleById(id);
        if (article == null) {
            return "redirect:/admin/content";
        }
        // If your sourceUrl is an external link, this works. 
        // If it's internal, you might need a proper view.
        return "redirect:" + article.getSourceUrl();
    }

    @GetMapping("/content/{id}/edit")
    public String showEditArticle(@PathVariable Long id, Model model) {
        Article article = articleService.getArticleById(id);
        if (article == null) {
            return "redirect:/admin/content";
        }
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
        // Ensure Article has a no-args constructor
        model.addAttribute("article", new Article());
        return "adminModule/addArticle";
    }

    @PostMapping("/content/add")
    public String addArticle(@RequestParam("title") String title,
                             @RequestParam("description") String description,
                             @RequestParam("sourceUrl") String sourceUrl, 
                             @RequestParam("imageUrl") String imageUrl) {
        
        // Ensure your Article model has this constructor!
        Article article = new Article(null, title, description, sourceUrl, imageUrl);
        
        articleService.addArticle(article);
        return "redirect:/admin/content";
    }


    // ==========================================
    //           DAILY QUIZ MANAGEMENT
    // ==========================================

    // 1. LIST ALL QUIZZES
    @GetMapping("/quiz")
    public String showQuizList(Model model) {
        model.addAttribute("quizzes", dailyQuizService.getAllQuizzes());
        return "adminModule/quizList"; // You will create this HTML next
    }

    // 2. ADD QUIZ - SHOW FORM
    @GetMapping("/quiz/add")
    public String showAddQuizForm(Model model) {
        model.addAttribute("quiz", new DailyQuiz());
        return "adminModule/addQuiz"; // You will create this HTML next
    }

    // 3. ADD QUIZ - PROCESS DATA
    @PostMapping("/quiz/add")
    public String addQuiz(@ModelAttribute DailyQuiz quiz) {
        // The @DateTimeFormat in your Entity automatically handles the date string conversion!
        dailyQuizService.addQuiz(quiz);
        return "redirect:/admin/quiz";
    }

    // 4. EDIT QUIZ - SHOW FORM
    @GetMapping("/quiz/{id}/edit")
    public String showEditQuizForm(@PathVariable Long id, Model model) {
        DailyQuiz quiz = dailyQuizService.getQuizById(id);
        if (quiz != null) {
            model.addAttribute("quiz", quiz);
            return "adminModule/editQuiz"; // Reuses the form or a specific edit page
        }
        return "redirect:/admin/quiz";
    }

    // 5. EDIT QUIZ - PROCESS UPDATE
    @PostMapping("/quiz/{id}/update")
    public String updateQuiz(@PathVariable Long id, @ModelAttribute DailyQuiz quiz) {
        // Ensure the ID is set so Hibernate knows to UPDATE, not INSERT
        quiz.setId(id);
        dailyQuizService.addQuiz(quiz); // saveOrUpdate handles the rest
        return "redirect:/admin/quiz";
    }

    // 6. DELETE QUIZ
    @GetMapping("/quiz/{id}/delete")
    public String deleteQuiz(@PathVariable Long id) {
        dailyQuizService.deleteQuiz(id);
        return "redirect:/admin/quiz";
    }


    // ==========================================
    //           FORUM MANAGEMENT
    // ==========================================

    @PostMapping("/deletePost/{id}")
    public String deletePost(@PathVariable Long id, 
                             HttpSession session, 
                             RedirectAttributes redirectAttributes) {

        forumPostService.deletePost(id);
        
        redirectAttributes.addFlashAttribute("successMessage", "Post deleted by Admin.");
        return "redirect:/peer/posts";
    }

    @PostMapping("/deleteComment")
    public String deleteComment(@RequestParam("commentId") Long commentId, 
                                @RequestParam("postId") Long postId,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        forumCommentService.deleteComment(commentId);
        
        return "redirect:/peer/reply/" + postId;
    }

}