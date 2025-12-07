package com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/peer")
public class PeerSupportController {
    
    @GetMapping("/posts")
    public String showPostsPage(Model model){
        return "peerSupportModule/posts";
    }

    @GetMapping("/createPost")
    public String createPostPage(Model model){
        return "peerSupportModule/createPost";
    }

    @GetMapping("/reply/{postId}")
    public String showCommentPage(@PathVariable Long postId, Model model){
        model.addAttribute("postId", postId);
        return "peerSupportModule/commentPost";
    }
}
