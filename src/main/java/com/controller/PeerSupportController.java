package com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/peer")
public class PeerSupportController {
    
    @GetMapping("/posts")
    public String showPostsPage(Model model){
        return "peerSupportModule/posts";
    }

    @GetMapping("/new")
    public String createPostPage(Model model){
        return "peerSupportModule/createPost";
    }

    @GetMapping("/reply/{postId}")
    public String showCommentPage(Model model){
        return "peerSupportModule/commentPost";
    }
}
