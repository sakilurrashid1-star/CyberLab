package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CurriculumTopic
import com.example.model.LabType
import com.example.model.SessionCurriculum
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
import com.example.ui.theme.CyberThreatRed
import com.example.ui.theme.CyberWarningYellow
import com.example.viewmodel.AppScreen

@Composable
fun CurriculumTopicsScreen(
    onNavigateToSlide: (Int) -> Unit,
    onNavigateToLab: (AppScreen) -> Unit,
    onNavigateToForum: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val topics = SessionCurriculum.officialTopics
    var selectedTopicId by remember { mutableStateOf<Int?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberNavyDark)
            .padding(16.dp)
            .testTag("curriculum_topics_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            PresidencyHeader(sessionDate = SessionCurriculum.SESSION_DATE)
        }

        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = CyberNavySurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
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
                            Box(modifier = Modifier.size(32.dp), contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = CyberCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "OFFICIAL WORKSHOP TOPICS",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = CyberCyan,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = "8 Core Curriculum Pillars • Alkamah Sakilur Rashid Session Blueprint",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = CyberTextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Text(
                        text = "Explore the complete curriculum outlined for the Presidency University Cyber Security Workshop. Each topic connects directly to live interactive sandbox simulations, peer forum discussions, and deck slides.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = CyberTextPrimary,
                            lineHeight = 18.sp
                        )
                    )
                }
            }
        }

        // List of 8 Official Topics
        items(topics, key = { it.id }) { topic ->
            val isExpanded = selectedTopicId == topic.id
            val targetScreen = topic.practicalLabType?.let { mapLabTypeToAppScreen(it) }

            CurriculumTopicCard(
                topic = topic,
                targetScreen = targetScreen,
                isExpanded = isExpanded,
                onToggleExpand = {
                    selectedTopicId = if (isExpanded) null else topic.id
                },
                onGoToSlide = { onNavigateToSlide(topic.correspondingSlideIndex) },
                onGoToLab = {
                    targetScreen?.let { onNavigateToLab(it) }
                },
                onGoToForum = { onNavigateToForum(topic.title) }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CurriculumTopicCard(
    topic: CurriculumTopic,
    targetScreen: AppScreen?,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onGoToSlide: () -> Unit,
    onGoToLab: () -> Unit,
    onGoToForum: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("curriculum_topic_card_${topic.id}"),
        shape = RoundedCornerShape(14.dp),
        color = CyberNavyCard,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isExpanded) CyberCyan else CyberNavyBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row: Topic Number & Title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CyberCyan.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan)
                    ) {
                        Text(
                            text = "0${topic.id}",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = CyberCyan,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }

                    Column {
                        Text(
                            text = topic.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = CyberTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = "Presidency University Workshop Core Module",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = CyberWarningYellow,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Expand",
                    tint = CyberCyan,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Short Description Text
            Text(
                text = topic.shortDescription,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = CyberTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            )

            // Key Concept Chips
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                topic.keyConcepts.forEach { concept ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = CyberNavyDark,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberNavyBorder)
                    ) {
                        Text(
                            text = concept,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyberAccentBlue,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            // Expanded Attack & Remediation Breakdown
            if (isExpanded) {
                // Attack Scenario Box
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = CyberThreatRed.copy(alpha = 0.1f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberThreatRed.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.BugReport, contentDescription = null, tint = CyberThreatRed, modifier = Modifier.size(16.dp))
                            Text(
                                text = "ATTACK VECTORS & SCENARIOS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CyberThreatRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                        Text(
                            text = topic.attackScenarios,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = CyberTextPrimary,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }

                // Defensive Remediation Box
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = CyberGreen.copy(alpha = 0.1f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberGreen.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = CyberGreen, modifier = Modifier.size(16.dp))
                            Text(
                                text = "DEFENSIVE MITIGATION & REMEDIATION",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CyberGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                        Text(
                            text = topic.defensiveRemediation,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = CyberTextPrimary,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }
            }

            // Action Buttons: Slide, Lab, Forum
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onGoToSlide,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.6f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Slideshow, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Deck Slide ${topic.correspondingSlideIndex + 1}", color = CyberCyan, fontSize = 11.sp)
                }

                if (targetScreen != null) {
                    Button(
                        onClick = onGoToLab,
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color(0xFF0A0F1D)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Launch Lab", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }

                OutlinedButton(
                    onClick = onGoToForum,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberAccentBlue),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Forum, contentDescription = null, tint = CyberAccentBlue, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Discuss", color = CyberAccentBlue, fontSize = 11.sp)
                }
            }
        }
    }
}

private fun mapLabTypeToAppScreen(labType: LabType): AppScreen {
    return when (labType) {
        LabType.SQL_INJECTION -> AppScreen.SQLI_LAB
        LabType.CROSS_SITE_SCRIPTING -> AppScreen.XSS_LAB
        LabType.MAN_IN_THE_MIDDLE -> AppScreen.MITM_LAB
        LabType.CRYPTOGRAPHY -> AppScreen.CRYPTO_LAB
        LabType.PHISHING_SOCIAL_ENG -> AppScreen.PHISHING_LAB
    }
}
