package com.services;

import com.dao.UserDAO;
import com.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserDAO userDAO;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Find the user in your DB
        User myUser = userDAO.getByEmail(email);
        if (myUser == null) {
            throw new UsernameNotFoundException("User not found");
        }

        // 2. Translate to Spring Security User
        // Note: We add "ROLE_" prefix because Spring expects it by default
        return org.springframework.security.core.userdetails.User
                .withUsername(myUser.getEmail())
                .password(myUser.getPassword()) // Pass the encrypted password
                .roles(myUser.getRole().toUpperCase()) // e.g., "STUDENT" -> ROLE_STUDENT
                .build();
    }
}