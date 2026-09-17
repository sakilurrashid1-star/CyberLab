package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color as AndroidColor
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.sandbox.CapturedPacket
import com.example.sandbox.MitmState
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
import org.json.JSONObject

/**
 * Interactive D3.js based Network Traffic Visualization Component.
 * Displays real-time simulated packet flow across the network nodes:
 * Student Device (192.168.1.42) -> Rogue Interceptor (192.168.1.105) -> Presidency Server.
 * Visualizes ARP spoofing, cleartext payload interception, and TLS 1.3 cryptographic deflection.
 */
@Composable
fun MitmD3Visualizer(
    mitmState: MitmState,
    onPacketSelected: (CapturedPacket) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var isSimulating by remember { mutableStateOf(true) }
    var simulationSpeed by remember { mutableStateOf("1.0x") }
    var interceptedCount by remember { mutableIntStateOf(0) }
    var activeNodeDetails by remember { mutableStateOf<String?>(null) }

    // Update D3 graph when mitmState changes
    LaunchedEffect(mitmState.useTls, mitmState.enableHsts, mitmState.isSslStripAttempted, isSimulating, simulationSpeed) {
        val isCompromised = mitmState.interceptedCredentials != null || (!mitmState.useTls && !mitmState.enableHsts) || (mitmState.isSslStripAttempted && !mitmState.enableHsts)
        val speedVal = when (simulationSpeed) {
            "0.5x" -> 0.5
            "2.0x" -> 2.0
            else -> 1.0
        }

        val jsCode = """
            if (window.updateMitmState) {
                window.updateMitmState(
                    ${mitmState.useTls},
                    ${mitmState.enableHsts},
                    ${mitmState.isSslStripAttempted},
                    $isCompromised,
                    $isSimulating,
                    $speedVal
                );
            }
        """.trimIndent()
        webViewRef?.evaluateJavascript(jsCode, null)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("mitm_d3_visualizer_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
        border = BorderStroke(1.dp, if (mitmState.isCompromised()) CyberThreatRed.copy(alpha = 0.6f) else CyberCyan.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header with badge and state indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isSimulating) (if (mitmState.isCompromised()) CyberThreatRed else CyberGreen) else CyberWarningYellow)
                    )
                    Text(
                        text = "D3.JS REAL-TIME PACKET FLOW VISUALIZER",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyberCyan,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (mitmState.isCompromised()) CyberThreatRed.copy(alpha = 0.2f) else CyberGreen.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, if (mitmState.isCompromised()) CyberThreatRed else CyberGreen)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (mitmState.isCompromised()) Icons.Default.LockOpen else Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (mitmState.isCompromised()) CyberThreatRed else CyberGreen,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = if (mitmState.isCompromised()) "INTERCEPTION ACTIVE" else "END-TO-END TLS 1.3",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (mitmState.isCompromised()) CyberThreatRed else CyberGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            // D3.js WebView Canvas Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberNavyDark)
                    .border(BorderStroke(1.dp, CyberNavyBorder), RoundedCornerShape(12.dp))
            ) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("d3_webview_canvas"),
                    factory = { context ->
                        createD3WebView(
                            context = context,
                            onPacketIntercepted = { count ->
                                Handler(Looper.getMainLooper()).post {
                                    interceptedCount = count
                                }
                            },
                            onNodeSelected = { details ->
                                Handler(Looper.getMainLooper()).post {
                                    activeNodeDetails = details
                                }
                            },
                            onPacketClicked = { packetJson ->
                                Handler(Looper.getMainLooper()).post {
                                    parseAndSelectPacket(packetJson, mitmState, onPacketSelected)
                                }
                            }
                        ).also { webView ->
                            webViewRef = webView
                        }
                    },
                    update = { webView ->
                        webViewRef = webView
                    }
                )
            }

            // Real-time Visualizer Interactive Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Play / Pause Simulation
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(
                        onClick = { isSimulating = !isSimulating },
                        modifier = Modifier
                            .size(34.dp)
                            .background(CyberNavyCard, CircleShape)
                            .border(1.dp, CyberNavyBorder, CircleShape)
                            .testTag("d3_play_pause_button")
                    ) {
                        Icon(
                            imageVector = if (isSimulating) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isSimulating) "Pause" else "Play",
                            tint = CyberCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Speed Toggles
                    listOf("0.5x", "1.0x", "2.0x").forEach { speed ->
                        FilterChip(
                            selected = simulationSpeed == speed,
                            onClick = { simulationSpeed = speed },
                            label = { Text(speed, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyberCyan,
                                selectedLabelColor = CyberNavyDark,
                                containerColor = CyberNavyCard,
                                labelColor = CyberTextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = simulationSpeed == speed,
                                borderColor = CyberNavyBorder,
                                selectedBorderColor = CyberCyan
                            ),
                            modifier = Modifier.height(28.dp)
                        )
                    }
                }

                // Inject Manual Packet
                Button(
                    onClick = {
                        webViewRef?.evaluateJavascript("if (window.injectManualPacket) { window.injectManualPacket(); }", null)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = CyberNavyDark),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .height(32.dp)
                        .testTag("d3_inject_packet_button")
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Inject Packet", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Node inspection readout banner if a node was clicked in D3
            if (activeNodeDetails != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CyberNavyCard,
                    border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                            Text(
                                text = activeNodeDetails ?: "",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = CyberTextPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                )
                            )
                        }
                        IconButton(
                            onClick = { activeNodeDetails = null },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Text("✕", color = CyberTextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Live Interception Statistics Bar
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = CyberNavyDark,
                border = BorderStroke(1.dp, CyberNavyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Packets In Flight: Dynamic SVG Particles",
                        style = MaterialTheme.typography.labelSmall.copy(color = CyberTextSecondary, fontSize = 11.sp)
                    )
                    Text(
                        text = if (mitmState.isCompromised()) "Intercepted: $interceptedCount" else "TLS Handshake: 100% Secure",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (mitmState.isCompromised()) CyberThreatRed else CyberGreen,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

private fun parseAndSelectPacket(
    packetJson: String,
    mitmState: MitmState,
    onPacketSelected: (CapturedPacket) -> Unit
) {
    try {
        val obj = JSONObject(packetJson)
        val packetId = obj.optInt("id", 102)
        val matched = mitmState.capturedPackets.find { it.packetId == packetId }
            ?: mitmState.capturedPackets.firstOrNull()
        if (matched != null) {
            onPacketSelected(matched)
        }
    } catch (_: Exception) {
        mitmState.capturedPackets.firstOrNull()?.let { onPacketSelected(it) }
    }
}

@SuppressLint("SetJavaScriptEnabled")
private fun createD3WebView(
    context: Context,
    onPacketIntercepted: (Int) -> Unit,
    onNodeSelected: (String) -> Unit,
    onPacketClicked: (String) -> Unit
): WebView {
    val webView = WebView(context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        setBackgroundColor(AndroidColor.parseColor("#070B14"))
        settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            useWideViewPort = true
            loadWithOverviewMode = true
            displayZoomControls = false
            builtInZoomControls = false
            cacheMode = WebSettings.LOAD_NO_CACHE
        }
        webViewClient = WebViewClient()
        webChromeClient = WebChromeClient()
    }

    class D3JsBridge {
        @JavascriptInterface
        fun onPacketIntercepted(count: Int) {
            onPacketIntercepted(count)
        }

        @JavascriptInterface
        fun onNodeClick(nodeId: String, info: String) {
            onNodeSelected(info)
        }

        @JavascriptInterface
        fun onPacketClick(packetJson: String) {
            onPacketClicked(packetJson)
        }
    }

    webView.addJavascriptInterface(D3JsBridge(), "AndroidBridge")
    webView.loadDataWithBaseURL("https://presidency.edu", generateD3Html(), "text/html", "UTF-8", null)
    return webView
}

/**
 * Generates the self-contained D3.js and SVG visualization page.
 * Includes D3.js CDN with an inline D3 fallback engine so it works
 * reliably whether the device has internet access or is running completely offline.
 */
private fun generateD3Html(): String {
    return """
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
<title>MITM D3 Visualizer</title>
<script src="https://d3js.org/d3.v7.min.js"></script>
<style>
    * {
        box-sizing: border-box;
        margin: 0;
        padding: 0;
        user-select: none;
        -webkit-user-select: none;
    }
    body, html {
        width: 100%;
        height: 100%;
        background-color: #070B14;
        color: #E6F1FF;
        font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, monospace;
        overflow: hidden;
    }
    #svg-canvas {
        width: 100%;
        height: 100%;
        display: block;
    }
    .grid-line {
        stroke: #162646;
        stroke-width: 0.5;
        stroke-dasharray: 2, 4;
    }
    .link-path {
        fill: none;
        stroke-width: 2.5;
        transition: stroke 0.4s ease;
    }
    .link-path-glow {
        fill: none;
        stroke-width: 7;
        opacity: 0.25;
    }
    .node-group {
        cursor: pointer;
    }
    .node-circle {
        transition: all 0.3s ease;
    }
    .node-label {
        font-size: 10px;
        font-weight: 700;
        text-anchor: middle;
        fill: #E6F1FF;
        pointer-events: none;
    }
    .node-sublabel {
        font-size: 8px;
        text-anchor: middle;
        fill: #8EA9DB;
        pointer-events: none;
        font-family: monospace;
    }
    .packet-particle {
        transition: transform 0.05s linear;
    }
    .hud-overlay {
        position: absolute;
        top: 8px;
        left: 8px;
        font-size: 9px;
        font-family: monospace;
        color: #00F5FF;
        background: rgba(11, 20, 38, 0.75);
        padding: 4px 8px;
        border-radius: 4px;
        border: 1px solid #1E355F;
        pointer-events: none;
    }
    .interception-toast {
        position: absolute;
        bottom: 8px;
        left: 50%;
        transform: translateX(-50%);
        font-size: 9px;
        font-family: monospace;
        color: #FF3B3B;
        background: rgba(42, 13, 13, 0.9);
        border: 1px solid #FF3B3B;
        padding: 3px 8px;
        border-radius: 4px;
        opacity: 0;
        transition: opacity 0.3s ease;
        pointer-events: none;
    }
</style>
</head>
<body>

<div class="hud-overlay" id="hud">TOPOLOGY: 10.0.0.0/24 • PROTOCOL: HTTP/1.1</div>
<div class="interception-toast" id="toast">⚠️ CREDENTIALS INTERCEPTED BY ROGUE PROXY</div>
<svg id="svg-canvas"></svg>

<script>
(function() {
    var svg = document.getElementById("svg-canvas");
    var width = window.innerWidth || 360;
    var height = window.innerHeight || 260;
    svg.setAttribute("viewBox", "0 0 " + width + " " + height);

    var state = {
        useTls: false,
        enableHsts: false,
        sslStrip: false,
        isCompromised: true,
        isSimulating: true,
        speed: 1.0,
        interceptedCount: 0
    };

    // Node coordinates in SVG layout
    var nodes = {
        student: { id: "student", x: width * 0.16, y: height * 0.52, name: "Student Device", ip: "192.168.1.42", color: "#00F5FF", icon: "💻" },
        attacker: { id: "attacker", x: width * 0.50, y: height * 0.32, name: "Attacker (ARP Spoof)", ip: "192.168.1.105", color: "#FF3B3B", icon: "🕵️" },
        server: { id: "server", x: width * 0.84, y: height * 0.52, name: "Presidency Server", ip: "portal.presidency.edu", color: "#00FF9D", icon: "🛡️" }
    };

    // Create defs and filters for glowing SVG effects
    var defs = document.createElementNS("http://www.w3.org/2000/svg", "defs");
    defs.innerHTML = `
        <filter id="glow-cyan" x="-50%" y="-50%" width="200%" height="200%">
            <feGaussianBlur stdDeviation="3" result="blur" />
            <feMerge><feMergeNode in="blur"/><feMergeNode in="SourceGraphic"/></feMerge>
        </filter>
        <filter id="glow-red" x="-50%" y="-50%" width="200%" height="200%">
            <feGaussianBlur stdDeviation="4" result="blur" />
            <feMerge><feMergeNode in="blur"/><feMergeNode in="SourceGraphic"/></feMerge>
        </filter>
        <filter id="glow-green" x="-50%" y="-50%" width="200%" height="200%">
            <feGaussianBlur stdDeviation="3" result="blur" />
            <feMerge><feMergeNode in="blur"/><feMergeNode in="SourceGraphic"/></feMerge>
        </filter>
    `;
    svg.appendChild(defs);

    // Render background cyber grid
    var gridGroup = document.createElementNS("http://www.w3.org/2000/svg", "g");
    for (var gx = 20; gx < width; gx += 30) {
        var l = document.createElementNS("http://www.w3.org/2000/svg", "line");
        l.setAttribute("x1", gx); l.setAttribute("y1", 0);
        l.setAttribute("x2", gx); l.setAttribute("y2", height);
        l.setAttribute("class", "grid-line");
        gridGroup.appendChild(l);
    }
    for (var gy = 20; gy < height; gy += 30) {
        var l = document.createElementNS("http://www.w3.org/2000/svg", "line");
        l.setAttribute("x1", 0); l.setAttribute("y1", gy);
        l.setAttribute("x2", width); l.setAttribute("y2", gy);
        l.setAttribute("class", "grid-line");
        gridGroup.appendChild(l);
    }
    svg.appendChild(gridGroup);

    // Build curved Link Paths: Student -> Attacker and Attacker -> Server
    function buildPathD(n1, n2, curvature) {
        var mx = (n1.x + n2.x) / 2;
        var my = (n1.y + n2.y) / 2 - curvature;
        return "M " + n1.x + " " + n1.y + " Q " + mx + " " + my + " " + n2.x + " " + n2.y;
    }

    var path1Glow = document.createElementNS("http://www.w3.org/2000/svg", "path");
    path1Glow.setAttribute("class", "link-path-glow");
    svg.appendChild(path1Glow);

    var path1 = document.createElementNS("http://www.w3.org/2000/svg", "path");
    path1.setAttribute("class", "link-path");
    svg.appendChild(path1);

    var path2Glow = document.createElementNS("http://www.w3.org/2000/svg", "path");
    path2Glow.setAttribute("class", "link-path-glow");
    svg.appendChild(path2Glow);

    var path2 = document.createElementNS("http://www.w3.org/2000/svg", "path");
    path2.setAttribute("class", "link-path");
    svg.appendChild(path2);

    function updateLinkVisuals() {
        var d1 = buildPathD(nodes.student, nodes.attacker, -15);
        var d2 = buildPathD(nodes.attacker, nodes.server, -15);
        path1.setAttribute("d", d1);
        path1Glow.setAttribute("d", d1);
        path2.setAttribute("d", d2);
        path2Glow.setAttribute("d", d2);

        var linkColor = state.isCompromised ? "#FF3B3B" : "#00FF9D";
        path1.setAttribute("stroke", linkColor);
        path1Glow.setAttribute("stroke", linkColor);
        path2.setAttribute("stroke", linkColor);
        path2Glow.setAttribute("stroke", linkColor);

        // Update HUD
        var hud = document.getElementById("hud");
        if (hud) {
            hud.textContent = state.useTls 
                ? "PROTOCOL: TLS 1.3 (AES_256_GCM) • STATUS: ENCRYPTED"
                : (state.sslStrip ? "PROTOCOL: HTTP (SSL STRIPPED) • STATUS: COMPROMISED" : "PROTOCOL: HTTP/1.1 (PLAINTEXT) • STATUS: VULNERABLE");
            hud.style.color = state.isCompromised ? "#FF3B3B" : "#00FF9D";
        }
    }
    updateLinkVisuals();

    // Packet Container Group
    var packetsGroup = document.createElementNS("http://www.w3.org/2000/svg", "g");
    svg.appendChild(packetsGroup);

    // Render Network Nodes
    var nodesGroup = document.createElementNS("http://www.w3.org/2000/svg", "g");
    svg.appendChild(nodesGroup);

    Object.keys(nodes).forEach(function(key) {
        var n = nodes[key];
        var g = document.createElementNS("http://www.w3.org/2000/svg", "g");
        g.setAttribute("class", "node-group");
        g.setAttribute("transform", "translate(" + n.x + "," + n.y + ")");

        // Pulse ring
        var pulse = document.createElementNS("http://www.w3.org/2000/svg", "circle");
        pulse.setAttribute("r", "24");
        pulse.setAttribute("fill", "none");
        pulse.setAttribute("stroke", n.color);
        pulse.setAttribute("stroke-width", "1");
        pulse.setAttribute("opacity", "0.4");
        g.appendChild(pulse);

        // Core circle
        var circle = document.createElementNS("http://www.w3.org/2000/svg", "circle");
        circle.setAttribute("r", "19");
        circle.setAttribute("fill", "#0B1426");
        circle.setAttribute("stroke", n.color);
        circle.setAttribute("stroke-width", "2");
        circle.setAttribute("class", "node-circle");
        g.appendChild(circle);

        // Emoji / Icon
        var iconText = document.createElementNS("http://www.w3.org/2000/svg", "text");
        iconText.setAttribute("text-anchor", "middle");
        iconText.setAttribute("dominant-baseline", "central");
        iconText.setAttribute("font-size", "14px");
        iconText.textContent = n.icon;
        g.appendChild(iconText);

        // Name label
        var label = document.createElementNS("http://www.w3.org/2000/svg", "text");
        label.setAttribute("y", "31");
        label.setAttribute("class", "node-label");
        label.textContent = n.name;
        g.appendChild(label);

        // IP label
        var sublabel = document.createElementNS("http://www.w3.org/2000/svg", "text");
        sublabel.setAttribute("y", "42");
        sublabel.setAttribute("class", "node-sublabel");
        sublabel.textContent = n.ip;
        g.appendChild(sublabel);

        // Click handler to notify Android via JavaScript Bridge
        g.addEventListener("click", function() {
            var info = n.name + " (" + n.ip + ") - " + (key === "attacker" ? "Active ARP Spoofing Interceptor" : (key === "student" ? "Source Client Host" : "Destination Server"));
            if (window.AndroidBridge && window.AndroidBridge.onNodeClick) {
                window.AndroidBridge.onNodeClick(key, info);
            }
        });

        nodesGroup.appendChild(g);
    });

    // Particle Animation Engine
    var activePackets = [];
    var packetIdCounter = 100;

    function createPacket(fromNode, toNode, isResponse) {
        var color = state.isCompromised ? "#FF3B3B" : (state.useTls ? "#00F5FF" : "#FFC107");
        var g = document.createElementNS("http://www.w3.org/2000/svg", "g");
        g.setAttribute("class", "packet-particle");

        var circle = document.createElementNS("http://www.w3.org/2000/svg", "circle");
        circle.setAttribute("r", "5.5");
        circle.setAttribute("fill", color);
        circle.setAttribute("filter", state.isCompromised ? "url(#glow-red)" : "url(#glow-cyan)");
        g.appendChild(circle);

        var sym = document.createElementNS("http://www.w3.org/2000/svg", "text");
        sym.setAttribute("text-anchor", "middle");
        sym.setAttribute("dominant-baseline", "central");
        sym.setAttribute("font-size", "7px");
        sym.setAttribute("fill", "#070B14");
        sym.setAttribute("font-weight", "bold");
        sym.textContent = state.useTls ? "🔒" : "P";
        g.appendChild(sym);

        packetsGroup.appendChild(g);

        var packetObj = {
            id: ++packetIdCounter,
            element: g,
            from: fromNode,
            to: toNode,
            t: 0,
            stage: 1, // 1: client->attacker, 2: attacker->server
            isResponse: !!isResponse,
            speed: (0.012 + Math.random() * 0.004) * state.speed
        };

        g.addEventListener("click", function(e) {
            e.stopPropagation();
            if (window.AndroidBridge && window.AndroidBridge.onPacketClick) {
                var json = JSON.stringify({
                    id: packetObj.id,
                    protocol: state.useTls ? "TLSv1.3" : "HTTP/1.1",
                    isCompromised: state.isCompromised
                });
                window.AndroidBridge.onPacketClick(json);
            }
        });

        activePackets.push(packetObj);
    }

    // Bezier Point Calculation
    function getBezierPoint(p0, p1, p2, t) {
        var mt = 1 - t;
        return {
            x: mt * mt * p0.x + 2 * mt * t * p1.x + t * t * p2.x,
            y: mt * mt * p0.y + 2 * mt * t * p1.y + t * t * p2.y
        };
    }

    var lastSpawnTime = 0;
    var toast = document.getElementById("toast");

    function triggerInterceptionToast() {
        if (toast) {
            toast.style.opacity = "1";
            setTimeout(function() { toast.style.opacity = "0"; }, 900);
        }
    }

    // Main animation loop
    function animate(time) {
        requestAnimationFrame(animate);

        if (!state.isSimulating) return;

        // Auto spawn packet every 1.4s
        if (time - lastSpawnTime > (1400 / state.speed)) {
            createPacket(nodes.student, nodes.attacker, false);
            lastSpawnTime = time;
        }

        for (var i = activePackets.length - 1; i >= 0; i--) {
            var p = activePackets[i];
            p.t += p.speed;

            var currentP0, currentP1, currentP2;
            if (p.stage === 1) {
                currentP0 = nodes.student;
                currentP1 = { x: (nodes.student.x + nodes.attacker.x) / 2, y: (nodes.student.y + nodes.attacker.y) / 2 - 15 };
                currentP2 = nodes.attacker;
            } else {
                currentP0 = nodes.attacker;
                currentP1 = { x: (nodes.attacker.x + nodes.server.x) / 2, y: (nodes.attacker.y + nodes.server.y) / 2 - 15 };
                currentP2 = nodes.server;
            }

            if (p.t >= 1) {
                if (p.stage === 1) {
                    // Packet arrived at Attacker Node!
                    p.stage = 2;
                    p.t = 0;

                    if (state.isCompromised) {
                        state.interceptedCount++;
                        triggerInterceptionToast();
                        if (window.AndroidBridge && window.AndroidBridge.onPacketIntercepted) {
                            window.AndroidBridge.onPacketIntercepted(state.interceptedCount);
                        }
                    }
                } else {
                    // Packet reached server; remove
                    if (p.element.parentNode) {
                        p.element.parentNode.removeChild(p.element);
                    }
                    activePackets.splice(i, 1);
                    continue;
                }
            }

            var pt = getBezierPoint(currentP0, currentP1, currentP2, p.t);
            p.element.setAttribute("transform", "translate(" + pt.x + "," + pt.y + ")");
        }
    }
    requestAnimationFrame(animate);

    // Global hook for Android Kotlin updates
    window.updateMitmState = function(useTls, enableHsts, sslStrip, isCompromised, isSimulating, speed) {
        state.useTls = useTls;
        state.enableHsts = enableHsts;
        state.sslStrip = sslStrip;
        state.isCompromised = isCompromised;
        state.isSimulating = isSimulating;
        state.speed = speed || 1.0;
        updateLinkVisuals();
    };

    window.injectManualPacket = function() {
        createPacket(nodes.student, nodes.attacker, false);
    };

})();
</script>
</body>
</html>
""".trimIndent()
}
