package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ForumCategory
import com.example.data.model.ForumMessage
import com.example.model.SessionCurriculum
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
import com.example.viewmodel.UiLabState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ForumScreen(
    messages: List<ForumMessage>,
    state: UiLabState,
    onPostMessage: (category: String, topic: String, content: String) -> Unit,
    onUpvote: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }
    var selectedTopicFilter by remember { mutableStateOf<String?>(null) }
    var isComposerExpanded by remember { mutableStateOf(false) }

    var draftContent by remember { mutableStateOf("") }
    var draftCategory by remember { mutableStateOf(ForumCategory.QUESTION.name) }
    var draftTopic by remember { mutableStateOf("Introduction") }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val topicOptions = remember {
        listOf(
            "Introduction",
            "Securing Accounts",
            "Securing Data",
            "Securing Systems",
            "Securing Software",
            "Preserving Privacy",
            "Cyber Security Career Opportunities",
            "Club Activities & Future Opportunities"
        )
    }

    val filteredMessages = remember(messages, selectedCategoryFilter, selectedTopicFilter) {
        messages.filter { msg ->
            val matchCategory = selectedCategoryFilter == null || msg.category == selectedCategoryFilter
            val matchTopic = selectedTopicFilter == null || msg.topicTag.equals(selectedTopicFilter, ignoreCase = true)
            matchCategory && matchTopic
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberNavyDark)
            .imePadding()
            .testTag("forum_screen")
    ) {
        // Forum Header Banner
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = CyberNavySurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberNavyBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
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
                            Box(
                                modifier = Modifier
                                    .size(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Forum,
                                    contentDescription = "Forum",
                                    tint = CyberCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "LIVE LAB DISCUSSION FEED",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = CyberTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(CyberGreen)
                                )
                                Text(
                                    text = "Real-time peer & speaker exchange • ${messages.size} threads",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = CyberGreen,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }

                    // Button to toggle compose
                    Button(
                        onClick = { isComposerExpanded = !isComposerExpanded },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isComposerExpanded) CyberThreatRed else CyberCyan,
                            contentColor = Color(0xFF0A0F1D)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("toggle_composer_button")
                    ) {
                        Icon(
                            imageVector = if (isComposerExpanded) Icons.Default.Close else Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isComposerExpanded) "Cancel" else "Post",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Category Filters
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedCategoryFilter == null,
                        onClick = { selectedCategoryFilter = null },
                        label = { Text("All Categories (${messages.size})", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberCyan,
                            selectedLabelColor = Color(0xFF0A0F1D),
                            containerColor = CyberNavyCard,
                            labelColor = CyberTextSecondary
                        )
                    )
                    ForumCategory.values().filter { it != ForumCategory.ALL }.forEach { cat ->
                        val count = messages.count { it.category == cat.name }
                        val isSel = selectedCategoryFilter == cat.name
                        FilterChip(
                            selected = isSel,
                            onClick = { selectedCategoryFilter = if (isSel) null else cat.name },
                            label = { Text("${cat.label} ($count)", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = getCategoryColor(cat.name),
                                selectedLabelColor = Color(0xFF0A0F1D),
                                containerColor = CyberNavyCard,
                                labelColor = CyberTextSecondary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Topic Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedTopicFilter == null,
                        onClick = { selectedTopicFilter = null },
                        label = { Text("All Topics", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberAccentBlue,
                            selectedLabelColor = Color.White,
                            containerColor = CyberNavyDark,
                            labelColor = CyberTextSecondary
                        )
                    )
                    topicOptions.forEach { top ->
                        val isSel = selectedTopicFilter == top
                        FilterChip(
                            selected = isSel,
                            onClick = { selectedTopicFilter = if (isSel) null else top },
                            label = { Text(top, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyberAccentBlue,
                                selectedLabelColor = Color.White,
                                containerColor = CyberNavyDark,
                                labelColor = CyberTextSecondary
                            )
                        )
                    }
                }
            }
        }

        // Expandable Composer Card
        AnimatedVisibility(visible = isComposerExpanded) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .testTag("forum_composer_card"),
                shape = RoundedCornerShape(14.dp),
                color = CyberNavySurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "NEW DISCUSSION ENTRY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyberCyan,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )

                    // Category Selector
                    Text("Select Category:", color = CyberTextSecondary, fontSize = 11.sp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ForumCategory.values().filter { it != ForumCategory.ALL }.forEach { cat ->
                            val isSel = draftCategory == cat.name
                            Surface(
                                onClick = { draftCategory = cat.name },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) getCategoryColor(cat.name) else CyberNavyCard,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSel) getCategoryColor(cat.name) else CyberNavyBorder
                                )
                            ) {
                                Text(
                                    text = cat.label,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSel) Color(0xFF0A0F1D) else CyberTextPrimary,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }

                    // Topic Selector
                    Text("Select Topic:", color = CyberTextSecondary, fontSize = 11.sp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        topicOptions.forEach { top ->
                            val isSel = draftTopic == top
                            Surface(
                                onClick = { draftTopic = top },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) CyberCyan.copy(alpha = 0.2f) else CyberNavyCard,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSel) CyberCyan else CyberNavyBorder
                                )
                            ) {
                                Text(
                                    text = top,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSel) CyberCyan else CyberTextSecondary,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }

                    // Fast Templates
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val quickPrompts = listOf(
                            "SQLi auth bypass query used:",
                            "How does CSP nonce prevent inline XSS?",
                            "Observed TLS handshake cipher downgrade:",
                            "Best practices for campus 2FA implementation:"
                        )
                        quickPrompts.forEach { prompt ->
                            Surface(
                                onClick = { draftContent = "$prompt " },
                                shape = RoundedCornerShape(6.dp),
                                color = CyberNavyDark,
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyberNavyBorder)
                            ) {
                                Text(
                                    text = prompt,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = CyberCyan,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }

                    // Message Content Input
                    OutlinedTextField(
                        value = draftContent,
                        onValueChange = { draftContent = it },
                        label = { Text("Share findings, attack methodologies, or questions...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("forum_content_input"),
                        minLines = 3,
                        maxLines = 6,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = CyberNavyBorder,
                            focusedContainerColor = CyberNavyDark,
                            unfocusedContainerColor = CyberNavyDark,
                            focusedTextColor = CyberTextPrimary,
                            unfocusedTextColor = CyberTextPrimary
                        )
                    )

                    // Post Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Posting as: ${state.studentName} (${if (state.studentName.contains("Alkamah", true)) "Speaker" else "Student"})",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = CyberTextSecondary,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = {
                                if (draftContent.isNotBlank()) {
                                    onPostMessage(draftCategory, draftTopic, draftContent)
                                    draftContent = ""
                                    isComposerExpanded = false
                                    scope.launch {
                                        listState.animateScrollToItem(0)
                                    }
                                }
                            },
                            enabled = draftContent.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyberCyan,
                                contentColor = Color(0xFF0A0F1D)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("submit_post_button")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Publish Message", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Messages List
        if (filteredMessages.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Forum,
                        contentDescription = null,
                        tint = CyberCyan.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "No discussions match your filter.",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = CyberTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Be the first to post a question or attack methodology!",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = CyberTextSecondary
                        )
                    )
                    Button(
                        onClick = { isComposerExpanded = true },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color(0xFF0A0F1D)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Create Post", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredMessages, key = { it.id }) { msg ->
                    ForumMessageCard(
                        message = msg,
                        currentUserName = state.studentName,
                        onUpvote = { onUpvote(msg.id) },
                        onDelete = { onDelete(msg.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ForumMessageCard(
    message: ForumMessage,
    currentUserName: String,
    onUpvote: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSpeaker = message.authorRole == "SPEAKER" || message.authorName.contains("Alkamah", ignoreCase = true)
    val categoryColor = getCategoryColor(message.category)
    val categoryIcon = getCategoryIcon(message.category)
    val canDelete = isSpeaker || message.authorName == currentUserName

    val timeString = remember(message.timestamp) {
        val diff = System.currentTimeMillis() - message.timestamp
        when {
            diff < 60_000 -> "Just now"
            diff < 3600_000 -> "${diff / 60_000}m ago"
            diff < 86400_000 -> "${diff / 3600_000}h ago"
            else -> SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(message.timestamp))
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("forum_message_${message.id}"),
        shape = RoundedCornerShape(14.dp),
        color = if (message.isPinned) CyberNavySurface else CyberNavyCard,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (message.isPinned) CyberCyan.copy(alpha = 0.8f) else CyberNavyBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Pinned Announcement Banner
            if (message.isPinned) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = "Pinned",
                        tint = CyberWarningYellow,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "PINNED ANNOUNCEMENT BY SPEAKER",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyberWarningYellow,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            // Author Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isSpeaker) CyberCyan.copy(alpha = 0.2f) else CyberNavyDark)
                            .border(1.5.dp, if (isSpeaker) CyberCyan else CyberAccentBlue, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSpeaker) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = CyberTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = message.authorName,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = if (isSpeaker) CyberCyan else CyberTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            )
                            if (isSpeaker) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = CyberCyan.copy(alpha = 0.15f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = "SPEAKER / LEAD",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = CyberCyan,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }
                        }
                        Text(
                            text = "${message.studentId} • $timeString",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = CyberTextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Category Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = categoryColor.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, categoryColor.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = categoryIcon,
                            contentDescription = null,
                            tint = categoryColor,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = formatCategoryName(message.category),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = categoryColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            // Topic Tag
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = CyberNavyDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberNavyBorder)
            ) {
                Text(
                    text = "Topic: ${message.topicTag}",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = CyberAccentBlue,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp
                    )
                )
            }

            // Message Content
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = CyberTextPrimary,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            )

            // Footer with Actions (Upvote, Delete)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    onClick = onUpvote,
                    shape = RoundedCornerShape(8.dp),
                    color = CyberNavyDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f)),
                    modifier = Modifier.testTag("upvote_btn_${message.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = "Upvote",
                            tint = CyberCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${message.upvotes}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyberCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                if (canDelete) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("delete_msg_${message.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Message",
                            tint = CyberThreatRed.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun getCategoryColor(category: String): Color {
    return when (category) {
        ForumCategory.ATTACK_METHODOLOGY.name -> CyberThreatRed
        ForumCategory.QUESTION.name -> CyberCyan
        ForumCategory.LAB_FINDING.name -> CyberWarningYellow
        ForumCategory.DEFENSE_TIPS.name -> CyberGreen
        else -> CyberAccentBlue
    }
}

private fun getCategoryIcon(category: String): ImageVector {
    return when (category) {
        ForumCategory.ATTACK_METHODOLOGY.name -> Icons.Default.BugReport
        ForumCategory.QUESTION.name -> Icons.Default.HelpOutline
        ForumCategory.LAB_FINDING.name -> Icons.Default.Science
        ForumCategory.DEFENSE_TIPS.name -> Icons.Default.Shield
        else -> Icons.Default.Lightbulb
    }
}

private fun formatCategoryName(category: String): String {
    return when (category) {
        ForumCategory.ATTACK_METHODOLOGY.name -> "Attack Methodology"
        ForumCategory.QUESTION.name -> "Question"
        ForumCategory.LAB_FINDING.name -> "Lab Finding"
        ForumCategory.DEFENSE_TIPS.name -> "Defense Tip"
        else -> category.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
    }
}
