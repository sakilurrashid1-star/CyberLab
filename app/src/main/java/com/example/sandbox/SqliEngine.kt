package com.example.sandbox

data class SqliResult(
    val executedSql: String,
    val tokens: List<SqlToken>,
    val isBypassed: Boolean,
    val isUnionInjected: Boolean,
    val returnedRecords: List<Map<String, String>>,
    val errorMessage: String? = null,
    val flagUnlocked: String? = null
)

data class SqlToken(
    val text: String,
    val type: SqlTokenType
)

enum class SqlTokenType {
    KEYWORD,
    IDENTIFIER,
    LITERAL,
    OPERATOR,
    COMMENT,
    INJECTED
}

object SqliEngine {
    // Isolated in-memory mock database
    val mockUsers = listOf(
        mapOf("id" to "1", "username" to "pu_admin", "role" to "SysAdmin", "secret" to "PU_ROOT_KEY_9921", "dept" to "IT Infrastructure"),
        mapOf("id" to "2", "username" to "general_sec", "role" to "Club Executive", "secret" to "PU_CYBER_CLUB_2026", "dept" to "Cyber Security Club"),
        mapOf("id" to "3", "username" to "student_alice", "role" to "Student", "secret" to "AlicePass2026!", "dept" to "Computer Science"),
        mapOf("id" to "4", "username" to "faculty_rahim", "role" to "Professor", "secret" to "RahimResearch2026", "dept" to "Software Engineering")
    )

    fun executeAuthQuery(usernameInput: String, passwordInput: String, isSafeMode: Boolean): SqliResult {
        if (isSafeMode) {
            // Prepared statement parameterized simulation
            val executedSql = "SELECT * FROM users WHERE username = ? AND password = ?"
            val tokens = listOf(
                SqlToken("SELECT", SqlTokenType.KEYWORD),
                SqlToken("*", SqlTokenType.OPERATOR),
                SqlToken("FROM", SqlTokenType.KEYWORD),
                SqlToken("users", SqlTokenType.IDENTIFIER),
                SqlToken("WHERE", SqlTokenType.KEYWORD),
                SqlToken("username", SqlTokenType.IDENTIFIER),
                SqlToken("=", SqlTokenType.OPERATOR),
                SqlToken("?", SqlTokenType.KEYWORD),
                SqlToken("[PARAM: \"$usernameInput\"]", SqlTokenType.LITERAL),
                SqlToken("AND", SqlTokenType.KEYWORD),
                SqlToken("password", SqlTokenType.IDENTIFIER),
                SqlToken("=", SqlTokenType.OPERATOR),
                SqlToken("?", SqlTokenType.KEYWORD),
                SqlToken("[PARAM: \"$passwordInput\"]", SqlTokenType.LITERAL)
            )

            // Safe lookup: only matches exact match
            val matched = mockUsers.filter {
                it["username"] == usernameInput.trim() && it["secret"] == passwordInput.trim()
            }

            return SqliResult(
                executedSql = executedSql,
                tokens = tokens,
                isBypassed = false,
                isUnionInjected = false,
                returnedRecords = matched,
                errorMessage = if (matched.isEmpty()) "Authentication Failed: Invalid user credentials (Safe Parameterization blocked injection)" else null,
                flagUnlocked = if (usernameInput.contains("'") || usernameInput.contains("OR", ignoreCase = true)) {
                    "FLAG{PU_SQLI_REMEDIATED_PREPARED_STMT_2026}"
                } else null
            )
        } else {
            // Vulnerable concatenation
            val executedSql = "SELECT * FROM users WHERE username = '$usernameInput' AND password = '$passwordInput'"
            val normalizedUser = usernameInput.trim()

            // Token parsing simulation
            val tokens = mutableListOf<SqlToken>()
            tokens.add(SqlToken("SELECT * FROM users WHERE username = '", SqlTokenType.KEYWORD))

            val isInjectionDetected = normalizedUser.contains("'") ||
                    normalizedUser.contains("OR", ignoreCase = true) ||
                    normalizedUser.contains("--") ||
                    normalizedUser.contains("UNION", ignoreCase = true)

            if (isInjectionDetected) {
                tokens.add(SqlToken(normalizedUser, SqlTokenType.INJECTED))
            } else {
                tokens.add(SqlToken(normalizedUser, SqlTokenType.LITERAL))
            }
            tokens.add(SqlToken("' AND password = '$passwordInput'", SqlTokenType.KEYWORD))

            // Check classic bypasses
            val isBypass = normalizedUser.contains("' OR '1'='1", ignoreCase = true) ||
                    normalizedUser.contains("' OR 1=1", ignoreCase = true) ||
                    normalizedUser.contains("admin' --", ignoreCase = true) ||
                    normalizedUser.contains("' OR 'a'='a", ignoreCase = true) ||
                    normalizedUser.contains("' OR ''='", ignoreCase = true)

            val isUnion = normalizedUser.contains("UNION", ignoreCase = true) &&
                    normalizedUser.contains("SELECT", ignoreCase = true)

            return when {
                isBypass -> {
                    // Auth bypassed! Return all users including admin
                    SqliResult(
                        executedSql = executedSql,
                        tokens = tokens,
                        isBypassed = true,
                        isUnionInjected = false,
                        returnedRecords = mockUsers,
                        errorMessage = null,
                        flagUnlocked = "FLAG{PU_SQLI_AUTH_BYPASS_EXPLOITED_2026}"
                    )
                }
                isUnion -> {
                    // UNION injection exfiltration
                    val exfiltrated = listOf(
                        mapOf("id" to "ROOT_SECRET", "username" to "SYS_VAULT", "role" to "CONFIDENTIAL", "secret" to "DB_MASTER_SALT_PU_9981", "dept" to "Presidency Datacenter")
                    ) + mockUsers
                    SqliResult(
                        executedSql = executedSql,
                        tokens = tokens,
                        isBypassed = true,
                        isUnionInjected = true,
                        returnedRecords = exfiltrated,
                        errorMessage = null,
                        flagUnlocked = "FLAG{PU_SQLI_UNION_EXTRACTION_SUCCESS_2026}"
                    )
                }
                else -> {
                    val matched = mockUsers.filter {
                        it["username"] == normalizedUser && it["secret"] == passwordInput.trim()
                    }
                    SqliResult(
                        executedSql = executedSql,
                        tokens = tokens,
                        isBypassed = false,
                        isUnionInjected = false,
                        returnedRecords = matched,
                        errorMessage = if (matched.isEmpty()) "Invalid username or password" else null
                    )
                }
            }
        }
    }
}
