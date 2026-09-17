package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.sandbox.CryptoEngine
import com.example.sandbox.MitmEngine
import com.example.sandbox.PhishingEngine
import com.example.sandbox.SqliEngine
import com.example.sandbox.XssEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read app name from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("CyberLab", appName)
  }

  @Test
  fun `verify sqli engine bypass and safe parameterized defense`() {
    // 1. Vulnerable mode bypass
    val vulResult = SqliEngine.executeAuthQuery("admin' OR '1'='1' --", "any", isSafeMode = false)
    assertTrue("SQLi bypass should succeed in vulnerable mode", vulResult.isBypassed)
    assertNotNull(vulResult.flagUnlocked)

    // 2. Safe mode remediation
    val safeResult = SqliEngine.executeAuthQuery("admin' OR '1'='1' --", "any", isSafeMode = true)
    assertTrue("Safe mode should neutralize bypass", !safeResult.isBypassed)
    assertTrue(safeResult.executedSql.contains("?"))
  }

  @Test
  fun `verify xss engine sanitization`() {
    val payload = "<script>alert('pwned')</script>"

    // Vulnerable execution
    val vuln = XssEngine.evaluateInput(payload, isSanitizedMode = false)
    assertTrue(vuln.isScriptTriggered)

    // Sanitized defense
    val sanitized = XssEngine.evaluateInput(payload, isSanitizedMode = true)
    assertTrue(!sanitized.isScriptTriggered)
    assertTrue(sanitized.renderedHtml.contains("&lt;script&gt;"))
  }

  @Test
  fun `verify mitm engine tls and hsts defense`() {
    // HTTP unprotected
    val httpState = MitmEngine.simulateTraffic(useTls = false, enableHsts = false, sslStrip = false)
    assertNotNull(httpState.interceptedCredentials)

    // TLS 1.3 protected
    val tlsState = MitmEngine.simulateTraffic(useTls = true, enableHsts = true, sslStrip = false)
    assertEquals(null, tlsState.interceptedCredentials)
    assertNotNull(tlsState.flagUnlocked)
  }

  @Test
  fun `verify crypto engine hashing and dictionary attack`() {
    val md5 = CryptoEngine.calculateMd5("password")
    assertEquals("5f4dcc3b5aa765d61d8327deb882cf99", md5)

    val (cracked, plaintext) = CryptoEngine.performDictionaryAttack(md5)
    assertTrue(cracked)
    assertEquals("password", plaintext)
  }

  @Test
  fun `verify phishing campaigns and email forensics`() {
    val scenarios = PhishingEngine.emailScenarios
    assertTrue("Should have multiple realistic phishing campaigns", scenarios.size >= 4)

    val registrarEmail = scenarios.first { it.id == "PHISH-2026-REGISTRAR" }
    assertEquals(5, registrarEmail.redFlags.size)
    assertTrue(registrarEmail.attachmentName?.endsWith(".exe") == true)
    assertTrue(registrarEmail.spfStatus.startsWith("FAIL"))

    val becEmail = scenarios.first { it.id == "PHISH-2026-DEAN-BEC" }
    assertTrue(becEmail.senderEmail.contains("@gmail.com"))
    assertTrue(becEmail.category.contains("Executive"))
  }

  @Test
  fun `verify social engineering pretexting dialogues`() {
    val dialogues = PhishingEngine.socialEngScenarios
    assertTrue(dialogues.isNotEmpty())

    val vishing = dialogues.first { it.scenarioId == "VISHING_IT_DESK" }
    val secureOpt = vishing.options.first { it.isSecure }
    assertTrue(secureOpt.feedback.contains("SECURE"))
  }

  @Test
  fun `verify speaker profile and github configuration`() {
    assertEquals("Alkamah Sakilur Rashid", com.example.model.SessionCurriculum.SPEAKER_NAME)
    assertEquals("sakilurrashid1", com.example.model.SessionCurriculum.SPEAKER_GITHUB_USERNAME)
    assertTrue(com.example.model.SessionCurriculum.SPEAKER_ROLE.contains("General Secretary"))
    assertTrue(
      com.example.model.SessionCurriculum.SPEAKER_AVATAR_URL.contains("sakil") ||
      com.example.model.SessionCurriculum.SPEAKER_FALLBACK_GITHUB_AVATAR.contains("sakilurrashid1.png")
    )
  }

  @Test
  fun `verify in app notification models and types`() {
    val notif = com.example.data.model.InAppNotification(
      title = "New Lab Available: SQL Injection Sandbox",
      message = "Interactive sandbox unlocked",
      type = com.example.data.model.NotificationType.LAB_AVAILABILITY.name,
      targetScreen = "SQLI_LAB",
      priority = "HIGH",
      actionLabel = "Launch Lab"
    )
    assertEquals("LAB_AVAILABILITY", notif.type)
    assertEquals("SQLI_LAB", notif.targetScreen)
    assertFalse(notif.isRead)
    assertEquals("HIGH", notif.priority)
    assertEquals("Launch Lab", notif.actionLabel)
  }

  @Test
  fun `verify mitm simulation and d3 visualizer state transitions`() {
    // 1. Plain HTTP without TLS -> Compromised
    val plainHttpState = com.example.sandbox.MitmEngine.simulateTraffic(useTls = false, enableHsts = false, sslStrip = false)
    assertTrue(plainHttpState.isCompromised())
    assertNotNull(plainHttpState.interceptedCredentials)

    // 2. SSL Stripping without HSTS -> Compromised
    val sslStripState = com.example.sandbox.MitmEngine.simulateTraffic(useTls = true, enableHsts = false, sslStrip = true)
    assertTrue(sslStripState.isCompromised())
    assertEquals("student_2026", sslStripState.interceptedCredentials?.first)

    // 3. TLS 1.3 enforced with HSTS -> Secure / Not Compromised
    val secureState = com.example.sandbox.MitmEngine.simulateTraffic(useTls = true, enableHsts = true, sslStrip = true)
    assertFalse(secureState.isCompromised())
    assertNull(secureState.interceptedCredentials)
    assertTrue(secureState.statusMessage.contains("DEFENSE ACTIVE"))
  }
}
