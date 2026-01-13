package com.dao;

import com.model.ForumPost;
import java.util.List;

public interface ForumPostDao {
    void save(ForumPost post);       // Create or Update
    List<ForumPost> findAll();       // Get all posts (newest first)
    ForumPost findById(Long id);     // Get one post (for the detailed view)
    void deleteById(Long id);        // Delete a post
}