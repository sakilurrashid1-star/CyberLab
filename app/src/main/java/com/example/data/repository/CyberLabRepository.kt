package com.example.data.repository

import com.example.data.dao.ForumDao
import com.example.data.dao.NotificationDao
import com.example.data.dao.StudentSubmissionDao
import com.example.data.model.ForumCategory
import com.example.data.model.ForumMessage
import com.example.data.model.InAppNotification
import com.example.data.model.NotificationType
import com.example.data.model.StudentSubmission
import kotlinx.coroutines.flow.Flow

class CyberLabRepository(
    private val dao: StudentSubmissionDao,
    private val forumDao: ForumDao,
    private val notificationDao: NotificationDao
) {
    val allSubmissions: Flow<List<StudentSubmission>> = dao.getAllSubmissions()
    val allForumMessages: Flow<List<ForumMessage>> = forumDao.getAllMessages()
    val allNotifications: Flow<List<InAppNotification>> = notificationDao.getAllNotifications()
    val unreadNotificationCount: Flow<Int> = notificationDao.getUnreadCount()

    fun getSubmissionById(id: Long): Flow<StudentSubmission?> = dao.getSubmissionById(id)

    suspend fun saveSubmission(submission: StudentSubmission): Long = dao.insertSubmission(submission)

    suspend fun updateSubmission(submission: StudentSubmission) = dao.updateSubmission(submission)

    suspend fun deleteSubmission(submission: StudentSubmission) = dao.deleteSubmission(submission)

    suspend fun clearAllSubmissions() = dao.clearAllSubmissions()

    // Forum Methods
    fun getMessagesByCategory(category: String): Flow<List<ForumMessage>> =
        forumDao.getMessagesByCategory(category)

    fun getMessagesByTopic(topic: String): Flow<List<ForumMessage>> =
        forumDao.getMessagesByTopic(topic)

    suspend fun postForumMessage(message: ForumMessage): Long =
        forumDao.insertMessage(message)

    suspend fun upvoteForumMessage(id: Long) =
        forumDao.upvoteMessage(id)

    suspend fun deleteForumMessage(id: Long) =
        forumDao.deleteMessage(id)

    suspend fun seedInitialForumIfEmpty() {
        if (forumDao.getMessageCount() == 0) {
            val now = System.currentTimeMillis()
            val initialThreads = listOf(
                ForumMessage(
                    authorName = "Alkamah Sakilur Rashid",
                    authorRole = "SPEAKER",
                    studentId = "SPEAKER-LEAD",
                    category = ForumCategory.DEFENSE_TIPS.name,
                    topicTag = "Introduction",
                    content = "Welcome everyone to the Presidency University Cyber Security Workshop! Please use this live discussion feed to share your exploit discoveries, ask questions regarding sandbox mechanics, and discuss defensive countermeasures.",
                    timestamp = now - 3600_000 * 2,
                    upvotes = 14,
                    isPinned = true
                ),
                ForumMessage(
                    authorName = "Tahmidur Rahman",
                    authorRole = "STUDENT",
                    studentId = "PU-2023-CSE-042",
                    category = ForumCategory.ATTACK_METHODOLOGY.name,
                    topicTag = "SQL Injection",
                    content = "During Lab 1, using `' OR '1'='1' -- ` caused the AST parser to evaluate the OR condition to TRUE, skipping password verification completely. If you want to dump hidden columns, try `' UNION SELECT username, password_hash, role FROM students -- `.",
                    timestamp = now - 1800_000,
                    upvotes = 9,
                    isPinned = false
                ),
                ForumMessage(
                    authorName = "Nusrat Jahan",
                    authorRole = "STUDENT",
                    studentId = "PU-2024-CSE-112",
                    category = ForumCategory.QUESTION.name,
                    topicTag = "Securing Accounts",
                    content = "Question for the speaker: If SMS-based 2FA is susceptible to SIM swapping and SS7 interception, what is the best MFA implementation for university portals? FIDO2 WebAuthn or TOTP Authenticator apps?",
                    timestamp = now - 900_000,
                    upvotes = 7,
                    isPinned = false
                ),
                ForumMessage(
                    authorName = "Alkamah Sakilur Rashid",
                    authorRole = "SPEAKER",
                    studentId = "SPEAKER-LEAD",
                    category = ForumCategory.DEFENSE_TIPS.name,
                    topicTag = "Securing Accounts",
                    content = "@Nusrat Excellent question! Hardware security keys (FIDO2/WebAuthn) offer cryptographically bound origin verification, completely immune to phishing. For mass student deployments, TOTP apps (Google Authenticator, Aegis) are the practical standard over SMS.",
                    timestamp = now - 600_000,
                    upvotes = 12,
                    isPinned = false
                ),
                ForumMessage(
                    authorName = "Arefin Shuvo",
                    authorRole = "STUDENT",
                    studentId = "PU-2023-CSE-095",
                    category = ForumCategory.LAB_FINDING.name,
                    topicTag = "XSS Defense",
                    content = "Finding on Lab 2: Standard blacklisting `<script>` tags was easily bypassed using `<img src=x onerror=alert(1)>`. The only robust mitigation is context-aware output encoding plus strict Content Security Policy (CSP) headers.",
                    timestamp = now - 300_000,
                    upvotes = 6,
                    isPinned = false
                ),
                ForumMessage(
                    authorName = "Sadia Islam",
                    authorRole = "STUDENT",
                    studentId = "PU-2024-CSE-019",
                    category = ForumCategory.ATTACK_METHODOLOGY.name,
                    topicTag = "MITM & Network",
                    content = "When testing the MITM simulator, without HSTS preloading, an attacker performing ARP spoofing can strip HTTPS via SSLsplit and downgrade the connection to plaintext HTTP on port 80!",
                    timestamp = now - 120_000,
                    upvotes = 5,
                    isPinned = false
                )
            )
            forumDao.insertAll(initialThreads)
        }
    }

    // Notification Methods
    suspend fun addNotification(notification: InAppNotification): Long =
        notificationDao.insertNotification(notification)

    suspend fun markNotificationAsRead(id: Long) =
        notificationDao.markAsRead(id)

    suspend fun markAllNotificationsAsRead() =
        notificationDao.markAllAsRead()

    suspend fun deleteNotification(id: Long) =
        notificationDao.deleteNotification(id)

    suspend fun clearAllNotifications() =
        notificationDao.clearAll()

    suspend fun seedInitialNotificationsIfEmpty() {
        if (notificationDao.getNotificationCount() == 0) {
            val now = System.currentTimeMillis()
            val initialNotifications = listOf(
                InAppNotification(
                    title = "Milestone: Keynote & CIA Triad Concluded",
                    message = "Speaker Alkamah Sakilur Rashid concluded Module 1 (Security Landscape & CIA Triad). Session moving into offensive sandboxes.",
                    type = NotificationType.SESSION_MILESTONE.name,
                    targetScreen = "PRESENTATION",
                    timestamp = now - 3600_000,
                    isRead = false,
                    priority = "NORMAL",
                    actionLabel = "Review Slides"
                ),
                InAppNotification(
                    title = "New Lab Unlocked: SQL Injection Sandbox",
                    message = "Interactive SQL Injection sandbox is live! Practice authentication bypass (' OR '1'='1' --) and UNION-based extraction.",
                    type = NotificationType.LAB_AVAILABILITY.name,
                    targetScreen = "SQLI_LAB",
                    timestamp = now - 2400_000,
                    isRead = false,
                    priority = "HIGH",
                    actionLabel = "Launch Lab"
                ),
                InAppNotification(
                    title = "Feedback: Lab Submission Reviewed",
                    message = "Instructor Alkamah Sakilur Rashid verified your SQLi defense report: Score 96/100 (Distinction). Commended for parameterized query mitigation!",
                    type = NotificationType.REPORT_FEEDBACK.name,
                    targetScreen = "INSTRUCTOR_REPORT",
                    timestamp = now - 1500_000,
                    isRead = false,
                    priority = "URGENT",
                    actionLabel = "View Report"
                ),
                InAppNotification(
                    title = "New Lab Unlocked: Stored & Reflected XSS",
                    message = "Lab 2: Cross-Site Scripting sandbox is now open. Test DOM injection and verify Content Security Policy (CSP) headers.",
                    type = NotificationType.LAB_AVAILABILITY.name,
                    targetScreen = "XSS_LAB",
                    timestamp = now - 900_000,
                    isRead = false,
                    priority = "HIGH",
                    actionLabel = "Launch Lab"
                ),
                InAppNotification(
                    title = "Upcoming Milestone: Live Q&A at 3:30 PM",
                    message = "Session milestone alert: 20-minute open Q&A with Alkamah Sakilur Rashid on cyber security careers and PU Cyber Security Club enrollment.",
                    type = NotificationType.SESSION_MILESTONE.name,
                    targetScreen = "FORUM",
                    timestamp = now - 300_000,
                    isRead = false,
                    priority = "NORMAL",
                    actionLabel = "Open Forum"
                ),
                InAppNotification(
                    title = "Feedback: Assessment Certificate Eligible",
                    message = "Your overall workshop performance score is above 85%. Official Presidency University Cyber Security Certificate is ready for export.",
                    type = NotificationType.REPORT_FEEDBACK.name,
                    targetScreen = "INSTRUCTOR_REPORT",
                    timestamp = now - 60_000,
                    isRead = false,
                    priority = "HIGH",
                    actionLabel = "Claim Certificate"
                )
            )
            notificationDao.insertAll(initialNotifications)
        }
    }
}
