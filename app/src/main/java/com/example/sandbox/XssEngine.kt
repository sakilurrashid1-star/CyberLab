package com.example.sandbox

data class XssResult(
    val rawInput: String,
    val renderedHtml: String,
    val isScriptTriggered: Boolean,
    val isCookieExfiltrated: Boolean,
    val isDomDefaced: Boolean,
    val simulatedAlertText: String? = null,
    val simulatedExfiltratedCookie: String? = null,
    val flagUnlocked: String? = null,
    val sanitizationNote: String
)

data class ForumPost(
    val id: Int,
    val author: String,
    val content: String,
    val isVulnerable: Boolean,
    val timestamp: String
)

object XssEngine {
    private const val MOCK_SESSION_COOKIE = "PU_SSO_SESSION=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdHVkZW50XzIwMjYiLCJyb2xlIjoiU3R1ZGVudCJ9.X8aK29s"

    val defaultForumPosts = listOf(
        ForumPost(1, "Sakilur (Club Sec)", "Welcome to the Presidency University Cyber Security Session! Share your queries below.", false, "10:00 AM"),
        ForumPost(2, "Alice_CSE", "Looking forward to the live sandbox demonstrations on October 4th!", false, "10:15 AM")
    )

    fun evaluateInput(input: String, isSanitizedMode: Boolean): XssResult {
        val trimmed = input.trim()

        if (isSanitizedMode) {
            // HTML Entity Encoding + Tag sanitization
            val sanitized = trimmed
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;")
                .replace("/", "&#x2F;")

            val wasMalicious = trimmed.contains("<script", ignoreCase = true) ||
                    trimmed.contains("onerror=", ignoreCase = true) ||
                    trimmed.contains("onload=", ignoreCase = true) ||
                    trimmed.contains("javascript:", ignoreCase = true) ||
                    trimmed.contains("<img", ignoreCase = true)

            return XssResult(
                rawInput = trimmed,
                renderedHtml = sanitized,
                isScriptTriggered = false,
                isCookieExfiltrated = false,
                isDomDefaced = false,
                simulatedAlertText = null,
                simulatedExfiltratedCookie = null,
                flagUnlocked = if (wasMalicious) "FLAG{PU_XSS_DOM_SANITIZED_SUCCESS_2026}" else null,
                sanitizationNote = "Input neutralized via HTML entity encoding. Browser renders tags as plain string literals, preventing execution."
            )
        } else {
            // Vulnerable mode: raw execution
            val hasScriptTag = trimmed.contains("<script>", ignoreCase = true) ||
                    trimmed.contains("<script", ignoreCase = true)
            val hasImgOnError = trimmed.contains("onerror=", ignoreCase = true)
            val hasSvgOnLoad = trimmed.contains("onload=", ignoreCase = true)
            val attemptsCookie = trimmed.contains("document.cookie", ignoreCase = true)
            val attemptsDeface = trimmed.contains("defaced", ignoreCase = true) ||
                    trimmed.contains("hacked", ignoreCase = true) ||
                    trimmed.contains("login", ignoreCase = true)

            val isMalicious = hasScriptTag || hasImgOnError || hasSvgOnLoad || trimmed.contains("javascript:", ignoreCase = true)

            if (isMalicious) {
                val alertMsg = when {
                    attemptsCookie -> "XSS Payload Executed! Stolen Cookie: $MOCK_SESSION_COOKIE"
                    trimmed.contains("alert(", ignoreCase = true) -> {
                        val start = trimmed.indexOf("alert(") + 6
                        val end = trimmed.indexOf(")", start)
                        if (start in 6..end) trimmed.substring(start, end).replace("'", "").replace("\"", "")
                        else "XSS Vulnerability Confirmed!"
                    }
                    else -> "Simulated Alert: XSS Payload Triggered in Sandbox DOM!"
                }

                return XssResult(
                    rawInput = trimmed,
                    renderedHtml = trimmed,
                    isScriptTriggered = true,
                    isCookieExfiltrated = attemptsCookie,
                    isDomDefaced = attemptsDeface,
                    simulatedAlertText = alertMsg,
                    simulatedExfiltratedCookie = if (attemptsCookie) MOCK_SESSION_COOKIE else null,
                    flagUnlocked = "FLAG{PU_XSS_ARBITRARY_JS_TRIGGERED_2026}",
                    sanitizationNote = "WARNING: Raw script tag or inline event handler executed directly in DOM context without sanitization!"
                )
            } else {
                return XssResult(
                    rawInput = trimmed,
                    renderedHtml = trimmed,
                    isScriptTriggered = false,
                    isCookieExfiltrated = false,
                    isDomDefaced = false,
                    simulatedAlertText = null,
                    simulatedExfiltratedCookie = null,
                    flagUnlocked = null,
                    sanitizationNote = "Benign text rendered normally."
                )
            }
        }
    }
}
