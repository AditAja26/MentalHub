package com.services;

import com.dao.ForumPostDao;
import com.dao.ForumCommentDao;
import com.model.ForumPost;
import com.model.ForumComment;
import com.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ForumCommentServiceImpl implements ForumCommentService {

    @Autowired
    private ForumCommentDao forumCommentDao;

    @Autowired
    private ForumPostDao forumPostDao; // We still need this to find the Post object

    @Override
    public void addComment(Long postId, String content, User user, boolean isAnonymous) {
        // 1. Find the parent post
        ForumPost post = forumPostDao.findById(postId);

        if (post != null) {
            // 2. Create the comment
            ForumComment comment = new ForumComment(content, post, user, isAnonymous);
            
            // 3. Save it
            forumCommentDao.save(comment);
        }
    }

    public void deleteComment(Long id){
        forumCommentDao.deleteById(id);
    }
}