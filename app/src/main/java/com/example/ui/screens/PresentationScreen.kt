package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import com.example.ui.components.SpeakerProfileCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CaseStudy
import com.example.model.LabType
import com.example.model.SessionCurriculum
import com.example.model.SessionSlide
import com.example.ui.components.PresidencyHeader
import com.example.ui.components.SandboxBanner
import com.example.ui.theme.CyberAccentBlue
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberNavyBorder
import com.example.ui.theme.CyberNavyCard
import com.example.ui.theme.CyberNavyDark
import com.example.ui.theme.CyberNavySurface
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.CyberWarningYellow
import com.example.viewmodel.AppScreen
import com.example.viewmodel.UiLabState

@Composable
fun PresentationScreen(
    state: UiLabState,
    onNextSlide: () -> Unit,
    onPrevSlide: () -> Unit,
    onSelectSlide: (Int) -> Unit,
    onLaunchLab: (AppScreen) -> Unit,
    onUpdateGithubUsername: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val slides = SessionCurriculum.slides
    val currentSlide = slides[state.currentSlideIndex]
    var showSpeakerNotes by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableStateOf(0) } // 0: Slides, 1: Speaker, 2: Case Studies, 3: Career Paths

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("presentation_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            PresidencyHeader(sessionDate = SessionCurriculum.SESSION_DATE)
        }

        item {
            SandboxBanner(
                title = "ACADEMIC WORKSHOP ENVIRONMENT",
                subtitle = "Presidency University Cyber Security Club • Oct 4, 2026"
            )
        }

        // Section Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberNavySurface, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TabButton(
                    text = "Slides (${state.currentSlideIndex + 1}/${slides.size})",
                    isSelected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    modifier = Modifier.weight(1f)
                )
                TabButton(
                    text = "Speaker",
                    isSelected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.weight(1f)
                )
                TabButton(
                    text = "Case Studies",
                    isSelected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    modifier = Modifier.weight(1f)
                )
                TabButton(
                    text = "Career Paths",
                    isSelected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        when (selectedTab) {
            0 -> {
                // On Welcome Slide (Index 0), show Speaker Profile Card
                if (state.currentSlideIndex == 0) {
                    item {
                        SpeakerProfileCard(
                            speakerName = state.studentName,
                            speakerRole = state.speakerRole,
                            speakerDept = state.department,
                            githubUsername = state.speakerGithubUsername,
                            avatarUrl = state.speakerAvatarUrl
                        )
                    }
                }

                // Quick Topic Selector Bar
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CyberNavySurface, RoundedCornerShape(12.dp))
                            .border(1.dp, CyberNavyBorder, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "TOPIC: ${currentSlide.section.uppercase()}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CyberCyan,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = "Jump to Topic ▾",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CyberTextSecondary,
                                    fontSize = 10.sp
                                )
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            SessionCurriculum.officialTopics.forEach { topicItem ->
                                val isCurrent = currentSlide.section.contains(topicItem.title, ignoreCase = true) ||
                                        (currentSlide.id in (topicItem.id * 2 - 2)..(topicItem.id * 2 - 1))
                                Surface(
                                    onClick = { onSelectSlide(topicItem.correspondingSlideIndex) },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isCurrent) CyberCyan else CyberNavyCard,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isCurrent) CyberCyan else CyberNavyBorder
                                    ),
                                    modifier = Modifier.testTag("quick_topic_btn_${topicItem.id}")
                                ) {
                                    Text(
                                        text = "${topicItem.id}. ${topicItem.title}",
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isCurrent) Color(0xFF0A0F1D) else CyberTextPrimary,
                                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Main Slide Card
                item {
                    SlideCard(
                        slide = currentSlide,
                        currentIndex = state.currentSlideIndex,
                        totalSlides = slides.size,
                        showSpeakerNotes = showSpeakerNotes,
                        onToggleSpeakerNotes = { showSpeakerNotes = !showSpeakerNotes },
                        onLaunchLab = { labType ->
                            val target = when (labType) {
                                LabType.SQL_INJECTION -> AppScreen.SQLI_LAB
                                LabType.CROSS_SITE_SCRIPTING -> AppScreen.XSS_LAB
                                LabType.MAN_IN_THE_MIDDLE -> AppScreen.MITM_LAB
                                LabType.CRYPTOGRAPHY -> AppScreen.CRYPTO_LAB
                                LabType.PHISHING_SOCIAL_ENG -> AppScreen.PHISHING_LAB
                            }
                            onLaunchLab(target)
                        }
                    )
                }

                // Slide Navigation Controls
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = onPrevSlide,
                            enabled = state.currentSlideIndex > 0,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("prev_slide_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Previous")
                        }

                        Text(
                            text = "Slide ${state.currentSlideIndex + 1} of ${slides.size}",
                            style = MaterialTheme.typography.labelMedium.copy(color = CyberCyan)
                        )

                        Button(
                            onClick = onNextSlide,
                            enabled = state.currentSlideIndex < slides.size - 1,
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = CyberNavyDark),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("next_slide_button")
                        ) {
                            Text("Next")
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
                        }
                    }
                }

                // Slide Directory Quick Switcher
                item {
                    Text(
                        text = "SESSION AGENDA & SLIDE DIRECTORY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyberTextSecondary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                }

                items(slides) { slide ->
                    val isCurrent = slide.id - 1 == state.currentSlideIndex
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectSlide(slide.id - 1) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isCurrent) CyberNavyCard else CyberNavySurface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isCurrent) CyberCyan else CyberNavyBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(if (isCurrent) CyberCyan else CyberNavyBorder),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${slide.id}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isCurrent) CyberNavyDark else CyberTextSecondary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = slide.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (isCurrent) CyberCyan else CyberTextPrimary,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                                Text(
                                    text = "${slide.section} • ${slide.durationMinutes} mins",
                                    style = MaterialTheme.typography.bodySmall.copy(color = CyberTextSecondary)
                                )
                            }
                            if (slide.targetLabType != null) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = CyberGreen.copy(alpha = 0.15f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberGreen.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = "LIVE LAB",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = CyberGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Speaker Profile, Leadership & GitHub Integration
                item {
                    SpeakerProfileCard(
                        speakerName = state.studentName,
                        speakerRole = state.speakerRole,
                        speakerDept = state.department,
                        githubUsername = state.speakerGithubUsername,
                        avatarUrl = state.speakerAvatarUrl
                    )
                }

                item {
                    GitHubProfileConfigCard(
                        currentUsername = state.speakerGithubUsername,
                        avatarUrl = state.speakerAvatarUrl,
                        onUpdateUsername = onUpdateGithubUsername
                    )
                }

                item {
                    ClubLeadershipCard()
                }
            }

            2 -> {
                // Real-world Case Studies Deep-dive
                items(SessionCurriculum.caseStudies) { caseStudy ->
                    CaseStudyCard(caseStudy = caseStudy)
                }
            }

            3 -> {
                // Hands-on Career Advice & Pathways
                item {
                    CareerRoadmapCard()
                }
            }
        }
    }
}

@Composable
fun GitHubProfileConfigCard(
    currentUsername: String,
    avatarUrl: String,
    onUpdateUsername: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var usernameInput by remember(currentUsername) { mutableStateOf(currentUsername) }
    val context = androidx.compose.ui.platform.LocalContext.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = CyberCyan,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "GITHUB PROFILE & AVATAR SYNC",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = CyberCyan,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }

            Text(
                text = "The profile photo is loaded dynamically from GitHub via Coil (https://github.com/$currentUsername.png). You can customize or verify your GitHub handle below.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = CyberTextSecondary,
                    lineHeight = 16.sp
                )
            )

            OutlinedTextField(
                value = usernameInput,
                onValueChange = { usernameInput = it },
                label = { Text("GitHub Username", color = CyberTextSecondary) },
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Code, contentDescription = null, tint = CyberCyan)
                },
                trailingIcon = {
                    IconButton(onClick = { onUpdateUsername(usernameInput) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Sync Avatar", tint = CyberGreen)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = CyberTextPrimary,
                    unfocusedTextColor = CyberTextPrimary,
                    focusedBorderColor = CyberCyan,
                    unfocusedBorderColor = CyberNavyBorder,
                    cursorColor = CyberCyan
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("github_username_input")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onUpdateUsername(usernameInput) },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = CyberNavyDark),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).testTag("sync_github_button")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Sync Profile Photo", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = {
                        val gitHubIntent = android.content.Intent(
                            android.content.Intent.ACTION_VIEW,
                            android.net.Uri.parse("https://github.com/${usernameInput.trim().removePrefix("@")}")
                        )
                        context.startActivity(gitHubIntent)
                    },
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberNavyBorder),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("test_github_link_button")
                ) {
                    Icon(Icons.Default.Link, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Test URL", color = CyberCyan, fontSize = 12.sp)
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = CyberNavyCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberNavyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(CyberGreen))
                    Text(
                        text = "Live Avatar URI: $avatarUrl",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyberTextSecondary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun ClubLeadershipCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberNavyBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = CyberCyan,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "CLUB LEADERSHIP & 2026 ROADMAP",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = CyberCyan,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }

            Text(
                text = "Presidency University Cyber Security Club (PUCSC) bridges academic curriculum with real-world defensive engineering, secure coding standards, and ethical bug hunting.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = CyberTextPrimary,
                    lineHeight = 18.sp
                )
            )

            listOf(
                "Executive Lead: Alkamah Sakilur Rashid (General Secretary)" to "Overseeing technical curricula, student workshops, and industry partnerships.",
                "Primary Focus Areas" to "Offensive Security (Web & Network), Cryptographic Engineering, Blue Team Threat Hunting, and Cloud Hardening.",
                "Upcoming Event: 4 October 2026" to "Comprehensive 3.5-hour workshop with hands-on client-side sandboxes and automated performance tracking."
            ).forEach { (heading, desc) ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CyberNavyCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberNavyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = heading,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyberCyan,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = CyberTextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SlideCard(
    slide: SessionSlide,
    currentIndex: Int,
    totalSlides: Int,
    showSpeakerNotes: Boolean,
    onToggleSpeakerNotes: () -> Unit,
    onLaunchLab: (LabType) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberNavyBorder)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Slide Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = CyberCyan.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = slide.section.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyberCyan,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${slide.durationMinutes} min segment",
                    style = MaterialTheme.typography.labelSmall.copy(color = CyberTextSecondary)
                )
            }

            Text(
                text = slide.title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = CyberTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            )

            LinearProgressIndicator(
                progress = { (currentIndex + 1).toFloat() / totalSlides },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = CyberCyan,
                trackColor = CyberNavyBorder,
            )

            // Bullet points
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                slide.bulletPoints.forEach { point ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(CyberCyan)
                        )
                        Text(
                            text = point,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = CyberTextPrimary,
                                lineHeight = 20.sp
                            )
                        )
                    }
                }
            }

            // Key Takeaway callout
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = CyberNavyCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "Takeaway",
                        tint = CyberCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Column {
                        Text(
                            text = "CORE TAKEAWAY FOR STUDENTS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyberCyan,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = slide.keyTakeaways,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = CyberTextPrimary
                            )
                        )
                    }
                }
            }

            // Live Lab Quick Launch Button
            if (slide.targetLabType != null) {
                Button(
                    onClick = { onLaunchLab(slide.targetLabType) },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberGreen, contentColor = CyberNavyDark),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("launch_lab_from_slide")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Launch")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Launch Live Sandbox Lab (${slide.targetLabType.name.replace("_", " ")})",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Speaker Notes Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleSpeakerNotes() }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.RecordVoiceOver,
                    contentDescription = "Speaker Notes",
                    tint = CyberWarningYellow,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = if (showSpeakerNotes) "Hide General Secretary Speaker Notes" else "Show General Secretary Speaker Notes",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = CyberWarningYellow,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            AnimatedVisibility(visible = showSpeakerNotes) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF1E1705),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberWarningYellow.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "SPEAKER CUE & AUDIENCE DELIVERY TIP",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyberWarningYellow,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = slide.speakerNotes,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFFDE68A),
                                lineHeight = 18.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CaseStudyCard(caseStudy: CaseStudy, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberNavyBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = CyberAccentBlue.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = caseStudy.year,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyberAccentBlue,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Text(
                    text = caseStudy.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = CyberTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "ATTACK VECTOR",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = CyberCyan,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = caseStudy.attackVector,
                    style = MaterialTheme.typography.bodySmall.copy(color = CyberTextPrimary)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "REAL-WORLD IMPACT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = CyberWarningYellow,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = caseStudy.impact,
                    style = MaterialTheme.typography.bodySmall.copy(color = CyberTextSecondary)
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = CyberNavyCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberGreen.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "DEFENSIVE LESSON",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyberGreen,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = caseStudy.defensiveTakeaway,
                        style = MaterialTheme.typography.bodySmall.copy(color = CyberTextPrimary)
                    )
                }
            }
        }
    }
}

@Composable
fun CareerRoadmapCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberNavyBorder)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.School, contentDescription = "Career", tint = CyberCyan)
                Text(
                    text = "Presidency University Cyber Career Roadmap",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = CyberTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Text(
                text = "Tailored advice from the General Secretary for engineering and computing students aiming to enter cybersecurity:",
                style = MaterialTheme.typography.bodySmall.copy(color = CyberTextSecondary)
            )

            // Tracks
            CareerTrackItem(
                title = "Blue Team (Defensive & Operations)",
                roles = "SOC Analyst Tier 1/2, Incident Responder, Threat Hunter",
                skills = "SIEM (Splunk/Elastic), Network Traffic Analysis (Wireshark), EDR triage, Log forensics",
                cert = "CompTIA Security+ -> Blue Team Level 1 (BTL1) -> CySA+"
            )

            CareerTrackItem(
                title = "Red Team (Offensive & Penetration Testing)",
                roles = "Junior Pen Tester, Web Application Security Tester, Bug Hunter",
                skills = "Burp Suite Pro, Metasploit, Python scripting, OWASP Top 10 exploitation, Linux internals",
                cert = "eJPT -> Certified Ethical Hacker (CEH) -> OSCP (OffSec)"
            )

            CareerTrackItem(
                title = "Cloud & DevSecOps Engineering",
                roles = "Cloud Security Specialist, DevSecOps Pipeline Architect",
                skills = "AWS/GCP IAM policies, Terraform security, Docker container sandboxing, CI/CD SAST/DAST",
                cert = "AWS Certified Security Specialty / Certified Kubernetes Security (CKS)"
            )

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = CyberNavyCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "HOW TO START AS A PRESIDENCY STUDENT TODAY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyberCyan,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "1. Play CTFs weekly: TryHackMe Pre-Security path & PicoCTF.\n" +
                                "2. Join the Club's defense projects on GitHub to build public proof-of-work.\n" +
                                "3. Read CVE write-ups and reproduce vulnerabilities in isolated sandbox VMs.\n" +
                                "4. Prepare for CompTIA Security+ or eJPT in your 3rd academic year.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = CyberTextPrimary,
                            lineHeight = 18.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun CareerTrackItem(
    title: String,
    roles: String,
    skills: String,
    cert: String
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = CyberNavyCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberNavyBorder)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = CyberCyan,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "Roles: $roles",
                style = MaterialTheme.typography.bodySmall.copy(color = CyberTextPrimary)
            )
            Text(
                text = "Key Skills: $skills",
                style = MaterialTheme.typography.bodySmall.copy(color = CyberTextSecondary, fontSize = 11.sp)
            )
            Text(
                text = "Certifications: $cert",
                style = MaterialTheme.typography.bodySmall.copy(color = CyberGreen, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            )
        }
    }
}

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) CyberCyan else Color.Transparent
    ) {
        Box(
            modifier = Modifier.padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isSelected) CyberNavyDark else CyberTextSecondary,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            )
        }
    }
}
