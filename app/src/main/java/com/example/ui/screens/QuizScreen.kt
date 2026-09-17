package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.QuizBank
import com.example.model.QuizDifficulty
import com.example.model.QuizQuestion
import com.example.model.QuizTopic
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

@Composable
fun InteractiveQuizScreen(
    studentName: String,
    onNavigateToDeck: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTopic by remember { mutableStateOf(QuizTopic.ALL) }
    var selectedDifficulty by remember { mutableStateOf(QuizDifficulty.ALL) }
    var currentQuestions by remember(selectedTopic, selectedDifficulty) {
        mutableStateOf(QuizBank.generateQuiz(selectedTopic, selectedDifficulty, count = 10, randomize = true))
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var isSubmitted by remember { mutableStateOf(false) }
    var correctCount by remember { mutableIntStateOf(0) }
    var currentStreak by remember { mutableIntStateOf(0) }
    var bestStreak by remember { mutableIntStateOf(0) }
    var isFinished by remember { mutableStateOf(false) }

    fun restartQuiz() {
        currentQuestions = QuizBank.generateQuiz(selectedTopic, selectedDifficulty, count = 10, randomize = true)
        currentIndex = 0
        selectedOption = null
        isSubmitted = false
        correctCount = 0
        currentStreak = 0
        isFinished = false
    }

    val currentQuestion = currentQuestions.getOrNull(currentIndex)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("interactive_quiz_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner Header
        item {
            SandboxBanner(
                title = "INTERACTIVE CYBERSECURITY ASSESSMENT",
                subtitle = "Instant feedback • Session-aligned questions • Explanations & key takeaways"
            )
        }

        // Live Topic & Difficulty Filter Bar
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
                border = BorderStroke(1.dp, CyberNavyBorder)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.FilterList, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                        Text(
                            text = "FILTER ASSESSMENT TOPIC",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyberCyan,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                    }

                    // Topic Chips Row (Horizontal Scroll)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuizTopic.entries.forEach { topic ->
                            val isSelected = selectedTopic == topic
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (selectedTopic != topic) {
                                        selectedTopic = topic
                                        restartQuiz()
                                    }
                                },
                                label = {
                                    Text(
                                        text = topic.displayName,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isSelected) CyberNavyDark else CyberTextPrimary,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyberCyan,
                                    containerColor = CyberNavyCard
                                ),
                                border = BorderStroke(1.dp, if (isSelected) CyberCyan else CyberNavyBorder),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }

                    // Difficulty selector row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Difficulty:",
                            style = MaterialTheme.typography.labelSmall.copy(color = CyberTextSecondary)
                        )
                        QuizDifficulty.entries.forEach { diff ->
                            val isSelected = selectedDifficulty == diff
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (selectedDifficulty != diff) {
                                        selectedDifficulty = diff
                                        restartQuiz()
                                    }
                                },
                                label = {
                                    Text(
                                        text = diff.name,
                                        fontSize = 11.sp,
                                        color = if (isSelected) CyberNavyDark else CyberTextPrimary
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyberGreen,
                                    containerColor = CyberNavyCard
                                ),
                                border = BorderStroke(1.dp, if (isSelected) CyberGreen else CyberNavyBorder),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        OutlinedButton(
                            onClick = { restartQuiz() },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Shuffle", tint = CyberCyan, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Shuffle", color = CyberCyan, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Assessment Summary or Active Question View
        if (isFinished) {
            item {
                QuizResultSummaryCard(
                    studentName = studentName,
                    correctCount = correctCount,
                    totalQuestions = currentQuestions.size,
                    bestStreak = bestStreak,
                    selectedTopic = selectedTopic,
                    onRestart = { restartQuiz() },
                    onNavigateToDeck = onNavigateToDeck
                )
            }
        } else if (currentQuestion != null) {
            // Live Stats Bar
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
                    border = BorderStroke(1.dp, CyberNavyBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "QUESTION ${currentIndex + 1} OF ${currentQuestions.size}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CyberCyan,
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = CyberWarningYellow, modifier = Modifier.size(16.dp))
                                    Text(
                                        text = "Score: $correctCount/${currentQuestions.size}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = CyberTextPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }

                                if (currentStreak > 1) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = CyberGreen.copy(alpha = 0.2f),
                                        border = BorderStroke(1.dp, CyberGreen)
                                    ) {
                                        Text(
                                            text = "🔥 ${currentStreak}x Streak",
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = CyberGreen,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        LinearProgressIndicator(
                            progress = { (currentIndex + 1).toFloat() / currentQuestions.size },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = CyberCyan,
                            trackColor = CyberNavyBorder
                        )
                    }
                }
            }

            // Question Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
                    border = BorderStroke(1.dp, CyberNavyBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Meta badges: Topic & Difficulty & Ref Slide
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = CyberCyan.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = currentQuestion.topic.displayName.uppercase(),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = CyberCyan,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            val diffColor = when (currentQuestion.difficulty) {
                                QuizDifficulty.BEGINNER -> CyberGreen
                                QuizDifficulty.INTERMEDIATE -> CyberWarningYellow
                                QuizDifficulty.ADVANCED -> CyberThreatRed
                                QuizDifficulty.ALL -> CyberTextSecondary
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = diffColor.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = currentQuestion.difficulty.name,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = diffColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Text(
                                text = currentQuestion.referenceSlide.split(":").firstOrNull() ?: "",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CyberTextSecondary,
                                    fontSize = 10.sp
                                )
                            )
                        }

                        // Question Prompt
                        Text(
                            text = currentQuestion.questionText,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = CyberTextPrimary,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 24.sp
                            )
                        )

                        // Answer Options List
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            currentQuestion.options.forEachIndexed { optIndex, optionText ->
                                val isSelectedOption = selectedOption == optIndex
                                val isCorrectOption = optIndex == currentQuestion.correctOptionIndex

                                val containerColor = when {
                                    !isSubmitted -> if (isSelectedOption) CyberCyan.copy(alpha = 0.15f) else CyberNavyCard
                                    isCorrectOption -> CyberGreen.copy(alpha = 0.2f)
                                    isSelectedOption -> CyberThreatRed.copy(alpha = 0.2f)
                                    else -> CyberNavyCard
                                }

                                val borderColor = when {
                                    !isSubmitted -> if (isSelectedOption) CyberCyan else CyberNavyBorder
                                    isCorrectOption -> CyberGreen
                                    isSelectedOption -> CyberThreatRed
                                    else -> CyberNavyBorder
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = containerColor,
                                    border = BorderStroke(1.5.dp, borderColor),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("quiz_option_${optIndex}")
                                        .clickable(enabled = !isSubmitted) {
                                            selectedOption = optIndex
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        // Option letter marker
                                        val letter = ('A'.code + optIndex).toChar().toString()
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    when {
                                                        !isSubmitted && isSelectedOption -> CyberCyan
                                                        isSubmitted && isCorrectOption -> CyberGreen
                                                        isSubmitted && isSelectedOption -> CyberThreatRed
                                                        else -> CyberNavyDark
                                                    }
                                                )
                                                .border(
                                                    1.dp,
                                                    if (isSelectedOption || (isSubmitted && isCorrectOption)) Color.Transparent else CyberNavyBorder,
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = letter,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = when {
                                                        !isSubmitted && isSelectedOption -> CyberNavyDark
                                                        isSubmitted && (isCorrectOption || isSelectedOption) -> CyberNavyDark
                                                        else -> CyberTextPrimary
                                                    },
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }

                                        Text(
                                            text = optionText,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = CyberTextPrimary,
                                                lineHeight = 20.sp
                                            ),
                                            modifier = Modifier.weight(1f)
                                        )

                                        if (isSubmitted) {
                                            if (isCorrectOption) {
                                                Icon(
                                                    Icons.Default.CheckCircle,
                                                    contentDescription = "Correct",
                                                    tint = CyberGreen,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            } else if (isSelectedOption) {
                                                Icon(
                                                    Icons.Default.Cancel,
                                                    contentDescription = "Incorrect",
                                                    tint = CyberThreatRed,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Instant Feedback Explanation Banner
                        AnimatedVisibility(
                            visible = isSubmitted,
                            enter = fadeIn() + expandVertically()
                        ) {
                            val isCorrect = selectedOption == currentQuestion.correctOptionIndex
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isCorrect) CyberGreen.copy(alpha = 0.1f) else CyberThreatRed.copy(alpha = 0.1f)
                                ),
                                border = BorderStroke(1.dp, if (isCorrect) CyberGreen else CyberThreatRed)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                            contentDescription = null,
                                            tint = if (isCorrect) CyberGreen else CyberThreatRed,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = if (isCorrect) "CORRECT! GREAT DEFENSIVE INSTINCT." else "INCORRECT. DEFENSIVE CORRECTION:",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                color = if (isCorrect) CyberGreen else CyberThreatRed,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }

                                    Text(
                                        text = currentQuestion.explanation,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = CyberTextPrimary,
                                            lineHeight = 18.sp
                                        )
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = CyberNavyCard,
                                        border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.3f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                                            Column {
                                                Text(
                                                    text = "PRACTICAL TAKEAWAY",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = CyberCyan,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 10.sp
                                                    )
                                                )
                                                Text(
                                                    text = currentQuestion.practicalTakeaway,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        color = CyberTextSecondary,
                                                        fontSize = 11.sp
                                                    )
                                                )
                                            }
                                        }
                                    }

                                    Text(
                                        text = "Covered in: ${currentQuestion.referenceSlide}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = CyberTextSecondary,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }

                        // Submit & Next Controls
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            if (!isSubmitted) {
                                Button(
                                    onClick = {
                                        if (selectedOption != null) {
                                            isSubmitted = true
                                            val wasCorrect = selectedOption == currentQuestion.correctOptionIndex
                                            if (wasCorrect) {
                                                correctCount++
                                                currentStreak++
                                                if (currentStreak > bestStreak) {
                                                    bestStreak = currentStreak
                                                }
                                            } else {
                                                currentStreak = 0
                                            }
                                        }
                                    },
                                    enabled = selectedOption != null,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = CyberCyan,
                                        contentColor = CyberNavyDark
                                    ),
                                    modifier = Modifier.testTag("submit_answer_button")
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Submit Answer", fontWeight = FontWeight.Bold)
                                }
                            } else {
                                val isLastQuestion = currentIndex >= currentQuestions.size - 1
                                Button(
                                    onClick = {
                                        if (isLastQuestion) {
                                            isFinished = true
                                        } else {
                                            currentIndex++
                                            selectedOption = null
                                            isSubmitted = false
                                        }
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = CyberGreen,
                                        contentColor = CyberNavyDark
                                    ),
                                    modifier = Modifier.testTag("next_question_button")
                                ) {
                                    Text(
                                        text = if (isLastQuestion) "View Assessment Report" else "Next Question",
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuizResultSummaryCard(
    studentName: String,
    correctCount: Int,
    totalQuestions: Int,
    bestStreak: Int,
    selectedTopic: QuizTopic,
    onRestart: () -> Unit,
    onNavigateToDeck: () -> Unit,
    modifier: Modifier = Modifier
) {
    val percentage = if (totalQuestions > 0) (correctCount * 100) / totalQuestions else 0
    val gradeTier = when {
        percentage >= 90 -> "Distinction (Mastery)"
        percentage >= 75 -> "Proficient (Ready for CTF)"
        percentage >= 50 -> "Intermediate (Review Takeaways)"
        else -> "Foundational (Review Deck Slides)"
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
        border = BorderStroke(1.dp, CyberCyan)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(CyberCyan.copy(alpha = 0.2f))
                    .border(2.dp, CyberCyan, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(36.dp))
            }

            Text(
                text = "ASSESSMENT COMPLETED",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = CyberCyan,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )

            Text(
                text = "Candidate: $studentName",
                style = MaterialTheme.typography.bodyMedium.copy(color = CyberTextPrimary)
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = CyberNavyCard,
                border = BorderStroke(1.dp, CyberNavyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "$percentage%",
                        style = MaterialTheme.typography.displaySmall.copy(
                            color = if (percentage >= 75) CyberGreen else CyberWarningYellow,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "$correctCount of $totalQuestions Questions Answered Correctly",
                        style = MaterialTheme.typography.bodyMedium.copy(color = CyberTextSecondary)
                    )

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = (if (percentage >= 75) CyberGreen else CyberCyan).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = gradeTier.uppercase(),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (percentage >= 75) CyberGreen else CyberCyan,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    if (bestStreak > 1) {
                        Text(
                            text = "Peak Accuracy Streak: $bestStreak in a row 🔥",
                            style = MaterialTheme.typography.labelSmall.copy(color = CyberGreen)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onRestart,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, CyberCyan),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Retake Quiz", color = CyberCyan)
                }

                Button(
                    onClick = onNavigateToDeck,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = CyberNavyDark),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Review Slides", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
