package com.model;

import javax.persistence.*;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat; 

@Entity
@Table(name = "daily_quizzes")
public class DailyQuiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Stores only the date (e.g., 2026-01-12) to assign this question to a specific day
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd") // Helps input from HTML forms
    @Column(name = "quiz_date", nullable = false, unique = true) // unique=true prevents duplicate quizzes for the same day
    private Date quizDate;

    @Column(nullable = false, length = 500) 
    private String question;

    @Column(name = "correct_answer", nullable = false)
    private String correctAnswer;

    @Column(name = "wrong_answer_1", nullable = false)
    private String wrongAnswer1;

    @Column(name = "wrong_answer_2", nullable = false)
    private String wrongAnswer2;

    @Column(name = "wrong_answer_3", nullable = false)
    private String wrongAnswer3;

    public DailyQuiz() {
    }

    public DailyQuiz(Date quizDate, String question, String correctAnswer, String wrongAnswer1, String wrongAnswer2, String wrongAnswer3) {
        this.quizDate = quizDate;
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.wrongAnswer1 = wrongAnswer1;
        this.wrongAnswer2 = wrongAnswer2;
        this.wrongAnswer3 = wrongAnswer3;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Date getQuizDate() { return quizDate; }
    public void setQuizDate(Date quizDate) { this.quizDate = quizDate; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    public String getWrongAnswer1() { return wrongAnswer1; }
    public void setWrongAnswer1(String wrongAnswer1) { this.wrongAnswer1 = wrongAnswer1; }

    public String getWrongAnswer2() { return wrongAnswer2; }
    public void setWrongAnswer2(String wrongAnswer2) { this.wrongAnswer2 = wrongAnswer2; }

    public String getWrongAnswer3() { return wrongAnswer3; }
    public void setWrongAnswer3(String wrongAnswer3) { this.wrongAnswer3 = wrongAnswer3; }
}