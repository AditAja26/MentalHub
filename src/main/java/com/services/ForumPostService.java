package com.services;

import com.model.ForumPost;
import com.model.User;
import java.util.List;

public interface ForumPostService {
    
    // --- POST METHODS ---
    void createPost(ForumPost post, User user); // Takes the raw post and the logged-in user
    List<ForumPost> getAllPosts();
    ForumPost getPostById(Long id);
    void deletePost(Long id);
}