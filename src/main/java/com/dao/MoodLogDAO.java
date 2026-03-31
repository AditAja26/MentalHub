package com.dao;

import com.model.MoodLog;
import java.util.Date;
import java.util.List;

public interface MoodLogDAO {
    void save(MoodLog moodLog);
    MoodLog getById(Long id);
    List<MoodLog> getByUserId(Long userId);
    List<MoodLog> getByUserIdAndDateRange(Long userId, Date startDate, Date endDate);
    MoodLog getLatestByUserId(Long userId);
    boolean hasLoggedMoodToday(Long userId);
    void update(MoodLog moodLog);
    void delete(Long id);
}
