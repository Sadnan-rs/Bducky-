package com.example.ui.screens

import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HidProfile
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.TextSlate
import com.example.ui.theme.TextWhite
import com.example.viewmodel.BduckyUiState

@Composable
fun SettingsTab(
    state: BduckyUiState,
    onSelectProfile: (HidProfile) -> Unit,
    onSetTypingDelay: (Long) -> Unit,
    onToggleHaptic: () -> Unit,
    onToggleSound: () -> Unit,
    onToggleDarkMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 680.dp)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Rootless Architecture Callout
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF09231E),
                    border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color(0xFF34D399),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ROOTLESS HID ARCHITECTURE",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF34D399)
                            )
                            Text(
                                text = "Engineered with standard Android Bluetooth HID specs. No root, no bootloader unlocking, and zero system file modifications required.",
                                fontSize = 11.5.sp,
                                color = Color(0xFFA7F3D0)
                            )
                        }
                    }
                }
            }

            // 1. Profile Settings
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, DarkBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "PROFILE SETTINGS",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextWhite,
                                letterSpacing = 1.sp
                            )
                        }

                        Text(
                            text = "Select the hardware emulation identity exposed to host operating systems.",
                            fontSize = 12.sp,
                            color = TextSlate,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            state.hidProfiles.forEach { profile ->
                                val isSelected = profile.id == state.selectedProfile.id
                                Surface(
                                    onClick = { onSelectProfile(profile) },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) Color(0xFF0F2642) else Color(0xFF0F1828),
                                    border = BorderStroke(1.2.dp, if (isSelected) CyanNeon else DarkBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = profile.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.5.sp,
                                                color = TextWhite
                                            )
                                            Text(
                                                text = profile.description,
                                                fontSize = 11.sp,
                                                color = TextSlate
                                            )
                                            Text(
                                                text = "VID: ${profile.vendorId} • PID: ${profile.productId}",
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.Monospace,
                                                color = CyanNeon,
                                                modifier = Modifier.padding(top = 2.dp)
                                            )
                                        }

                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = CyanNeon,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 2. Transmit & Engine Preferences
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Speed, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "TIMING & PREFERENCES",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextWhite,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Typing delay slider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Keystroke Delay", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                            Text(text = "${state.typingDelayMs} ms", fontSize = 13.sp, fontFamily = FontFamily.Monospace, color = CyanNeon)
                        }

                        Slider(
                            value = state.typingDelayMs.toFloat(),
                            onValueChange = { onSetTypingDelay(it.toLong()) },
                            valueRange = 10f..100f,
                            steps = 8,
                            colors = SliderDefaults.colors(thumbColor = CyanNeon, activeTrackColor = CyanNeon, inactiveTrackColor = Color(0xFF1E293B))
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Haptic Feedback toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Vibration, contentDescription = null, tint = TextSlate, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = "Haptic Feedback", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                                    Text(text = "Vibrate subtly on key press and click", fontSize = 11.sp, color = TextSlate)
                                }
                            }
                            Switch(
                                checked = state.hapticEnabled,
                                onCheckedChange = { onToggleHaptic() },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF090D16), checkedTrackColor = CyanNeon)
                            )
                        }
                    }
                }
            }

            // 3. System Info Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SYSTEM INFO",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextWhite,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val infoRows = listOf(
                            "App Name" to "BDUCKY (made by Sadat)",
                            "App Version" to "v2.4.0 (Build 42)",
                            "Target Device Model" to "${Build.MANUFACTURER} ${Build.MODEL}",
                            "Android OS Version" to "API Level ${Build.VERSION.SDK_INT} (Android ${Build.VERSION.RELEASE})",
                            "Root Status" to "ROOTLESS (Standard Android API)",
                            "HID Protocol" to "Bluetooth HID Profile v1.1.2",
                            "Active Target" to state.targetName,
                            "Engine State" to state.status
                        )

                        infoRows.forEachIndexed { index, (label, value) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = label, fontSize = 12.sp, color = TextSlate)
                                Text(
                                    text = value,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (label == "Root Status") Color(0xFF34D399) else TextWhite
                                )
                            }
                            if (index < infoRows.lastIndex) {
                                Box(modifier = Modifier.fillMaxWidth().height(1.dp).padding(vertical = 2.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
