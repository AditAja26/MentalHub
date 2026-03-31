# ✅ Per-Login Mood Tracking - Implementation Complete

## What Changed

### Before (Per-Day Tracking) ❌
- User logs in → Quiz appears
- User submits quiz → Saved to database
- User logs out and logs in again **same day** → Quiz does NOT appear
- **Problem**: Only tracks mood once per calendar day

### After (Per-Login Tracking) ✅
- User logs in → Quiz appears
- User submits quiz → Saved to database
- User logs out and logs in again → **Quiz APPEARS again**
- **Result**: Tracks mood on **every login session**, multiple entries per day possible

---

## 🔧 Technical Changes

### 1. AuthController.java
**Changed**: Removed daily mood check on login

**Before:**
```java
boolean hasLoggedMoodToday = moodLogService.hasLoggedMoodToday(user.getId());
session.setAttribute("moodLoggedToday", hasLoggedMoodToday);
```

**After:**
```java
// ALWAYS ask for mood on each login
session.setAttribute("moodLoggedToday", false);
session.setAttribute("moodSkippedToday", false);
```

**Why**: This ensures the modal shows on **every new login**, not just once per day.

---

### 2. MoodController.java
**Changed**: Check session attribute instead of database

**Before:**
```java
boolean hasLogged = moodLogService.hasLoggedMoodToday(userId);
if (hasLogged) {
    // Don't save - already logged today
}
```

**After:**
```java
Boolean hasLoggedThisSession = (Boolean) session.getAttribute("moodLoggedToday");
if (hasLoggedThisSession != null && hasLoggedThisSession) {
    // Don't save - already logged in this session
}
```

**Why**: This prevents duplicate submissions **within the same login session** while allowing multiple entries per day across different sessions.

---

### 3. moodQuizModal.html
**Changed**: Updated comments to reflect per-session behavior

**Added:**
```javascript
console.log("Mood Check (Per Login Session) - Logged:", moodLoggedToday, "Skipped:", moodSkippedToday);
// This will show the modal on every new login, even if user logged in earlier today
```

**Why**: Clarifies that tracking is per-session, not per-day.

---

## 📊 How It Works Now

### Flow Diagram

```
Login #1 (Morning)
├── User logs in → moodLoggedToday = false
├── Landing page loads → Modal appears
├── User answers quiz → Mood saved to DB (Entry #1)
├── moodLoggedToday = true
└── User logs out

Login #2 (Afternoon, same day)
├── User logs in → moodLoggedToday = false (RESET!)
├── Landing page loads → Modal appears AGAIN
├── User answers quiz → Mood saved to DB (Entry #2)
├── moodLoggedToday = true
└── User logs out

Login #3 (Evening, same day)
├── User logs in → moodLoggedToday = false (RESET!)
├── Landing page loads → Modal appears AGAIN
├── User answers quiz → Mood saved to DB (Entry #3)
└── And so on...
```

---

## 🗄️ Database Results

### Example: User logs in 3 times on Jan 19, 2026

```sql
SELECT 
    id, user_id, mood_type, score, 
    q1_overall_feeling, logged_at
FROM mood_logs 
WHERE user_id = 1 
  AND DATE(logged_at) = '2026-01-19'
ORDER BY logged_at;
```

**Result:**
| id | user_id | mood_type | score | q1 | logged_at           |
|----|---------|-----------|-------|----|---------------------|
| 1  | 1       | happy     | 4.2   | 5  | 2026-01-19 08:30:00 |
| 2  | 1       | average   | 3.4   | 3  | 2026-01-19 13:15:00 |
| 3  | 1       | average   | 3.6   | 4  | 2026-01-19 18:45:00 |

✅ **Multiple entries per user per day are now possible!**

---

## 🎯 Benefits

### 1. Better Mood Tracking
- Captures mood changes throughout the day
- Can see morning vs. afternoon vs. evening moods
- More accurate mental health assessment

### 2. Per-Login Insights
- Track mood before/after classes
- Monitor stress patterns across the day
- Identify triggers based on time of day

### 3. Flexible Analysis
```sql
-- Average mood per user per day
SELECT 
    user_id, 
    DATE(logged_at) as date,
    AVG(score) as avg_daily_mood,
    COUNT(*) as login_count
FROM mood_logs
GROUP BY user_id, DATE(logged_at)
ORDER BY date DESC;

-- Mood changes within a day
SELECT 
    user_id,
    HOUR(logged_at) as hour,
    mood_type,
    score
FROM mood_logs
WHERE user_id = 1 
  AND DATE(logged_at) = CURDATE()
ORDER BY logged_at;
```

---

## 🔒 Session Protection

### Prevents Multiple Submissions in Same Session

**Scenario**: User submits quiz, then refreshes the page

```
Login → Quiz appears → User submits → moodLoggedToday = true
     → User refreshes page → Modal does NOT appear ✓
     → User navigates around → Modal does NOT appear ✓
     → User logs out → Session ends
     → User logs in again → Modal appears again ✓
```

**How it works:**
1. `moodLoggedToday` is stored in **HTTP Session**
2. Persists across page navigation **within the same session**
3. **Resets to false** on new login (new session)
4. Logout invalidates the session

---

## 🧪 Testing Scenarios

### Test 1: Basic Per-Login Tracking ✅
```
1. Login as user1 → Quiz appears
2. Submit quiz → Check database (Entry #1)
3. Logout
4. Login as user1 again → Quiz appears AGAIN
5. Submit quiz → Check database (Entry #2)
Expected: Two separate entries in database
```

### Test 2: Same Session Protection ✅
```
1. Login → Quiz appears
2. Submit quiz
3. Refresh page → Quiz should NOT appear
4. Navigate to different page → Quiz should NOT appear
5. Still same session → No new entry in database
Expected: Only one entry for this session
```

### Test 3: Multiple Users ✅
```
1. Login as user1 → Submit quiz (Entry #1)
2. Logout, Login as user2 → Submit quiz (Entry #2)
3. Logout, Login as user1 → Submit quiz (Entry #3)
Expected: Three entries, two for user1, one for user2
```

### Test 4: Skip and Re-login ✅
```
1. Login → Quiz appears
2. Click "Skip for Now"
3. Navigate around → Quiz stays hidden
4. Logout
5. Login again → Quiz appears AGAIN
Expected: Modal shows on new login even if skipped in previous session
```

---

## 📈 Analytics Queries

### Mood Frequency
```sql
-- How often does a user log in and log mood per day?
SELECT 
    user_id,
    DATE(logged_at) as date,
    COUNT(*) as login_sessions,
    AVG(score) as avg_mood
FROM mood_logs
GROUP BY user_id, DATE(logged_at)
ORDER BY date DESC, user_id;
```

### Mood Trends by Time of Day
```sql
-- Morning vs Evening mood patterns
SELECT 
    CASE 
        WHEN HOUR(logged_at) BETWEEN 6 AND 11 THEN 'Morning'
        WHEN HOUR(logged_at) BETWEEN 12 AND 17 THEN 'Afternoon'
        WHEN HOUR(logged_at) BETWEEN 18 AND 23 THEN 'Evening'
        ELSE 'Night'
    END as time_period,
    AVG(score) as avg_mood,
    COUNT(*) as entry_count
FROM mood_logs
WHERE user_id = 1
  AND logged_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)
GROUP BY time_period
ORDER BY FIELD(time_period, 'Morning', 'Afternoon', 'Evening', 'Night');
```

### Mood Volatility
```sql
-- How much does mood vary throughout the day?
SELECT 
    user_id,
    DATE(logged_at) as date,
    MIN(score) as lowest_mood,
    MAX(score) as highest_mood,
    (MAX(score) - MIN(score)) as mood_swing,
    AVG(score) as avg_mood
FROM mood_logs
GROUP BY user_id, DATE(logged_at)
HAVING COUNT(*) > 1
ORDER BY mood_swing DESC;
```

---

## 🔍 Debugging

### Check Session State
```javascript
// In browser console
console.log("Mood Logged:", sessionStorage.getItem('moodLoggedToday'));
```

### Check Database Entries
```sql
-- View all mood logs for a user today
SELECT * FROM mood_logs 
WHERE user_id = 1 
  AND DATE(logged_at) = CURDATE()
ORDER BY logged_at;
```

### Check Server Logs
Look for:
```
>>> New login session started for user: Student
>>> Mood tracking flags reset - will show quiz modal
```

---

## ⚙️ Configuration Options

If you want to change back to **once-per-day** tracking:

### In AuthController.java
```java
// Option 1: Once per day (OLD behavior)
boolean hasLoggedMoodToday = moodLogService.hasLoggedMoodToday(user.getId());
session.setAttribute("moodLoggedToday", hasLoggedMoodToday);

// Option 2: Every login (CURRENT behavior)
session.setAttribute("moodLoggedToday", false);

// Option 3: Configurable frequency
// Add to application.properties: mood.tracking.frequency=login
// Then check the setting here
```

---

## 📋 Summary

✅ **Modal appears on every login**  
✅ **Multiple mood entries per day possible**  
✅ **Each login creates a separate mood log**  
✅ **Session protection prevents duplicates within same session**  
✅ **Database stores all entries with timestamps**  
✅ **Better mood tracking and analysis**  

---

## 🚀 Testing Instructions

1. **Rebuild application**
   ```bash
   mvn clean package
   ```

2. **Deploy and test**
   - Login → Submit quiz → Logout
   - Login again → Quiz should appear again!
   - Submit → Check database for new entry

3. **Verify database**
   ```sql
   SELECT id, user_id, mood_type, score, logged_at 
   FROM mood_logs 
   ORDER BY logged_at DESC 
   LIMIT 10;
   ```

**Expected**: Multiple entries for same user with different timestamps! 🎉

---

**Files Modified**:
- ✅ `AuthController.java` - Reset mood flags on every login
- ✅ `MoodController.java` - Check session instead of database
- ✅ `moodQuizModal.html` - Updated comments

**Status**: Per-login mood tracking is now ACTIVE! 🚀
