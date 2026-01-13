package com.config;

import com.model.User;
import com.services.CustomUserDetailsService;
import com.services.UserService;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.servlet.http.HttpSession;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    @Lazy
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. CSRF (Disabled for simplicity in this lab)
            .csrf(csrf -> csrf.disable())

            // 2. Authorization (Who can see what)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/resources/**", "/login", "/register", "/logout").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/advisor/**").hasRole("ADVISOR")
                .requestMatchers("/student/**").hasRole("STUDENT")
                // Shared pages
                .requestMatchers("/peer/**", "/literacy/**").authenticated()
                // Everything else requires login
                .anyRequest().authenticated()
            )

            // 3. Login Configuration
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .successHandler(mySuccessHandler())
                .permitAll()
            )

            // 4. Logout Configuration
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder)
            .and()
            .build();
    }

    // @Bean
    // public PasswordEncoder passwordEncoder() {
    //     return new BCryptPasswordEncoder();
    // }

    @Bean
    public AuthenticationSuccessHandler mySuccessHandler() {
        return (request, response, authentication) -> {
            String email = authentication.getName();
            User user = userService.getUserByEmail(email);

            // Put user in session so your old controllers still work
            HttpSession session = request.getSession();
            session.setAttribute("loggedInUser", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userName", user.getName());
            session.setAttribute("userRole", user.getRole());

            String role = authentication.getAuthorities().iterator().next().getAuthority();
            if (role.contains("ADMIN")) {
                response.sendRedirect(request.getContextPath() + "/admin");
            } else if (role.contains("ADVISOR")) {
                response.sendRedirect(request.getContextPath() + "/advisor");
            } else {
                response.sendRedirect(request.getContextPath() + "/student");
            }
        };
    }
}