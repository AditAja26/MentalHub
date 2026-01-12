package com.dao;

import com.model.User;
import java.util.List;

public interface UserDAO {
    void save(User user);
    User getById(Long id);
    User getByEmail(String email);
    List<User> getAll();
    List<User> getByRole(String role);
    void update(User user);
    void delete(Long id);
}