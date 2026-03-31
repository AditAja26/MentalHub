# Mood Tracking Feature Documentation

## Overview
The Mood Tracking feature allows users to log their mood each time they log in. This helps track mental well-being over time and provides valuable data for analysis and counseling.

## Features

### 1. Login-Time Mood Quiz
- When a user logs in, a modal appears asking "How are you feeling today?"
- Four mood options are available:
  - **Happy** 😊 - Feeling great and positive (Score: 5.0)
  - **Average** 😐 - Feeling neutral or okay (Score: 3.0)
  - **Sad** 😢 - Feeling down or upset (Score: 2.0)
  - **Depressed** 😞 - Feeling very low or hopeless (Score: 1.0)

### 2. Automatic Tracking
- Mood is logged with a timestamp each time the user selects an option
- System checks if the user has already logged mood for the current day
- Modal only appears once per day (or until the user skips it for that session)

### 3. Data Storage
- Mood data is stored in the `mood_logs` table with:
  - User ID
  - Mood type (happy, average, sad, depressed)
  - Score (1.0 to 5.0 for chart compatibility)
  - Timestamp (when the mood was logged)
  - Label (optional, for backward compatibility with existing charts)

### 4. Historical Tracking
- All mood entries are stored per login
- Users can view their mood history
- Data can be used for trend analysis and reports

## Technical Implementation

### Database Schema
```sql
mood_logs table:
- id (PRIMARY KEY)
- user_id (FOREIGN KEY -> users)
- mood_type (VARCHAR: 'happy', 'average', 'sad', 'depressed')
- score (DOUBLE: 1.0, 2.0, 3.0, or 5.0)
- logged_at (TIMESTAMP)
- label (VARCHAR, optional)
```

### Architecture

#### Backend Components

1. **Model: `MoodLog.java`**
   - Entity class representing mood log entries
   - Added `moodType` and `loggedAt` fields

2. **DAO Layer**
   - `MoodLogDAO.java` - Interface defining data operations
   - `MoodLogDAOHibernate.java` - Hibernate implementation
   - Key methods:
     - `save()` - Save new mood log
     - `hasLoggedMoodToday()` - Check if user logged mood today
     - `getByUserId()` - Get all mood logs for a user
     - `getLatestByUserId()` - Get most recent mood log

3. **Service Layer: `MoodLogService.java`**
   - Business logic for mood tracking
   - Converts mood types to scores
   - Handles mood validation and timestamp management

4. **Controller: `MoodController.java`**
   - Handles HTTP requests for mood logging
   - Endpoints:
     - `POST /mood/log` - Log user's mood
     - `GET /mood/check` - Check if mood logged today (AJAX)
     - `POST /mood/skip` - Skip mood logging for current session

5. **Integration Points**
   - `AuthController.java` - Updated to check mood status on login
   - `StudentController.java` - Updated to display mood history
   - Session attributes:
     - `moodLoggedToday` - Boolean flag
     - `moodSkippedToday` - Boolean flag

#### Frontend Components

1. **Modal View: `fragments/moodQuizModal.html`**
   - Reusable Thymeleaf fragment
   - Beautiful, responsive design with emoji icons
   - Smooth animations and transitions
   - Automatic display logic based on session state

2. **Integration in Landing Pages**
   - `studentLandingPage.html`
   - `adminLandingPage.html`
   - `advisorLandingPage.html`
   - All include the mood modal fragment

3. **JavaScript Functionality**
   - Auto-display on page load if mood not logged
   - Skip functionality with session persistence
   - Form submission handling

## User Flow

### First Login of the Day
1. User enters credentials and clicks "Login"
2. System authenticates user
3. `AuthController` checks if mood logged today
4. User is redirected to their landing page
5. Mood quiz modal automatically appears after 500ms
6. User selects mood and clicks "Submit" OR clicks "Skip for now"
7. If submitted:
   - Mood is saved to database with timestamp
   - Session flag `moodLoggedToday` set to true
   - Modal closes
   - Success message shown (optional)
8. If skipped:
   - Session flag `moodSkippedToday` set to true
   - Modal closes
   - Modal won't show again during this session

### Subsequent Logins Same Day
1. User logs in
2. System detects mood already logged today
3. Modal does not appear
4. User proceeds to their dashboard normally

### Next Day Login
1. User logs in
2. System checks and finds no mood log for new day
3. Modal appears again
4. Process repeats

## API Endpoints

### POST /mood/log
**Description:** Log user's mood for the current session

**Parameters:**
- `moodType` (String, required): One of ["happy", "average", "sad", "depressed"]

**Response:** Redirect to user's landing page

**Example:**
```html
<form action="/mood/log" method="post">
    <input type="radio" name="moodType" value="happy">
    <button type="submit">Submit</button>
</form>
```

### GET /mood/check
**Description:** Check if user has logged mood today (AJAX endpoint)

**Response:** JSON boolean
- `true` - Mood already logged today
- `false` - Mood not logged yet

**Example:**
```javascript
fetch('/mood/check')
  .then(response => response.json())
  .then(hasLogged => {
    if (!hasLogged) {
      // Show modal
    }
  });
```

### POST /mood/skip
**Description:** Skip mood logging for current session

**Response:** Redirect to user's landing page with skip flag set

## Configuration

### Session Attributes
The following session attributes are used:

- `moodLoggedToday` (Boolean) - True if user logged mood today
- `moodSkippedToday` (Boolean) - True if user skipped mood for this session
- `userId` (Long) - Current user's ID
- `userRole` (String) - Current user's role

### Database Configuration
Using Hibernate with auto-update:
```xml
<prop key="hibernate.hbm2ddl.auto">update</prop>
```
This will automatically create the new columns on first run.

For manual migration, use the provided SQL script:
```bash
mysql -u root -p mentalhub < mood_tracking_migration.sql
```

## Customization

### Changing Mood Options
To add/modify mood options:

1. Update `MoodLogService.getMoodScore()` method
2. Update the modal HTML in `fragments/moodQuizModal.html`
3. Update database constraints if applicable

### Changing Display Frequency
Currently set to once per day. To modify:

1. Edit `MoodLogDAOHibernate.hasLoggedMoodToday()` method
2. Adjust date range calculation (currently checks 00:00:00 to 23:59:59)

### Styling the Modal
All styles are inline in `fragments/moodQuizModal.html`:
- Colors defined in CSS variables section
- Animations can be adjusted
- Responsive breakpoints at 600px

## Testing Checklist

- [ ] User can log in and see mood modal
- [ ] Mood modal appears only once per day
- [ ] All four mood options can be selected
- [ ] Mood data is saved to database correctly
- [ ] Timestamp is recorded accurately
- [ ] Modal doesn't appear if already logged today
- [ ] Skip functionality works correctly
- [ ] Modal appears for Student, Admin, and Advisor roles
- [ ] Session persistence works correctly
- [ ] Modal is responsive on mobile devices
- [ ] Database stores multiple mood entries per user
- [ ] Historical mood data can be viewed

## Future Enhancements

1. **Mood History Visualization**
   - Add chart showing mood trends over time
   - Weekly/monthly mood summaries

2. **Smart Reminders**
   - Remind users who haven't logged mood in X days
   - Send notifications for mood logging

3. **Mood Analysis**
   - AI-based pattern recognition
   - Correlation with activities/sessions

4. **Mood-Based Recommendations**
   - Suggest articles based on mood
   - Recommend counseling sessions for low moods

5. **Export Functionality**
   - Allow users to download mood history
   - Generate PDF reports

## Troubleshooting

### Modal Not Appearing
1. Check browser console for JavaScript errors
2. Verify session attributes in controller
3. Check Thymeleaf fragment inclusion
4. Ensure JavaScript is enabled

### Mood Not Saving
1. Check database connection
2. Verify MoodLogService is autowired correctly
3. Check transaction management
4. Review server logs for exceptions

### "Already Logged" Error When Not True
1. Clear session cookies
2. Check system time/timezone settings
3. Verify date comparison logic in DAO
4. Check database timezone settings

## Support
For issues or questions, please contact the development team or create an issue in the project repository.

---
**Version:** 1.0  
**Last Updated:** January 2026  
**Author:** MentalHub Development Team
