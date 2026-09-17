package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CyberLabDatabase
import com.example.data.model.ForumMessage
import com.example.data.model.InAppNotification
import com.example.data.model.NotificationType
import com.example.data.model.StudentSubmission
import com.example.data.repository.CyberLabRepository
import com.example.model.SessionCurriculum
import com.example.sandbox.CryptoAnalysis
import com.example.sandbox.CryptoEngine
import com.example.sandbox.DialogueOption
import com.example.sandbox.MitmEngine
import com.example.sandbox.MitmState
import com.example.sandbox.PhishingEmail
import com.example.sandbox.PhishingEngine
import com.example.sandbox.SqliEngine
import com.example.sandbox.SqliResult
import com.example.sandbox.XssEngine
import com.example.sandbox.XssResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.security.MessageDigest

enum class AppScreen(val title: String) {
    PRESENTATION("Session Deck"),
    TOPICS("Curriculum Topics"),
    FORUM("Live Lab Forum"),
    QUIZ_ASSESSMENT("Interactive Quiz"),
    SQLI_LAB("SQL Injection"),
    XSS_LAB("XSS Defense"),
    MITM_LAB("MITM / Network"),
    CRYPTO_LAB("Cryptography"),
    PHISHING_LAB("Phishing / Pretext"),
    INSTRUCTOR_REPORTS("Grading & Reports")
}

data class LabPerformanceMetrics(
    val sqliScore: Int,
    val xssScore: Int,
    val mitmScore: Int,
    val cryptoScore: Int,
    val phishingScore: Int,
    val totalScore: Int,
    val gradeTier: String,
    val completionPercentage: Int,
    val totalInteractiveActions: Int,
    val flagsUnlockedCount: Int,
    val remarks: String
)

data class UiLabState(
    // Student & Speaker identification
    val studentName: String = "Alkamah Sakilur Rashid",
    val studentId: String = "PU-2023-CSE-088",
    val department: String = "Computer Science & Engineering",
    val speakerGithubUsername: String = SessionCurriculum.SPEAKER_GITHUB_USERNAME,
    val speakerAvatarUrl: String = SessionCurriculum.SPEAKER_AVATAR_URL,
    val speakerRole: String = SessionCurriculum.SPEAKER_ROLE,

    // Presentation Slide Index
    val currentSlideIndex: Int = 0,

    // SQLi Lab
    val sqliUsername: String = "admin' OR '1'='1' --",
    val sqliPassword: String = "password",
    val sqliSafeMode: Boolean = false,
    val sqliResult: SqliResult? = null,
    val sqliExecutionsCount: Int = 0,

    // XSS Lab
    val xssInput: String = "<script>alert('Presidency Cyber XSS')</script>",
    val xssSanitizedMode: Boolean = false,
    val xssResult: XssResult? = null,
    val xssSimulatedAlert: String? = null,
    val xssEvaluationsCount: Int = 0,

    // MITM Lab
    val mitmState: MitmState = MitmEngine.simulateTraffic(useTls = false, enableHsts = false, sslStrip = false),
    val mitmSimulationsCount: Int = 0,

    // Crypto Lab
    val cryptoInput: String = "PresidencyCyber2026",
    val caesarShift: Int = 3,
    val xorKey: Char = 'K',
    val targetHashToCrack: String = "5f4dcc3b5aa765d61d8327deb882cf99", // "password"
    val cryptoAnalysis: CryptoAnalysis = CryptoEngine.analyzeInput("PresidencyCyber2026", "5f4dcc3b5aa765d61d8327deb882cf99"),
    val cryptoCalculationsCount: Int = 0,
    val cryptoCracksCount: Int = 0,

    // Phishing & Social Engineering Lab
    val selectedPhishingEmailIndex: Int = 0,
    val discoveredRedFlags: Set<String> = emptySet(),
    val reportedPhishingEmails: Set<String> = emptySet(),
    val selectedScenarioId: String = "VISHING_IT_DESK",
    val socialEngStep: Int = 1,
    val socialEngFeedback: String? = null,
    val completedSocialScenarios: Set<String> = emptySet(),

    // Flag collection
    val capturedFlags: Set<String> = emptySet(),

    // UI Notices
    val snackbarMessage: String? = null
) {
    val currentPhishingEmail: PhishingEmail
        get() = PhishingEngine.emailScenarios.getOrElse(selectedPhishingEmailIndex) {
            PhishingEngine.emailScenarios.first()
        }
}

class CyberLabViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CyberLabRepository

    private val _currentScreen = MutableStateFlow(AppScreen.PRESENTATION)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _uiState = MutableStateFlow(UiLabState())
    val uiState: StateFlow<UiLabState> = _uiState.asStateFlow()

    val allSubmissions: StateFlow<List<StudentSubmission>>
    val allForumMessages: StateFlow<List<ForumMessage>>
    val allNotifications: StateFlow<List<InAppNotification>>
    val unreadNotificationCount: StateFlow<Int>

    private val _latestAlert = MutableStateFlow<InAppNotification?>(null)
    val latestAlert: StateFlow<InAppNotification?> = _latestAlert.asStateFlow()

    init {
        val db = CyberLabDatabase.getDatabase(application)
        repository = CyberLabRepository(db.studentSubmissionDao(), db.forumDao(), db.notificationDao())
        allSubmissions = repository.allSubmissions.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        allForumMessages = repository.allForumMessages.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        allNotifications = repository.allNotifications.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        unreadNotificationCount = repository.unreadNotificationCount.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

        // Seed initial benchmark submissions, forum, and notifications if empty
        viewModelScope.launch {
            repository.allSubmissions.collect { list ->
                if (list.isEmpty()) {
                    seedBenchmarkSubmissions()
                }
            }
        }
        viewModelScope.launch {
            repository.seedInitialForumIfEmpty()
            repository.seedInitialNotificationsIfEmpty()
        }

        // Initialize default lab runs
        runSqliQuery()
        runXssEvaluation()
    }

    fun postForumMessage(category: String, topicTag: String, content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            val studentName = _uiState.value.studentName.ifBlank { "PU Student" }
            val studentId = _uiState.value.studentId.ifBlank { "PU-GUEST-${(1000..9999).random()}" }
            val isSpeaker = studentName.contains("Alkamah", ignoreCase = true) || studentName.contains("Rashid", ignoreCase = true)
            val role = if (isSpeaker) "SPEAKER" else "STUDENT"
            val message = ForumMessage(
                authorName = studentName,
                authorRole = role,
                studentId = studentId,
                category = category,
                topicTag = topicTag,
                content = content.trim(),
                timestamp = System.currentTimeMillis(),
                upvotes = 0,
                isPinned = isSpeaker
            )
            repository.postForumMessage(message)
            _uiState.value = _uiState.value.copy(snackbarMessage = "Message posted to Live Forum")
        }
    }

    fun upvoteForumMessage(id: Long) {
        viewModelScope.launch {
            repository.upvoteForumMessage(id)
        }
    }

    fun deleteForumMessage(id: Long) {
        viewModelScope.launch {
            repository.deleteForumMessage(id)
            _uiState.value = _uiState.value.copy(snackbarMessage = "Message removed")
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun setStudentDetails(name: String, id: String, dept: String) {
        _uiState.value = _uiState.value.copy(
            studentName = name,
            studentId = id,
            department = dept
        )
    }

    fun updateSpeakerGithub(username: String) {
        val cleanUser = username.trim().removePrefix("@")
        _uiState.value = _uiState.value.copy(
            speakerGithubUsername = cleanUser,
            speakerAvatarUrl = "https://github.com/$cleanUser.png"
        )
    }

    fun nextSlide() {
        val next = (_uiState.value.currentSlideIndex + 1).coerceAtMost(SessionCurriculum.slides.size - 1)
        _uiState.value = _uiState.value.copy(currentSlideIndex = next)
    }

    fun previousSlide() {
        val prev = (_uiState.value.currentSlideIndex - 1).coerceAtLeast(0)
        _uiState.value = _uiState.value.copy(currentSlideIndex = prev)
    }

    fun selectSlide(index: Int) {
        if (index in SessionCurriculum.slides.indices) {
            _uiState.value = _uiState.value.copy(currentSlideIndex = index)
        }
    }

    // --- SQLi Lab Actions ---
    fun updateSqliUsername(value: String) {
        _uiState.value = _uiState.value.copy(sqliUsername = value)
    }

    fun updateSqliPassword(value: String) {
        _uiState.value = _uiState.value.copy(sqliPassword = value)
    }

    fun toggleSqliSafeMode(isSafe: Boolean) {
        _uiState.value = _uiState.value.copy(sqliSafeMode = isSafe)
        runSqliQuery()
    }

    fun runSqliQuery() {
        val current = _uiState.value
        val result = SqliEngine.executeAuthQuery(
            usernameInput = current.sqliUsername,
            passwordInput = current.sqliPassword,
            isSafeMode = current.sqliSafeMode
        )
        val flags = current.capturedFlags.toMutableSet()
        result.flagUnlocked?.let { flags.add(it) }

        _uiState.value = current.copy(
            sqliResult = result,
            capturedFlags = flags,
            sqliExecutionsCount = current.sqliExecutionsCount + 1
        )
    }

    // --- XSS Lab Actions ---
    fun updateXssInput(value: String) {
        _uiState.value = _uiState.value.copy(xssInput = value)
    }

    fun toggleXssSanitizedMode(isSanitized: Boolean) {
        _uiState.value = _uiState.value.copy(xssSanitizedMode = isSanitized)
        runXssEvaluation()
    }

    fun runXssEvaluation() {
        val current = _uiState.value
        val result = XssEngine.evaluateInput(current.xssInput, current.xssSanitizedMode)
        val flags = current.capturedFlags.toMutableSet()
        result.flagUnlocked?.let { flags.add(it) }

        _uiState.value = current.copy(
            xssResult = result,
            xssSimulatedAlert = result.simulatedAlertText,
            capturedFlags = flags,
            xssEvaluationsCount = current.xssEvaluationsCount + 1
        )
    }

    fun dismissXssAlert() {
        _uiState.value = _uiState.value.copy(xssSimulatedAlert = null)
    }

    // --- MITM Lab Actions ---
    fun updateMitmConfig(useTls: Boolean, enableHsts: Boolean, sslStrip: Boolean) {
        val newState = MitmEngine.simulateTraffic(useTls, enableHsts, sslStrip)
        val flags = _uiState.value.capturedFlags.toMutableSet()
        newState.flagUnlocked?.let { flags.add(it) }

        _uiState.value = _uiState.value.copy(
            mitmState = newState,
            capturedFlags = flags,
            mitmSimulationsCount = _uiState.value.mitmSimulationsCount + 1
        )
    }

    // --- Crypto Lab Actions ---
    fun updateCryptoInput(text: String) {
        val analysis = CryptoEngine.analyzeInput(text, _uiState.value.targetHashToCrack)
        _uiState.value = _uiState.value.copy(
            cryptoInput = text,
            cryptoAnalysis = analysis,
            cryptoCalculationsCount = _uiState.value.cryptoCalculationsCount + 1
        )
    }

    fun updateCaesarShift(shift: Int) {
        _uiState.value = _uiState.value.copy(
            caesarShift = shift,
            cryptoCalculationsCount = _uiState.value.cryptoCalculationsCount + 1
        )
    }

    fun updateTargetHashToCrack(hash: String) {
        val analysis = CryptoEngine.analyzeInput(_uiState.value.cryptoInput, hash)
        val flags = _uiState.value.capturedFlags.toMutableSet()
        analysis.flagUnlocked?.let { flags.add(it) }

        _uiState.value = _uiState.value.copy(
            targetHashToCrack = hash,
            cryptoAnalysis = analysis,
            capturedFlags = flags,
            cryptoCracksCount = _uiState.value.cryptoCracksCount + 1
        )
    }

    // --- Phishing & Social Engineering Lab Actions ---
    fun selectPhishingEmail(index: Int) {
        if (index in PhishingEngine.emailScenarios.indices) {
            _uiState.value = _uiState.value.copy(selectedPhishingEmailIndex = index)
        }
    }

    fun toggleRedFlagDiscovered(flagId: String) {
        val currentDiscovered = _uiState.value.discoveredRedFlags.toMutableSet()
        if (currentDiscovered.contains(flagId)) {
            currentDiscovered.remove(flagId)
        } else {
            currentDiscovered.add(flagId)
        }

        val flags = _uiState.value.capturedFlags.toMutableSet()
        if (currentDiscovered.size >= 4) {
            flags.add("FLAG{PU_PHISHING_DEFENSE_AWARE_2026}")
        }

        _uiState.value = _uiState.value.copy(
            discoveredRedFlags = currentDiscovered,
            capturedFlags = flags
        )
    }

    fun reportCurrentEmailToSoc() {
        val current = _uiState.value
        val email = current.currentPhishingEmail
        val emailFlags = email.redFlags.map { it.id }
        val foundForThisEmail = current.discoveredRedFlags.filter { emailFlags.contains(it) }

        val reported = current.reportedPhishingEmails.toMutableSet()
        reported.add(email.id)

        val flags = current.capturedFlags.toMutableSet()
        flags.add("FLAG{PU_SOC_INCIDENT_REPORTED_2026}")

        val notice = if (foundForThisEmail.size >= 2) {
            "✓ Incident Report #PU-SOC-${email.id.takeLast(4)} created! ${foundForThisEmail.size} red flags corroborated. Campus firewall rule deployed."
        } else {
            "Incident logged. Pro tip: Inspect authentication headers and lookalike domains before filing."
        }

        _uiState.value = current.copy(
            reportedPhishingEmails = reported,
            capturedFlags = flags,
            snackbarMessage = notice
        )
    }

    fun selectSocialScenario(scenarioId: String) {
        _uiState.value = _uiState.value.copy(
            selectedScenarioId = scenarioId,
            socialEngStep = 1,
            socialEngFeedback = null
        )
    }

    fun selectDialogueOption(option: DialogueOption) {
        val flags = _uiState.value.capturedFlags.toMutableSet()
        val completed = _uiState.value.completedSocialScenarios.toMutableSet()

        if (option.isSecure) {
            flags.add("FLAG{PU_SOCIAL_ENG_DEFENSE_AWARE_2026}")
            completed.add(_uiState.value.selectedScenarioId)
        }

        _uiState.value = _uiState.value.copy(
            socialEngStep = option.nextStepId ?: 1,
            socialEngFeedback = option.feedback,
            capturedFlags = flags,
            completedSocialScenarios = completed
        )
    }

    // --- Automated Instructor Grading & Persistence ---
    fun calculateScoreBreakdown(): Map<String, Int> {
        val state = _uiState.value
        val flags = state.capturedFlags

        // SQLi score (20 pts): solved if safe mode tested or bypass discovered
        val sqliPts = if (flags.any { it.contains("SQLI") }) 20 else if (state.sqliSafeMode) 15 else 5

        // XSS score (20 pts): solved if sanitization verified
        val xssPts = if (flags.any { it.contains("XSS") }) 20 else if (state.xssSanitizedMode) 15 else 5

        // MITM score (20 pts): solved if TLS/HSTS enforced
        val mitmPts = if (flags.any { it.contains("MITM") }) 20 else if (state.mitmState.useTls) 15 else 5

        // Crypto score (20 pts): solved if hash analyzed/cracked
        val cryptoPts = if (flags.any { it.contains("CRYPTO") }) 20 else 10

        // Phishing score (20 pts): based on red flags identified, reports filed, and social engineering scenarios handled
        val redFlagPts = (state.discoveredRedFlags.size * 2).coerceAtMost(10)
        val reportingPts = (state.reportedPhishingEmails.size * 3).coerceAtMost(6)
        val socialEngPts = if (state.completedSocialScenarios.isNotEmpty()) 4 else 0
        val phishPts = (redFlagPts + reportingPts + socialEngPts).coerceIn(0, 20)

        return mapOf(
            "SQL Injection Defense" to sqliPts,
            "Cross-Site Scripting (XSS) Sanitization" to xssPts,
            "Network Interception & TLS/HSTS" to mitmPts,
            "Cryptography & Hash Analysis" to cryptoPts,
            "Phishing & Social Engineering Awareness" to phishPts
        )
    }

    fun getPerformanceMetrics(): LabPerformanceMetrics {
        val breakdown = calculateScoreBreakdown()
        val total = breakdown.values.sum()
        val tier = when {
            total >= 90 -> "Distinction"
            total >= 75 -> "Merit"
            total >= 60 -> "Pass"
            else -> "Needs Remediation"
        }

        val state = _uiState.value
        val totalActions = state.sqliExecutionsCount +
                state.xssEvaluationsCount +
                state.mitmSimulationsCount +
                state.cryptoCalculationsCount +
                state.cryptoCracksCount +
                state.discoveredRedFlags.size +
                state.reportedPhishingEmails.size +
                state.completedSocialScenarios.size

        val remarks = generateAcademicRemarks(total, breakdown)

        return LabPerformanceMetrics(
            sqliScore = breakdown["SQL Injection Defense"] ?: 0,
            xssScore = breakdown["Cross-Site Scripting (XSS) Sanitization"] ?: 0,
            mitmScore = breakdown["Network Interception & TLS/HSTS"] ?: 0,
            cryptoScore = breakdown["Cryptography & Hash Analysis"] ?: 0,
            phishingScore = breakdown["Phishing & Social Engineering Awareness"] ?: 0,
            totalScore = total,
            gradeTier = tier,
            completionPercentage = (total).coerceIn(0, 100),
            totalInteractiveActions = totalActions,
            flagsUnlockedCount = state.capturedFlags.size,
            remarks = remarks
        )
    }

    fun submitAssessment(onComplete: (Long) -> Unit) {
        viewModelScope.launch {
            val metrics = getPerformanceMetrics()
            val flagsStr = _uiState.value.capturedFlags.joinToString(", ")
            val verificationHash = generateSandboxHash(_uiState.value.studentId, metrics.totalScore, flagsStr)

            val submission = StudentSubmission(
                studentName = _uiState.value.studentName,
                studentId = _uiState.value.studentId,
                department = _uiState.value.department,
                sessionDate = SessionCurriculum.SESSION_DATE,
                completedAt = System.currentTimeMillis(),
                sqliScore = metrics.sqliScore,
                xssScore = metrics.xssScore,
                mitmScore = metrics.mitmScore,
                cryptoScore = metrics.cryptoScore,
                phishingScore = metrics.phishingScore,
                totalScore = metrics.totalScore,
                gradeTier = metrics.gradeTier,
                flagsCaptured = flagsStr,
                instructorRemarks = metrics.remarks,
                sandboxVerificationHash = verificationHash
            )

            val newId = repository.saveSubmission(submission)

            // Alert student about grading report feedback
            val feedbackNotification = InAppNotification(
                title = "Feedback: Assessment Graded (${metrics.gradeTier})",
                message = "Official review completed by Alkamah Sakilur Rashid! Total score: ${metrics.totalScore}/100. Remarks: ${metrics.remarks.take(120)}...",
                type = NotificationType.REPORT_FEEDBACK.name,
                targetScreen = "INSTRUCTOR_REPORTS",
                timestamp = System.currentTimeMillis(),
                isRead = false,
                priority = "HIGH",
                actionLabel = "View Grading Report"
            )
            repository.addNotification(feedbackNotification)
            _latestAlert.value = feedbackNotification

            _uiState.value = _uiState.value.copy(snackbarMessage = "Assessment submitted successfully for instructor grading!")
            onComplete(newId)
        }
    }

    // --- In-App Notification Operations ---
    fun dismissLatestAlert() {
        _latestAlert.value = null
    }

    fun markNotificationAsRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
            _uiState.value = _uiState.value.copy(snackbarMessage = "All notifications marked as read")
        }
    }

    fun deleteNotification(id: Long) {
        viewModelScope.launch {
            repository.deleteNotification(id)
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            repository.clearAllNotifications()
            _uiState.value = _uiState.value.copy(snackbarMessage = "Notification inbox cleared")
        }
    }

    fun postNotification(notification: InAppNotification) {
        viewModelScope.launch {
            repository.addNotification(notification)
            _latestAlert.value = notification
        }
    }

    fun simulateIncomingNotification(type: NotificationType) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val notif = when (type) {
                NotificationType.SESSION_MILESTONE -> {
                    val milestones = listOf(
                        Triple(
                            "Upcoming Milestone: Live CTF Scoring Window",
                            "Session milestone approaching: 30-minute competitive CTF sprint begins after Lab 3. Keep sandboxes open!",
                            "QUIZ_ASSESSMENT"
                        ),
                        Triple(
                            "Session Milestone: Speaker Q&A Starting",
                            "Alkamah Sakilur Rashid is now answering student questions live on cyber security careers in Bangladesh & abroad.",
                            "FORUM"
                        ),
                        Triple(
                            "Session Milestone: Presidency University Certification Check",
                            "Workshop milestone: Certificate issuing portal is now verifying student attendance and lab completion hashes.",
                            "INSTRUCTOR_REPORTS"
                        )
                    )
                    val chosen = milestones.random()
                    InAppNotification(
                        title = chosen.first,
                        message = chosen.second,
                        type = NotificationType.SESSION_MILESTONE.name,
                        targetScreen = chosen.third,
                        timestamp = now,
                        isRead = false,
                        priority = "HIGH",
                        actionLabel = "View Milestone"
                    )
                }
                NotificationType.LAB_AVAILABILITY -> {
                    val labs = listOf(
                        Triple(
                            "New Lab Available: Advanced SQL Injection",
                            "Blind Boolean-based and Time-based SQLi evasion vectors have been unlocked in the sandbox.",
                            "SQLI_LAB"
                        ),
                        Triple(
                            "New Lab Available: Advanced XSS Filter Bypasses",
                            "Unicode normalization and SVG onload event handler sandbox challenges are now active.",
                            "XSS_LAB"
                        ),
                        Triple(
                            "New Lab Available: Wireshark & ARP Spoofing",
                            "Deep packet inspection stream & rogue gateway intercept simulator is now online.",
                            "MITM_LAB"
                        ),
                        Triple(
                            "New Lab Available: AES-256 vs RSA Key Exchange",
                            "Asymmetric Diffie-Hellman & cipher suite degradation testing module unlocked.",
                            "CRYPTO_LAB"
                        ),
                        Triple(
                            "New Lab Available: Executive Vishing Scenario",
                            "Simulated CFO authorization call unlocked in the Social Engineering lab module.",
                            "PHISHING_LAB"
                        )
                    )
                    val chosen = labs.random()
                    InAppNotification(
                        title = chosen.first,
                        message = chosen.second,
                        type = NotificationType.LAB_AVAILABILITY.name,
                        targetScreen = chosen.third,
                        timestamp = now,
                        isRead = false,
                        priority = "HIGH",
                        actionLabel = "Launch Lab"
                    )
                }
                NotificationType.REPORT_FEEDBACK -> {
                    val feedbacks = listOf(
                        Triple(
                            "Feedback: Instructor Verified Distinction",
                            "Alkamah Sakilur Rashid evaluated your lab defense: 'Exceptional mitigation strategy on XSS & SQLi. Hash verified: PU-VERIFIED-PASS'.",
                            "INSTRUCTOR_REPORTS"
                        ),
                        Triple(
                            "Feedback: Defense Flag #4 Validated",
                            "Presidency University grading engine accepted your cryptographic mitigation flag. +20 Points added to your scorecard!",
                            "INSTRUCTOR_REPORTS"
                        ),
                        Triple(
                            "Feedback: Certificate Eligibility Approved",
                            "Official Certificate of Completion (Oct 4, 2026) is ready for download in your grading reports tab.",
                            "INSTRUCTOR_REPORTS"
                        )
                    )
                    val chosen = feedbacks.random()
                    InAppNotification(
                        title = chosen.first,
                        message = chosen.second,
                        type = NotificationType.REPORT_FEEDBACK.name,
                        targetScreen = chosen.third,
                        timestamp = now,
                        isRead = false,
                        priority = "URGENT",
                        actionLabel = "View Feedback"
                    )
                }
                NotificationType.SECURITY_ALERT -> {
                    InAppNotification(
                        title = "Security Alert: Sandbox Threat Detected",
                        message = "Malicious payload detected during live packet capture. Protective sandbox sandboxing kept host environment isolated.",
                        type = NotificationType.SECURITY_ALERT.name,
                        targetScreen = "MITM_LAB",
                        timestamp = now,
                        isRead = false,
                        priority = "URGENT",
                        actionLabel = "Inspect Threat"
                    )
                }
            }
            repository.addNotification(notif)
            _latestAlert.value = notif
        }
    }

    fun clearSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }

    private fun generateAcademicRemarks(total: Int, breakdown: Map<String, Int>): String {
        return buildString {
            append("Official evaluation for Presidency University Cyber Security Session (Oct 4, 2026). ")
            if (total >= 90) {
                append("Outstanding technical proficiency across all five sandbox challenges. Demonstrated strong defensive instinct in prepared statements, HTML sanitization, cryptographic resilience, and incident response.")
            } else if (total >= 75) {
                append("Commendable practical understanding. High accuracy in threat mitigation; recommend practicing advanced HSTS preload configurations and multi-factor authentication vishing trees.")
            } else {
                append("Foundational understanding established. Focus on reviewing SQL parameterization mechanisms and email authentication header triaging (SPF/DKIM/DMARC alignment).")
            }
        }
    }

    private fun generateSandboxHash(studentId: String, score: Int, flags: String): String {
        val raw = "PU_SANDBOX_STAMP:$studentId:$score:$flags:${SessionCurriculum.SESSION_DATE}"
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(raw.toByteArray()).joinToString("") { "%02x".format(it) }.take(16).uppercase()
    }

    private suspend fun seedBenchmarkSubmissions() {
        val sample1 = StudentSubmission(
            studentName = "Tariqul Islam",
            studentId = "PU-2023-CSE-012",
            department = "Computer Science & Engineering",
            sessionDate = "4 October 2026",
            completedAt = System.currentTimeMillis() - 3600000 * 3,
            sqliScore = 20,
            xssScore = 20,
            mitmScore = 20,
            cryptoScore = 20,
            phishingScore = 20,
            totalScore = 100,
            gradeTier = "Distinction",
            flagsCaptured = "FLAG{PU_SQLI_REMEDIATED_PREPARED_STMT_2026}, FLAG{PU_XSS_DOM_SANITIZED_SUCCESS_2026}, FLAG{PU_MITM_TLS13_HSTS_ENFORCED_2026}, FLAG{PU_CRYPTO_HASH_CRACKED_SALT_REMEDIATED_2026}, FLAG{PU_PHISHING_DEFENSE_AWARE_2026}, FLAG{PU_SOC_INCIDENT_REPORTED_2026}",
            instructorRemarks = "Exemplary performance. Completed all attack demonstrations, defensive mitigations, and filed high-fidelity SOC reports.",
            sandboxVerificationHash = "PU-HASH-9981A7F2"
        )
        val sample2 = StudentSubmission(
            studentName = "Nusrat Jahan",
            studentId = "PU-2024-EEE-044",
            department = "Electrical & Electronic Engineering",
            sessionDate = "4 October 2026",
            completedAt = System.currentTimeMillis() - 3600000 * 2,
            sqliScore = 15,
            xssScore = 20,
            mitmScore = 15,
            cryptoScore = 20,
            phishingScore = 16,
            totalScore = 86,
            gradeTier = "Merit",
            flagsCaptured = "FLAG{PU_XSS_DOM_SANITIZED_SUCCESS_2026}, FLAG{PU_CRYPTO_HASH_CRACKED_SALT_REMEDIATED_2026}, FLAG{PU_SOC_INCIDENT_REPORTED_2026}",
            instructorRemarks = "Solid defense implementations in Web & Cryptography. Review network ARP packet structures and executive spear-phishing cues.",
            sandboxVerificationHash = "PU-HASH-31C4B890"
        )
        repository.saveSubmission(sample1)
        repository.saveSubmission(sample2)
    }
}
