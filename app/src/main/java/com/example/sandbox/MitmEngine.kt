package com.example.sandbox

data class CapturedPacket(
    val packetId: Int,
    val timestamp: String,
    val sourceIp: String,
    val destIp: String,
    val protocol: String,
    val lengthBytes: Int,
    val payloadPreview: String,
    val isEncrypted: Boolean,
    val isCompromised: Boolean,
    val headers: Map<String, String>
)

data class MitmState(
    val useTls: Boolean = false,
    val enableHsts: Boolean = false,
    val isArpPoisoned: Boolean = true,
    val isSslStripAttempted: Boolean = false,
    val capturedPackets: List<CapturedPacket> = emptyList(),
    val interceptedCredentials: Pair<String, String>? = null,
    val statusMessage: String = "Listening on simulated interface wlan0...",
    val flagUnlocked: String? = null
) {
    fun isCompromised(): Boolean {
        return interceptedCredentials != null || (!useTls && !enableHsts) || (isSslStripAttempted && !enableHsts)
    }
}

object MitmEngine {
    fun simulateTraffic(useTls: Boolean, enableHsts: Boolean, sslStrip: Boolean): MitmState {
        val packets = mutableListOf<CapturedPacket>()
        var interceptedCreds: Pair<String, String>? = null
        var flag: String? = null
        val status: String

        // 1. ARP Spoof / Gateway broadcast
        packets.add(
            CapturedPacket(
                packetId = 101,
                timestamp = "14:22:01.102",
                sourceIp = "192.168.1.105 [Attacker MAC: 00:0c:29:ab]",
                destIp = "192.168.1.42 [Student MAC: a4:83:e7:12]",
                protocol = "ARP",
                lengthBytes = 42,
                payloadPreview = "Reply: 192.168.1.1 (Gateway) is at 00:0c:29:ab (Poisoned Cache)",
                isEncrypted = false,
                isCompromised = true,
                headers = mapOf("Hardware Type" to "Ethernet (0x0001)", "Opcode" to "reply (2)")
            )
        )

        // 2. Client to Server Web Traffic
        if (sslStrip && !enableHsts) {
            // SSL Stripping succeeds because HSTS is absent
            status = "ATTACK SUCCESSFUL: Attacker stripped HTTPS downgrade to plaintext HTTP! Credentials intercepted."
            interceptedCreds = "student_2026" to "PresidencyPass#2026"
            packets.add(
                CapturedPacket(
                    packetId = 102,
                    timestamp = "14:22:02.314",
                    sourceIp = "192.168.1.42 (Student)",
                    destIp = "portal.presidency.edu:80",
                    protocol = "HTTP/1.1",
                    lengthBytes = 486,
                    payloadPreview = "POST /api/login HTTP/1.1\\r\\nHost: portal.presidency.edu\\r\\nContent-Type: application/x-www-form-urlencoded\\r\\n\\r\\nusername=student_2026&password=PresidencyPass#2026",
                    isEncrypted = false,
                    isCompromised = true,
                    headers = mapOf(
                        "Method" to "POST",
                        "Path" to "/api/login",
                        "User-Agent" to "Mozilla/5.0 (Android; Presidency-App)",
                        "Plaintext Credentials" to "username=student_2026&password=PresidencyPass#2026"
                    )
                )
            )
            flag = "FLAG{PU_MITM_SSL_STRIP_EXPLOITED_2026}"
        } else if (useTls || (sslStrip && enableHsts)) {
            // TLS 1.3 active or HSTS forced
            if (sslStrip && enableHsts) {
                status = "DEFENSE ACTIVE: SSL Strip blocked by HSTS preloading! Browser refused HTTP downgrade. Handshake secured with TLS 1.3."
                flag = "FLAG{PU_MITM_TLS13_HSTS_ENFORCED_2026}"
            } else {
                status = "SECURE: TLS 1.3 Encrypted Session. Interceptor only observes ciphertext and ephemeral Diffie-Hellman parameters."
                flag = "FLAG{PU_MITM_TLS13_HSTS_ENFORCED_2026}"
            }

            packets.add(
                CapturedPacket(
                    packetId = 102,
                    timestamp = "14:22:02.150",
                    sourceIp = "192.168.1.42",
                    destIp = "portal.presidency.edu:443",
                    protocol = "TLSv1.3",
                    lengthBytes = 512,
                    payloadPreview = "Client Hello (Cipher Suite: TLS_AES_256_GCM_SHA384, SNI: portal.presidency.edu, KeyShare: X25519)",
                    isEncrypted = false,
                    isCompromised = false,
                    headers = mapOf("Handshake Protocol" to "Client Hello", "Version" to "TLS 1.3 (0x0304)")
                )
            )

            packets.add(
                CapturedPacket(
                    packetId = 103,
                    timestamp = "14:22:02.180",
                    sourceIp = "portal.presidency.edu:443",
                    destIp = "192.168.1.42",
                    protocol = "TLSv1.3",
                    lengthBytes = 1420,
                    payloadPreview = "Server Hello, Change Cipher Spec, Encrypted Extensions, Certificate, Certificate Verify, Finished",
                    isEncrypted = true,
                    isCompromised = false,
                    headers = mapOf("Cipher" to "TLS_AES_256_GCM_SHA384", "Status" to "Mutual Trust Established")
                )
            )

            packets.add(
                CapturedPacket(
                    packetId = 104,
                    timestamp = "14:22:02.240",
                    sourceIp = "192.168.1.42",
                    destIp = "portal.presidency.edu:443",
                    protocol = "TLSv1.3",
                    lengthBytes = 890,
                    payloadPreview = "Application Data: [256-bit AES Ciphertext: e7a1b94c... MAC: 8f3d... (Payload Unreadable to Interceptor)]",
                    isEncrypted = true,
                    isCompromised = false,
                    headers = mapOf("Content Type" to "Application Data (23)", "Security" to "Confidentiality & Integrity Guaranteed")
                )
            )
        } else {
            // Plain HTTP without TLS
            status = "VULNERABLE: Plaintext HTTP over ARP poisoned network. Attacker captured user credentials!"
            interceptedCreds = "student_2026" to "PresidencyPass#2026"
            packets.add(
                CapturedPacket(
                    packetId = 102,
                    timestamp = "14:22:02.314",
                    sourceIp = "192.168.1.42",
                    destIp = "portal.presidency.edu:80",
                    protocol = "HTTP/1.1",
                    lengthBytes = 460,
                    payloadPreview = "POST /login HTTP/1.1\\r\\nHost: presidency.edu\\r\\nusername=student_2026&password=PresidencyPass#2026",
                    isEncrypted = false,
                    isCompromised = true,
                    headers = mapOf(
                        "Payload" to "username=student_2026&password=PresidencyPass#2026",
                        "Security Warning" to "Unencrypted Cleartext on Port 80"
                    )
                )
            )
        }

        return MitmState(
            useTls = useTls,
            enableHsts = enableHsts,
            isArpPoisoned = true,
            isSslStripAttempted = sslStrip,
            capturedPackets = packets,
            interceptedCredentials = interceptedCreds,
            statusMessage = status,
            flagUnlocked = flag
        )
    }
}
