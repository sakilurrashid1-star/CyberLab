package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.sandbox.CapturedPacket
import com.example.ui.components.FlagBadge
import com.example.ui.components.MitmD3Visualizer
import com.example.ui.components.SandboxBanner
import com.example.ui.components.TerminalWindow
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
fun MitmLabScreen(
    state: UiLabState,
    onConfigChange: (useTls: Boolean, enableHsts: Boolean, sslStrip: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val mitm = state.mitmState
    var selectedPacket by remember { mutableStateOf<CapturedPacket?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("mitm_lab_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SandboxBanner(
                title = "NETWORK INTERCEPTION & MITM SANDBOX",
                subtitle = "Simulates ARP cache poisoning • Packet sniffer & TLS 1.3 / HSTS defense"
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
                        Icon(Icons.Default.Wifi, contentDescription = "MITM", tint = CyberCyan)
                        Text(
                            text = "Lab 3: Man-in-the-Middle (ARP Spoofing & TLS Defense)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = CyberTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Text(
                        text = "Objective: Observe how unencrypted HTTP traffic allows an adversary on Presidency University Wi-Fi to intercept passwords. Test SSL Stripping attacks and defend against them using TLS 1.3 and HSTS (HTTP Strict Transport Security).",
                        style = MaterialTheme.typography.bodySmall.copy(color = CyberTextSecondary)
                    )
                }
            }
        }

        // Flag Badge
        if (mitm.flagUnlocked != null || state.capturedFlags.any { it.contains("MITM") }) {
            item {
                val flag = mitm.flagUnlocked ?: state.capturedFlags.first { it.contains("MITM") }
                FlagBadge(flag = flag)
            }
        }

        // Real-time D3.js Network Traffic & Interception Visualizer
        item {
            MitmD3Visualizer(
                mitmState = mitm,
                onPacketSelected = { selectedPacket = it }
            )
        }

        // Network Topology Diagram
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
                        text = "SIMULATED NETWORK TOPOLOGY (SUBNET 192.168.1.0/24)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyberCyan,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    // Node diagram
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TopologyNode(
                            title = "Student Device",
                            ip = "192.168.1.42",
                            icon = Icons.Default.Computer,
                            tint = CyberCyan
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(2.dp)
                                .background(if (mitm.isCompromised()) CyberThreatRed else CyberGreen)
                        )
                        TopologyNode(
                            title = "Rogue Attacker",
                            ip = "192.168.1.105 (ARP Spoof)",
                            icon = Icons.Default.Router,
                            tint = CyberThreatRed
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(2.dp)
                                .background(if (mitm.isCompromised()) CyberThreatRed else CyberGreen)
                        )
                        TopologyNode(
                            title = "Presidency Server",
                            ip = "portal.presidency.edu",
                            icon = Icons.Default.Security,
                            tint = if (mitm.useTls) CyberGreen else CyberWarningYellow
                        )
                    }

                    // Interception status banner
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (mitm.isCompromised()) CyberThreatRed.copy(alpha = 0.15f) else CyberGreen.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (mitm.isCompromised()) CyberThreatRed else CyberGreen
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (mitm.isCompromised()) Icons.Default.LockOpen else Icons.Default.Lock,
                                contentDescription = "Status",
                                tint = if (mitm.isCompromised()) CyberThreatRed else CyberGreen
                            )
                            Text(
                                text = mitm.statusMessage,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = CyberTextPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
            }
        }

        // Defense / Protocol Configuration Controls
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
                        text = "NETWORK SECURITY CONTROLS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyberCyan,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    // TLS Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Enforce HTTPS (TLS 1.3)", style = MaterialTheme.typography.bodyMedium.copy(color = CyberTextPrimary, fontWeight = FontWeight.Bold))
                            Text("Encrypts payloads using AES-256-GCM and Diffie-Hellman", style = MaterialTheme.typography.bodySmall.copy(color = CyberTextSecondary))
                        }
                        Switch(
                            checked = mitm.useTls,
                            onCheckedChange = { onConfigChange(it, mitm.enableHsts, mitm.isSslStripAttempted) },
                            colors = SwitchDefaults.colors(checkedThumbColor = CyberNavyDark, checkedTrackColor = CyberGreen),
                            modifier = Modifier.testTag("mitm_tls_switch")
                        )
                    }

                    // HSTS Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("HSTS Preloaded (Strict Transport)", style = MaterialTheme.typography.bodyMedium.copy(color = CyberTextPrimary, fontWeight = FontWeight.Bold))
                            Text("Prevents protocol downgrade / SSL Stripping attacks", style = MaterialTheme.typography.bodySmall.copy(color = CyberTextSecondary))
                        }
                        Switch(
                            checked = mitm.enableHsts,
                            onCheckedChange = { onConfigChange(mitm.useTls, it, mitm.isSslStripAttempted) },
                            colors = SwitchDefaults.colors(checkedThumbColor = CyberNavyDark, checkedTrackColor = CyberCyan),
                            modifier = Modifier.testTag("mitm_hsts_switch")
                        )
                    }

                    // SSL Strip Attack Simulation Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Attacker: Launch SSL Strip Downgrade", style = MaterialTheme.typography.bodyMedium.copy(color = CyberThreatRed, fontWeight = FontWeight.Bold))
                            Text("Adversary proxy attempts stripping 301 HTTPS redirects", style = MaterialTheme.typography.bodySmall.copy(color = CyberTextSecondary))
                        }
                        Switch(
                            checked = mitm.isSslStripAttempted,
                            onCheckedChange = { onConfigChange(mitm.useTls, mitm.enableHsts, it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = CyberNavyDark, checkedTrackColor = CyberThreatRed),
                            modifier = Modifier.testTag("mitm_ssl_strip_switch")
                        )
                    }
                }
            }
        }

        // Live Packet Stream (Wireshark-style Packet Sniffer)
        item {
            Text(
                text = "LIVE PACKET INTERCEPTOR (SIMULATED WLAN0)",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = CyberCyan,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        items(mitm.capturedPackets) { packet ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedPacket = packet },
                shape = RoundedCornerShape(10.dp),
                color = if (packet.isCompromised) Color(0xFF2A0D0D) else CyberNavySurface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (packet.isCompromised) CyberThreatRed else CyberNavyBorder
                )
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "#${packet.packetId} • ${packet.timestamp} • ${packet.protocol}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (packet.isCompromised) CyberThreatRed else CyberCyan,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                        Text(
                            text = "${packet.lengthBytes} bytes",
                            style = MaterialTheme.typography.labelSmall.copy(color = CyberTextSecondary)
                        )
                    }
                    Text(
                        text = "${packet.sourceIp} -> ${packet.destIp}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = CyberTextPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = packet.payloadPreview,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (packet.isCompromised) CyberThreatRed else if (packet.isEncrypted) CyberGreen else CyberTextSecondary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        // Packet Inspection Details Modal or Card
        if (selectedPacket != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberNavyDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "DETAILED PACKET INSPECTOR (FRAME #${selectedPacket?.packetId})",
                            style = MaterialTheme.typography.labelSmall.copy(color = CyberCyan, fontWeight = FontWeight.Bold)
                        )
                        selectedPacket?.headers?.forEach { (key, value) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(key, style = MaterialTheme.typography.bodySmall.copy(color = CyberTextSecondary))
                                Text(
                                    value,
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
}


@Composable
fun TopologyNode(
    title: String,
    ip: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.15f))
                .border(1.dp, tint, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = title, tint = tint, modifier = Modifier.size(22.dp))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                color = CyberTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        )
        Text(
            text = ip,
            style = MaterialTheme.typography.bodySmall.copy(
                color = CyberTextSecondary,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
            )
        )
    }
}
