# 🔍 Debugging Mood Quiz - Step by Step

## Issue: NULL values in database

The quiz appears and works, but all values are NULL in the database.

## Fixes Applied ✅

### 1. Added `/mood/**` to Spring Security
**File**: `SecurityConfig.java`
**Change**: Added `/mood/**` to authenticated paths

```java
.requestMatchers("/peer/**", "/literacy/**", "/notifications/**", "/mood/**").authenticated()
```

### 2. Added Extensive Debug Logging
**Files**: `MoodController.java` and `MoodLogService.java`

The application will now print detailed logs showing:
- When the controller endpoint is called
- What values are received (q1-q5)
- User information
- Each step of the save process
- Any errors that occur

### 3. Made Parameters Optional with Null Checks
**File**: `MoodController.java`

Changed from:
```java
@RequestParam("q1") Integer q1
```

To:
```java
@RequestParam(value = "q1", required = false) Integer q1
```

And added null validation before processing.

---

## 🧪 Testing Steps

### Step 1: Rebuild and Deploy

```bash
cd /Users/hilmipasha/Downloads/MentalHub-new-main
mvn clean package
```

Then deploy the WAR file to your Tomcat server.

### Step 2: Clear Browser Cache

- Open Dev Tools (F12)
- Right-click the refresh button
- Select "Empty Cache and Hard Reload"

OR use incognito mode

### Step 3: Login and Watch Server Logs

1. **Open your server logs** (Tomcat console or catalina.out)

2. **Login as any user** (student, admin, or advisor)

3. **Wait for the quiz modal** to appear (~1 second)

4. **Answer all 5 questions** - select one option for each

5. **Click "Submit Assessment"**

### Step 4: Check Server Logs

You should see output like this:

```
============================================
>>> MOOD CONTROLLER /log endpoint called!
>>> Received values: q1=4, q2=3, q3=2, q4=4, q5=5
============================================
>>> User found: Student (ID: 1)
>>> Has logged mood today: false
>>> Calling logMoodFromQuiz with values: 4,3,2,4,5
>>> MoodLogService.logMoodFromQuiz called
>>> User: Student (ID: 1)
>>> Raw values - Q1:4 Q2:3 Q3:2 Q4:4 Q5:5
>>> Q3 inverted from 2 to 4
>>> MoodLog created - Type: average, Score: 4.0
>>> MoodLog values - Q1:4 Q2:3 Q3:4 Q4:4 Q5:5
>>> Calling DAO.save()...
Hibernate: insert into mood_logs...
>>> DAO.save() completed. MoodLog ID: 1
>>> MOOD LOG (QUIZ): User Student logged mood: average (score: 4.0) [Q1:4 Q2:3 Q3:2 Q4:4 Q5:5]
>>> Mood logged successfully!
```

### Step 5: Check Database

```sql
SELECT 
    id,
    user_id,
    mood_type,
    score,
    q1_overall_feeling,
    q2_sleep_quality,
    q3_stress_level,
    q4_focus_ability,
    q5_social_connection,
    logged_at
FROM mood_logs
WHERE logged_at IS NOT NULL
ORDER BY id DESC
LIMIT 5;
```

You should see your quiz responses with non-NULL values!

---

## 🐛 Troubleshooting

### Issue 1: No logs appear at all

**Problem**: The controller method isn't being called

**Check**:
1. Is the form submitting? (Check browser Network tab)
2. Is there a 403 or 404 error?
3. Is Spring Security blocking it?

**Solution**:
- Verify URL in form is `/mood/log`
- Check browser console for JavaScript errors
- Verify Spring Security allows `/mood/**`

### Issue 2: Logs show "q1=null, q2=null..." 

**Problem**: Form parameters aren't being sent

**Check**:
1. Browser Network tab → Look at the form submission
2. Check "Form Data" or "Payload" section
3. Verify the radio buttons have `name="q1"`, `name="q2"`, etc.

**Solution**:
- Verify all radio buttons have correct `name` attributes
- Make sure form method is "post"
- Check if JavaScript is interfering

### Issue 3: Logs show values but "ERROR saving mood"

**Problem**: Database or transaction issue

**Check**:
1. Do the columns exist in the database?
2. Is there a Hibernate error in the logs?
3. Is transaction management enabled?

**Solution**:
```sql
-- Add missing columns if needed
ALTER TABLE mood_logs ADD COLUMN IF NOT EXISTS q1_overall_feeling INT;
ALTER TABLE mood_logs ADD COLUMN IF NOT EXISTS q2_sleep_quality INT;
ALTER TABLE mood_logs ADD COLUMN IF NOT EXISTS q3_stress_level INT;
ALTER TABLE mood_logs ADD COLUMN IF NOT EXISTS q4_focus_ability INT;
ALTER TABLE mood_logs ADD COLUMN IF NOT EXISTS q5_social_connection INT;
```

### Issue 4: Values saved but still showing NULL

**Problem**: Looking at wrong table or old cached data

**Check**:
1. Are you querying the right database?
2. Is there connection pooling caching old structure?

**Solution**:
```sql
-- Refresh table definition
DESCRIBE mood_logs;

-- Check the newest entry
SELECT * FROM mood_logs ORDER BY id DESC LIMIT 1;
```

---

## 📊 Expected Database Result

After submitting the quiz, you should see:

| id | user_id | mood_type | score | q1 | q2 | q3 | q4 | q5 | logged_at           |
|----|---------|-----------|-------|----|----|----|----|-----|---------------------|
| 1  | 1       | average   | 3.6   | 4  | 3  | 4  | 4  | 3   | 2026-01-19 10:30:00|

**Note**: 
- `score` = (q1 + q2 + q3_inverted + q4 + q5) / 5
- `q3` is inverted in storage (high stress = low score for consistency)

---

## 🔧 Quick Fixes

### If still getting NULLs after all checks:

1. **Restart your application server completely**
   - Stop Tomcat
   - Delete `work/` and `temp/` directories
   - Redeploy WAR
   - Start Tomcat

2. **Verify database schema**
   ```bash
   mysql -u root -p mentalhub < mood_quiz_migration.sql
   ```

3. **Check Hibernate is updating schema**
   In `dispatcher-servlet.xml`, verify:
   ```xml
   <prop key="hibernate.hbm2ddl.auto">update</prop>
   ```

4. **Test with direct SQL**
   ```sql
   INSERT INTO mood_logs 
   (user_id, mood_type, score, q1_overall_feeling, q2_sleep_quality, 
    q3_stress_level, q4_focus_ability, q5_social_connection, logged_at)
   VALUES 
   (1, 'happy', 4.5, 5, 4, 5, 4, 5, NOW());
   
   SELECT * FROM mood_logs WHERE id = LAST_INSERT_ID();
   ```
   
   If this works, the problem is in the application code.
   If this fails, the problem is in the database schema.

---

## 📞 Next Steps

1. **Follow the testing steps above**
2. **Copy the server log output** and share it
3. **Run the database query** and share the result
4. **Check browser Network tab** for the form submission

With the extensive logging now in place, we'll be able to pinpoint exactly where the issue is!

---

**Files Modified**:
- ✅ `SecurityConfig.java` - Added /mood/** to authenticated paths
- ✅ `MoodController.java` - Added debug logging + null checks
- ✅ `MoodLogService.java` - Added debug logging

**Status**: Ready for testing with full debug output 🚀
