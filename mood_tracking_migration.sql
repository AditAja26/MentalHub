-- MentalHub Mood Tracking Feature
-- Database Migration Script
-- This script adds mood tracking columns to the existing mood_logs table

-- Add new columns for mood tracking
-- Note: Hibernate will auto-create these columns if using hbm2ddl.auto=update
-- But this script is provided for manual migration or documentation purposes

ALTER TABLE mood_logs 
ADD COLUMN IF NOT EXISTS mood_type VARCHAR(20) COMMENT 'Type of mood: happy, average, sad, depressed';

ALTER TABLE mood_logs 
ADD COLUMN IF NOT EXISTS logged_at TIMESTAMP NULL COMMENT 'Timestamp when mood was logged';

-- Add index for better query performance
CREATE INDEX IF NOT EXISTS idx_mood_logs_user_date 
ON mood_logs(user_id, logged_at);

-- Optional: Add check constraint for mood_type values
-- (Note: MySQL doesn't support CHECK constraints in older versions)
-- For MySQL 8.0.16+:
-- ALTER TABLE mood_logs 
-- ADD CONSTRAINT chk_mood_type 
-- CHECK (mood_type IN ('happy', 'average', 'sad', 'depressed') OR mood_type IS NULL);

-- Verify the changes
DESCRIBE mood_logs;

-- Sample query to view mood logs with timestamps
-- SELECT ml.id, u.name, ml.mood_type, ml.score, ml.logged_at
-- FROM mood_logs ml
-- JOIN users u ON ml.user_id = u.id
-- WHERE ml.mood_type IS NOT NULL
-- ORDER BY ml.logged_at DESC
-- LIMIT 10;
