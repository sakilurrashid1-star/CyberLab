package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sandbox.SqlTokenType
import com.example.ui.components.FlagBadge
import com.example.ui.components.SandboxBanner
import com.example.ui.components.TerminalWindow
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SqliLabScreen(
    state: UiLabState,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onToggleSafeMode: (Boolean) -> Unit,
    onExecuteQuery: () -> Unit,
    modifier: Modifier = Modifier
) {
    val result = state.sqliResult

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("sqli_lab_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SandboxBanner(
                title = "SQL INJECTION ISOLATED SANDBOX",
                subtitle = "Simulates in-memory relational parser • Demonstrates Prepared Statement remediation"
            )
        }

        // Lab Objectives
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
                        Icon(Icons.Default.BugReport, contentDescription = "Lab", tint = CyberCyan)
                        Text(
                            text = "Lab 1: SQL Injection (Authentication Bypass & Defense)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = CyberTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Text(
                        text = "Objective: Demonstrate how string concatenation in SQL alters command logic (e.g. ' OR 1=1 --), and how Parameterized Prepared Statements neutralize the attack by binding untrusted input as literal values.",
                        style = MaterialTheme.typography.bodySmall.copy(color = CyberTextSecondary)
                    )
                }
            }
        }

        // Flag if solved
        if (result?.flagUnlocked != null || state.capturedFlags.any { it.contains("SQLI") }) {
            item {
                val flag = result?.flagUnlocked ?: state.capturedFlags.first { it.contains("SQLI") }
                FlagBadge(flag = flag)
            }
        }

        // Interactive Target: Presidency University Student Portal Login
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
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Presidency Portal Mock Login",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = CyberCyan,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        // Safe Mode Toggle
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (state.sqliSafeMode) "Prepared Stmts (Safe)" else "Concatenation (Vuln)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (state.sqliSafeMode) CyberGreen else CyberThreatRed,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Switch(
                                checked = state.sqliSafeMode,
                                onCheckedChange = onToggleSafeMode,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = CyberNavyDark,
                                    checkedTrackColor = CyberGreen,
                                    uncheckedThumbColor = CyberNavyDark,
                                    uncheckedTrackColor = CyberThreatRed
                                ),
                                modifier = Modifier.testTag("sqli_safe_mode_switch")
                            )
                        }
                    }

                    // Quick Payload Chips
                    Text(
                        text = "QUICK ATTACK PAYLOADS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyberTextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PayloadChip(
                            label = "' OR '1'='1' --",
                            onClick = {
                                onUsernameChange("admin' OR '1'='1' --")
                                onPasswordChange("anypass")
                            }
                        )
                        PayloadChip(
                            label = "' UNION SELECT secret...",
                            onClick = {
                                onUsernameChange("' UNION SELECT id, username, secret, dept FROM users --")
                                onPasswordChange("any")
                            }
                        )
                        PayloadChip(
                            label = "admin' --",
                            onClick = {
                                onUsernameChange("pu_admin' --")
                                onPasswordChange("any")
                            }
                        )
                        PayloadChip(
                            label = "Normal User",
                            onClick = {
                                onUsernameChange("student_alice")
                                onPasswordChange("AlicePass2026!")
                            }
                        )
                    }

                    // Input fields
                    OutlinedTextField(
                        value = state.sqliUsername,
                        onValueChange = onUsernameChange,
                        label = { Text("Username / Student ID") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sqli_username_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = CyberNavyBorder,
                            focusedTextColor = CyberTextPrimary,
                            unfocusedTextColor = CyberTextPrimary
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = state.sqliPassword,
                        onValueChange = onPasswordChange,
                        label = { Text("Password") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sqli_password_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = CyberNavyBorder,
                            focusedTextColor = CyberTextPrimary,
                            unfocusedTextColor = CyberTextPrimary
                        ),
                        singleLine = true
                    )

                    Button(
                        onClick = onExecuteQuery,
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = CyberNavyDark),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sqli_execute_button")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Run")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Execute SQL in Sandbox Engine", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Live AST Token & Query Breakdown
        if (result != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberNavyBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "EXECUTED SQL QUERY & PARSER TOKENS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyberCyan,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        TerminalWindow(
                            title = if (state.sqliSafeMode) "Prepared Statement" else "Raw Concatenated String",
                            content = result.executedSql,
                            tintColor = if (state.sqliSafeMode) CyberGreen else if (result.isBypassed) CyberThreatRed else CyberCyan
                        )

                        // Tokens
                        Text(
                            text = "Parsed AST Tokens:",
                            style = MaterialTheme.typography.labelSmall.copy(color = CyberTextSecondary)
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            result.tokens.forEach { token ->
                                val (bg, textCol) = when (token.type) {
                                    SqlTokenType.KEYWORD -> CyberNavyCard to CyberCyan
                                    SqlTokenType.IDENTIFIER -> CyberNavyCard to CyberTextPrimary
                                    SqlTokenType.LITERAL -> Color(0xFF1E293B) to CyberGreen
                                    SqlTokenType.OPERATOR -> CyberNavyCard to CyberWarningYellow
                                    SqlTokenType.COMMENT -> CyberNavyCard to CyberTextSecondary
                                    SqlTokenType.INJECTED -> Color(0xFF450A0A) to CyberThreatRed
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = bg,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, textCol.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = token.text,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = textCol,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }

                        // Execution Outcome
                        if (result.isBypassed) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = CyberThreatRed.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyberThreatRed)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.BugReport, contentDescription = "Vuln", tint = CyberThreatRed)
                                    Column {
                                        Text(
                                            text = "VULNERABILITY EXPLOITED: Authentication Bypassed!",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                color = CyberThreatRed,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Text(
                                            text = "The injected condition evaluated to TRUE, dumping records without valid credentials.",
                                            style = MaterialTheme.typography.bodySmall.copy(color = CyberTextPrimary)
                                        )
                                    }
                                }
                            }
                        } else if (state.sqliSafeMode) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = CyberGreen.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyberGreen)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.Security, contentDescription = "Safe", tint = CyberGreen)
                                    Column {
                                        Text(
                                            text = "REMEDIATION VERIFIED: Parameterized Query Defended Database",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                color = CyberGreen,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Text(
                                            text = "The database driver compiled the query tree beforehand. Input was treated strictly as a string literal.",
                                            style = MaterialTheme.typography.bodySmall.copy(color = CyberTextPrimary)
                                        )
                                    }
                                }
                            }
                        }

                        // Returned DB Records
                        Text(
                            text = "Returned Database Rows (${result.returnedRecords.size}):",
                            style = MaterialTheme.typography.labelSmall.copy(color = CyberTextSecondary)
                        )
                        if (result.returnedRecords.isEmpty()) {
                            Text(
                                text = result.errorMessage ?: "No records returned (Authentication Rejected).",
                                style = MaterialTheme.typography.bodySmall.copy(color = CyberWarningYellow)
                            )
                        } else {
                            result.returnedRecords.forEach { row ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    color = CyberNavyDark,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberNavyBorder)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "${row["username"]} (${row["role"]})",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = CyberCyan,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                            Text(
                                                text = "Dept: ${row["dept"]}",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = CyberTextSecondary,
                                                    fontSize = 10.sp
                                                )
                                            )
                                        }
                                        Text(
                                            text = "Secret: ${row["secret"]}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = if (result.isBypassed) CyberThreatRed else CyberGreen,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.SemiBold
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
fun PayloadChip(label: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = CyberNavyCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberNavyBorder),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodySmall.copy(
                color = CyberCyan,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp
            )
        )
    }
}
