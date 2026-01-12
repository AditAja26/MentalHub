package com.dao;

import com.model.DailyQuiz;
import java.util.Date;
import java.util.List;

public interface DailyQuizDao {
    DailyQuiz save(DailyQuiz quiz);           // Create or Update
    List<DailyQuiz> findAll();                // List all quizzes (for Admin)
    DailyQuiz findById(Long id);              // Find by ID
    List<DailyQuiz> findByDate(String dateString);          // Find quiz for a specific day (Student View)
    void deleteById(Long id);                 // Delete
}