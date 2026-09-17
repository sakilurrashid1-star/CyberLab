package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import com.example.sandbox.DialogueOption
import com.example.sandbox.PhishingEngine
import com.example.ui.components.FlagBadge
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
import com.example.viewmodel.UiLabState

@Composable
fun PhishingLabScreen(
    state: UiLabState,
    onSelectEmail: (Int) -> Unit,
    onToggleRedFlag: (String) -> Unit,
    onReportToSoc: () -> Unit,
    onSelectSocialScenario: (String) -> Unit,
    onSelectDialogueOption: (DialogueOption) -> Unit,
    modifier: Modifier = Modifier
) {
    var subTab by remember { mutableStateOf(0) } // 0: Email Triage & SOC Reporting, 1: Social Engineering Pretexting
    val email = state.currentPhishingEmail
    val isReported = state.reportedPhishingEmails.contains(email.id)

    val currentScenario = PhishingEngine.socialEngScenarios.firstOrNull {
        it.scenarioId == state.selectedScenarioId && it.stepId == state.socialEngStep
    } ?: PhishingEngine.socialEngScenarios.first { it.scenarioId == state.selectedScenarioId }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("phishing_lab_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SandboxBanner(
                title = "PHISHING & SOCIAL ENGINEERING SIMULATION",
                subtitle = "Realistic email forensics • Unicode homoglyphs • Vishing & Physical pretexting • Campus SOC reporting"
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberNavyBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Email, contentDescription = "Phishing", tint = CyberCyan)
                        Text(
                            text = "Lab 5: Human Factors & Social Engineering Defense",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = CyberTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Text(
                        text = "Attackers exploit psychological urgency, authority, and fear rather than software vulnerabilities. In this safe sandbox, inspect raw RFC email authentication headers, identify subtle deception cues, file incident tickets to the Security Operations Center (SOC), and roleplay live vishing and physical pretexting defenses.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = CyberTextSecondary,
                            lineHeight = 18.sp
                        )
                    )
                }
            }
        }

        // Flag Badges
        if (state.capturedFlags.any { it.contains("PHISHING") || it.contains("SOCIAL_ENG") || it.contains("SOC") }) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    state.capturedFlags.filter {
                        it.contains("PHISHING") || it.contains("SOCIAL_ENG") || it.contains("SOC")
                    }.forEach { flag ->
                        FlagBadge(flag = flag)
                    }
                }
            }
        }

        // Primary Module Switcher: Email Forensics vs Social Engineering Pretexting
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberNavySurface, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                PhishTabButton(
                    text = "Email Triage (${state.discoveredRedFlags.size} Flags Found)",
                    isSelected = subTab == 0,
                    onClick = { subTab = 0 },
                    modifier = Modifier.weight(1f)
                )
                PhishTabButton(
                    text = "Pretexting & Vishing Lab",
                    isSelected = subTab == 1,
                    onClick = { subTab = 1 },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        when (subTab) {
            0 -> {
                // Email Scenario Selector Chips
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "SELECT SIMULATED PHISHING CAMPAIGN (${state.selectedPhishingEmailIndex + 1} of ${PhishingEngine.emailScenarios.size}):",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyberCyan,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PhishingEngine.emailScenarios.forEachIndexed { index, scenario ->
                                val isSelected = index == state.selectedPhishingEmailIndex
                                val isScenarioReported = state.reportedPhishingEmails.contains(scenario.id)

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) CyberCyan.copy(alpha = 0.2f) else CyberNavyCard,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) CyberCyan else CyberNavyBorder
                                    ),
                                    modifier = Modifier
                                        .clickable { onSelectEmail(index) }
                                        .testTag("email_scenario_chip_$index")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        if (isScenarioReported) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Reported",
                                                tint = CyberGreen,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                        Text(
                                            text = scenario.category,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (isSelected) CyberCyan else CyberTextPrimary,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Email Client Viewport Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CyberNavyDark),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberNavyBorder)
                    ) {
                        Column {
                            // Subject & Sender Header Box
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = Color(0xFF0F172A),
                                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = email.subject,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            color = CyberThreatRed,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "From: ${email.senderName} <${email.senderEmail}>",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = CyberTextPrimary,
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 11.sp
                                            )
                                        )
                                        Text(
                                            text = email.receivedDate,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = CyberTextSecondary,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                    Text(
                                        text = "Reply-To: ${email.replyTo}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = CyberWarningYellow,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }

                            // Email Headers Inspection Bar (RFC 5321/5322 Forensics)
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = Color(0xFF162032)
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = "AUTHENTICATION-RESULTS (MAIL TRANSFER AGENT):",
                                        style = MaterialTheme.typography.labelSmall.copy(color = CyberCyan, fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "SPF: ${email.spfStatus}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (email.spfStatus.startsWith("PASS")) CyberGreen else CyberThreatRed,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp
                                        )
                                    )
                                    Text(
                                        text = "DKIM: ${email.dkimStatus}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (email.dkimStatus.startsWith("PASS")) CyberGreen else CyberThreatRed,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp
                                        )
                                    )
                                    Text(
                                        text = "DMARC: ${email.dmarcStatus}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (email.dmarcStatus.startsWith("PASS")) CyberGreen else CyberThreatRed,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp
                                        )
                                    )
                                    Text(
                                        text = "Originating IP: ${email.originatingIp}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = CyberTextSecondary,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }

                            // Email Body
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = email.bodyHtml,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = CyberTextPrimary,
                                        lineHeight = 22.sp
                                    )
                                )

                                // Malicious Attachment Alert if present
                                email.attachmentName?.let { attach ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFF2E0F15),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberThreatRed),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(Icons.Default.Attachment, contentDescription = "Attachment", tint = CyberThreatRed)
                                            Column {
                                                Text(
                                                    text = attach,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        color = CyberThreatRed,
                                                        fontWeight = FontWeight.Bold,
                                                        fontFamily = FontFamily.Monospace
                                                    )
                                                )
                                                Text(
                                                    text = "Sandbox Warning: Suspicious file extension detected!",
                                                    style = MaterialTheme.typography.bodySmall.copy(color = CyberTextSecondary, fontSize = 10.sp)
                                                )
                                            }
                                        }
                                    }
                                }

                                // Malicious Destination Link inspection if present
                                email.destinationUrl?.let { url ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = CyberNavyCard,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberWarningYellow.copy(alpha = 0.5f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(Icons.Default.Link, contentDescription = "Link", tint = CyberWarningYellow)
                                            Column {
                                                Text(
                                                    text = "Inspected Target Destination:",
                                                    style = MaterialTheme.typography.labelSmall.copy(color = CyberWarningYellow, fontWeight = FontWeight.Bold)
                                                )
                                                Text(
                                                    text = url,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        color = CyberTextPrimary,
                                                        fontFamily = FontFamily.Monospace,
                                                        fontSize = 11.sp
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Incident Response / SOC Action Bar
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = CyberNavySurface,
                                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyberNavyBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isReported) "STATUS: INCIDENT REPORTED" else "INCIDENT ACTION REQUIRED",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (isReported) CyberGreen else CyberCyan,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Text(
                                            text = if (isReported) "Campus SOC ticket logged • Gateway firewall rule active" else "Report this threat to the Presidency University SOC",
                                            style = MaterialTheme.typography.bodySmall.copy(color = CyberTextSecondary, fontSize = 10.sp)
                                        )
                                    }

                                    Button(
                                        onClick = onReportToSoc,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isReported) CyberGreen else CyberThreatRed,
                                            contentColor = CyberNavyDark
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.testTag("report_phishing_button")
                                    ) {
                                        Icon(
                                            imageVector = if (isReported) Icons.Default.Check else Icons.Default.Report,
                                            contentDescription = "Report",
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isReported) "Reported" else "Report to SOC",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Red Flags Checklist for Selected Email
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "IDENTIFY THE INDICATORS OF COMPROMISE (IOCs):",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyberCyan,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        val flagsFoundThisEmail = email.redFlags.count { state.discoveredRedFlags.contains(it.id) }
                        Text(
                            text = "$flagsFoundThisEmail / ${email.redFlags.size} Discovered",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (flagsFoundThisEmail == email.redFlags.size) CyberGreen else CyberWarningYellow,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                items(email.redFlags) { flagItem ->
                    val isChecked = state.discoveredRedFlags.contains(flagItem.id)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isChecked) CyberNavyCard else CyberNavySurface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isChecked) CyberGreen else CyberNavyBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleRedFlag(flagItem.id) }
                            .testTag("red_flag_item_${flagItem.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { onToggleRedFlag(flagItem.id) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = CyberGreen,
                                    uncheckedColor = CyberNavyBorder,
                                    checkmarkColor = CyberNavyDark
                                )
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = flagItem.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (isChecked) CyberGreen else CyberTextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = flagItem.explanation,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = CyberTextSecondary,
                                        fontSize = 11.sp,
                                        lineHeight = 16.sp
                                    )
                                )
                            }
                        }
                    }
                }

                // Educational SOC Advice Card
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CyberNavySurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Security, contentDescription = "SOC", tint = CyberCyan, modifier = Modifier.size(16.dp))
                                Text(
                                    text = "SOC DEFENDER PLAYBOOK:",
                                    style = MaterialTheme.typography.labelSmall.copy(color = CyberCyan, fontWeight = FontWeight.Bold)
                                )
                            }
                            Text(
                                text = email.socReportRecommendation,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = CyberTextPrimary,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )
                            )
                        }
                    }
                }
            }

            1 -> {
                // Social Engineering Pretexting & Vishing Simulation
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "SELECT SOCIAL ENGINEERING SCENARIO:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyberCyan,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        val scenarios = listOf(
                            "VISHING_IT_DESK" to "Voice Phishing (Vishing Call)",
                            "PHYSICAL_TAILGATING" to "Physical Tailgating",
                            "DISCORD_WATERING_HOLE" to "Discord Watering Hole"
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            scenarios.forEach { (id, label) ->
                                val isSelected = state.selectedScenarioId == id
                                val isDone = state.completedSocialScenarios.contains(id)

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) CyberCyan.copy(alpha = 0.2f) else CyberNavyCard,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) CyberCyan else CyberNavyBorder
                                    ),
                                    modifier = Modifier
                                        .clickable { onSelectSocialScenario(id) }
                                        .testTag("scenario_chip_$id")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        if (isDone) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Completed",
                                                tint = CyberGreen,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (isSelected) CyberCyan else CyberTextPrimary,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Pretexting Simulation Dialogue Box
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (currentScenario.scenarioType.contains("Voice")) Icons.Default.PhoneInTalk else Icons.Default.Warning,
                                    contentDescription = "Pretext",
                                    tint = CyberThreatRed
                                )
                                Column {
                                    Text(
                                        text = currentScenario.title,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            color = CyberTextPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = "${currentScenario.scenarioType} • Step ${currentScenario.stepId}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = CyberCyan,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }

                            // Caller / Adversary Speech Bubble
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = CyberNavyDark,
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyberThreatRed.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = currentScenario.speaker,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = CyberThreatRed,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = currentScenario.message,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = CyberTextPrimary,
                                            lineHeight = 20.sp
                                        )
                                    )
                                }
                            }

                            Text(
                                text = "HOW DO YOU RESPOND AS A PRESIDENCY STUDENT?",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CyberCyan,
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            // Dialogue Response Choices
                            currentScenario.options.forEachIndexed { index, option ->
                                OutlinedButton(
                                    onClick = { onSelectDialogueOption(option) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("dialogue_option_$index"),
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberNavyBorder)
                                ) {
                                    Text(
                                        text = option.text,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = CyberTextPrimary,
                                            lineHeight = 18.sp
                                        ),
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }
                            }

                            // Feedback Display if student chose an option
                            if (state.socialEngFeedback != null) {
                                val isSecureFeedback = state.socialEngFeedback.contains("SECURE") || state.socialEngFeedback.contains("EXCELLENT")
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSecureFeedback) CyberGreen.copy(alpha = 0.15f) else CyberThreatRed.copy(alpha = 0.15f),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSecureFeedback) CyberGreen else CyberThreatRed
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = if (isSecureFeedback) "DEFENSIVE POSTURE: SUCCESS" else "DEFENSIVE POSTURE: FAILED",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (isSecureFeedback) CyberGreen else CyberThreatRed,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = state.socialEngFeedback,
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
                }
            }
        }
    }
}

@Composable
private fun PhishTabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) CyberCyan else Color.Transparent,
        modifier = modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
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
