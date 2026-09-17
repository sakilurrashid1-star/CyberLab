package com.example.model

enum class QuizDifficulty {
    ALL,
    BEGINNER,
    INTERMEDIATE,
    ADVANCED
}

enum class QuizTopic(val displayName: String) {
    ALL("All Topics"),
    INTRODUCTION("Introduction"),
    SECURING_ACCOUNTS("Securing Accounts"),
    SECURING_DATA("Securing Data"),
    SECURING_SYSTEMS("Securing Systems"),
    SECURING_SOFTWARE("Securing Software"),
    PRESERVING_PRIVACY("Preserving Privacy"),
    CAREERS("Career Opportunities"),
    CLUB_ACTIVITIES("Club & Future Opportunities")
}

data class QuizQuestion(
    val id: String,
    val topic: QuizTopic,
    val difficulty: QuizDifficulty,
    val questionText: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String,
    val practicalTakeaway: String,
    val referenceSlide: String
)

data class QuizSessionState(
    val currentQuestionIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val isAnswerRevealed: Boolean = false,
    val userAnswers: Map<Int, Int> = emptyMap(), // questionIndex -> selectedOptionIndex
    val totalScore: Int = 0,
    val isFinished: Boolean = false,
    val streak: Int = 0,
    val maxStreak: Int = 0,
    val filterTopic: QuizTopic = QuizTopic.ALL,
    val filterDifficulty: QuizDifficulty = QuizDifficulty.ALL
)

object QuizBank {
    val allQuestions: List<QuizQuestion> = listOf(
        // Topic 1: Introduction
        QuizQuestion(
            id = "Q_FOUND_1",
            topic = QuizTopic.INTRODUCTION,
            difficulty = QuizDifficulty.BEGINNER,
            questionText = "Which pillar of the CIA Triad is directly violated when an unauthorized adversary reads cleartext student grades stored on a university server?",
            options = listOf(
                "Integrity",
                "Confidentiality",
                "Availability",
                "Non-repudiation"
            ),
            correctOptionIndex = 1,
            explanation = "Confidentiality ensures that sensitive information is accessible only to authorized individuals. Unauthorized disclosure or eavesdropping directly violates Confidentiality.",
            practicalTakeaway = "Always enforce strong encryption (AES-256 at rest, TLS 1.3 in transit) to uphold confidentiality.",
            referenceSlide = "Topic 1: Introduction"
        ),
        QuizQuestion(
            id = "Q_FOUND_2",
            topic = QuizTopic.INTRODUCTION,
            difficulty = QuizDifficulty.INTERMEDIATE,
            questionText = "What is the foundational principle underlying modern Zero Trust architecture?",
            options = listOf(
                "Trust all internal traffic behind the corporate perimeter firewall",
                "Never trust, always verify every request regardless of origin",
                "Rely strictly on long-lived VPN tunnels for user validation",
                "Disable all multi-factor authentication (MFA) on internal networks"
            ),
            correctOptionIndex = 1,
            explanation = "Zero Trust assumes breaches are inevitable or already occurring. It eliminates implicit trust based on network locality ('Never trust, always verify').",
            practicalTakeaway = "Implement continuous authentication, least-privilege access, and strict microsegmentation.",
            referenceSlide = "Topic 1: Introduction"
        ),

        // Topic 2: Securing Accounts
        QuizQuestion(
            id = "Q_ACCT_1",
            topic = QuizTopic.SECURING_ACCOUNTS,
            difficulty = QuizDifficulty.INTERMEDIATE,
            questionText = "Why are FIDO2 / WebAuthn hardware security keys considered phish-resistant compared to SMS and push-based MFA?",
            options = listOf(
                "They use longer numbers than SMS codes",
                "The authentication challenge is cryptographically bound to the specific verified origin domain in the browser",
                "They operate without any internet connectivity",
                "They store passwords on a remote cloud server"
            ),
            correctOptionIndex = 1,
            explanation = "FIDO2/WebAuthn public-key authentication binds the credential to the specific browser origin (domain name). A phishing site at 'presıdency.edu' will never receive valid signatures for 'presidency.edu'.",
            practicalTakeaway = "Deploy FIDO2/WebAuthn security keys for root administrators and privileged accounts.",
            referenceSlide = "Topic 2: Securing Accounts"
        ),
        QuizQuestion(
            id = "Q_ACCT_2",
            topic = QuizTopic.SECURING_ACCOUNTS,
            difficulty = QuizDifficulty.BEGINNER,
            questionText = "What critical security enhancement does adding a unique cryptographic 'salt' provide before hashing a password?",
            options = listOf(
                "It automatically encrypts the database hard drive",
                "It ensures identical passwords generate distinct hashes, defeating precomputed rainbow tables",
                "It shortens long passwords so they fit into fixed-length columns",
                "It sends an SMS verification code to the student's phone"
            ),
            correctOptionIndex = 1,
            explanation = "A unique per-user salt guarantees that two users with the password 'password123' will have completely different hashes, neutralizing rainbow tables and batch attacks.",
            practicalTakeaway = "Generate at least 16 bytes of cryptographically secure random salt per account (SecureRandom).",
            referenceSlide = "Topic 2: Securing Accounts"
        ),

        // Topic 3: Securing Data
        QuizQuestion(
            id = "Q_DATA_1",
            topic = QuizTopic.SECURING_DATA,
            difficulty = QuizDifficulty.INTERMEDIATE,
            questionText = "What key property does Ephemeral Diffie-Hellman (ECDHE) provide in TLS 1.3 handshakes?",
            options = listOf(
                "Unlimited session timeouts without renewal",
                "Perfect Forward Secrecy (PFS), ensuring past sessions cannot be decrypted even if server private keys leak later",
                "Automatic bypass of corporate firewalls",
                "Zero network bandwidth consumption"
            ),
            correctOptionIndex = 1,
            explanation = "Perfect Forward Secrecy generates unique per-session keys via ECDHE. Even if the server's long-term private RSA key is compromised later, recorded past traffic remains indecipherable.",
            practicalTakeaway = "Disable legacy cipher suites (e.g., static RSA key exchange) and enforce TLS 1.3 with ECDHE.",
            referenceSlide = "Topic 3: Securing Data"
        ),
        QuizQuestion(
            id = "Q_DATA_2",
            topic = QuizTopic.SECURING_DATA,
            difficulty = QuizDifficulty.ADVANCED,
            questionText = "Which symmetric encryption mode provides Authenticated Encryption with Associated Data (AEAD), preventing ciphertext bit-flipping attacks?",
            options = listOf(
                "AES in ECB (Electronic Codebook) mode",
                "AES in CBC (Cipher Block Chaining) without HMAC",
                "AES in GCM (Galois/Counter Mode)",
                "Caesar Cipher with shift of 13"
            ),
            correctOptionIndex = 2,
            explanation = "AES-GCM combines counter-mode confidentiality with GMAC authentication tags. Any tampering or bit-flipping with the ciphertext causes authentication verification to fail immediately.",
            practicalTakeaway = "Always choose authenticated symmetric modes like AES-GCM or ChaCha20-Poly1305 over legacy AES-CBC.",
            referenceSlide = "Topic 3: Securing Data"
        ),

        // Topic 4: Securing Systems
        QuizQuestion(
            id = "Q_SYS_1",
            topic = QuizTopic.SECURING_SYSTEMS,
            difficulty = QuizDifficulty.BEGINNER,
            questionText = "In the 2017 Equifax data breach (CVE-2017-5638), what was the fundamental defensive failure that enabled attackers to steal 147 million records?",
            options = listOf(
                "A physical break-in at the server room",
                "Failing to patch a known critical Apache Struts vulnerability disclosed 2 months earlier",
                "A compromised Wi-Fi router password",
                "The complete absence of database backups"
            ),
            correctOptionIndex = 1,
            explanation = "Equifax failed to apply a security patch released months prior for Apache Struts. Attackers exploited this public vulnerability via crafted Content-Type headers.",
            practicalTakeaway = "Establish automated patch management, vulnerability scanners, and emergency remediation SLA cycles.",
            referenceSlide = "Topic 4: Securing Systems"
        ),
        QuizQuestion(
            id = "Q_SYS_2",
            topic = QuizTopic.SECURING_SYSTEMS,
            difficulty = QuizDifficulty.INTERMEDIATE,
            questionText = "How does network microsegmentation prevent an attacker from escalating a compromised lab workstation into a full campus-wide breach?",
            options = listOf(
                "It speeds up campus Wi-Fi downloads",
                "It isolates workloads into granular subnet zones and blocks unauthorized lateral movement via zero-trust ingress/egress rules",
                "It disables student login passwords",
                "It removes all campus firewalls"
            ),
            correctOptionIndex = 1,
            explanation = "Microsegmentation confines compromised hosts within restricted zones, preventing attackers from scanning and pivoting laterally to critical servers.",
            practicalTakeaway = "Enforce VLAN isolation and zero-trust firewall policies between student Wi-Fi and production databases.",
            referenceSlide = "Topic 4: Securing Systems"
        ),

        // Topic 5: Securing Software
        QuizQuestion(
            id = "Q_SOFT_1",
            topic = QuizTopic.SECURING_SOFTWARE,
            difficulty = QuizDifficulty.BEGINNER,
            questionText = "In the classic SQL injection payload 'admin' OR '1'='1' --', what is the purpose of the trailing double dashes ('--')?",
            options = listOf(
                "To encrypt the rest of the database query",
                "To comment out the remainder of the original SQL statement such as password verification",
                "To execute a remote OS shell command",
                "To speed up database indexing"
            ),
            correctOptionIndex = 1,
            explanation = "In SQL syntax, '--' marks the beginning of a single-line comment. This causes the database engine to ignore remaining clauses like \"AND password = '...'\", granting unauthorized login.",
            practicalTakeaway = "Understand how SQL lexers tokenize raw strings versus literals.",
            referenceSlide = "Topic 5: Securing Software"
        ),
        QuizQuestion(
            id = "Q_SOFT_2",
            topic = QuizTopic.SECURING_SOFTWARE,
            difficulty = QuizDifficulty.INTERMEDIATE,
            questionText = "What is the absolute, industry-standard programmatic defense against all variants of SQL Injection?",
            options = listOf(
                "Stripping single quotes using simple string replacement (str.replace(\"'\", \"\"))",
                "Using Parameterized Queries / Prepared Statements",
                "Hashing the user's username before query execution",
                "Hiding the database behind an obscure custom port"
            ),
            correctOptionIndex = 1,
            explanation = "Parameterized queries pre-compile the SQL statement into an Abstract Syntax Tree (AST). User input is passed strictly as data literals and can never alter query execution logic.",
            practicalTakeaway = "Never concatenate untrusted input into SQL queries; use Room/JPA/PDO prepared statements.",
            referenceSlide = "Topic 5: Securing Software"
        ),
        QuizQuestion(
            id = "Q_SOFT_3",
            topic = QuizTopic.SECURING_SOFTWARE,
            difficulty = QuizDifficulty.INTERMEDIATE,
            questionText = "How does a well-configured Content Security Policy (CSP) header protect modern web applications from XSS execution?",
            options = listOf(
                "By encrypting all database records with AES-256",
                "By restricting which domain sources can load and execute scripts, and disallowing inline script tags",
                "By forcing all passwords to be at least 16 characters long",
                "By blocking all HTTP GET requests entirely"
            ),
            correctOptionIndex = 1,
            explanation = "CSP headers (e.g., Content-Security-Policy: default-src 'self') instruct the browser to reject inline scripts (<script>...</script>) and untrusted external script CDNs, mitigating XSS even if injected.",
            practicalTakeaway = "Deploy strict CSP rules with nonces/hashes and ban unsafe-inline/eval.",
            referenceSlide = "Topic 5: Securing Software"
        ),

        // Topic 6: Preserving Privacy
        QuizQuestion(
            id = "Q_PRIV_1",
            topic = QuizTopic.PRESERVING_PRIVACY,
            difficulty = QuizDifficulty.BEGINNER,
            questionText = "What does the fundamental security and privacy engineering principle of 'Data Minimization' state?",
            options = listOf(
                "Always compress files using ZIP format",
                "Organizations should only collect, store, and process the personal data strictly necessary for legitimate specified purposes",
                "Delete all system logs every 10 minutes",
                "Store user passwords in small fonts"
            ),
            correctOptionIndex = 1,
            explanation = "Data Minimization mandates collecting only the data essential for the task. Data you do not hold cannot be stolen in a breach.",
            practicalTakeaway = "Audit your application databases and purge unneeded user metadata and telemetry.",
            referenceSlide = "Topic 6: Preserving Privacy"
        ),
        QuizQuestion(
            id = "Q_PRIV_2",
            topic = QuizTopic.PRESERVING_PRIVACY,
            difficulty = QuizDifficulty.INTERMEDIATE,
            questionText = "Why can an adversary track a student across multiple browser tabs even in 'Incognito / Private' mode without relying on cookies?",
            options = listOf(
                "Because private mode increases screen brightness",
                "Via Device Fingerprinting (Canvas rendering, WebGL signatures, installed fonts, audio stack hashes)",
                "Because incognito mode sends text messages to the university",
                "Because private mode disables RAM memory"
            ),
            correctOptionIndex = 1,
            explanation = "Browser fingerprinting aggregates hardware and software characteristics (GPU, Canvas draw differences, audio API latency) to construct a high-entropy identifier unique to that machine.",
            practicalTakeaway = "Use anti-fingerprinting browsers like Firefox with Enhanced Tracking Protection or Tor Browser.",
            referenceSlide = "Topic 6: Preserving Privacy"
        ),

        // Topic 7: Cyber Security Career Opportunities
        QuizQuestion(
            id = "Q_CAREER_1",
            topic = QuizTopic.CAREERS,
            difficulty = QuizDifficulty.BEGINNER,
            questionText = "In cybersecurity terminology, what is the primary role of a 'Blue Team'?",
            options = listOf(
                "Simulating adversary attacks and exploiting zero-day vulnerabilities",
                "Defensive engineering, continuous monitoring (SOC), incident response, and hardening",
                "Selling stolen database records on dark web forums",
                "Designing campus marketing brochures"
            ),
            correctOptionIndex = 1,
            explanation = "Blue Teams are internal security defenders responsible for maintaining defenses, hunting threats, analyzing logs in SIEM tools, and containing security incidents.",
            practicalTakeaway = "Presidency University Cyber Club prepares students for Blue Team SOC roles and threat hunting.",
            referenceSlide = "Topic 7: Cyber Security Career Opportunities"
        ),
        QuizQuestion(
            id = "Q_CAREER_2",
            topic = QuizTopic.CAREERS,
            difficulty = QuizDifficulty.INTERMEDIATE,
            questionText = "What does the abbreviation 'CTF' stand for in collegiate and professional cyber competitions?",
            options = listOf(
                "Certified Telecommunications Framework",
                "Capture The Flag (competitive hacking & defense challenges)",
                "Central Threat Firewall",
                "Continuous Transmission Frequency"
            ),
            correctOptionIndex = 1,
            explanation = "Capture The Flag competitions challenge participants to solve real-world security puzzles across cryptography, reverse engineering, web security, and forensics to discover hidden 'flags'.",
            practicalTakeaway = "Compete regularly on PicoCTF and HackTheBox to build practical skills for industry roles.",
            referenceSlide = "Topic 7: Cyber Security Career Opportunities"
        ),

        // Topic 8: Club Activities & Future Opportunities
        QuizQuestion(
            id = "Q_CLUB_1",
            topic = QuizTopic.CLUB_ACTIVITIES,
            difficulty = QuizDifficulty.BEGINNER,
            questionText = "How can students actively engage in the Presidency University Cyber Security Club to launch their cybersecurity journey?",
            options = listOf(
                "Wait until graduation before learning any security tools",
                "Participate in weekly club sandbox labs, join the competitive CTF team, and connect with peers in the live discussion forum",
                "Only memorize multiple-choice answers without practical labs",
                "Never share security findings with others"
            ),
            correctOptionIndex = 1,
            explanation = "Presidency University Cyber Security Club empowers students through hands-on sandbox labs, collaborative CTF competitions, executive leadership roles, and direct mentorship.",
            practicalTakeaway = "Active participation in club workshops and peer discussions builds an exceptional portfolio for career success.",
            referenceSlide = "Topic 8: Club Activities & Future Opportunities"
        )
    )

    fun generateQuiz(
        topic: QuizTopic = QuizTopic.ALL,
        difficulty: QuizDifficulty = QuizDifficulty.ALL,
        count: Int = 10,
        randomize: Boolean = true
    ): List<QuizQuestion> {
        val filtered = allQuestions.filter { q ->
            (topic == QuizTopic.ALL || q.topic == topic) &&
            (difficulty == QuizDifficulty.ALL || q.difficulty == difficulty)
        }
        val pool = if (filtered.isEmpty()) allQuestions else filtered
        return if (randomize) {
            pool.shuffled().take(count)
        } else {
            pool.take(count)
        }
    }
}
