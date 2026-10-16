package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.KeyboardReturn
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.InputSubTab
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.BlueSecondary
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.TextSlate
import com.example.ui.theme.TextWhite
import com.example.viewmodel.BduckyUiState

@Composable
fun InputTab(
    state: BduckyUiState,
    onSelectSubTab: (InputSubTab) -> Unit,
    onSetInputText: (String) -> Unit,
    onSendKey: (String, Context) -> Unit,
    onSendString: (Context) -> Unit,
    onClearInput: () -> Unit,
    onToggleModifier: (String) -> Unit,
    onMouseDrag: (Float, Float) -> Unit,
    onMouseClick: (String, Context) -> Unit,
    onMouseScroll: (Int, Context) -> Unit,
    onSetMouseSensitivity: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 680.dp)
                .padding(horizontal = 16.dp)
        ) {
            // Sub-tabs: KEYBOARD | MOUSE | QUICK ACTIONS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InputSubTab.values().forEach { subTab ->
                    val isSelected = state.inputSubTab == subTab
                    val bgColor by animateColorAsState(
                        targetValue = if (isSelected) Color(0xFF0E2843) else DarkCard,
                        label = "subTabBg"
                    )
                    val textColor by animateColorAsState(
                        targetValue = if (isSelected) CyanNeon else TextSlate,
                        label = "subTabTxt"
                    )
                    val borderColor by animateColorAsState(
                        targetValue = if (isSelected) CyanNeon else DarkBorder,
                        label = "subTabBorder"
                    )

                    Surface(
                        onClick = { onSelectSubTab(subTab) },
                        shape = RoundedCornerShape(12.dp),
                        color = bgColor,
                        border = BorderStroke(1.2.dp, borderColor),
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .testTag("subtab_${subTab.name.lowercase()}")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = subTab.label,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        }
                    }
                }
            }

            when (state.inputSubTab) {
                InputSubTab.KEYBOARD -> KeyboardSubScreen(
                    state = state,
                    onSetInputText = onSetInputText,
                    onSendKey = { onSendKey(it, context) },
                    onSendString = { onSendString(context) },
                    onClearInput = onClearInput,
                    onToggleModifier = onToggleModifier
                )
                InputSubTab.MOUSE -> MouseSubScreen(
                    state = state,
                    onMouseDrag = onMouseDrag,
                    onMouseClick = { onMouseClick(it, context) },
                    onMouseScroll = { onMouseScroll(it, context) },
                    onSetMouseSensitivity = onSetMouseSensitivity
                )
                InputSubTab.QUICK_ACTIONS -> QuickActionsSubScreen(
                    onSendKey = { onSendKey(it, context) }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun KeyboardSubScreen(
    state: BduckyUiState,
    onSetInputText: (String) -> Unit,
    onSendKey: (String) -> Unit,
    onSendString: () -> Unit,
    onClearInput: () -> Unit,
    onToggleModifier: (String) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // 1. Text input box + Send / Enter / Clear
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "LIVE STRING TRANSMIT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = BlueAccent,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.keyboardInputText,
                        onValueChange = onSetInputText,
                        placeholder = { Text("Type text to send directly to host...", fontSize = 13.sp, color = Color(0xFF64748B)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_text_field"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = CyanNeon,
                            unfocusedBorderColor = DarkBorder,
                            focusedContainerColor = Color(0xFF0B1220),
                            unfocusedContainerColor = Color(0xFF0B1220)
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { onSendString() }),
                        singleLine = false,
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onSendString,
                            colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = DarkBg),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .testTag("btn_send_string")
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "SEND", fontWeight = FontWeight.Black, fontSize = 12.sp)
                        }

                        Button(
                            onClick = { onSendKey("ENTER") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1), contentColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .testTag("btn_send_enter")
                        ) {
                            Icon(imageVector = Icons.Default.KeyboardReturn, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "ENTER", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = onClearInput,
                            border = BorderStroke(1.dp, DarkBorder),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSlate),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(0.8f)
                                .height(40.dp)
                                .testTag("btn_clear_input")
                        ) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "CLEAR", fontWeight = FontWeight.SemiBold, fontSize = 11.5.sp)
                        }
                    }
                }
            }
        }

        // 2. Modifiers row
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "MODIFIER KEYS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = BlueAccent,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("CTRL", "ALT", "SHIFT", "GUI/WIN").forEach { mod ->
                            val isActive = state.activeModifiers.contains(mod)
                            Surface(
                                onClick = { onToggleModifier(mod) },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isActive) CyanNeon else Color(0xFF0F1A2D),
                                border = BorderStroke(1.2.dp, if (isActive) CyanNeon else DarkBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = mod,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isActive) DarkBg else TextWhite
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Function Keys Grid
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "FUNCTION KEYS (F1–F12, ESC, DEL)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = BlueAccent,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        maxItemsInEachRow = 5
                    ) {
                        val fnKeys = listOf("ESC", "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10", "F11", "F12", "PRTSC", "DEL")
                        fnKeys.forEach { key ->
                            KeyButton(
                                label = key,
                                onClick = { onSendKey(key) },
                                modifier = Modifier.weight(1f, fill = false)
                            )
                        }
                    }
                }
            }
        }

        // 4. Navigation Keys
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "NAVIGATION & EDITING",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = BlueAccent,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        maxItemsInEachRow = 4
                    ) {
                        listOf("TAB", "INS", "HOME", "PGUP", "END", "PGDN", "SPACE", "BACKSPACE").forEach { navKey ->
                            KeyButton(
                                label = navKey,
                                onClick = { onSendKey(navKey) },
                                modifier = Modifier.weight(1f, fill = false)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Arrow Key Cluster
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        KeyButton(
                            label = "▲ UP",
                            onClick = { onSendKey("UP") },
                            modifier = Modifier.width(90.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            KeyButton(label = "◀ LEFT", onClick = { onSendKey("LEFT") }, modifier = Modifier.width(90.dp))
                            KeyButton(label = "▼ DOWN", onClick = { onSendKey("DOWN") }, modifier = Modifier.width(90.dp))
                            KeyButton(label = "▶ RIGHT", onClick = { onSendKey("RIGHT") }, modifier = Modifier.width(90.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KeyButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF0F1B2F),
        border = BorderStroke(1.dp, DarkBorder),
        modifier = modifier
            .height(38.dp)
            .testTag("key_$label")
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 10.dp)
        ) {
            Text(
                text = label,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = TextWhite
            )
        }
    }
}

@Composable
private fun MouseSubScreen(
    state: BduckyUiState,
    onMouseDrag: (Float, Float) -> Unit,
    onMouseClick: (String) -> Unit,
    onMouseScroll: (Int) -> Unit,
    onSetMouseSensitivity: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Touchpad Area
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.2.dp, CyanNeon.copy(alpha = 0.7f)),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .testTag("mouse_touchpad")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0A101C))
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            onMouseDrag(dragAmount.x, dragAmount.y)
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { onMouseClick("LMB") },
                            onDoubleTap = { onMouseClick("Double_LMB") }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E5FF).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = "Trackpad",
                            tint = CyanNeon,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "PRECISION TRACKPAD",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = TextWhite
                    )
                    Text(
                        text = "Drag to steer cursor • Tap for LMB • Double-tap to open",
                        fontSize = 11.5.sp,
                        color = TextSlate,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = "Status: ${state.lastMouseAction}",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = CyanNeon,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        }

        // Left / Middle / Right Click Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onMouseClick("LEFT") },
                colors = ButtonDefaults.buttonColors(containerColor = DarkCard),
                border = BorderStroke(1.5.dp, CyanNeon),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("btn_mouse_lmb")
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "LEFT CLICK", fontWeight = FontWeight.Black, fontSize = 12.sp, color = TextWhite)
                    Text(text = "LMB", fontSize = 10.sp, color = CyanNeon)
                }
            }

            Button(
                onClick = { onMouseClick("MIDDLE") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F1A2E)),
                border = BorderStroke(1.dp, DarkBorder),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(0.7f)
                    .height(52.dp)
                    .testTag("btn_mouse_mmb")
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "WHEEL", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextWhite)
                    Text(text = "MMB", fontSize = 10.sp, color = TextSlate)
                }
            }

            Button(
                onClick = { onMouseClick("RIGHT") },
                colors = ButtonDefaults.buttonColors(containerColor = DarkCard),
                border = BorderStroke(1.5.dp, BlueSecondary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("btn_mouse_rmb")
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "RIGHT CLICK", fontWeight = FontWeight.Black, fontSize = 12.sp, color = TextWhite)
                    Text(text = "RMB", fontSize = 10.sp, color = BlueAccent)
                }
            }
        }

        // Scroll Bar & Sensitivity Row
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, DarkBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Scroll Buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "SCROLL:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BlueAccent)
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = { onMouseScroll(1) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "Scroll Up", tint = TextWhite)
                    }
                    IconButton(
                        onClick = { onMouseScroll(-1) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = "Scroll Down", tint = TextWhite)
                    }
                }

                // Sensitivity
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.width(180.dp)
                ) {
                    Text(text = "DPI", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSlate)
                    Spacer(modifier = Modifier.width(6.dp))
                    Slider(
                        value = state.mouseSensitivity,
                        onValueChange = onSetMouseSensitivity,
                        valueRange = 0.5f..2.5f,
                        colors = SliderDefaults.colors(
                            thumbColor = CyanNeon,
                            activeTrackColor = CyanNeon,
                            inactiveTrackColor = Color(0xFF1E293B)
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionsSubScreen(
    onSendKey: (String) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Media Controls
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "MEDIA CONTROLLER",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = BlueAccent,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ActionButton(icon = Icons.Default.FastRewind, label = "Prev", onClick = { onSendKey("MEDIA_PREV") })
                        ActionButton(icon = Icons.Default.PlayArrow, label = "Play", onClick = { onSendKey("MEDIA_PLAY_PAUSE") })
                        ActionButton(icon = Icons.Default.Pause, label = "Pause", onClick = { onSendKey("MEDIA_PLAY_PAUSE") })
                        ActionButton(icon = Icons.Default.FastForward, label = "Next", onClick = { onSendKey("MEDIA_NEXT") })
                        ActionButton(icon = Icons.Default.VolumeMute, label = "Mute", onClick = { onSendKey("VOLUME_MUTE") })
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onSendKey("VOLUME_DOWN") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F1B2F)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                        ) {
                            Icon(imageVector = Icons.Default.VolumeDown, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("VOL DOWN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        }

                        Button(
                            onClick = { onSendKey("VOLUME_UP") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F1B2F)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                        ) {
                            Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("VOL UP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        }
                    }
                }
            }
        }

        // Window & Desktop Controls
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "WORKSTATION & SYSTEM SHORTCUTS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = BlueAccent,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ShortcutCard(
                            title = "Show Desktop",
                            hotkey = "WIN + D",
                            onClick = { onSendKey("GUI+d") },
                            modifier = Modifier.weight(1f)
                        )
                        ShortcutCard(
                            title = "Lock Screen",
                            hotkey = "WIN + L",
                            onClick = { onSendKey("GUI+l") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ShortcutCard(
                            title = "Switch Window",
                            hotkey = "ALT + TAB",
                            onClick = { onSendKey("ALT+TAB") },
                            modifier = Modifier.weight(1f)
                        )
                        ShortcutCard(
                            title = "Task Manager",
                            hotkey = "CTRL+SHIFT+ESC",
                            onClick = { onSendKey("CTRL+SHIFT+ESC") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ShortcutCard(
                            title = "Copy",
                            hotkey = "CTRL + C",
                            onClick = { onSendKey("CTRL+c") },
                            modifier = Modifier.weight(1f)
                        )
                        ShortcutCard(
                            title = "Paste",
                            hotkey = "CTRL + V",
                            onClick = { onSendKey("CTRL+v") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF0F1B2F),
        border = BorderStroke(1.dp, DarkBorder),
        modifier = Modifier.size(54.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = CyanNeon, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSlate)
        }
    }
}

@Composable
private fun ShortcutCard(
    title: String,
    hotkey: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF0E1726),
        border = BorderStroke(1.dp, DarkBorder),
        modifier = modifier.height(54.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = title, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                Text(text = hotkey, fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = CyanNeon)
            }
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = CyanNeon,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
