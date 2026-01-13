package com.services;

import com.dao.UserDAO;
import com.model.Goal;
import com.model.MoodLog;
import com.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserDAO userDao;

    // --- NEW: AUTHENTICATION METHODS ---

    /**
     * Validates credentials. Returns the User object if successful, null otherwise.
     * This object will be stored in the Session by the Controller.
     */
    @Transactional(readOnly = true)
    public User authenticate(String email, String password) {
        // We assume UserDAO has a getByEmail method. 
        // If it doesn't, you'll need to add it to your DAO.
        User user = userDao.getByEmail(email);
        
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    /**
     * Handles new signups. Checks if the email is already taken first.
     */
    @Transactional
    public User registerUser(User user) {
        if (userDao.getByEmail(user.getEmail()) != null) {
            return null; // Email already exists
        }
        userDao.save(user);
        return user;
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userDao.getByEmail(email);
    }

    // --- EXISTING METHODS (Maintained) ---

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

    /**
     * Optimized method for fetching user with all details (goals and mood logs).
     * Use this for reports and dashboards to avoid lazy loading issues.
     */
    @Transactional(readOnly = true)
    public User getUserByIdWithDetails(Long id) {
        return userDao.getByIdWithDetails(id);
    }

    @Transactional
    public User updateUser(Long id, User updatedUser) {
        User existing = userDao.getById(id);
        if (existing != null) {
            if (updatedUser.getName() != null) existing.setName(updatedUser.getName());
            if (updatedUser.getAge() != null) existing.setAge(updatedUser.getAge());
            if (updatedUser.getEmail() != null) existing.setEmail(updatedUser.getEmail());
            if (updatedUser.getPhone() != null) existing.setPhone(updatedUser.getPhone());
            if (updatedUser.getPassword() != null) existing.setPassword(updatedUser.getPassword());
            if (updatedUser.getGoals() != null) existing.setGoals(updatedUser.getGoals());
            if (updatedUser.getMoodLogs() != null) existing.setMoodLogs(updatedUser.getMoodLogs());

            userDao.update(existing);
            return existing;
        }
        return null;
    }

    // --- SEEDING LOGIC (Maintained) ---

    @EventListener(ContextRefreshedEvent.class)
    @Transactional
    public void seedDatabase() {
        try {
            List<User> users = userDao.getAll();
            
            if (users.isEmpty()) {
                System.out.println(">>> MENTALHUB: Database empty. Creating initial users...");
                User bambang = new User(null, "Bambang", 21, "bambang@yahoo.com", "082337729130", "password123", "student");
                userDao.save(bambang);
                seedUserData(bambang);

                User udin = new User(null, "Udin", 20, "udin@gmail.com", "0812345678", "password123", "student");
                userDao.save(udin);
                
                User hakimi = new User(null, "Hakimi", 25, "hakimi@email.com", "0811223344", "password123", "advisor");
                userDao.save(hakimi);
            } else {
                System.out.println(">>> MENTALHUB: Users exist. Checking for missing goal/mood data...");
                for (User user : users) {
                    if (user.getGoals().isEmpty() || user.getMoodLogs().isEmpty()) {
                        seedUserData(user);
                    }
                }
            }
            System.out.println(">>> MENTALHUB: Seeding check complete.");
        } catch (Exception e) {
            System.err.println(">>> MENTALHUB ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void seedUserData(User user) {
        if ("student".equalsIgnoreCase(user.getRole())) {
            System.out.println(">>> Seeding data for: " + user.getName());
            
            List<Goal> goals = new ArrayList<>();
            goals.add(new Goal(user, "Read 2 articles on self reflection", true));
            goals.add(new Goal(user, "Complete 10 quizzes", false));
            goals.add(new Goal(user, "Book an appointment", false));
            user.setGoals(goals);

            List<MoodLog> moods = new ArrayList<>();
            moods.add(new MoodLog(user, 3.0, "W1"));
            moods.add(new MoodLog(user, 3.8, "W2"));
            moods.add(new MoodLog(user, 3.5, "W3"));
            moods.add(new MoodLog(user, 4.2, "W4"));
            moods.add(new MoodLog(user, 4.0, "W5"));
            moods.add(new MoodLog(user, 4.5, "W6"));
            user.setMoodLogs(moods);

            userDao.update(user);
        }
    }
}