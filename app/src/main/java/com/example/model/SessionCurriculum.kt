package com.example.model

data class SessionSlide(
    val id: Int,
    val title: String,
    val section: String,
    val durationMinutes: Int,
    val bulletPoints: List<String>,
    val speakerNotes: String,
    val keyTakeaways: String,
    val targetLabType: LabType? = null,
    val caseStudy: CaseStudy? = null
)

data class CaseStudy(
    val title: String,
    val year: String,
    val attackVector: String,
    val impact: String,
    val rootCause: String,
    val defensiveTakeaway: String
)

data class CurriculumTopic(
    val id: Int,
    val title: String,
    val shortDescription: String,
    val keyConcepts: List<String>,
    val attackScenarios: String,
    val defensiveRemediation: String,
    val practicalLabType: LabType? = null,
    val correspondingSlideIndex: Int
)

enum class LabType {
    SQL_INJECTION,
    CROSS_SITE_SCRIPTING,
    MAN_IN_THE_MIDDLE,
    CRYPTOGRAPHY,
    PHISHING_SOCIAL_ENG
}

object SessionCurriculum {
    const val EVENT_TITLE = "Introduction to Cybersecurity"
    const val CLUB_NAME = "Presidency University Cyber Security Club"
    const val SESSION_DATE = "4 October 2026"
    const val SPEAKER_NAME = "Alkamah Sakilur Rashid"
    const val SPEAKER_ROLE = "General Secretary, Presidency University Cyber Security Club"
    const val SPEAKER_DEPARTMENT = "Dept. of Computer Science & Engineering (CSE)"
    const val SPEAKER_EMAIL = "sakilurrashid1@gmail.com"
    const val SPEAKER_GITHUB_USERNAME = "sakilurrashid1"
    const val SPEAKER_GITHUB_URL = "https://github.com/sakilurrashid1"
    const val SPEAKER_PORTFOLIO_URL = "https://sakilurrashid1-star.github.io/alkamah-portfolio/"
    const val SPEAKER_FACEBOOK_URL = "https://www.facebook.com/share/1K8LyfMcxx/"
    const val SPEAKER_INSTAGRAM_URL = "https://www.instagram.com/alkamahrashid?stkn=MXJvcmV5MmRybmRkOQ=="
    const val SPEAKER_LINKEDIN_URL = "https://www.linkedin.com/in/alkamah-sakilur-rashid-70855b42a?utm_source=share_via&utm_content=profile&utm_medium=member_android"
    const val SPEAKER_AVATAR_URL = "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEhzWxQSS9eE4wOdGPksN1z3hcn826K-l1zCXGgfFYBryZT2DxASJhBQ0bm7oidz6bom3zxK9ksjiq_rLffithMvZm1e-EiOEfi2wJ93OzWd2Czb3G2XYGDB8obYPplRVUGGPPiXFLKrLaDw8xGjJfkk89luyjJSAGc81hJDRTTAfW6QwCeZg8i2pPYz9jI/s800/sakil.jpeg"
    const val SPEAKER_FALLBACK_GITHUB_AVATAR = "https://github.com/sakilurrashid1.png"
    const val SPEAKER_BIO = "Alkamah Sakilur Rashid is the General Secretary of Presidency University Cyber Security Club. He leads university cyber defense initiatives, hands-on penetration testing workshops, and competitive CTF training for engineering students."
    const val VENUE = "Main Auditorium / Cyber Lab Sandbox"

    val officialTopics = listOf(
        CurriculumTopic(
            id = 1,
            title = "Introduction",
            shortDescription = "Overview of the modern cyber threat landscape, ethical hacking boundaries, and session roadmap.",
            keyConcepts = listOf(
                "Presidency University Cyber Security Club mission & leadership",
                "Ethics First: CFAA, Cyber Law 2026, Responsible Disclosure",
                "CIA Triad (Confidentiality, Integrity, Availability)",
                "The Zero Trust Philosophy: 'Never Trust, Always Verify'"
            ),
            attackScenarios = "Reckless scanning vs authorized penetration testing; legal consequences of unauthorized intrusion.",
            defensiveRemediation = "Establish clear Rules of Engagement (RoE) and ethical governance prior to any security audit.",
            practicalLabType = null,
            correspondingSlideIndex = 0
        ),
        CurriculumTopic(
            id = 2,
            title = "Securing Accounts",
            shortDescription = "Identity governance, multi-factor authentication, credential entropy, and brute-force mitigation.",
            keyConcepts = listOf(
                "Passphrase entropy & dictionary attacks",
                "Multi-Factor Authentication (MFA): TOTP (RFC 6238) vs SMS vulnerabilities",
                "FIDO2 / WebAuthn cryptographically bound hardware security keys",
                "Account lockout policies, rate limiting, and adaptive risk-based auth"
            ),
            attackScenarios = "Credential stuffing from dark web leaks, SIM swap attacks against SMS OTPs, brute-force dictionary attacks.",
            defensiveRemediation = "Enforce phish-resistant MFA (FIDO2/WebAuthn), salted Argon2id hashes, and exponential backoff on auth endpoints.",
            practicalLabType = LabType.SQL_INJECTION,
            correspondingSlideIndex = 1
        ),
        CurriculumTopic(
            id = 3,
            title = "Securing Data",
            shortDescription = "Protecting data in transit and at rest through robust cryptographic ciphers and key lifecycles.",
            keyConcepts = listOf(
                "Encryption at rest: AES-256-GCM authenticated encryption",
                "Encryption in transit: TLS 1.3 ephemeral Diffie-Hellman (ECDHE)",
                "Cryptographic hashing: SHA-256 vs collisions in MD5/SHA-1",
                "Envelope encryption, HSM key management, and cryptographic erasure"
            ),
            attackScenarios = "Eavesdropping on unencrypted HTTP networks, stealing database backups, GPU-accelerated hash cracking.",
            defensiveRemediation = "Mandate TLS 1.3 across all services, salt and pepper passwords with Argon2/bcrypt, and use AWS KMS/Vault for keys.",
            practicalLabType = LabType.CRYPTOGRAPHY,
            correspondingSlideIndex = 2
        ),
        CurriculumTopic(
            id = 4,
            title = "Securing Systems",
            shortDescription = "Hardening host operating systems, microsegmentation, patch management, and endpoint telemetry.",
            keyConcepts = listOf(
                "OS Hardening: Disabling unused daemons, enforcing CIS benchmarks",
                "Network segmentation, VLANs, and stateful firewall rules",
                "Automated vulnerability scanning and CVE patch management",
                "Endpoint Detection & Response (EDR) and SIEM log monitoring"
            ),
            attackScenarios = "Lateral movement through flat university networks, exploiting unpatched services (like Equifax CVE-2017-5638), privilege escalation.",
            defensiveRemediation = "Microsegment server subnets, enforce least-privilege RBAC, and deploy automated patch orchestration pipelines.",
            practicalLabType = LabType.MAN_IN_THE_MIDDLE,
            correspondingSlideIndex = 3
        ),
        CurriculumTopic(
            id = 5,
            title = "Securing Software",
            shortDescription = "Eliminating application vulnerabilities through secure coding, input validation, and DevSecOps pipelines.",
            keyConcepts = listOf(
                "OWASP Top 10 vulnerabilities (Injection, Broken Auth, SSRF, XSS)",
                "Parameterized queries to prevent SQL Injection AST pollution",
                "Context-aware output encoding and Content Security Policy (CSP)",
                "Software Bill of Materials (SBOM) and supply-chain CI/CD signing"
            ),
            attackScenarios = "Authentication bypass via `' OR '1'='1' -- `, stored session theft via XSS `<script>` payloads, SolarWinds-style build tampering.",
            defensiveRemediation = "Never concatenate user input into queries; implement strict CSP headers, SAST/DAST automated scanners, and signed Git commits.",
            practicalLabType = LabType.CROSS_SITE_SCRIPTING,
            correspondingSlideIndex = 4
        ),
        CurriculumTopic(
            id = 6,
            title = "Preserving Privacy",
            shortDescription = "Data minimization, counter-fingerprinting, metadata sanitization, and compliance frameworks.",
            keyConcepts = listOf(
                "Data minimization principle: Collecting only what is strictly necessary",
                "Browser fingerprinting (Canvas, WebGL, AudioContext) and telemetry leaks",
                "Network privacy: Commercial VPN limitations vs Onion Routing (Tor)",
                "Regulatory privacy frameworks: GDPR, CCPA, and Cyber Privacy Standards"
            ),
            attackScenarios = "ISP packet metadata aggregation, cross-site behavioral tracking beacons, exfiltrating EXIF geolocation metadata from student photos.",
            defensiveRemediation = "Strip EXIF metadata on upload, enforce end-to-end encryption, respect DNT/GPC privacy signals, and utilize privacy-focused browsers.",
            practicalLabType = LabType.PHISHING_SOCIAL_ENG,
            correspondingSlideIndex = 5
        ),
        CurriculumTopic(
            id = 7,
            title = "Cyber Security Career Opportunities",
            shortDescription = "Strategic pathways, industry certifications, CTF roadmaps, and technical specialization tracks.",
            keyConcepts = listOf(
                "Offensive Red Team: Penetration Tester, Vulnerability Researcher, Exploit Dev",
                "Defensive Blue Team: SOC Tier 1-3 Analyst, Threat Hunter, Incident Responder",
                "Application Security (AppSec) & DevSecOps Engineering",
                "Certification Hierarchy: CompTIA Security+ -> eJPT / CEH -> OSCP -> CISSP"
            ),
            attackScenarios = "Transitioning from hobbyist scripting to industry-certified ethical security analyst.",
            defensiveRemediation = "Build a verifiable GitHub portfolio of security tools, write technical CTF writeups, and attain practical hands-on certs.",
            practicalLabType = null,
            correspondingSlideIndex = 6
        ),
        CurriculumTopic(
            id = 8,
            title = "Club Activities & Future Opportunities",
            shortDescription = "Presidency University Cyber Security Club initiatives, CTF teams, hackathons, and research mentorship.",
            keyConcepts = listOf(
                "Weekly hands-on sandbox labs and CTF training sessions",
                "Inter-university cybersecurity competitions and hackathons",
                "Peer-to-peer mentorship in ethical hacking and defensive engineering",
                "Executive Club membership, technical research papers, and alumni network"
            ),
            attackScenarios = "Overcoming theoretical academic silos through competitive team-based CTF defense simulations.",
            defensiveRemediation = "Join the Presidency University Cyber Security Club official Telegram/Discord, register on the club GitHub, and contribute.",
            practicalLabType = null,
            correspondingSlideIndex = 7
        )
    )

    val caseStudies = listOf(
        CaseStudy(
            title = "Equifax Data Breach",
            year = "2017",
            attackVector = "Apache Struts Vulnerability (CVE-2017-5638)",
            impact = "147 million consumers' PII, SSNs, credit cards compromised; $700M+ fines.",
            rootCause = "Failure to patch known critical vulnerability within 2 months of CVE disclosure.",
            defensiveTakeaway = "Rigorous vulnerability scanning, automated patch management, and strict egress filtering."
        ),
        CaseStudy(
            title = "Stuxnet Worm",
            year = "2010",
            attackVector = "Multi-zero-day worm, infected USB, Siemens PLC manipulation",
            impact = "Physical destruction of ~1,000 uranium centrifuges in Natanz facility.",
            rootCause = "Air-gapped network penetration via human social engineering and industrial control protocol tampering.",
            defensiveTakeaway = "Zero Trust even within air-gapped zones; PLC firmware cryptographic signature verification."
        ),
        CaseStudy(
            title = "SolarWinds Orion Supply Chain Breach",
            year = "2020",
            attackVector = "SUNBURST trojan injected into CI/CD build pipeline",
            impact = "18,000+ government and Fortune 500 organizations compromised.",
            rootCause = "Compromised developer credentials leading to silent build artifact injection before code signing.",
            defensiveTakeaway = "Software Bill of Materials (SBOM), deterministic builds, and strict pipeline integrity attestations."
        ),
        CaseStudy(
            title = "CrowdStrike Falcon Sensor Incident",
            year = "2024",
            attackVector = "Faulty channel file update causing Windows kernel logic error",
            impact = "8.5 million Windows devices crashed globally; grounded flights, halted hospitals.",
            rootCause = "Inadequate staging, canary deployment absence, and kernel-level single point of failure.",
            defensiveTakeaway = "Staged canary rollouts, kernel memory isolation (eBPF models), and resilient fallback modes."
        )
    )

    val slides = listOf(
        SessionSlide(
            id = 1,
            title = "Introduction: Leadership, Ethics & Zero Trust",
            section = "Introduction",
            durationMinutes = 15,
            bulletPoints = listOf(
                "Session Lead: Alkamah Sakilur Rashid, General Secretary, Presidency University Cyber Security Club",
                "Speaker Profile: CSE Dept, Cybersecurity Defense & Research • GitHub: @sakilurrashid1",
                "Club Mission: Bridging university computer science with practical offensive & defensive security",
                "Ethics First: CFAA, Cyber Security Act 2026, Responsible Disclosure & Rules of Engagement",
                "CIA Triad & The Zero Trust Revolution: Continuous authentication and microsegmentation"
            ),
            speakerNotes = "Warm welcome by Alkamah Sakilur Rashid. Introduce the club vision, emphasize that cybersecurity protects human life and critical infrastructure, and establish the non-negotiable ethical hacker code.",
            keyTakeaways = "Ethical mindset is mandatory; Zero Trust assumes the perimeter is already breached."
        ),
        SessionSlide(
            id = 2,
            title = "Securing Accounts: Credential Hardening & Phish-Resistant MFA",
            section = "Securing Accounts",
            durationMinutes = 20,
            bulletPoints = listOf(
                "Password Security: Math of entropy, memory-hard hashing (Argon2id, bcrypt, PBKDF2)",
                "Credential Stuffing & Password Spraying: Mitigating automated dictionary botnets",
                "Multi-Factor Authentication (MFA): Inherent flaws of SMS OTPs (SIM-swapping & SS7)",
                "Phish-Resistant Authentication: FIDO2 / WebAuthn cryptographic hardware keys",
                "Adaptive Authentication: Risk-based IP, device fingerprinting, and automated lockout"
            ),
            speakerNotes = "Walk students through why 8-character passwords crack in seconds on modern GPUs. Contrast SMS 2FA with hardware security keys (FIDO2/WebAuthn).",
            keyTakeaways = "Enforce phish-resistant MFA and memory-hard hashing to neutralize credential theft.",
            targetLabType = LabType.SQL_INJECTION
        ),
        SessionSlide(
            id = 3,
            title = "Securing Data: Cryptographic Foundations & TLS 1.3",
            section = "Securing Data",
            durationMinutes = 20,
            bulletPoints = listOf(
                "Data States: Data in Transit, Data at Rest, and Data in Use (Confidential Computing)",
                "Symmetric Encryption: AES-256-GCM providing confidentiality and authenticated integrity",
                "Transport Layer Security: TLS 1.3 ephemeral Diffie-Hellman handshake & Forward Secrecy",
                "Cryptographic Hashing: Collision resistance in SHA-256 vs the death of MD5 and SHA-1",
                "Key Management: HSMs, envelope encryption, and secrets lifecycle governance"
            ),
            speakerNotes = "In Lab 4: Cryptography, have students test collision resistance, crack legacy MD5 hashes, and inspect how TLS scrambles plaintext traffic.",
            keyTakeaways = "Always use authenticated encryption (AES-GCM); never roll custom cryptographic primitives.",
            targetLabType = LabType.CRYPTOGRAPHY
        ),
        SessionSlide(
            id = 4,
            title = "Securing Systems: Host Hardening, Segmentation & EDR",
            section = "Securing Systems",
            durationMinutes = 20,
            bulletPoints = listOf(
                "Operating System Hardening: Disabling legacy ports, CIS benchmarks, and minimal attack surface",
                "Network Security: VLAN microsegmentation, stateful firewalls, and bastion jumpboxes",
                "Vulnerability Management: Tracking CVEs, automated patch orchestration pipelines",
                "Threat Detection: Endpoint Detection & Response (EDR), SIEM log ingestion & MITRE ATT&CK",
                "Case Study Analysis: Equifax & Stuxnet — how unpatched dependencies and weak segmentation lead to collapse"
            ),
            speakerNotes = "Demonstrate MITM and network interception in Lab 3. Explain how attackers pivot horizontally once inside a flat, unsegmented network.",
            keyTakeaways = "Assume breach; isolate critical system tiers and automate patch deployments.",
            targetLabType = LabType.MAN_IN_THE_MIDDLE,
            caseStudy = caseStudies[0]
        ),
        SessionSlide(
            id = 5,
            title = "Securing Software: OWASP Top 10, Injection & DevSecOps",
            section = "Securing Software",
            durationMinutes = 25,
            bulletPoints = listOf(
                "SQL Injection (SQLi): AST tokenization, why concatenating strings breaks parser logic",
                "Cross-Site Scripting (XSS): Reflected, Stored, and DOM-based script execution sinks",
                "Absolute Defenses: Parameterized queries / Prepared statements and Contextual HTML encoding",
                "Defense-in-Depth: Strict Content Security Policy (CSP), subresource integrity, and CORS",
                "DevSecOps Pipeline: SAST/DAST automated scans, Software Bill of Materials (SBOM), signed commits"
            ),
            speakerNotes = "Directly transition to Lab 1 (SQLi) and Lab 2 (XSS). Have students verify how prepared statements neutralize SQL injection payloads by treating user input strictly as literals.",
            keyTakeaways = "Treat all user input as hostile; enforce parameterized queries and contextual sanitization.",
            targetLabType = LabType.CROSS_SITE_SCRIPTING
        ),
        SessionSlide(
            id = 6,
            title = "Preserving Privacy: Counter-Tracking & Metadata Sanitization",
            section = "Preserving Privacy",
            durationMinutes = 15,
            bulletPoints = listOf(
                "Data Minimization: The strongest security guarantee is not storing unnecessary data",
                "Surveillance Economics: Canvas fingerprinting, WebGL hashes, and cross-site beacons",
                "Metadata Leaks: EXIF GPS coordinates, document author metadata, and DNS telemetry",
                "Anonymity & E2EE: Onion routing (Tor), Signal protocol, and private DNS (DoH/DoT)",
                "Compliance & Human Rights: GDPR, digital rights, and student privacy protections"
            ),
            speakerNotes = "Guide students through Lab 5: Phishing & Pretexting. Discuss how attackers aggregate public metadata to craft targeted social engineering spear-phishing campaigns.",
            keyTakeaways = "Minimize data footprint; sanitize metadata and utilize end-to-end encrypted protocols.",
            targetLabType = LabType.PHISHING_SOCIAL_ENG
        ),
        SessionSlide(
            id = 7,
            title = "Cyber Security Career Opportunities: Red, Blue & Engineering",
            section = "Career Opportunities",
            durationMinutes = 20,
            bulletPoints = listOf(
                "Defensive Blue Team: SOC Tier 1-3 Analyst, Digital Forensics, Incident Responder (DFIR)",
                "Offensive Red Team: Penetration Tester, Red Teamer, Exploit & Vulnerability Researcher",
                "Cloud & AppSec Engineering: DevSecOps Engineer, Cloud Security Architect, Cryptographer",
                "Certification Hierarchy: CompTIA Security+ -> eJPT / CEH -> OSCP / PNPT -> CISSP",
                "University Roadmap: PicoCTF, HackTheBox, TryHackMe, open-source security tool development"
            ),
            speakerNotes = "Share real career advice from General Secretary Alkamah Sakilur Rashid: Build a verifiable GitHub portfolio, write CTF walkthroughs, and practice hands-on labs.",
            keyTakeaways = "Hands-on labs and verifiable CTF accomplishments matter far more than theoretical credentials alone."
        ),
        SessionSlide(
            id = 8,
            title = "Club Activities & Future Opportunities: CTFs & Mentorship",
            section = "Club Activities",
            durationMinutes = 15,
            bulletPoints = listOf(
                "Presidency University Cyber Security Club: Hands-on weekend workshops and lab exercises",
                "Competitive CTF Team: Training for National & International Capture The Flag tournaments",
                "Security Research Group: Tool building, vulnerability audits, and student research papers",
                "Alumni Mentorship Network: Direct connection to industry cybersecurity professionals",
                "Join the Club: Follow official updates, participate in the discussion forum, and earn certifications"
            ),
            speakerNotes = "Encourage every student to join the club, contribute to the discussion forum, connect on LinkedIn & GitHub, and become an active defender.",
            keyTakeaways = "Presidency University Cyber Security Club is your launchpad for cybersecurity excellence."
        )
    )
}
