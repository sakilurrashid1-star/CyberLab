package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sandbox.CryptoEngine
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
fun CryptoLabScreen(
    state: UiLabState,
    onInputTextChange: (String) -> Unit,
    onCaesarShiftChange: (Int) -> Unit,
    onTargetHashChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val analysis = state.cryptoAnalysis
    val caesarCiphered = CryptoEngine.caesarCipher(state.cryptoInput, state.caesarShift)
    val xorCiphered = CryptoEngine.xorEncrypt(state.cryptoInput, state.xorKey)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("crypto_lab_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SandboxBanner(
                title = "CRYPTOGRAPHY & HASH CRACKING SANDBOX",
                subtitle = "Classical ciphers • Cryptographic hashing • Rainbow table dictionary attack"
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
                        Icon(Icons.Default.Key, contentDescription = "Crypto", tint = CyberCyan)
                        Text(
                            text = "Lab 4: Modern Cryptography & Hash Cracking",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = CyberTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Text(
                        text = "Objective: Understand how cryptographic hashing functions produce one-way digests (MD5, SHA-256). Crack an unsalted MD5 hash using an in-memory dictionary attack, and observe how cryptographic salts prevent rainbow table lookups.",
                        style = MaterialTheme.typography.bodySmall.copy(color = CyberTextSecondary)
                    )
                }
            }
        }

        // Flag Badge
        if (analysis.flagUnlocked != null || state.capturedFlags.any { it.contains("CRYPTO") }) {
            item {
                val flag = analysis.flagUnlocked ?: state.capturedFlags.first { it.contains("CRYPTO") }
                FlagBadge(flag = flag)
            }
        }

        // Part 1: Interactive Hash Generation & Salting
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
                    Text(
                        text = "1. CRYPTOGRAPHIC HASH GENERATOR",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyberCyan,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    OutlinedTextField(
                        value = state.cryptoInput,
                        onValueChange = onInputTextChange,
                        label = { Text("Plaintext String / Password") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("crypto_plaintext_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = CyberNavyBorder,
                            focusedTextColor = CyberTextPrimary,
                            unfocusedTextColor = CyberTextPrimary
                        ),
                        singleLine = true
                    )

                    // MD5 Output
                    HashDisplayRow(
                        algorithm = "MD5 (128-bit) - BROKEN",
                        hash = analysis.md5Hash,
                        isVulnerable = true,
                        explanation = "High collision risk & vulnerable to rainbow tables."
                    )

                    // SHA-256 Output
                    HashDisplayRow(
                        algorithm = "SHA-256 (256-bit) - SECURE INTEGRITY",
                        hash = analysis.sha256Hash,
                        isVulnerable = false,
                        explanation = "Standard for TLS, digital signatures, and blockchain."
                    )

                    // Salted Hash Output
                    HashDisplayRow(
                        algorithm = "Salted SHA-256 (Salt: ${analysis.saltUsed})",
                        hash = analysis.saltedHash,
                        isVulnerable = false,
                        explanation = "Random per-user salt defeats precomputed rainbow tables completely."
                    )
                }
            }
        }

        // Part 2: Live Dictionary Attack / Rainbow Table Hash Cracker
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
                    Text(
                        text = "2. DICTIONARY ATTACK / HASH CRACKER CHALLENGE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyberCyan,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = "Adversaries use precomputed wordlists (RockYou, SecLists) to invert unsalted hashes in milliseconds. Select a captured student hash below to run the sandbox cracker:",
                        style = MaterialTheme.typography.bodySmall.copy(color = CyberTextSecondary)
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PayloadChip(
                            label = "Hash: presidency123",
                            onClick = { onTargetHashChange(CryptoEngine.calculateMd5("presidency123")) }
                        )
                        PayloadChip(
                            label = "Hash: admin2026",
                            onClick = { onTargetHashChange(CryptoEngine.calculateMd5("admin2026")) }
                        )
                        PayloadChip(
                            label = "Hash: password",
                            onClick = { onTargetHashChange(CryptoEngine.calculateMd5("password")) }
                        )
                        PayloadChip(
                            label = "Hash: cybersecurity",
                            onClick = { onTargetHashChange(CryptoEngine.calculateMd5("cybersecurity")) }
                        )
                    }

                    OutlinedTextField(
                        value = state.targetHashToCrack,
                        onValueChange = onTargetHashChange,
                        label = { Text("Target Hash to Crack (MD5 or SHA-256)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("target_hash_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = CyberNavyBorder,
                            focusedTextColor = CyberTextPrimary,
                            unfocusedTextColor = CyberTextPrimary
                        ),
                        singleLine = true
                    )

                    if (analysis.isCracked && analysis.crackedPlaintext != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CyberGreen.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberGreen),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Cracked", tint = CyberGreen)
                                Column {
                                    Text(
                                        text = "HASH CRACKED IN 0.002s!",
                                        style = MaterialTheme.typography.labelMedium.copy(color = CyberGreen, fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Recovered Plaintext: \"${analysis.crackedPlaintext}\"",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = CyberTextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    )
                                    Text(
                                        text = "Why this worked: Unsalted MD5 matched standard university dictionary entry.",
                                        style = MaterialTheme.typography.bodySmall.copy(color = CyberTextSecondary, fontSize = 11.sp)
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "Target hash not found in current fast dictionary wordlist.",
                            style = MaterialTheme.typography.bodySmall.copy(color = CyberWarningYellow)
                        )
                    }
                }
            }
        }

        // Part 3: Classical Ciphers (Caesar & XOR)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberNavyBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "3. CLASSICAL CIPHERS (CAESAR SHIFT)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyberCyan,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Caesar Key Shift: +${state.caesarShift}", style = MaterialTheme.typography.bodyMedium.copy(color = CyberTextPrimary))
                        Text("ROT${state.caesarShift}", style = MaterialTheme.typography.labelMedium.copy(color = CyberCyan, fontWeight = FontWeight.Bold))
                    }

                    Slider(
                        value = state.caesarShift.toFloat(),
                        onValueChange = { onCaesarShiftChange(it.toInt()) },
                        valueRange = 1f..25f,
                        steps = 23,
                        colors = SliderDefaults.colors(thumbColor = CyberCyan, activeTrackColor = CyberCyan),
                        modifier = Modifier.testTag("caesar_slider")
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CyberNavyDark,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberNavyBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Ciphertext Output:", style = MaterialTheme.typography.labelSmall.copy(color = CyberTextSecondary))
                            Text(
                                text = caesarCiphered,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = CyberCyan,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HashDisplayRow(
    algorithm: String,
    hash: String,
    isVulnerable: Boolean,
    explanation: String
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = CyberNavyCard,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isVulnerable) CyberThreatRed.copy(alpha = 0.5f) else CyberGreen.copy(alpha = 0.5f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = algorithm,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isVulnerable) CyberThreatRed else CyberGreen,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Text(
                text = hash,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = CyberTextPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
            )
            Text(
                text = explanation,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = CyberTextSecondary,
                    fontSize = 10.sp
                )
            )
        }
    }
}
