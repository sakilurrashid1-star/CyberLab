package com.example.sandbox

data class PhishingEmail(
    val id: String,
    val category: String,
    val senderName: String,
    val senderEmail: String,
    val replyTo: String,
    val subject: String,
    val receivedDate: String,
    val spfStatus: String,
    val dkimStatus: String,
    val dmarcStatus: String,
    val originatingIp: String,
    val bodyHtml: String,
    val attachmentName: String? = null,
    val destinationUrl: String? = null,
    val isMalicious: Boolean = true,
    val redFlags: List<RedFlagItem>,
    val socReportRecommendation: String
)

data class RedFlagItem(
    val id: String,
    val title: String,
    val explanation: String
)

data class SocialEngDialogue(
    val scenarioId: String,
    val title: String,
    val scenarioType: String,
    val stepId: Int,
    val speaker: String,
    val message: String,
    val options: List<DialogueOption>
)

data class DialogueOption(
    val text: String,
    val isSecure: Boolean,
    val feedback: String,
    val nextStepId: Int?
)

object PhishingEngine {

    val emailScenarios = listOf(
        PhishingEmail(
            id = "PHISH-2026-REGISTRAR",
            category = "Exam Suspension & Unicode Homograph",
            senderName = "Presidency University Office of Registrar",
            senderEmail = "registrar-notice@presıdency-portal.com", // Note the Turkish dotless 'ı' (U+0131)
            replyTo = "collector-drop99@external-relay.ru",
            subject = "CRITICAL: Fall 2026 Exam Registration Suspension Warning!",
            receivedDate = "Oct 4, 2026, 02:18 AM",
            spfStatus = "FAIL (Originating IP 185.220.101.5 not listed in presidency.edu TXT record)",
            dkimStatus = "NONE (Header missing RSA cryptographic signature d=presidency.edu)",
            dmarcStatus = "FAIL (p=reject; email should be discarded by receiving MTA)",
            originatingIp = "185.220.101.5 (Known Tor Exit Node / Bulletproof VPS)",
            bodyHtml = """
                Dear Presidency University Student,
                
                Our automated compliance audit detected an unverified identity flag on your Fall 2026 student portal account.
                
                YOUR FINAL EXAM ADMIT CARD AND COURSE REGISTRATION WILL BE PERMANENTLY SUSPENDED WITHIN 2 HOURS UNLESS YOU RE-AUTHENTICATE IMMEDIATELY.
                
                Click below to download your verified admit card voucher and preserve your exam eligibility:
                http://presıdency-portal.com/auth/student-verify?token=PU-92841
                
                Office of the Controller of Examinations
                Presidency University, Dhaka
            """.trimIndent(),
            attachmentName = "Presidency_AdmitCard_Final.pdf.exe",
            destinationUrl = "http://presıdency-portal.com/auth/student-verify",
            redFlags = listOf(
                RedFlagItem(
                    id = "HOMOGRAPH_DOMAIN",
                    title = "Unicode Homograph Domain Spoofing",
                    explanation = "'presıdency-portal.com' uses the Latin small letter dotless 'ı' (U+0131) to deceive students into believing it is the official university portal."
                ),
                RedFlagItem(
                    id = "SPF_DKIM_FAIL",
                    title = "SPF / DKIM / DMARC Authentication Failure",
                    explanation = "The sending mail server failed SPF authorization for presidency.edu, and the cryptographic DKIM signature was absent."
                ),
                RedFlagItem(
                    id = "ARTIFICIAL_URGENCY",
                    title = "Manufactured Psychological Urgency",
                    explanation = "An arbitrary 2-hour deadline exploits anxiety regarding exam eligibility, prompting impulsive compliance before rational verification."
                ),
                RedFlagItem(
                    id = "DOUBLE_EXTENSION",
                    title = "Executable PE Attachment Masked as PDF (.pdf.exe)",
                    explanation = "The file hides a Windows PE binary executable payload behind a double extension, designed to drop malware onto the student device."
                ),
                RedFlagItem(
                    id = "REPLY_TO_MISMATCH",
                    title = "Anomalous External Reply-To Header",
                    explanation = "Replies route to an external drop inbox 'external-relay.ru' rather than an authorized @presidency.edu mailbox."
                )
            ),
            socReportRecommendation = "Forward original email with full RFC 822 headers to cert@presidency.edu. Block sender IP 185.220.101.5 on campus firewalls and sinkhole the punycode domain."
        ),
        PhishingEmail(
            id = "PHISH-2026-PAYROLL",
            category = "Financial Aid & Payroll Credential Harvest",
            senderName = "Presidency Student Accounts & Financial Aid",
            senderEmail = "accounts-bursar@presidency-payportal.net",
            replyTo = "finance-harvest@mail-forwarder.io",
            subject = "ACTION REQUIRED: Fall 2026 Scholarship / Stipend Direct Deposit Update",
            receivedDate = "Oct 4, 2026, 09:42 AM",
            spfStatus = "SOFTFAIL (Domain presidency-payportal.net does not match presidency.edu)",
            dkimStatus = "FAIL (Body hash does not verify)",
            dmarcStatus = "FAIL (No alignment between sender domain and dkim selector)",
            originatingIp = "104.244.72.115 (Commercial Hosting / Proxy)",
            bodyHtml = """
                Attention Scholarship Recipient,
                
                A pending disbursement of BDT 18,500 for your academic excellence tuition stipend has failed due to outdated banking routing information in the campus bursar database.
                
                To avoid forfeiture of your semester stipend, please log in to the Student Financial Services portal within 12 hours to verify your banking credentials and NID number:
                
                http://presidency-payportal.net/finance/direct-deposit-update
                
                Financial Accounts & Bursar Operations
                Presidency University
            """.trimIndent(),
            attachmentName = null,
            destinationUrl = "http://presidency-payportal.net/finance/direct-deposit-update",
            redFlags = listOf(
                RedFlagItem(
                    id = "UNOFFICIAL_TLD",
                    title = "Lookalike Domain with Unofficial TLD (.net)",
                    explanation = "Presidency University uses .edu / .edu.bd. The adversary registered 'presidency-payportal.net' to capture banking and student login credentials."
                ),
                RedFlagItem(
                    id = "FINANCIAL_GREED_TRIGGER",
                    title = "Financial Incentive / Loss Aversion Pretext",
                    explanation = "Promises of unexpected stipend disbursement manipulate students into bypassing basic authentication checks."
                ),
                RedFlagItem(
                    id = "CREDENTIAL_HARVEST_LINK",
                    title = "Unencrypted Insecure HTTP Link",
                    explanation = "The login link uses plain HTTP on an unverified third-party domain rather than official HTTPS Single Sign-On (SSO)."
                ),
                RedFlagItem(
                    id = "HEADER_ALIGNMENT_FAIL",
                    title = "DMARC Alignment Failure",
                    explanation = "The sender domain failed to achieve identifier alignment across SPF and DKIM mechanisms."
                )
            ),
            socReportRecommendation = "Issue a campus-wide alert regarding bursar spoofing. Submit 'presidency-payportal.net' to Google Safe Browsing and block the domain on campus DNS resolvers."
        ),
        PhishingEmail(
            id = "PHISH-2026-DEAN-BEC",
            category = "Executive Impersonation (Business Email Compromise)",
            senderName = "Prof. Dr. M. Rahman (Dean of Engineering)",
            senderEmail = "dean.rahman.presidency.edu@gmail.com",
            replyTo = "urgent.executive.inbox@protonmail.com",
            subject = "Urgent task for Club Executive Committee - Are you on campus?",
            receivedDate = "Oct 4, 2026, 11:15 AM",
            spfStatus = "PASS (gmail.com SPF passes for consumer Google account)",
            dkimStatus = "PASS (Signed by google.com, NOT by presidency.edu)",
            dmarcStatus = "NEUTRAL (Sending as public webmail address)",
            originatingIp = "209.85.220.41 (Google Webmail Egress)",
            bodyHtml = """
                Hello,
                
                Are you currently in the CSE Department building? I am chairing a closed-door ABET accreditation meeting with foreign evaluators and cannot take phone calls right now.
                
                I need you to urgently assist with purchasing 5 Google Play / Apple digital gift cards ($100 each) for the visiting committee honorarium. The university finance office will reimburse you in cash by 4:00 PM today.
                
                Please email the digital claim codes directly to this address as soon as possible. Do not discuss this in public until the formal accreditation announcement.
                
                Best regards,
                Prof. Dr. M. Rahman
                Dean, School of Engineering
                Presidency University
            """.trimIndent(),
            attachmentName = null,
            destinationUrl = null,
            redFlags = listOf(
                RedFlagItem(
                    id = "PUBLIC_WEBMAIL_SPOOF",
                    title = "Executive Impersonation via Free Webmail (Gmail)",
                    explanation = "High-ranking university leadership will never conduct official financial business using a personal or burner @gmail.com address."
                ),
                RedFlagItem(
                    id = "AUTHORITY_EXPLOITATION",
                    title = "Authority & Secrecy Psychological Pressure",
                    explanation = "The attacker invokes the Dean's authority and demands confidentiality ('do not discuss this in public') to prevent out-of-band verification."
                ),
                RedFlagItem(
                    id = "GIFT_CARD_EXTORTION",
                    title = "Irreversible Gift Card Payment Request",
                    explanation = "Gift card codes are untraceable, non-refundable, and instantly liquidatable on secondary markets—a hallmark of BEC scams."
                ),
                RedFlagItem(
                    id = "OUT_OF_BAND_EVASION",
                    title = "Evasion of Verbal / In-Person Verification",
                    explanation = "Claiming to be 'in a closed meeting and unable to take phone calls' prevents the victim from picking up the phone to verify."
                )
            ),
            socReportRecommendation = "Educate student leaders on BEC gift card scams. Advise student to verify directly via the Dean's official office landline. Report ProtonMail drop address to abuse team."
        ),
        PhishingEmail(
            id = "PHISH-2026-CLOUD-DRIVE",
            category = "Compromised Account & Malicious Cloud Share",
            senderName = "Sadia Islam (CSE Student)",
            senderEmail = "sadia.islam@presidency.edu",
            replyTo = "sadia.islam@presidency.edu",
            subject = "Shared Document: CSE321_Midterm_Questions_Solutions_2026.xlsm",
            receivedDate = "Oct 4, 2026, 01:05 PM",
            spfStatus = "PASS (Originates from official Presidency University G-Suite)",
            dkimStatus = "PASS (Valid signature from presidency.edu)",
            dmarcStatus = "PASS (Full organizational alignment)",
            originatingIp = "192.88.99.14 (Compromised Student Session Token)",
            bodyHtml = """
                Hey everyone!
                
                Here are the solved previous year questions and lab exam solution templates for the upcoming midterm exam.
                
                Please review before the lecture this afternoon:
                https://drive-presidency-share.storage.cloud/CSE321_Solutions.xlsm
                
                NOTE: You MUST enable macros ('Enable Content') when opening the Excel sheet to render the question answer keys.
                
                Cheers,
                Sadia
            """.trimIndent(),
            attachmentName = "CSE321_Solutions_Final.xlsm",
            destinationUrl = "https://drive-presidency-share.storage.cloud/download",
            redFlags = listOf(
                RedFlagItem(
                    id = "COMPROMISED_INTERNAL_ACCOUNT",
                    title = "Compromised Internal Peer Account",
                    explanation = "Even with valid SPF/DKIM passes, the student account was hijacked via session theft to launch lateral phishing inside the university."
                ),
                RedFlagItem(
                    id = "MACRO_ENABLED_PAYLOAD",
                    title = "Macro-Enabled Office Document (.xlsm)",
                    explanation = "Excel files with macros (.xlsm) can execute arbitrary Visual Basic for Applications (VBA) code, downloading Cobalt Strike or trojans upon clicking 'Enable Content'."
                ),
                RedFlagItem(
                    id = "THIRD_PARTY_CLOUD_HOSTING",
                    title = "Deceptive Third-Party Cloud URL",
                    explanation = "The link points to an external bucket ('storage.cloud') disguised as university cloud storage."
                ),
                RedFlagItem(
                    id = "ACADEMIC_PEER_LURE",
                    title = "Cheating / Leaked Exam Answers Lure",
                    explanation = "The lure preys on academic anxiety and curiosity, enticing students into reckless behavior to gain an unfair advantage."
                )
            ),
            socReportRecommendation = "Immediately revoke Sadia's session tokens, force an enterprise password reset, and purge this email from all university inboxes via Microsoft 365 / Google Workspace admin console."
        )
    )

    val socialEngScenarios = listOf(
        SocialEngDialogue(
            scenarioId = "VISHING_IT_DESK",
            title = "Scenario 1: Campus IT Helpdesk Vishing Call",
            scenarioType = "Voice Phishing (Vishing)",
            stepId = 1,
            speaker = "Caller (+880-1700-FAKE-IT)",
            message = "Hello, is this Presidency University student portal user? This is Engineer Kabir from Presidency Central IT Operations. We are performing an emergency security upgrade on the campus VPN for the October 4 workshop, and our intrusion system flagged your account as compromised. I have just dispatched a 6-digit verification code to your phone. Read it out to me immediately so I can restore your access before your account is permanently locked.",
            options = listOf(
                DialogueOption(
                    text = "Sure Engineer Kabir, the 6-digit code on my phone is 749-218. Please don't lock my account!",
                    isSecure = false,
                    feedback = "COMPROMISED! You just handed your Multi-Factor Authentication (MFA) OTP to an attacker. The attacker used it to complete a session hijack on your university account.",
                    nextStepId = null
                ),
                DialogueOption(
                    text = "Presidency IT never asks for OTP or MFA tokens over the phone. I am hanging up and will verify this ticket in person at the IT Department in Building 2.",
                    isSecure = true,
                    feedback = "SECURE! Correct protocol: legitimate administrators will NEVER ask for your one-time passwords or credentials over phone or email. You thwarted a classic vishing attack.",
                    nextStepId = 2
                )
            )
        ),
        SocialEngDialogue(
            scenarioId = "VISHING_IT_DESK",
            title = "Scenario 1: Campus IT Helpdesk Vishing Call",
            scenarioType = "Voice Phishing (Vishing)",
            stepId = 2,
            speaker = "Attacker Follow-up SMS",
            message = "The attacker notices the call failed and immediately sends an urgent SMS from a spoofed shortcode: 'URGENT PRESIDENCY IT ALERT: Student refusal to cooperate with ticket #PU-9021. You will be fined BDT 5,000 and de-registered unless you confirm your password at: http://pu-portal-bypass.org'.",
            options = listOf(
                DialogueOption(
                    text = "Forward the SMS to the Presidency University Cyber Security Club and Campus IT CERT (cert@presidency.edu), then block the number.",
                    isSecure = true,
                    feedback = "EXCELLENT! Incident formally reported to university defenders. The fraudulent domain will be added to the campus security gateway blacklist.",
                    nextStepId = null
                ),
                DialogueOption(
                    text = "Click the link just to verify if the ticket number exists on the campus portal.",
                    isSecure = false,
                    feedback = "COMPROMISED! Never click unverified links from suspicious SMS messages. The landing page is a credential harvester designed to look like Presidency's login portal.",
                    nextStepId = null
                )
            )
        ),
        SocialEngDialogue(
            scenarioId = "PHYSICAL_TAILGATING",
            title = "Scenario 2: Physical Tailgating at the CSE Server Room",
            scenarioType = "Physical Security & Impersonation",
            stepId = 1,
            speaker = "Stranger in High-Vis Vest at CSE Server Door",
            message = "You swipe your student lab RFID card to open the secure entrance to the CSE Networking & Server Lab. A stranger carrying two heavy toolboxes and wearing an HVAC technician badge rushes up behind you: 'Hey buddy, quick! Could you hold the door open for me? My hands are completely full and the university facilities team needs me to inspect the server AC unit before it overheats!'",
            options = listOf(
                DialogueOption(
                    text = "Hold the door open politely. They look like a legit technician and have heavy tools.",
                    isSecure = false,
                    feedback = "BREACH! You permitted physical tailgating (piggybacking). The intruder placed a rogue hardware keylogger / packet-tap device onto the central switch rack.",
                    nextStepId = null
                ),
                DialogueOption(
                    text = "Politely say: 'I cannot hold the secure door. You must swipe your own university badge or report to the department security officer at the front desk.'",
                    isSecure = true,
                    feedback = "SECURE! Outstanding adherence to physical security protocols. Never bypass electronic access controls out of politeness or social pressure.",
                    nextStepId = 2
                )
            )
        ),
        SocialEngDialogue(
            scenarioId = "PHYSICAL_TAILGATING",
            title = "Scenario 2: Physical Tailgating at the CSE Server Room",
            scenarioType = "Physical Security & Impersonation",
            stepId = 2,
            speaker = "Department Security Escort",
            message = "The visitor becomes irritated and threatens to report you to the facilities manager for delaying critical repairs. How do you conclude the incident?",
            options = listOf(
                DialogueOption(
                    text = "Stay calm, let the door close and latch, and immediately notify the nearest floor security guard or lab faculty supervisor.",
                    isSecure = true,
                    feedback = "EXCELLENT! You followed Defense-in-Depth. Physical access control is the foundation of digital security. Real contractors are always prepared to sign the visitor log.",
                    nextStepId = null
                ),
                DialogueOption(
                    text = "Apologize and let them in just to avoid getting into trouble with the facilities manager.",
                    isSecure = false,
                    feedback = "COMPROMISED! Intimidation and manufactured urgency are textbook social engineering levers. Never compromise security for convenience.",
                    nextStepId = null
                )
            )
        ),
        SocialEngDialogue(
            scenarioId = "DISCORD_WATERING_HOLE",
            title = "Scenario 3: Unofficial Campus Discord Community Lure",
            scenarioType = "Watering Hole & Trojan Distribution",
            stepId = 1,
            speaker = "Discord User @Presidency_Study_Mod",
            message = "In an unofficial student Discord server, a user named '@Presidency_Study_Mod' with a fake club logo posts in #announcements: 'Hey guys! We obtained the exact leaked question bank and auto-grader script for the Cyber Security Lab midterm. Download the zip archive and run `install_grader.py` to test your code against the professor\'s hidden test cases!'",
            options = listOf(
                DialogueOption(
                    text = "Download the script and run it in terminal with administrator privileges (`sudo python install_grader.py`).",
                    isSecure = false,
                    feedback = "CATASTROPHIC COMPROMISE! The python script was a Discord token grabber and reverse-shell payload that exfiltrated your browser cookies and SSH keys.",
                    nextStepId = null
                ),
                DialogueOption(
                    text = "Do not execute unverified files. Report the post to Discord community moderators and notify the Cyber Security Club executive team.",
                    isSecure = true,
                    feedback = "SECURE! Academic curiosity and grade anxiety are prime vectors for malware delivery. Always inspect source code and sandbox unknown files.",
                    nextStepId = null
                )
            )
        )
    )
}
