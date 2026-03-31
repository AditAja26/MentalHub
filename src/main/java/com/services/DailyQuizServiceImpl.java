package com.services;

import com.dao.DailyQuizDao;
import com.model.DailyQuiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

@Service
@Transactional
public class DailyQuizServiceImpl implements DailyQuizService {

    @Autowired
    private DailyQuizDao dailyQuizDao;

    @PostConstruct
    public void init() {
        if (dailyQuizDao.findAll().isEmpty()) {
            Date today = new Date();
            Date tomorrow = getFutureDate(1);

            // --- 3 QUIZZES FOR TODAY ---
            addQuiz(new DailyQuiz(today, "What is a healthy way to handle stress?", "Talking to a friend", "Ignoring it", "Sleeping 18 hours", "Eating junk food"));
            addQuiz(new DailyQuiz(today, "Which hormone regulates sleep?", "Melatonin", "Insulin", "Adrenaline", "Cortisol"));
            addQuiz(new DailyQuiz(today, "True or False: Mental health is just as important as physical health.", "True", "False", "Only for adults", "Only for students"));

            // --- 3 QUIZZES FOR TOMORROW ---
            addQuiz(new DailyQuiz(tomorrow, "What is 'burnout'?", "Emotional exhaustion from chronic stress", "Being very tired after gym", "Getting a sunburn", "Forgetting your homework"));
            addQuiz(new DailyQuiz(tomorrow, "How much sleep should a student get?", "7-9 Hours", "3-4 Hours", "12+ Hours", "Whatever is left after gaming"));
            addQuiz(new DailyQuiz(tomorrow, "Which is a sign of anxiety?", "Constant excessive worry", "Feeling hungry", "Laughing at jokes", "Being sleepy at night"));
            
            System.out.println("--- 6 Sample Quizzes Added ---");
        }
    }

    private Date getFutureDate(int daysToAdd) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, daysToAdd);
        return cal.getTime();
    }

    @Override
    public DailyQuiz addQuiz(DailyQuiz quiz) {
        quiz.setQuizDate(normalizeDate(quiz.getQuizDate()));
        return dailyQuizDao.save(quiz);
    }

    @Override
    public List<DailyQuiz> getAllQuizzes() {
        return dailyQuizDao.findAll();
    }

    @Override
    public DailyQuiz getQuizById(Long id) {
        return dailyQuizDao.findById(id);
    }

    @Override
    public List<DailyQuiz> getQuizForToday() {
        // 1. Get today's date
        Date today = new Date();
        
        // 2. Convert it to a "dumb" String (e.g., "2026-01-13")
        // This locks the date to YOUR computer's timezone (Malaysia)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = sdf.format(today);
        
        System.out.println("DEBUG: Searching for quiz with Date String: " + dateString);
        
        // 3. Send the String to the DAO
        return dailyQuizDao.findByDate(dateString);
    }

    @Override
    public void deleteQuiz(Long id) {
        dailyQuizDao.deleteById(id);
    }

    // --- HELPER: Removes hours, minutes, seconds from a Date ---
    private Date normalizeDate(Date date) {
        if (date == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}