package com.services;

import com.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserService {
    
    private final ConcurrentHashMap<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private boolean initialized = false;

    private void initIfNeeded() {
        if (initialized) return;
        initialized = true;
        addUser(new User(null, "Bambang", 21, "bambang@yahoo.com", "082337729130", "password123", "student"));
        addUser(new User(null, "Udin", 20, "udinudang@gmail.com", "082337729130", "password123", "student"));
        addUser(new User(null, "Ahmad", 23, "ahmad@gmail.com", "082337729130", "password123", "student"));
        addUser(new User(null, "Kadir", 22, "kadirgorengan@yahoo.com", "082337729130", "password123", "student"));
        addUser(new User(null, "Mabrur", 21, "mabrurtelurdadar@gmail.com", "082337729130", "password123", "student"));
        addUser(new User(null, "Ujang", 20, "ujangkedu@yahoo.com", "082337729130", "password123", "student"));
        addUser(new User(null, "Khidr", 22, "khidrkarawita@yahoo.com", "082337729130", "password123", "student"));
        addUser(new User(null, "Karawita", 21, "karawitakhidr@yahoo.com", "082337729130", "password123", "student"));
        
        User bambang = getUserById(1L);
        if (bambang != null) {
            bambang.setGoals(Arrays.asList("Read 2 articles on self reflection", "Complete 10 quiz", "Book an appointment"));
        }
        
        User hakimi = new User(null, "Hakimi", 22, "hakimi@email.com", "082337729130", "password123", "student");
        addUser(hakimi);
    }

    public User addUser(User user) {
        initIfNeeded();
        if (user.getId() == null) {
            user.setId(idGenerator.getAndIncrement());
        }
        users.put(user.getId(), user);
        return user;
    }

    public List<User> getAllUsers() {
        initIfNeeded();
        return new ArrayList<>(users.values());
    }

    public List<User> getUsersByRole(String role) {
        List<User> result = new ArrayList<>();
        for (User user : users.values()) {
            if (role.equals(user.getRole())) {
                result.add(user);
            }
        }
        return result;
    }

    public User getUserById(Long id) {
        return users.get(id);
    }

    public User getUserByEmail(String email) {
        for (User user : users.values()) {
            if (email.equals(user.getEmail())) {
                return user;
            }
        }
        return null;
    }

    public User updateUser(Long id, User updatedUser) {
        User existing = users.get(id);
        if (existing != null) {
            if (updatedUser.getName() != null) existing.setName(updatedUser.getName());
            if (updatedUser.getAge() != null) existing.setAge(updatedUser.getAge());
            if (updatedUser.getEmail() != null) existing.setEmail(updatedUser.getEmail());
            if (updatedUser.getPhone() != null) existing.setPhone(updatedUser.getPhone());
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                existing.setPassword(updatedUser.getPassword());
            }
            if (updatedUser.getGoals() != null) existing.setGoals(updatedUser.getGoals());
            return existing;
        }
        return null;
    }

    public boolean deleteUser(Long id) {
        return users.remove(id) != null;
    }

    public int getUserCount() {
        return users.size();
    }
}
