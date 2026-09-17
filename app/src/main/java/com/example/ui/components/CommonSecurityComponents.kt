package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SessionCurriculum
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

@Composable
fun SandboxBanner(
    modifier: Modifier = Modifier,
    title: String = "SECURE ISOLATED SANDBOX",
    subtitle: String = "Zero external network leakage • Safe client-side attack simulation"
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("sandbox_banner"),
        shape = RoundedCornerShape(12.dp),
        color = CyberNavyCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(CyberGreen)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = CyberCyan,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = CyberTextSecondary,
                        fontSize = 11.sp
                    )
                )
            }
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Shield",
                tint = CyberCyan,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun PresidencyHeader(
    modifier: Modifier = Modifier,
    sessionDate: String = "4 October 2026"
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = CyberNavySurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberNavyBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = CyberCyan.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "PRESIDENCY UNIVERSITY",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyberCyan,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = sessionDate,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = CyberWarningYellow,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
            Text(
                text = "Cyber Security Club Workshop",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = CyberTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "Introduction to Cyber Security • General Secretary Session Blueprint",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = CyberTextSecondary
                )
            )
        }
    }
}

@Composable
fun FlagBadge(
    flag: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("flag_badge"),
        shape = RoundedCornerShape(10.dp),
        color = CyberGreen.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberGreen)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Flag Captured",
                tint = CyberGreen,
                modifier = Modifier.size(18.dp)
            )
            Column {
                Text(
                    text = "CHALLENGE SOLVED • FLAG UNLOCKED",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = CyberGreen,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
                Text(
                    text = flag,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = CyberTextPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

@Composable
fun TerminalWindow(
    title: String,
    content: String,
    modifier: Modifier = Modifier,
    tintColor: Color = CyberCyan
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF030712),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberNavyBorder)
    ) {
        Column {
            // Title bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(CyberThreatRed))
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(CyberWarningYellow))
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(CyberGreen))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = CyberTextSecondary,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }
            // Body
            Text(
                text = content,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = tintColor,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            )
        }
    }
}

@Composable
fun SpeakerProfileCard(
    speakerName: String = SessionCurriculum.SPEAKER_NAME,
    speakerRole: String = SessionCurriculum.SPEAKER_ROLE,
    speakerDept: String = SessionCurriculum.SPEAKER_DEPARTMENT,
    githubUsername: String = SessionCurriculum.SPEAKER_GITHUB_USERNAME,
    avatarUrl: String = SessionCurriculum.SPEAKER_AVATAR_URL,
    bio: String = SessionCurriculum.SPEAKER_BIO,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("speaker_profile_card"),
        shape = RoundedCornerShape(16.dp),
        color = CyberNavySurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = CyberCyan.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "SESSION SPEAKER & LEAD",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyberCyan,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = CyberGreen.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberGreen.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified",
                            tint = CyberGreen,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "PRESIDENCY PUCSC",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyberGreen,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            // Avatar & Name Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Profile Avatar with GitHub profile picture & local high-res asset
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(CyberNavyDark)
                        .border(2.dp, CyberCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val context = androidx.compose.ui.platform.LocalContext.current
                    val imageRequest = remember(avatarUrl) {
                        coil.request.ImageRequest.Builder(context)
                            .data(avatarUrl)
                            .error(com.example.R.drawable.speaker_avatar)
                            .placeholder(com.example.R.drawable.speaker_avatar)
                            .fallback(com.example.R.drawable.speaker_avatar)
                            .crossfade(true)
                            .build()
                    }

                    coil.compose.AsyncImage(
                        model = imageRequest,
                        contentDescription = "Profile photo of $speakerName",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = speakerName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = CyberTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                    Text(
                        text = speakerRole,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = CyberCyan,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = speakerDept,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = CyberTextSecondary,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            // Bio
            Text(
                text = bio,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = CyberTextPrimary,
                    lineHeight = 18.sp
                )
            )

            // Technical Focus Tags
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("OWASP Top 10", "Cryptography", "SOC Triage", "CTF / Red Team").forEach { tag ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = CyberNavyCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberNavyBorder)
                    ) {
                        Text(
                            text = tag,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyberTextSecondary,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            // Direct One-Click Social Media Links
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "CONNECT & DIRECT SOCIAL PROFILES",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = CyberCyan,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )

                // 2x2 Grid for attractive one-click direct profiles
                val socialLinks = listOf(
                    SocialMediaItem(
                        platform = "Portfolio",
                        handle = "alkamah-portfolio",
                        url = SessionCurriculum.SPEAKER_PORTFOLIO_URL,
                        icon = Icons.Default.Language,
                        accentColor = CyberCyan,
                        tag = "portfolio_link_button"
                    ),
                    SocialMediaItem(
                        platform = "LinkedIn",
                        handle = "alkamah-sakilur-rashid",
                        url = SessionCurriculum.SPEAKER_LINKEDIN_URL,
                        icon = Icons.Default.Public,
                        accentColor = CyberAccentBlue,
                        tag = "linkedin_link_button"
                    ),
                    SocialMediaItem(
                        platform = "Facebook",
                        handle = "Alkamah Rashid",
                        url = SessionCurriculum.SPEAKER_FACEBOOK_URL,
                        icon = Icons.Default.ThumbUp,
                        accentColor = Color(0xFF1877F2),
                        tag = "facebook_link_button"
                    ),
                    SocialMediaItem(
                        platform = "Instagram",
                        handle = "@alkamahrashid",
                        url = SessionCurriculum.SPEAKER_INSTAGRAM_URL,
                        icon = Icons.Default.Share,
                        accentColor = Color(0xFFE1306C),
                        tag = "instagram_link_button"
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    socialLinks.take(2).forEach { item ->
                        SocialProfileButton(
                            item = item,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                val intent = android.content.Intent(
                                    android.content.Intent.ACTION_VIEW,
                                    android.net.Uri.parse(item.url)
                                )
                                context.startActivity(intent)
                            }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    socialLinks.drop(2).forEach { item ->
                        SocialProfileButton(
                            item = item,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                val intent = android.content.Intent(
                                    android.content.Intent.ACTION_VIEW,
                                    android.net.Uri.parse(item.url)
                                )
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }

            // GitHub & Connect Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                androidx.compose.material3.Button(
                    onClick = {
                        val gitHubIntent = android.content.Intent(
                            android.content.Intent.ACTION_VIEW,
                            android.net.Uri.parse("https://github.com/$githubUsername")
                        )
                        context.startActivity(gitHubIntent)
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = CyberCyan,
                        contentColor = Color(0xFF0A0F1D)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).testTag("open_github_button")
                ) {
                    Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "GitHub: @$githubUsername",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                androidx.compose.material3.OutlinedButton(
                    onClick = {
                        val emailIntent = android.content.Intent(
                            android.content.Intent.ACTION_SENDTO,
                            android.net.Uri.parse("mailto:${SessionCurriculum.SPEAKER_EMAIL}")
                        )
                        context.startActivity(emailIntent)
                    },
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("contact_speaker_button")
                ) {
                    Text(
                        text = "Email",
                        color = CyberCyan,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

data class SocialMediaItem(
    val platform: String,
    val handle: String,
    val url: String,
    val icon: ImageVector,
    val accentColor: Color,
    val tag: String
)

@Composable
fun SocialProfileButton(
    item: SocialMediaItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = CyberNavyCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, item.accentColor.copy(alpha = 0.45f)),
        modifier = modifier.testTag(item.tag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(item.accentColor.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.platform,
                    tint = item.accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.platform,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = CyberTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                )
                Text(
                    text = item.handle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = CyberTextSecondary,
                        fontSize = 10.sp
                    ),
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.Default.OpenInNew,
                contentDescription = "Open ${item.platform}",
                tint = item.accentColor.copy(alpha = 0.8f),
                modifier = Modifier.size(12.dp)
            )
        }
    }
}
