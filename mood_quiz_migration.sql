-- MentalHub Mood Quiz Feature
-- Database Migration Script for Quiz-Based Assessment
-- This script adds quiz question columns to the mood_logs table

-- Add mood tracking columns if they don't exist
ALTER TABLE mood_logs 
ADD COLUMN IF NOT EXISTS mood_type VARCHAR(20) COMMENT 'Type of mood: happy, average, sad, depressed';

ALTER TABLE mood_logs 
ADD COLUMN IF NOT EXISTS logged_at TIMESTAMP NULL COMMENT 'Timestamp when mood was logged';

-- Add quiz question response columns
ALTER TABLE mood_logs 
ADD COLUMN IF NOT EXISTS q1_overall_feeling INT COMMENT 'Q1: How feeling overall (1-5)';

ALTER TABLE mood_logs 
ADD COLUMN IF NOT EXISTS q2_sleep_quality INT COMMENT 'Q2: Sleep quality (1-5)';

ALTER TABLE mood_logs 
ADD COLUMN IF NOT EXISTS q3_stress_level INT COMMENT 'Q3: Stress level (1-5, inverted)';

ALTER TABLE mood_logs 
ADD COLUMN IF NOT EXISTS q4_focus_ability INT COMMENT 'Q4: Ability to focus (1-5)';

ALTER TABLE mood_logs 
ADD COLUMN IF NOT EXISTS q5_social_connection INT COMMENT 'Q5: Social connection (1-5)';

-- Add indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_mood_logs_user_date 
ON mood_logs(user_id, logged_at);

CREATE INDEX IF NOT EXISTS idx_mood_logs_type 
ON mood_logs(mood_type);

-- Verify the changes
DESCRIBE mood_logs;

-- Sample queries to view mood quiz data

-- View recent quiz responses
-- SELECT 
--     ml.id, 
--     u.name, 
--     ml.mood_type, 
--     ml.score,
--     ml.q1_overall_feeling,
--     ml.q2_sleep_quality,
--     ml.q3_stress_level,
--     ml.q4_focus_ability,
--     ml.q5_social_connection,
--     ml.logged_at
-- FROM mood_logs ml
-- JOIN users u ON ml.user_id = u.id
-- WHERE ml.mood_type IS NOT NULL
-- ORDER BY ml.logged_at DESC
-- LIMIT 10;

-- View mood trends for a specific user
-- SELECT 
--     DATE(logged_at) as date,
--     mood_type,
--     score,
--     (q1_overall_feeling + q2_sleep_quality + q3_stress_level + q4_focus_ability + q5_social_connection) / 5.0 as avg_score
-- FROM mood_logs
-- WHERE user_id = 1 AND logged_at IS NOT NULL
-- ORDER BY logged_at DESC;
