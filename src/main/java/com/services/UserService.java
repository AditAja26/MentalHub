package com.services;

import com.dao.UserDAO;
import com.model.Goal;
import com.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserDAO userDao;

    @Transactional
    public User addUser(User user) {
        userDao.save(user);
        return user;
    }

    @Transactional
    public boolean deleteUser(Long id) {
        if (userDao.getById(id) != null) {
            userDao.delete(id);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userDao.getAll();
    }

    @Transactional(readOnly = true)
    public List<User> getUsersByRole(String role) {
        return userDao.getByRole(role);
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userDao.getById(id);
    }

    @Transactional
    public User updateUser(Long id, User updatedUser) {
        User existing = userDao.getById(id);
        if (existing != null) {
            if (updatedUser.getName() != null)
                existing.setName(updatedUser.getName());
            if (updatedUser.getAge() != null)
                existing.setAge(updatedUser.getAge());
            if (updatedUser.getEmail() != null)
                existing.setEmail(updatedUser.getEmail());
            if (updatedUser.getPhone() != null)
                existing.setPhone(updatedUser.getPhone());
            if (updatedUser.getGoals() != null)
                existing.setGoals(updatedUser.getGoals());

            userDao.update(existing);
            return existing;
        }
        return null;
    }

    @EventListener(ContextRefreshedEvent.class)
    @Transactional
    public void seedDatabase() {
        try {
            if (userDao.getAll().isEmpty()) {
                System.out.println(">>> MENTALHUB: Seeding database...");

                // 1. Create Bambang
                User bambang = new User(null, "Bambang", 21, "bambang@yahoo.com", "082337729130", "password123",
                        "student");
                userDao.save(bambang); // Save first to get ID

                // 2. Create Goal objects (NOT Strings)
                List<Goal> bambangGoals = new ArrayList<>();
                bambangGoals.add(new Goal(bambang, "Read 2 articles on self reflection", true));
                bambangGoals.add(new Goal(bambang, "Complete 10 quiz", false));
                bambangGoals.add(new Goal(bambang, "Book an appointment", false));

                // 3. Link and Update
                bambang.setGoals(bambangGoals);
                userDao.update(bambang);

                // Add Udin and Hakimi (simple versions)
                userDao.save(
                        new User(null, "Udin", 20, "udinudang@gmail.com", "082337729130", "password123", "student"));
                userDao.save(
                        new User(null, "Hakimi", 22, "hakimi@email.com", "082337729130", "password123", "student"));

                System.out.println(">>> MENTALHUB: Seeding complete.");
            }
        } catch (Exception e) {
            System.err.println(">>> MENTALHUB ERROR: Seeding failed - " + e.getMessage());
            e.printStackTrace();
        }
    }
}