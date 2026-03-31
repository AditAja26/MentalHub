package com.dao;

import com.model.ForumComment;

public interface ForumCommentDao {
    void save(ForumComment comment);
    ForumComment findById(Long id);
    void deleteById(Long id);
    // Note: We don't need 'findAllByPostId' because post.getComments() 
    // in the ForumPost entity handles that automatically for us!
}