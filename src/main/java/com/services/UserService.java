package com.services;

import com.dao.UserDAO;
import com.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.event.ContextRefreshedEvent; // NEW IMPORT
import org.springframework.context.event.EventListener;        // NEW IMPORT

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

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userDao.getByEmail(email);
    }

    @Transactional
    public User updateUser(Long id, User updatedUser) {
        User existing = userDao.getById(id);
        if (existing != null) {
            if (updatedUser.getName() != null) existing.setName(updatedUser.getName());
            if (updatedUser.getAge() != null) existing.setAge(updatedUser.getAge());
            if (updatedUser.getEmail() != null) existing.setEmail(updatedUser.getEmail());
            if (updatedUser.getPhone() != null) existing.setPhone(updatedUser.getPhone());
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                existing.setPassword(updatedUser.getPassword());
            }
            if (updatedUser.getGoals() != null) existing.setGoals(updatedUser.getGoals());
            
            userDao.update(existing);
            return existing;
        }
        return null;
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
    public int getUserCount() {
        return userDao.getAll().size();
    }

    /**
     * FIX: Switched from @PostConstruct to @EventListener.
     * This waits until the Transactional Proxy is ready, 
     * preventing the "Could not obtain transaction-synchronized Session" error.
     */
    @EventListener(ContextRefreshedEvent.class)
    @Transactional
    public void seedDatabase() {
        // We wrap in a try-catch just to be safe so a seeding error doesn't crash the whole app
        try {
            if (userDao.getAll().isEmpty()) {
                System.out.println(">>> MENTALHUB: Database empty. Seeding initial students...");

                User bambang = new User(null, "Bambang", 21, "bambang@yahoo.com", "082337729130", "password123", "student");
                bambang.setGoals(Arrays.asList("Read 2 articles on self reflection", "Complete 10 quiz", "Book an appointment"));
                userDao.save(bambang);

                userDao.save(new User(null, "Udin", 20, "udinudang@gmail.com", "082337729130", "password123", "student"));
                userDao.save(new User(null, "Hakimi", 22, "hakimi@email.com", "082337729130", "password123", "student"));
                
                System.out.println(">>> MENTALHUB: Seeding complete.");
            }
        } catch (Exception e) {
            System.err.println(">>> MENTALHUB ERROR: Seeding failed - " + e.getMessage());
        }
    }
}