package com.example.sandbox

import java.security.MessageDigest

data class CryptoAnalysis(
    val md5Hash: String,
    val sha256Hash: String,
    val saltedHash: String,
    val saltUsed: String,
    val isCracked: Boolean,
    val crackedPlaintext: String? = null,
    val flagUnlocked: String? = null
)

object CryptoEngine {
    // In-memory wordlist for live dictionary attack demonstration
    val sampleWordlist = listOf(
        "password", "123456", "presidency", "presidency123", "cybersecurity",
        "admin2026", "clubpresident", "dhaka2026", "student123", "secret"
    )

    fun calculateMd5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val bytes = md.digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun calculateSha256(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun calculateSaltedSha256(input: String, salt: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val combined = "$salt:$input"
        val bytes = md.digest(combined.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun caesarCipher(text: String, shift: Int): String {
        val s = (shift % 26 + 26) % 26
        return text.map { char ->
            when {
                char.isUpperCase() -> 'A' + (char - 'A' + s) % 26
                char.isLowerCase() -> 'a' + (char - 'a' + s) % 26
                else -> char
            }
        }.joinToString("")
    }

    fun xorEncrypt(text: String, key: Char): String {
        return text.map { it.code.xor(key.code).toChar() }.joinToString("")
    }

    fun performDictionaryAttack(targetHash: String): Pair<Boolean, String?> {
        val cleanHash = targetHash.trim().lowercase()
        for (word in sampleWordlist) {
            if (calculateMd5(word) == cleanHash) {
                return true to word
            }
            if (calculateSha256(word) == cleanHash) {
                return true to word
            }
        }
        return false to null
    }

    fun analyzeInput(plainText: String, targetHashToCrack: String): CryptoAnalysis {
        val md5 = calculateMd5(plainText)
        val sha256 = calculateSha256(plainText)
        val salt = "PU_SALT_8X9K"
        val salted = calculateSaltedSha256(plainText, salt)

        val (cracked, plaintextMatch) = performDictionaryAttack(targetHashToCrack)

        val flag = if (cracked) {
            "FLAG{PU_CRYPTO_HASH_CRACKED_SALT_REMEDIATED_2026}"
        } else null

        return CryptoAnalysis(
            md5Hash = md5,
            sha256Hash = sha256,
            saltedHash = salted,
            saltUsed = salt,
            isCracked = cracked,
            crackedPlaintext = plaintextMatch,
            flagUnlocked = flag
        )
    }
}
