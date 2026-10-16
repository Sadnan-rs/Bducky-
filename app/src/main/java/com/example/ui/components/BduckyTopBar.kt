package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BduckyTab
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.BlueSecondary
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.StatusDotCyan
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextSlate
import com.example.ui.theme.TextWhite

@Composable
fun BduckyTopBar(
    status: String,
    targetName: String,
    profileTag: String,
    txSpeed: String,
    txPackets: Int,
    isRegistered: Boolean,
    currentTab: BduckyTab,
    onTabSelected: (BduckyTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkBg)
            .statusBarsPadding() // CRITICAL: Prevents phone status bar (time, date, battery) from overlapping the app!
    ) {
        // 1. HARDWARE STATUS BAR
        Surface(
            color = Color(0xFF070B14),
            border = BorderStroke(1.dp, Color(0xFF141F33)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 680.dp)
                        .padding(horizontal = 10.dp, vertical = 7.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // STATUS indicator with glowing cyan dot
                    StatusIndicator(
                        label = "STATUS",
                        value = status,
                        isPositive = status == "CONNECTED" || status == "READY" || status == "TRANSMITTING"
                    )

                    StatusDivider()

                    // TARGET
                    StatusItem(
                        label = "TARGET",
                        value = if (targetName.length > 12) targetName.take(10) + ".." else targetName
                    )

                    StatusDivider()

                    // PROFILE
                    StatusItem(
                        label = "PROFILE",
                        value = if (profileTag.length > 8) profileTag.take(7) + ".." else profileTag
                    )

                    StatusDivider()

                    // TX PACKETS
                    StatusItem(
                        label = "TX",
                        value = "$txPackets pkts"
                    )

                    StatusDivider()

                    // REG
                    StatusBadge(
                        label = "REG",
                        value = if (isRegistered) "OK" else "OFF",
                        isActive = isRegistered
                    )
                }
            }
        }

        // 2. CENTERED HIGH-TECH TITLE & BRANDING
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF00E5FF), Color(0xFF0288D1))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "BDUCKY Logo",
                        tint = DarkBg,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "BDUCKY",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.5.sp,
                    color = TextWhite
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Non-root verified badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF0A2E24),
                    border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color(0xFF34D399),
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "NO ROOT",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF34D399),
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            Text(
                text = "made by Sadat",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.2.sp,
                color = BlueAccent,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        // 3. HORIZONTAL SCROLLABLE TABS
        val scrollState = rememberScrollState()
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 680.dp)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BduckyTab.values().forEach { tab ->
                    val isSelected = tab == currentTab
                    val backgroundColor by animateColorAsState(
                        targetValue = if (isSelected) Color(0xFF0D233A) else DarkCard,
                        label = "tabBg"
                    )
                    val borderColor by animateColorAsState(
                        targetValue = if (isSelected) CyanNeon else DarkBorder,
                        label = "tabBorder"
                    )
                    val textColor by animateColorAsState(
                        targetValue = if (isSelected) CyanNeon else TextSlate,
                        label = "tabText"
                    )

                    Surface(
                        onClick = { onTabSelected(tab) },
                        shape = RoundedCornerShape(12.dp),
                        color = backgroundColor,
                        border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor),
                        modifier = Modifier
                            .height(38.dp)
                            .testTag("tab_${tab.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(CyanNeon)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Text(
                                text = tab.label,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                                fontSize = 12.5.sp,
                                letterSpacing = 0.5.sp,
                                color = textColor
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))
    }
}

@Composable
private fun StatusIndicator(label: String, value: String, isPositive: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotAlpha"
    )

    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = BlueAccent,
            fontFamily = FontFamily.Monospace
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 1.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .alpha(if (isPositive) alpha else 1f)
                    .clip(CircleShape)
                    .background(if (isPositive) StatusDotCyan else StatusRed)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = value,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun StatusItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = BlueAccent,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = value,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(top = 1.dp)
        )
    }
}

@Composable
private fun StatusBadge(label: String, value: String, isActive: Boolean) {
    Column(horizontalAlignment = Alignment.End) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = BlueAccent,
            fontFamily = FontFamily.Monospace
        )
        Surface(
            color = if (isActive) Color(0xFF065F46) else Color(0xFF881337),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.padding(top = 1.dp)
        ) {
            Text(
                text = value,
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isActive) Color(0xFF34D399) else Color(0xFFFDA4AF),
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
            )
        }
    }
}

@Composable
private fun StatusDivider() {
    Box(
        modifier = Modifier
            .height(18.dp)
            .width(1.dp)
            .background(Color(0xFF1E293B))
    )
}
