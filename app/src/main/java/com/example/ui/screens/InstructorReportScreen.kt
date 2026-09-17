package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Grading
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudentSubmission
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
import com.example.viewmodel.LabPerformanceMetrics
import com.example.viewmodel.UiLabState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun InstructorReportScreen(
    state: UiLabState,
    performanceMetrics: LabPerformanceMetrics,
    submissions: List<StudentSubmission>,
    onStudentInfoChange: (name: String, id: String, dept: String) -> Unit,
    onSubmitAssessment: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isEditingStudent by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(state.studentName) }
    var editId by remember { mutableStateOf(state.studentId) }
    var editDept by remember { mutableStateOf(state.department) }

    var filterTier by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }

    val filteredSubmissions = remember(submissions, filterTier, searchQuery) {
        submissions.filter { sub ->
            val matchesTier = filterTier == "ALL" || sub.gradeTier.equals(filterTier, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    sub.studentName.contains(searchQuery, ignoreCase = true) ||
                    sub.studentId.contains(searchQuery, ignoreCase = true) ||
                    sub.department.contains(searchQuery, ignoreCase = true)
            matchesTier && matchesSearch
        }
    }

    // Classroom analytics computation
    val totalEnrolled = submissions.size
    val averageScore = if (totalEnrolled > 0) submissions.map { it.totalScore }.average().toInt() else 0
    val distinctionCount = submissions.count { it.gradeTier == "Distinction" }
    val distinctionRate = if (totalEnrolled > 0) (distinctionCount * 100 / totalEnrolled) else 0

    // Hardest lab determination
    val avgSqli = if (totalEnrolled > 0) submissions.map { it.sqliScore }.average() else 0.0
    val avgXss = if (totalEnrolled > 0) submissions.map { it.xssScore }.average() else 0.0
    val avgMitm = if (totalEnrolled > 0) submissions.map { it.mitmScore }.average() else 0.0
    val avgCrypto = if (totalEnrolled > 0) submissions.map { it.cryptoScore }.average() else 0.0
    val avgPhish = if (totalEnrolled > 0) submissions.map { it.phishingScore }.average() else 0.0

    val hardestLab = listOf(
        "SQL Injection" to avgSqli,
        "XSS Defense" to avgXss,
        "MITM Network" to avgMitm,
        "Cryptography" to avgCrypto,
        "Phishing & Pretexting" to avgPhish
    ).minByOrNull { it.second }?.first ?: "MITM Network"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("instructor_report_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            PresidencyHeader(sessionDate = SessionCurriculum.SESSION_DATE)
        }

        item {
            SandboxBanner(
                title = "AUTOMATED INSTRUCTOR GRADING & TELEMETRY SUITE",
                subtitle = "Room DB persistence • Interactive participation analytics • Cryptographic verification token • Gradebook export"
            )
        }

        // Student Profile & Current Assessment Summary Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberNavyBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Person, contentDescription = "Student", tint = CyberCyan)
                            Text(
                                text = "Candidate Assessment Profile",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = CyberTextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Text(
                            text = if (isEditingStudent) "Done" else "Edit",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = CyberCyan,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier
                                .clickable {
                                    if (isEditingStudent) {
                                        onStudentInfoChange(editName, editId, editDept)
                                    }
                                    isEditingStudent = !isEditingStudent
                                }
                                .padding(4.dp)
                        )
                    }

                    if (isEditingStudent) {
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("Student Full Name") },
                            modifier = Modifier.fillMaxWidth().testTag("student_name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = CyberNavyBorder,
                                focusedTextColor = CyberTextPrimary,
                                unfocusedTextColor = CyberTextPrimary
                            )
                        )
                        OutlinedTextField(
                            value = editId,
                            onValueChange = { editId = it },
                            label = { Text("Student ID (e.g. PU-2023-CSE-088)") },
                            modifier = Modifier.fillMaxWidth().testTag("student_id_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = CyberNavyBorder,
                                focusedTextColor = CyberTextPrimary,
                                unfocusedTextColor = CyberTextPrimary
                            )
                        )
                        OutlinedTextField(
                            value = editDept,
                            onValueChange = { editDept = it },
                            label = { Text("Department") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = CyberNavyBorder,
                                focusedTextColor = CyberTextPrimary,
                                unfocusedTextColor = CyberTextPrimary
                            )
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = state.studentName,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = CyberTextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "ID: ${state.studentId}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = CyberCyan,
                                        fontFamily = FontFamily.Monospace
                                    )
                                )
                                Text(
                                    text = state.department,
                                    style = MaterialTheme.typography.bodySmall.copy(color = CyberTextSecondary)
                                )
                            }
                            GradeTierBadge(tier = performanceMetrics.gradeTier)
                        }

                        // Key Performance Indicators Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MetricKpiPill(
                                label = "TOTAL SCORE",
                                value = "${performanceMetrics.totalScore} / 100",
                                color = if (performanceMetrics.totalScore >= 75) CyberGreen else CyberWarningYellow,
                                modifier = Modifier.weight(1f)
                            )
                            MetricKpiPill(
                                label = "INTERACTIONS",
                                value = "${performanceMetrics.totalInteractiveActions} Events",
                                color = CyberCyan,
                                modifier = Modifier.weight(1f)
                            )
                            MetricKpiPill(
                                label = "FLAGS CAPTURED",
                                value = "${performanceMetrics.flagsUnlockedCount} Flags",
                                color = CyberAccentBlue,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Classroom Cohort Analytics Card (For Instructors)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Analytics, contentDescription = "Analytics", tint = CyberCyan)
                        Text(
                            text = "CLASSROOM COHORT BENCHMARK",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyberCyan,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricKpiPill(
                            label = "ENROLLED",
                            value = "$totalEnrolled Students",
                            color = CyberTextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        MetricKpiPill(
                            label = "CLASS AVERAGE",
                            value = "$averageScore / 100",
                            color = if (averageScore >= 75) CyberGreen else CyberWarningYellow,
                            modifier = Modifier.weight(1f)
                        )
                        MetricKpiPill(
                            label = "DISTINCTION RATE",
                            value = "$distinctionRate%",
                            color = CyberGreen,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CyberNavyCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberNavyBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Grading, contentDescription = "Hardest", tint = CyberWarningYellow, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Hardest Challenge: $hardestLab (Requires additional instructor review)",
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

        // Live Lab Scoring Breakdown Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberNavyBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "GRADING RUBRIC BREAKDOWN (5 LAB MODULES)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyberCyan,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "${performanceMetrics.totalScore} / 100 PTS",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = if (performanceMetrics.totalScore >= 75) CyberGreen else CyberWarningYellow,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    val rubrics = listOf(
                        Triple("SQL Injection Defense", performanceMetrics.sqliScore, "Prepared statement parameterization & syntax AST parsing"),
                        Triple("Cross-Site Scripting (XSS)", performanceMetrics.xssScore, "Context-aware HTML encoding & cookie protection"),
                        Triple("Network Sniffing & TLS/HSTS", performanceMetrics.mitmScore, "TLS 1.3 cryptographic session & HSTS downgrade resistance"),
                        Triple("Cryptography & Hash Analysis", performanceMetrics.cryptoScore, "MD5 vs SHA-256 and dictionary rainbow attack cracking"),
                        Triple("Phishing & Pretexting Defense", performanceMetrics.phishingScore, "Email header verification, SOC incident reporting & vishing defense")
                    )

                    rubrics.forEach { (labName, pts, desc) ->
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = labName,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = CyberTextPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = CyberNavyCard,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberNavyBorder)
                                ) {
                                    Text(
                                        text = "$pts / 20",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (pts >= 15) CyberGreen else CyberWarningYellow,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    )
                                }
                            }
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = CyberTextSecondary,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    // Interactive Telemetry Audit Ledger
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = CyberNavyDark,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberNavyBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.TouchApp, contentDescription = "Telemetry", tint = CyberCyan, modifier = Modifier.size(16.dp))
                                Text(
                                    text = "STUDENT PARTICIPATION AUDIT LOG:",
                                    style = MaterialTheme.typography.labelSmall.copy(color = CyberCyan, fontWeight = FontWeight.Bold)
                                )
                            }
                            Text(
                                text = "• SQL Query Executions: ${state.sqliExecutionsCount} | Safe Mode Verified: ${state.sqliSafeMode}\n" +
                                        "• XSS Payloads Evaluated: ${state.xssEvaluationsCount} | Sanitization Verified: ${state.xssSanitizedMode}\n" +
                                        "• Packet Sniffer Simulations: ${state.mitmSimulationsCount} | TLS Enforced: ${state.mitmState.useTls}\n" +
                                        "• Crypto Calculations: ${state.cryptoCalculationsCount} | Hashes Cracked: ${state.cryptoCracksCount}\n" +
                                        "• Phishing Red Flags Discovered: ${state.discoveredRedFlags.size} | SOC Reports Filed: ${state.reportedPhishingEmails.size}\n" +
                                        "• Social Engineering Scenarios Completed: ${state.completedSocialScenarios.size}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = CyberTextSecondary,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    lineHeight = 16.sp
                                )
                            )
                        }
                    }

                    // Captured CTF Flags
                    Text(
                        text = "Flags Captured in Session (${state.capturedFlags.size}):",
                        style = MaterialTheme.typography.labelSmall.copy(color = CyberTextSecondary)
                    )
                    if (state.capturedFlags.isEmpty()) {
                        Text(
                            text = "No flags unlocked yet. Complete challenges in each lab to earn flags.",
                            style = MaterialTheme.typography.bodySmall.copy(color = CyberWarningYellow)
                        )
                    } else {
                        state.capturedFlags.forEach { flag ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = CyberGreen.copy(alpha = 0.1f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyberGreen.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = flag,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = CyberGreen,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }

                    // Action buttons: Save to Room DB & Export
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onSubmitAssessment,
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = CyberNavyDark),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("submit_assessment_button")
                        ) {
                            Icon(Icons.Default.Save, contentDescription = "Save")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save to Room DB", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                val transcript = buildString {
                                    append("====================================================\n")
                                    append("PRESIDENCY UNIVERSITY CYBER SECURITY CLUB\n")
                                    append("OFFICIAL WORKSHOP ACADEMIC TRANSCRIPT (4 OCT 2026)\n")
                                    append("====================================================\n\n")
                                    append("Candidate: ${state.studentName}\n")
                                    append("Student ID: ${state.studentId}\n")
                                    append("Department: ${state.department}\n")
                                    append("Session Date: ${SessionCurriculum.SESSION_DATE}\n\n")
                                    append("--- SCORES BREAKDOWN ---\n")
                                    append("1. SQL Injection Defense: ${performanceMetrics.sqliScore} / 20\n")
                                    append("2. Cross-Site Scripting (XSS): ${performanceMetrics.xssScore} / 20\n")
                                    append("3. Network Interception (MITM): ${performanceMetrics.mitmScore} / 20\n")
                                    append("4. Cryptography & Hashes: ${performanceMetrics.cryptoScore} / 20\n")
                                    append("5. Phishing & Pretexting: ${performanceMetrics.phishingScore} / 20\n")
                                    append("TOTAL SCORE: ${performanceMetrics.totalScore} / 100 (${performanceMetrics.gradeTier})\n\n")
                                    append("--- PARTICIPATION TELEMETRY ---\n")
                                    append("Total Interactive Sandbox Events: ${performanceMetrics.totalInteractiveActions}\n")
                                    append("Captured CTF Flags: ${state.capturedFlags.joinToString(", ")}\n\n")
                                    append("--- INSTRUCTOR EVALUATION ---\n")
                                    append("${performanceMetrics.remarks}\n\n")
                                    append("Certified Sandbox Stamp: PU-CERT-SHA256-VERIFIED\n")
                                }
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, transcript)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Export Student Performance Transcript"))
                            },
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan),
                            modifier = Modifier.testTag("share_report_button")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = CyberCyan)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Transcript", color = CyberCyan)
                        }

                        OutlinedButton(
                            onClick = {
                                val csv = buildString {
                                    append("Student Name,Student ID,Department,SQLi,XSS,MITM,Crypto,Phishing,Total Score,Grade Tier,Flags Captured\n")
                                    submissions.forEach { sub ->
                                        append("\"${sub.studentName}\",\"${sub.studentId}\",\"${sub.department}\",${sub.sqliScore},${sub.xssScore},${sub.mitmScore},${sub.cryptoScore},${sub.phishingScore},${sub.totalScore},\"${sub.gradeTier}\",\"${sub.flagsCaptured.replace("\"", "'")}\"\n")
                                    }
                                }
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, csv)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Export Gradebook CSV"))
                            },
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberGreen),
                            modifier = Modifier.testTag("export_csv_button")
                        ) {
                            Icon(Icons.Default.Download, contentDescription = "CSV", tint = CyberGreen)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("CSV", color = CyberGreen)
                        }
                    }
                }
            }
        }

        // Submissions Database Ledger Header & Controls
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ROOM DATABASE LEDGER (${filteredSubmissions.size} OF ${submissions.size})",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyberCyan,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by student name, ID or department...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = CyberCyan) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = CyberTextSecondary,
                                modifier = Modifier.clickable { searchQuery = "" }
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_submissions_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = CyberNavyBorder,
                        focusedTextColor = CyberTextPrimary,
                        unfocusedTextColor = CyberTextPrimary
                    )
                )

                // Filter Chips by Grade Tier
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val tiers = listOf("ALL", "Distinction", "Merit", "Pass", "Needs Remediation")
                    tiers.forEach { tier ->
                        val isSelected = filterTier.equals(tier, ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) CyberCyan else CyberNavyCard,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) CyberCyan else CyberNavyBorder
                            ),
                            modifier = Modifier.clickable { filterTier = tier }
                        ) {
                            Text(
                                text = tier.uppercase(),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) CyberNavyDark else CyberTextSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }

        if (filteredSubmissions.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CyberNavySurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberNavyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No matching submissions in database.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = CyberTextSecondary)
                        )
                    }
                }
            }
        } else {
            items(filteredSubmissions) { sub ->
                SubmissionHistoryCard(submission = sub)
            }
        }
    }
}

@Composable
fun MetricKpiPill(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = CyberNavyCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = CyberTextSecondary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
fun GradeTierBadge(tier: String, modifier: Modifier = Modifier) {
    val (bg, textCol) = when (tier) {
        "Distinction" -> CyberGreen.copy(alpha = 0.2f) to CyberGreen
        "Merit" -> CyberCyan.copy(alpha = 0.2f) to CyberCyan
        "Pass" -> CyberWarningYellow.copy(alpha = 0.2f) to CyberWarningYellow
        else -> CyberThreatRed.copy(alpha = 0.2f) to CyberThreatRed
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bg,
        border = androidx.compose.foundation.BorderStroke(1.dp, textCol),
        modifier = modifier
    ) {
        Text(
            text = tier.uppercase(),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                color = textCol,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        )
    }
}

@Composable
fun SubmissionHistoryCard(submission: StudentSubmission, modifier: Modifier = Modifier) {
    val dateStr = remember(submission.completedAt) {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        sdf.format(Date(submission.completedAt))
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberNavyBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = submission.studentName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = CyberTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "${submission.studentId} • ${submission.department}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = CyberTextSecondary,
                            fontSize = 11.sp
                        )
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${submission.totalScore} / 100",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = CyberCyan,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = submission.gradeTier,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (submission.gradeTier == "Distinction") CyberGreen else CyberWarningYellow,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            // Scores Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ScoreTag("SQLi", submission.sqliScore)
                ScoreTag("XSS", submission.xssScore)
                ScoreTag("MITM", submission.mitmScore)
                ScoreTag("Crypto", submission.cryptoScore)
                ScoreTag("Phish", submission.phishingScore)
            }

            // Verification hash & timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Hash: ${submission.sandboxVerificationHash}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = CyberTextSecondary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp
                    )
                )
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = CyberTextSecondary,
                        fontSize = 10.sp
                    )
                )
            }

            Text(
                text = submission.instructorRemarks,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = CyberTextPrimary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            )
        }
    }
}

@Composable
fun ScoreTag(label: String, score: Int) {
    Text(
        text = "$label: $score",
        style = MaterialTheme.typography.bodySmall.copy(
            color = if (score >= 15) CyberGreen else CyberWarningYellow,
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp
        )
    )
}
