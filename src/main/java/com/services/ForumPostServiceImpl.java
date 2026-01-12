package com.services;

import com.dao.ForumPostDao;
import com.dao.ForumCommentDao;
import com.model.ForumPost;
import com.model.ForumComment;
import com.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import javax.annotation.PostConstruct;

@Service
@Transactional
public class ForumPostServiceImpl implements ForumPostService {

    @Autowired
    private ForumPostDao forumDao;

    @Autowired
    private ForumCommentDao forumCommentDao;

    @Autowired
    private SessionFactory sessionFactory;

    // --- POST LOGIC ---

    @Override
    public void createPost(ForumPost post, User user) {
        // Link the logged-in user to the post before saving
        post.setUser(user);
        forumDao.save(post);
    }

    @Override
    public List<ForumPost> getAllPosts() {
        return forumDao.findAll();
    }

    @Override
    public ForumPost getPostById(Long id) {
        return forumDao.findById(id);
    }

    @Override
    public void deletePost(Long id) {
        forumDao.deleteById(id);
    }


    // --- AUTO-INITIALIZE DATA ---
    @PostConstruct
    public void init() {
        // Only run if the forum is empty
        if (forumDao.findAll().isEmpty()) {
            Session session = sessionFactory.openSession();
            Transaction tx = null;
            
            try {
                tx = session.beginTransaction();

                // 1. We need a User to be the author.
                // Check if any user exists; if not, create a "System Bot"
                User author = (User) session.createQuery("FROM User").setMaxResults(1).uniqueResult();
                
                if (author == null) {
                    author = new User();
                    author.setName("MentalHub Community");
                    author.setEmail("community@mentalhub.com");
                    author.setPassword("dummyPass123"); // Dummy password
                    author.setRole("ADMIN");
                    author.setPhone("0000000000");
                    author.setAge(99);
                    session.save(author);
                    System.out.println("--- Created Dummy User for Forum Posts ---");
                }

                // 2. Create Post 1: Anxiety Topic (Anonymous)
                ForumPost post1 = new ForumPost(
                    "Feeling overwhelmed with finals coming up...",
                    "Hi everyone. Finals are in 2 weeks and I haven't been sleeping well. I feel like I'm behind on everything. Does anyone have tips for managing exam stress without burning out?",
                    author,
                    true // isAnonymous = true
                );
                session.save(post1);

                // 3. Create Post 2: Resource Share (Public)
                ForumPost post2 = new ForumPost(
                    "Great meditation app for students",
                    "Just wanted to share that the 'Headspace' app has a student plan. I've been using their 'Focus' playlist while studying and it really helps block out distractions!",
                    author,
                    false // isAnonymous = false (Shows 'MentalHub Community')
                );
                session.save(post2);

                // 4. Add a Comment to Post 1
                ForumComment comment1 = new ForumComment(
                    "I totally relate! Last semester I burned out hard. What helps me is the 'Pomodoro' technique (25 min study, 5 min break). It makes the mountain of work feel smaller.",
                    post1,
                    author,
                    false
                );
                session.save(comment1);

                tx.commit();
                System.out.println("--- Preloaded 2 Posts and 1 Comment ---");

            } catch (Exception e) {
                if (tx != null) tx.rollback();
                e.printStackTrace();
            } finally {
                session.close();
            }
        }
    }
}