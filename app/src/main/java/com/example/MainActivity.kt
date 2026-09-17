package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.SessionCurriculum
import com.example.ui.components.HeadsUpNotificationBanner
import com.example.ui.components.NotificationCenterSheet
import com.example.ui.components.mapScreenNameToAppScreen
import com.example.ui.screens.CryptoLabScreen
import com.example.ui.screens.CurriculumTopicsScreen
import com.example.ui.screens.ForumScreen
import com.example.ui.screens.InstructorReportScreen
import com.example.ui.screens.InteractiveQuizScreen
import com.example.ui.screens.MitmLabScreen
import com.example.ui.screens.PhishingLabScreen
import com.example.ui.screens.PresentationScreen
import com.example.ui.screens.SqliLabScreen
import com.example.ui.screens.XssLabScreen
import com.example.ui.theme.CyberAccentBlue
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberNavyBorder
import com.example.ui.theme.CyberNavyCard
import com.example.ui.theme.CyberNavyDark
import com.example.ui.theme.CyberNavySurface
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.CyberThreatRed
import com.example.ui.theme.CyberWarningYellow
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.CyberLabViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: CyberLabViewModel = viewModel()
                CyberLabMainApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CyberLabMainApp(
    viewModel: CyberLabViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val submissions by viewModel.allSubmissions.collectAsStateWithLifecycle()
    val forumMessages by viewModel.allForumMessages.collectAsStateWithLifecycle()
    val notifications by viewModel.allNotifications.collectAsStateWithLifecycle()
    val unreadNotificationCount by viewModel.unreadNotificationCount.collectAsStateWithLifecycle()
    val latestAlert by viewModel.latestAlert.collectAsStateWithLifecycle()
    var showNotificationCenter by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CyberCyan.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = "Club Logo",
                                    tint = CyberCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "PU CYBERLAB",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = CyberCyan,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                )
                            }
                        }

                        Column {
                            Text(
                                text = currentScreen.title,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = CyberTextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Presidency University • 4 Oct 2026",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = CyberTextSecondary,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                },
                actions = {
                    // In-App Notification Bell with Unread Badge
                    IconButton(
                        onClick = { showNotificationCenter = true },
                        modifier = Modifier.testTag("notification_bell_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadNotificationCount > 0) {
                                    Badge(
                                        containerColor = CyberThreatRed,
                                        contentColor = Color.White
                                    ) {
                                        Text(
                                            text = if (unreadNotificationCount > 99) "99+" else unreadNotificationCount.toString(),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp
                                            )
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (unreadNotificationCount > 0) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                                contentDescription = "In-App Notifications",
                                tint = if (unreadNotificationCount > 0) CyberCyan else CyberTextSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    // Sandbox Security Indicator
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = CyberGreen.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberGreen.copy(alpha = 0.5f)),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(CyberGreen)
                            )
                            Text(
                                text = "ISOLATED SANDBOX",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CyberGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CyberNavyDark,
                    titleContentColor = CyberTextPrimary
                )
            )
        },
        bottomBar = {
            ScrollableNavigationHeader(
                currentScreen = currentScreen,
                onSelectScreen = { viewModel.navigateTo(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CyberNavyDark)
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.PRESENTATION -> {
                    PresentationScreen(
                        state = uiState,
                        onNextSlide = { viewModel.nextSlide() },
                        onPrevSlide = { viewModel.previousSlide() },
                        onSelectSlide = { viewModel.selectSlide(it) },
                        onLaunchLab = { viewModel.navigateTo(it) },
                        onUpdateGithubUsername = { viewModel.updateSpeakerGithub(it) }
                    )
                }
                AppScreen.TOPICS -> {
                    CurriculumTopicsScreen(
                        onNavigateToSlide = { slideIndex ->
                            viewModel.selectSlide(slideIndex)
                            viewModel.navigateTo(AppScreen.PRESENTATION)
                        },
                        onNavigateToLab = { labScreen ->
                            viewModel.navigateTo(labScreen)
                        },
                        onNavigateToForum = {
                            viewModel.navigateTo(AppScreen.FORUM)
                        }
                    )
                }
                AppScreen.FORUM -> {
                    ForumScreen(
                        messages = forumMessages,
                        state = uiState,
                        onPostMessage = { category, topic, content ->
                            viewModel.postForumMessage(category, topic, content)
                        },
                        onUpvote = { viewModel.upvoteForumMessage(it) },
                        onDelete = { viewModel.deleteForumMessage(it) }
                    )
                }
                AppScreen.QUIZ_ASSESSMENT -> {
                    InteractiveQuizScreen(
                        studentName = uiState.studentName,
                        onNavigateToDeck = { viewModel.navigateTo(AppScreen.PRESENTATION) }
                    )
                }
                AppScreen.SQLI_LAB -> {
                    SqliLabScreen(
                        state = uiState,
                        onUsernameChange = { viewModel.updateSqliUsername(it) },
                        onPasswordChange = { viewModel.updateSqliPassword(it) },
                        onToggleSafeMode = { viewModel.toggleSqliSafeMode(it) },
                        onExecuteQuery = { viewModel.runSqliQuery() }
                    )
                }
                AppScreen.XSS_LAB -> {
                    XssLabScreen(
                        state = uiState,
                        onInputChange = { viewModel.updateXssInput(it) },
                        onToggleSanitized = { viewModel.toggleXssSanitizedMode(it) },
                        onEvaluate = { viewModel.runXssEvaluation() },
                        onDismissAlert = { viewModel.dismissXssAlert() }
                    )
                }
                AppScreen.MITM_LAB -> {
                    MitmLabScreen(
                        state = uiState,
                        onConfigChange = { tls, hsts, sslStrip ->
                            viewModel.updateMitmConfig(tls, hsts, sslStrip)
                        }
                    )
                }
                AppScreen.CRYPTO_LAB -> {
                    CryptoLabScreen(
                        state = uiState,
                        onInputTextChange = { viewModel.updateCryptoInput(it) },
                        onCaesarShiftChange = { viewModel.updateCaesarShift(it) },
                        onTargetHashChange = { viewModel.updateTargetHashToCrack(it) }
                    )
                }
                AppScreen.PHISHING_LAB -> {
                    PhishingLabScreen(
                        state = uiState,
                        onSelectEmail = { viewModel.selectPhishingEmail(it) },
                        onToggleRedFlag = { viewModel.toggleRedFlagDiscovered(it) },
                        onReportToSoc = { viewModel.reportCurrentEmailToSoc() },
                        onSelectSocialScenario = { viewModel.selectSocialScenario(it) },
                        onSelectDialogueOption = { viewModel.selectDialogueOption(it) }
                    )
                }
                AppScreen.INSTRUCTOR_REPORTS -> {
                    InstructorReportScreen(
                        state = uiState,
                        performanceMetrics = viewModel.getPerformanceMetrics(),
                        submissions = submissions,
                        onStudentInfoChange = { name, id, dept ->
                            viewModel.setStudentDetails(name, id, dept)
                        },
                        onSubmitAssessment = {
                            viewModel.submitAssessment { }
                        }
                    )
                }
            }

            // Real-time Heads-Up Alert Banner overlay
            HeadsUpNotificationBanner(
                alert = latestAlert,
                onDismiss = { viewModel.dismissLatestAlert() },
                onActionClick = { screenName ->
                    if (screenName != null) {
                        mapScreenNameToAppScreen(screenName)?.let { viewModel.navigateTo(it) }
                    }
                },
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }

    // Modal In-App Notification Center
    NotificationCenterSheet(
        isOpen = showNotificationCenter,
        notifications = notifications,
        unreadCount = unreadNotificationCount,
        onDismiss = { showNotificationCenter = false },
        onMarkAsRead = { viewModel.markNotificationAsRead(it) },
        onMarkAllAsRead = { viewModel.markAllNotificationsAsRead() },
        onDeleteNotification = { viewModel.deleteNotification(it) },
        onClearAll = { viewModel.clearAllNotifications() },
        onNavigateToScreen = { screen ->
            viewModel.navigateTo(screen)
            showNotificationCenter = false
        },
        onSimulateAlert = { type ->
            viewModel.simulateIncomingNotification(type)
        }
    )
}

@Composable
fun ScrollableNavigationHeader(
    currentScreen: AppScreen,
    onSelectScreen: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavigationItemData(AppScreen.PRESENTATION, "Session Deck", Icons.Default.Slideshow),
        NavigationItemData(AppScreen.TOPICS, "Topics (8)", Icons.Default.School),
        NavigationItemData(AppScreen.FORUM, "Live Forum", Icons.Default.Forum),
        NavigationItemData(AppScreen.QUIZ_ASSESSMENT, "Quiz", Icons.Default.Quiz),
        NavigationItemData(AppScreen.SQLI_LAB, "SQL Injection", Icons.Default.BugReport),
        NavigationItemData(AppScreen.XSS_LAB, "XSS Defense", Icons.Default.Code),
        NavigationItemData(AppScreen.MITM_LAB, "MITM Network", Icons.Default.Wifi),
        NavigationItemData(AppScreen.CRYPTO_LAB, "Cryptography", Icons.Default.Key),
        NavigationItemData(AppScreen.PHISHING_LAB, "Phishing", Icons.Default.MarkEmailUnread),
        NavigationItemData(AppScreen.INSTRUCTOR_REPORTS, "Grading", Icons.Default.Assessment)
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = CyberNavySurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberNavyBorder)
    ) {
        ScrollableTabRow(
            selectedTabIndex = items.indexOfFirst { it.screen == currentScreen }.coerceAtLeast(0),
            containerColor = CyberNavySurface,
            contentColor = CyberCyan,
            edgePadding = 8.dp,
            indicator = {},
            divider = {}
        ) {
            items.forEach { item ->
                val isSelected = item.screen == currentScreen
                Tab(
                    selected = isSelected,
                    onClick = { onSelectScreen(item.screen) },
                    modifier = Modifier.testTag("nav_tab_${item.screen.name.lowercase()}"),
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) CyberCyan else Color.Transparent)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = if (isSelected) CyberNavyDark else CyberTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) CyberNavyDark else CyberTextSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    }
                )
            }
        }
    }
}

data class NavigationItemData(
    val screen: AppScreen,
    val label: String,
    val icon: ImageVector
)
