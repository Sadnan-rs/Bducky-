package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DeviceHub
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BluetoothDeviceItem
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.BlueSecondary
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextSlate
import com.example.ui.theme.TextWhite
import com.example.viewmodel.BduckyUiState

@Composable
fun ConnTab(
    state: BduckyUiState,
    onToggleHidService: () -> Unit,
    onRegisterHid: () -> Unit,
    onUnregisterHid: () -> Unit,
    onRefreshDevices: (Context) -> Unit,
    onConnectDevice: (String) -> Unit,
    onDisconnectDevice: () -> Unit,
    onDismissError: () -> Unit,
    onRequestPermissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 680.dp)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Permission Request Card if not yet granted
            if (!state.hasBluetoothPermissions) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E170A)),
                        border = BorderStroke(1.2.dp, StatusAmber),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(StatusAmber.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = "Permission",
                                        tint = StatusAmber,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "BLUETOOTH PERMISSION REQUIRED",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.8.sp,
                                        color = StatusAmber
                                    )
                                    Text(
                                        text = "Nearby Devices & Bluetooth Connect",
                                        fontSize = 11.5.sp,
                                        color = TextSlate
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "BDUCKY requires Bluetooth permissions to query your paired PC/Mac hosts and emulate keyboard keystrokes without root access.",
                                fontSize = 12.sp,
                                color = Color(0xFFFED7AA),
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = onRequestPermissions,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = StatusAmber,
                                    contentColor = Color(0xFF1E170A)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                            ) {
                                Icon(imageVector = Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "GRANT PERMISSION",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }
            }

            // 2. Rootless Architecture Verification Banner
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
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF064E3B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Safe",
                                tint = Color(0xFF34D399),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "100% ROOTLESS • COMPATIBLE WITH ALL ANDROID DEVICES",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.8.sp,
                                color = Color(0xFF34D399)
                            )
                            Text(
                                text = "Connects via official Android Bluetooth HID specification. Safe, zero root permissions, and works on all Android phones and tablets.",
                                fontSize = 11.sp,
                                color = Color(0xFFA7F3D0),
                                lineHeight = 15.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }

            // Error banner if any
            if (state.errorMessage != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A1017)),
                        border = BorderStroke(1.dp, StatusRed.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("error_banner")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Warning",
                                tint = StatusRed,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = state.errorMessage,
                                color = StatusRed,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = onDismissError,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = StatusRed,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            // SECTION 1: HID SERVICE
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_hid_service")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFF0F2642)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeviceHub,
                                        contentDescription = "HID Service",
                                        tint = CyanNeon,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "HID SERVICE",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 1.sp,
                                        color = TextWhite
                                    )
                                    Text(
                                        text = if (state.isHidServiceRunning) "Emulation Service Active" else "Service Stopped",
                                        fontSize = 12.sp,
                                        color = if (state.isHidServiceRunning) StatusGreen else TextSlate
                                    )
                                }
                            }

                            Switch(
                                checked = state.isHidServiceRunning,
                                onCheckedChange = { onToggleHidService() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = DarkBg,
                                    checkedTrackColor = CyanNeon,
                                    uncheckedThumbColor = TextSlate,
                                    uncheckedTrackColor = Color(0xFF1E293B)
                                ),
                                modifier = Modifier.testTag("toggle_hid_service")
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Emulates a physical Bluetooth Keyboard and Mouse using standard Android Bluetooth HID. Any paired device will treat your tablet/phone as a hardware input companion.",
                            fontSize = 12.sp,
                            color = TextSlate,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // QUICK START GUIDE
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = CyanNeon,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "QUICK START GUIDE",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = TextWhite
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val steps = listOf(
                            "1. Pair Device" to "Go to Android Bluetooth Settings and pair your target host (Infinix Hot 60i, PC, Mac).",
                            "2. Keep HID Active" to "Ensure the HID Service switch above is ON.",
                            "3. Connect to Host" to "Tap 'Connect' next to your paired device below and wait for handshake confirmation.",
                            "4. Start Typing & Control" to "Switch to the INPUT or MACROS tab to send live keystrokes, mouse moves, and scripts!"
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            steps.forEach { (title, desc) ->
                                Row(verticalAlignment = Alignment.Top) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .padding(top = 5.dp)
                                            .clip(CircleShape)
                                            .background(CyanNeon)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                        Text(text = desc, fontSize = 11.sp, color = TextSlate, lineHeight = 15.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 2: REAL PAIRED DEVICES
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_paired_devices")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFF0F2642)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bluetooth,
                                        contentDescription = "Bluetooth",
                                        tint = CyanNeon,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "PAIRED DEVICES",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 1.sp,
                                        color = TextWhite
                                    )
                                    Text(
                                        text = "${state.pairedDevices.size} paired hosts found",
                                        fontSize = 12.sp,
                                        color = TextSlate
                                    )
                                }
                            }

                            Button(
                                onClick = { onRefreshDevices(context) },
                                enabled = !state.isRefreshingDevices,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF162A48)),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier
                                    .height(36.dp)
                                    .testTag("btn_refresh_devices")
                            ) {
                                if (state.isRefreshingDevices) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = CyanNeon
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Refresh",
                                        tint = CyanNeon,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Refresh",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyanNeon
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (state.pairedDevices.isEmpty()) {
                            // Professional Empty State with Bluetooth Settings shortcut
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF0D1524),
                                border = BorderStroke(1.dp, Color(0xFF182842)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BluetoothSearching,
                                        contentDescription = null,
                                        tint = CyanNeon,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "No Paired Devices Found",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "BDUCKY connects to devices already paired in your Android device's Bluetooth settings. Pair your Windows PC, Mac, Linux, or phone first.",
                                        fontSize = 12.sp,
                                        color = TextSlate,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedButton(
                                            onClick = {
                                                try {
                                                    val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                                                    context.startActivity(intent)
                                                } catch (_: Exception) { }
                                            },
                                            border = BorderStroke(1.2.dp, CyanNeon),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.OpenInNew,
                                                contentDescription = null,
                                                tint = CyanNeon,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Pair New Device",
                                                color = CyanNeon,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        }

                                        Button(
                                            onClick = { onRefreshDevices(context) },
                                            colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = DarkBg),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text(
                                                text = "Check Again",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                state.pairedDevices.forEach { device ->
                                    DeviceCardItem(
                                        device = device,
                                        onConnect = { onConnectDevice(device.id) },
                                        onDisconnect = onDisconnectDevice
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceCardItem(
    device: BluetoothDeviceItem,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (device.isConnected) Color(0xFF0E2338) else Color(0xFF10192A),
        border = BorderStroke(
            1.dp,
            if (device.isConnected) CyanNeon else DarkBorder
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (device.isConnected) Color(0xFF00E5FF).copy(alpha = 0.2f) else Color(0xFF1B283F)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when {
                            device.deviceType.contains("Phone") || device.deviceType.contains("Tablet") -> Icons.Default.PhoneAndroid
                            else -> Icons.Default.Computer
                        },
                        contentDescription = null,
                        tint = if (device.isConnected) CyanNeon else TextSlate,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = device.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextWhite
                        )
                        if (device.isConnected) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF065F46)
                            ) {
                                Text(
                                    text = "ACTIVE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF34D399),
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        } else if (device.isConnecting) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF451A03)
                            ) {
                                Text(
                                    text = "CONNECTING...",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = StatusAmber,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "${device.deviceType} • ${device.address}",
                        fontSize = 11.5.sp,
                        fontFamily = FontFamily.Monospace,
                        color = TextSlate
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (device.isConnected) {
                OutlinedButton(
                    onClick = onDisconnect,
                    border = BorderStroke(1.dp, StatusRed),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(
                        text = "Disconnect",
                        fontSize = 11.5.sp,
                        color = StatusRed,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else if (device.isConnecting) {
                Button(
                    onClick = { },
                    enabled = false,
                    colors = ButtonDefaults.buttonColors(
                        disabledContainerColor = Color(0xFF162A48),
                        disabledContentColor = CyanNeon
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(13.dp),
                        strokeWidth = 2.dp,
                        color = CyanNeon
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Connecting...",
                        fontSize = 11.sp,
                        color = CyanNeon,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Button(
                    onClick = onConnect,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(
                        text = "Connect",
                        fontSize = 11.5.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
