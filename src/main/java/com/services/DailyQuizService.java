package com.services;

import com.model.DailyQuiz;
import java.util.List;

public interface DailyQuizService {
    DailyQuiz addQuiz(DailyQuiz quiz);
    List<DailyQuiz> getAllQuizzes();
    DailyQuiz getQuizById(Long id);
    List<DailyQuiz> getQuizForToday(); 
    void deleteQuiz(Long id);
}