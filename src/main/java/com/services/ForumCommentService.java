package com.services;

import com.model.User;

public interface ForumCommentService {
    void addComment(Long postId, String content, User user, boolean isAnonymous);
    void deleteComment(Long id);
}