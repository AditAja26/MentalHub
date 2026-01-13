package com.controller;

import com.model.ForumPost;
import com.model.User;
import com.services.ForumPostService;
import com.services.ForumCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/peer")
public class PeerSupportController {

    @Autowired
    private ForumPostService forumPostService;

    @Autowired
    private ForumCommentService forumCommentService;

    // --- 1. SHOW ALL POSTS (The Feed) ---
    @GetMapping("/posts")
    public String showPostsPage(Model model, HttpSession session) {
        // Security Check
        User user = (User) session.getAttribute("loggedInUser");
        //! if (user == null) return "redirect:/login";  TEMPORARY COMMENT, TESTING PURPOSE 

        // Fetch actual data from DB
        List<ForumPost> posts = forumPostService.getAllPosts();
        model.addAttribute("posts", posts);

        //DEBUG
        System.out.println("Found posts: " + posts.size());
        
        return "peerSupportModule/posts";
    }

    // --- 2. SHOW CREATE POST FORM ---
    @GetMapping("/createPost")
    public String createPostPage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        //! if (user == null) return "redirect:/login";

        // We must send an empty object for the form to bind to
        model.addAttribute("newPost", new ForumPost());
        
        return "peerSupportModule/createPost";
    }

    // --- 3. HANDLE CREATE POST SUBMISSION (New) ---
    @PostMapping("/createPost")
    public String submitPost(@ModelAttribute("newPost") ForumPost post, 
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        
        User user = (User) session.getAttribute("loggedInUser");
        //! if (user == null) return "redirect:/login";

        // Save to DB
        forumPostService.createPost(post, user);

        redirectAttributes.addFlashAttribute("successMessage", "Post published successfully!");
        return "redirect:/peer/posts";
    }

    // --- 4. SHOW PAGE TO REPLY/VIEW A SPECIFIC POST ---
    @GetMapping("/reply/{postId}")
    public String showCommentPage(@PathVariable Long postId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        //! if (user == null) return "redirect:/login";

        // Fetch the post so the user can see what they are replying to
        ForumPost post = forumPostService.getPostById(postId);
        
        if (post == null) {
            return "redirect:/peer/posts";
        }

        model.addAttribute("post", post);
        return "peerSupportModule/commentPost";
    }

    // --- 5. HANDLE COMMENT SUBMISSION (New) ---
    @PostMapping("/reply")
    public String submitComment(@RequestParam("postId") Long postId,
                                @RequestParam("content") String content,
                                @RequestParam(value = "isAnonymous", required = false) boolean isAnonymous,
                                HttpSession session) {
        
        User user = (User) session.getAttribute("loggedInUser");
        //! if (user == null) return "redirect:/login";

        if (content != null && !content.trim().isEmpty()) {
            forumCommentService.addComment(postId, content, user, isAnonymous);
        }

        // Redirect back to the same page so they see their new comment
        return "redirect:/peer/reply/" + postId;
    }

    
    @GetMapping(value = { "", "/" })
    public String showLandingPage(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String userRole = (String) session.getAttribute("userRole");
        
        if (userId == null) {
            return "redirect:/login";
        }
            if (userRole.equalsIgnoreCase("admin")) {
                return "redirect:/admin";
            } else if (userRole.equalsIgnoreCase("advisor")) {
                return "redirect:/advisor";
            } else {
                return "redirect:/student";
            }
    }
}