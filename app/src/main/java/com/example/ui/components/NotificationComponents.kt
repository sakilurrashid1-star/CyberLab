package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Grading
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.data.model.InAppNotification
import com.example.data.model.NotificationType
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
import com.example.viewmodel.AppScreen
import kotlinx.coroutines.delay

/**
 * Floating Heads-Up Alert Banner that slides into view at the top of the screen
 * when a new milestone, lab availability, or grading report feedback arrives.
 */
@Composable
fun HeadsUpNotificationBanner(
    alert: InAppNotification?,
    onDismiss: () -> Unit,
    onActionClick: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    // Auto-dismiss after 6 seconds
    LaunchedEffect(alert) {
        if (alert != null) {
            delay(6500)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = alert != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier
    ) {
        if (alert != null) {
            val typeColor = getNotificationTypeColor(alert.type)
            val icon = getNotificationIcon(alert.type)

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("heads_up_notification_banner"),
                shape = RoundedCornerShape(12.dp),
                color = CyberNavySurface,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, typeColor),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = typeColor.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, typeColor)
                    ) {
                        Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = typeColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = typeColor.copy(alpha = 0.25f)
                            ) {
                                Text(
                                    text = getNotificationTypeLabel(alert.type).uppercase(),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = typeColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                )
                            }

                            if (alert.priority == "URGENT") {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = CyberThreatRed.copy(alpha = 0.25f)
                                ) {
                                    Text(
                                        text = "URGENT",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = CyberThreatRed,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = alert.title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = CyberTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            ),
                            maxLines = 1
                        )

                        Text(
                            text = alert.message,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = CyberTextSecondary,
                                fontSize = 11.sp
                            ),
                            maxLines = 2
                        )
                    }

                    if (alert.actionLabel != null && alert.targetScreen != null) {
                        Button(
                            onClick = {
                                onActionClick(alert.targetScreen)
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = typeColor,
                                contentColor = Color(0xFF0A0F1D)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("banner_action_button")
                        ) {
                            Text(
                                text = alert.actionLabel,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = CyberTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Full-Featured In-App Notification Center BottomSheet
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NotificationCenterSheet(
    isOpen: Boolean,
    notifications: List<InAppNotification>,
    unreadCount: Int,
    onDismiss: () -> Unit,
    onMarkAsRead: (Long) -> Unit,
    onMarkAllAsRead: () -> Unit,
    onDeleteNotification: (Long) -> Unit,
    onClearAll: () -> Unit,
    onNavigateToScreen: (AppScreen) -> Unit,
    onSimulateAlert: (NotificationType) -> Unit
) {
    if (!isOpen) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedFilter by remember { mutableStateOf<String?>("ALL") }

    val filteredNotifications = remember(notifications, selectedFilter) {
        when (selectedFilter) {
            "UNREAD" -> notifications.filter { !it.isRead }
            "MILESTONES" -> notifications.filter { it.type == NotificationType.SESSION_MILESTONE.name }
            "LABS" -> notifications.filter { it.type == NotificationType.LAB_AVAILABILITY.name }
            "FEEDBACK" -> notifications.filter { it.type == NotificationType.REPORT_FEEDBACK.name }
            else -> notifications
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CyberNavyDark,
        dragHandle = {
            Surface(
                modifier = Modifier.padding(top = 10.dp),
                shape = RoundedCornerShape(4.dp),
                color = CyberNavyBorder
            ) {
                Box(modifier = Modifier.size(width = 40.dp, height = 4.dp))
            }
        },
        modifier = Modifier.testTag("notification_center_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = 16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = CyberCyan.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan)
                    ) {
                        Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "NOTIFICATION CENTER",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = CyberTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                            if (unreadCount > 0) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = CyberThreatRed
                                ) {
                                    Text(
                                        text = "$unreadCount new",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Presidency University Workshop Real-Time Alerts",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = CyberTextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (unreadCount > 0) {
                        IconButton(
                            onClick = onMarkAllAsRead,
                            modifier = Modifier.testTag("mark_all_read_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = "Mark all read",
                                tint = CyberCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    if (notifications.isNotEmpty()) {
                        IconButton(
                            onClick = onClearAll,
                            modifier = Modifier.testTag("clear_all_notifications_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Clear all",
                                tint = CyberThreatRed,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Chips Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val filters = listOf(
                    "ALL" to "All (${notifications.size})",
                    "UNREAD" to "Unread ($unreadCount)",
                    "MILESTONES" to "Milestones (${notifications.count { it.type == NotificationType.SESSION_MILESTONE.name }})",
                    "LABS" to "Labs (${notifications.count { it.type == NotificationType.LAB_AVAILABILITY.name }})",
                    "FEEDBACK" to "Feedback (${notifications.count { it.type == NotificationType.REPORT_FEEDBACK.name }})"
                )

                filters.forEach { (key, label) ->
                    val isSelected = selectedFilter == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = key },
                        label = { Text(label, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberCyan,
                            selectedLabelColor = Color(0xFF0A0F1D),
                            containerColor = CyberNavyCard,
                            labelColor = CyberTextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isSelected) CyberCyan else CyberNavyBorder,
                            enabled = true,
                            selected = isSelected
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Simulation & Trigger Bar (for live interactive testing)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = CyberNavySurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberNavyBorder)
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "TRIGGER TEST NOTIFICATION ALERT",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyberCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Text(
                            text = "Simulate Real-Time Push",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyberTextSecondary,
                                fontSize = 9.sp
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onSimulateAlert(NotificationType.SESSION_MILESTONE) },
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.6f)),
                            modifier = Modifier.weight(1f).testTag("sim_milestone_btn")
                        ) {
                            Text("+ Milestone", color = CyberCyan, fontSize = 10.sp, maxLines = 1)
                        }

                        OutlinedButton(
                            onClick = { onSimulateAlert(NotificationType.LAB_AVAILABILITY) },
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberGreen.copy(alpha = 0.6f)),
                            modifier = Modifier.weight(1f).testTag("sim_lab_btn")
                        ) {
                            Text("+ New Lab", color = CyberGreen, fontSize = 10.sp, maxLines = 1)
                        }

                        OutlinedButton(
                            onClick = { onSimulateAlert(NotificationType.REPORT_FEEDBACK) },
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberWarningYellow.copy(alpha = 0.6f)),
                            modifier = Modifier.weight(1f).testTag("sim_feedback_btn")
                        ) {
                            Text("+ Feedback", color = CyberWarningYellow, fontSize = 10.sp, maxLines = 1)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Notification List
            if (filteredNotifications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = CyberNavySurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberNavyBorder)
                        ) {
                            Box(modifier = Modifier.size(56.dp), contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = CyberTextSecondary.copy(alpha = 0.5f),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        Text(
                            text = "No notifications found",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = CyberTextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Text(
                            text = "Alerts about session milestones, lab unlockings, and grading feedback will appear here.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = CyberTextSecondary.copy(alpha = 0.7f),
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredNotifications, key = { it.id }) { notif ->
                        NotificationCardItem(
                            notification = notif,
                            onMarkAsRead = { onMarkAsRead(notif.id) },
                            onDelete = { onDeleteNotification(notif.id) },
                            onActionClick = { screenName ->
                                onMarkAsRead(notif.id)
                                onDismiss()
                                mapScreenNameToAppScreen(screenName)?.let { onNavigateToScreen(it) }
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCardItem(
    notification: InAppNotification,
    onMarkAsRead: () -> Unit,
    onDelete: () -> Unit,
    onActionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val typeColor = getNotificationTypeColor(notification.type)
    val icon = getNotificationIcon(notification.type)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("notification_item_${notification.id}"),
        shape = RoundedCornerShape(12.dp),
        color = if (notification.isRead) CyberNavyCard else CyberNavySurface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (!notification.isRead) typeColor.copy(alpha = 0.7f) else CyberNavyBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header Row: Type chip, unread badge, timestamp, and actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(typeColor)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = typeColor.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, typeColor.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = typeColor,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = getNotificationTypeLabel(notification.type),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = typeColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    if (notification.priority == "URGENT") {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = CyberThreatRed.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "URGENT",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CyberThreatRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = formatTimeAgo(notification.timestamp),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = CyberTextSecondary,
                            fontSize = 10.sp
                        )
                    )

                    if (!notification.isRead) {
                        IconButton(
                            onClick = onMarkAsRead,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Mark as read",
                                tint = CyberCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = CyberTextSecondary.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Title
            Text(
                text = notification.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = CyberTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            )

            // Message Body
            Text(
                text = notification.message,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = CyberTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            )

            // Interactive Navigation / Action Row
            if (notification.targetScreen != null && notification.actionLabel != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { onActionClick(notification.targetScreen) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = typeColor,
                            contentColor = Color(0xFF0A0F1D)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = notification.actionLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

fun getNotificationTypeColor(type: String): Color {
    return when (type) {
        NotificationType.SESSION_MILESTONE.name -> CyberCyan
        NotificationType.LAB_AVAILABILITY.name -> CyberGreen
        NotificationType.REPORT_FEEDBACK.name -> CyberWarningYellow
        NotificationType.SECURITY_ALERT.name -> CyberThreatRed
        else -> CyberAccentBlue
    }
}

fun getNotificationTypeLabel(type: String): String {
    return when (type) {
        NotificationType.SESSION_MILESTONE.name -> "Milestone"
        NotificationType.LAB_AVAILABILITY.name -> "Lab Available"
        NotificationType.REPORT_FEEDBACK.name -> "Grading Feedback"
        NotificationType.SECURITY_ALERT.name -> "Security Alert"
        else -> "Workshop Alert"
    }
}

fun getNotificationIcon(type: String): ImageVector {
    return when (type) {
        NotificationType.SESSION_MILESTONE.name -> Icons.Default.Schedule
        NotificationType.LAB_AVAILABILITY.name -> Icons.Default.PlayArrow
        NotificationType.REPORT_FEEDBACK.name -> Icons.Default.Grading
        NotificationType.SECURITY_ALERT.name -> Icons.Default.Warning
        else -> Icons.Default.Notifications
    }
}

fun mapScreenNameToAppScreen(screenName: String): AppScreen? {
    return when (screenName) {
        "PRESENTATION" -> AppScreen.PRESENTATION
        "TOPICS" -> AppScreen.TOPICS
        "FORUM" -> AppScreen.FORUM
        "QUIZ_ASSESSMENT" -> AppScreen.QUIZ_ASSESSMENT
        "SQLI_LAB" -> AppScreen.SQLI_LAB
        "XSS_LAB" -> AppScreen.XSS_LAB
        "MITM_LAB" -> AppScreen.MITM_LAB
        "CRYPTO_LAB" -> AppScreen.CRYPTO_LAB
        "PHISHING_LAB" -> AppScreen.PHISHING_LAB
        "INSTRUCTOR_REPORTS", "INSTRUCTOR_REPORT" -> AppScreen.INSTRUCTOR_REPORTS
        else -> null
    }
}

private fun formatTimeAgo(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    return when {
        diff < 60_000 -> "Just now"
        diff < 3600_000 -> "${diff / 60_000}m ago"
        diff < 86400_000 -> "${diff / 3600_000}h ago"
        else -> "${diff / 86400_000}d ago"
    }
}
