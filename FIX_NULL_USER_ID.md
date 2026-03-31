# 🔧 Fixing NULL user_id in mood_logs

## Problem
Quiz works, but database shows:
- user_id: **NULL** ❌
- All quiz values (q1-q5): **NULL** ❌
- Only score and mood_type have values

## Root Cause Analysis

The issue is likely one of these:

1. **User object is detached** from Hibernate session
2. **Foreign key relationship** not persisting
3. **Transaction not committing** properly
4. **User not being passed** correctly to MoodLog constructor

---

## ✅ Fixes Applied

### 1. Updated MoodLog Entity Mapping

**File**: `MoodLog.java`

Changed:
```java
@ManyToOne
@JoinColumn(name = "user_id")
private User user;
```

To:
```java
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "user_id", nullable = false)
private User user;
```

**Why**: 
- `EAGER` fetch ensures user is loaded
- `nullable = false` enforces foreign key constraint

### 2. Added Extensive Debugging

**Files**: `MoodController.java`, `MoodLogService.java`, `MoodLogDAOHibernate.java`

Now logs:
- User object details at every step
- User ID verification
- Hibernate save operations
- Any exceptions that occur

### 3. Added Null Checks

**File**: `MoodLogService.java`

```java
if (user == null || user.getId() == null) {
    throw new IllegalArgumentException("User cannot be null");
}
```

---

## 🧪 Testing Instructions

### Step 1: Rebuild Application

```bash
cd /Users/hilmipasha/Downloads/MentalHub-new-main
mvn clean package
```

### Step 2: Deploy and Start Server

Deploy to Tomcat and **watch the server logs carefully**.

### Step 3: Test the Quiz

1. **Login** as any user
2. **Wait for quiz modal** (~1 second)
3. **Answer all 5 questions**
4. **Click "Submit Assessment"**

### Step 4: Check Server Logs

You should see output like this:

```
============================================
>>> MOOD CONTROLLER /log endpoint called!
>>> Received values: q1=4, q2=3, q3=2, q4=4, q5=5
============================================
>>> User found: Student
>>> User.getId(): 1                           ← CHECK THIS
>>> User.getEmail(): student@gmail.com
>>> User object class: com.model.User
>>> User hashcode: 123456789
>>> Has logged mood today: false
>>> Calling logMoodFromQuiz with values: 4,3,2,4,5
>>> MoodLogService.logMoodFromQuiz called
>>> User: Student (ID: 1)                     ← CHECK THIS
>>> User object hashcode: 123456789
>>> Raw values - Q1:4 Q2:3 Q3:2 Q4:4 Q5:5
>>> Q3 inverted from 2 to 4
>>> MoodLog created - Type: average, Score: 4.0
>>> MoodLog.user: Student (ID: 1)             ← CHECK THIS - User is set
>>> MoodLog values - Q1:4 Q2:3 Q3:4 Q4:4 Q5:5
>>> Calling DAO.save()...
>>> MoodLogDAOHibernate.save() called
>>> MoodLog before save - ID: null
>>> MoodLog.user before save: User ID: 1, Name: Student  ← CRITICAL CHECK
>>> MoodLog.score: 4.0
>>> MoodLog.moodType: average
>>> MoodLog.q1: 4

Hibernate: insert into mood_logs (user_id, mood_type, score, q1_overall_feeling, ...) values (?, ?, ?, ?, ...)
                                   ↑
                            This should be 1, not null

>>> Hibernate saveOrUpdate completed successfully
>>> MoodLog after save - ID: 1
>>> After save - MoodLog.user: 1              ← CHECK THIS MATCHES
>>> MOOD LOG (QUIZ): User Student logged mood: average (score: 4.0) [Q1:4 Q2:3 Q3:2 Q4:4 Q5:5]
>>> Mood logged successfully!
```

---

## 🔍 Diagnostic Questions

Based on the logs, identify which scenario you're seeing:

### Scenario A: User ID is NULL in logs ❌

**Logs show:**
```
>>> User.getId(): null
```

**Problem**: User object doesn't have an ID

**Solution**: Check user authentication/session management

```java
// In AuthController, verify this is setting ID:
session.setAttribute("userId", user.getId());
```

### Scenario B: User is NULL ❌

**Logs show:**
```
>>> User: NULL
>>> ERROR: User or User ID is null!
```

**Problem**: UserService.getUserById() returning null

**Solution**: Check database - does the user exist?

```sql
SELECT * FROM users WHERE id = 1;
```

### Scenario C: User looks good but user_id still NULL in DB ❌

**Logs show:**
```
>>> MoodLog.user before save: User ID: 1, Name: Student
```

**But database shows user_id = NULL**

**Problem**: Hibernate relationship not persisting

**Possible Solutions:**

#### Solution 1: Check Hibernate SQL Output

Look for this in logs:
```
Hibernate: insert into mood_logs (user_id, ...) values (?, ...)
```

Then check binding:
```
binding parameter [1] as [BIGINT] - [1]  ← Should show the user ID
```

If you see `[null]` instead, the problem is in Hibernate.

#### Solution 2: Verify Database Schema

```sql
DESCRIBE mood_logs;
```

Check:
- Does `user_id` column exist?
- Is it the right type (BIGINT)?
- Is there a foreign key constraint?

```sql
SHOW CREATE TABLE mood_logs;
```

Should show:
```sql
CONSTRAINT `fk_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
```

#### Solution 3: Manually Test Insert

```sql
-- Test if you can manually insert with user_id
INSERT INTO mood_logs 
(user_id, mood_type, score, q1_overall_feeling, q2_sleep_quality, 
 q3_stress_level, q4_focus_ability, q5_social_connection, logged_at)
VALUES 
(1, 'happy', 4.5, 5, 4, 4, 4, 5, NOW());

-- Check if it worked
SELECT * FROM mood_logs WHERE id = LAST_INSERT_ID();
```

If this works, the problem is in the application code.
If this fails, the problem is in the database schema.

---

## 🔧 Additional Fixes to Try

### Fix 1: Ensure Transaction Commits

Add to `dispatcher-servlet.xml`:

```xml
<tx:annotation-driven transaction-manager="transactionManager" mode="aspectj" />
```

### Fix 2: Check Hibernate Logging

In `dispatcher-servlet.xml`, verify:

```xml
<prop key="hibernate.show_sql">true</prop>
<prop key="hibernate.format_sql">true</prop>
<prop key="hibernate.use_sql_comments">true</prop>
```

Add this for parameter logging:

```xml
<prop key="hibernate.type">trace</prop>
```

### Fix 3: Alternative DAO Save Method

If the issue persists, try this in `MoodLogDAOHibernate.java`:

```java
@Override
public void save(MoodLog moodLog) {
    Session session = sessionFactory.getCurrentSession();
    
    // Ensure user is in persistent state
    if (moodLog.getUser() != null && moodLog.getUser().getId() != null) {
        User user = session.get(User.class, moodLog.getUser().getId());
        moodLog.setUser(user);
    }
    
    session.saveOrUpdate(moodLog);
    session.flush(); // Force immediate write
}
```

### Fix 4: Check if cascading is needed

Try adding cascade to User entity:

```java
@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
private List<MoodLog> moodLogs = new ArrayList<>();
```

---

## 📊 Database Verification

After testing, run this query:

```sql
SELECT 
    ml.id,
    ml.user_id,                    -- Should NOT be NULL
    u.name as user_name,           -- Should show user name
    ml.mood_type,
    ml.score,
    ml.q1_overall_feeling,         -- Should NOT be NULL
    ml.q2_sleep_quality,           -- Should NOT be NULL
    ml.q3_stress_level,            -- Should NOT be NULL
    ml.q4_focus_ability,           -- Should NOT be NULL
    ml.q5_social_connection,       -- Should NOT be NULL
    ml.logged_at
FROM mood_logs ml
LEFT JOIN users u ON ml.user_id = u.id
WHERE ml.logged_at IS NOT NULL
ORDER BY ml.id DESC
LIMIT 5;
```

**Expected Result:**
| id | user_id | user_name | mood_type | score | q1 | q2 | q3 | q4 | q5 | logged_at |
|----|---------|-----------|-----------|-------|----|----|----|----|-----|-----------|
| 1  | 1       | Student   | average   | 3.6   | 4  | 3  | 4  | 4  | 3   | 2026-01-19...|

**If user_id is NULL:**
| id | user_id | user_name | mood_type | score | q1  | q2  | q3  | q4  | q5  | logged_at |
|----|---------|-----------|-----------|-------|-----|-----|-----|-----|-----|-----------|
| 1  | NULL    | NULL      | average   | 3.6   | NULL| NULL| NULL| NULL| NULL| 2026-01-19...|

---

## 🎯 Next Steps

1. **Rebuild and deploy** with the new changes
2. **Test the quiz** and submit answers
3. **Copy ALL server logs** from the moment you click submit
4. **Run the verification SQL query**
5. **Share the results**:
   - What does User.getId() show in logs?
   - What does MoodLog.user show before save?
   - What does the Hibernate INSERT statement show?
   - What does the database query show?

With all this debugging in place, we'll be able to pinpoint exactly where the user_id is getting lost!

---

**Files Modified**:
- ✅ `MoodLog.java` - Updated @ManyToOne mapping
- ✅ `MoodController.java` - Added detailed user logging
- ✅ `MoodLogService.java` - Added user validation and logging
- ✅ `MoodLogDAOHibernate.java` - Added Hibernate operation logging

**Status**: Ready for comprehensive debugging 🔍
