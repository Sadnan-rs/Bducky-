package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DefaultMacros
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextSlate
import com.example.ui.theme.TextWhite
import com.example.viewmodel.BduckyUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MacrosTab(
    state: BduckyUiState,
    onSetScriptText: (String) -> Unit,
    onClearScript: () -> Unit,
    onExecuteScript: (Context) -> Unit,
    onStopExecution: () -> Unit,
    onInsertCommand: (String) -> Unit,
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
            contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Script Editor Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.2.dp, DarkBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF0F2642)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Code,
                                        contentDescription = null,
                                        tint = CyanNeon,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "SCRIPT EDITOR",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextWhite,
                                    letterSpacing = 1.sp
                                )
                            }

                            // Line count badge
                            val lineCount = state.currentScript.lines().size
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF0A1322),
                                border = BorderStroke(1.dp, DarkBorder)
                            ) {
                                Text(
                                    text = "$lineCount lines",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = CyanNeon,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Text Editor Box
                        OutlinedTextField(
                            value = state.currentScript,
                            onValueChange = onSetScriptText,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(210.dp)
                                .testTag("script_editor_input"),
                            textStyle = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.5.sp,
                                lineHeight = 18.sp,
                                color = Color(0xFFE2E8F0)
                            ),
                            placeholder = {
                                Text(
                                    text = "// Enter commands here...\nREM Example\nHOTKEY GUI r\nSTRING notepad\nENTER",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    color = Color(0xFF475569)
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color(0xFFE2E8F0),
                                unfocusedTextColor = Color(0xFFCBD5E1),
                                focusedBorderColor = CyanNeon,
                                unfocusedBorderColor = DarkBorder,
                                focusedContainerColor = Color(0xFF0A101C),
                                unfocusedContainerColor = Color(0xFF0A101C)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Execution Progress Bar if running
                        if (state.isScriptExecuting) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Executing: ${state.currentExecutingLine}",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = CyanNeon,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "${(state.scriptProgress * 100).toInt()}%",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { state.scriptProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(CircleShape),
                                    color = CyanNeon,
                                    trackColor = Color(0xFF1E293B)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Action Buttons: EXECUTE / STOP and CLEAR
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (state.isScriptExecuting) {
                                Button(
                                    onClick = onStopExecution,
                                    colors = ButtonDefaults.buttonColors(containerColor = StatusRed, contentColor = Color.White),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1.4f)
                                        .height(44.dp)
                                        .testTag("btn_stop_script")
                                ) {
                                    Icon(imageVector = Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("STOP EXECUTION", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                                }
                            } else {
                                Button(
                                    onClick = { onExecuteScript(context) },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = DarkBg),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1.4f)
                                        .height(44.dp)
                                        .testTag("btn_execute_script")
                                ) {
                                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("EXECUTE", fontWeight = FontWeight.Black, fontSize = 12.5.sp, letterSpacing = 1.sp)
                                }
                            }

                            OutlinedButton(
                                onClick = onClearScript,
                                border = BorderStroke(1.dp, DarkBorder),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSlate),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(0.9f)
                                    .height(44.dp)
                                    .testTag("btn_clear_script")
                            ) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("CLEAR", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // 2. Command Reference Chips
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "COMMAND REFERENCE (TAP TO INSERT)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = BlueAccent,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val commands = listOf(
                                "STRING Hello World" to "Types text",
                                "DELAY 500" to "Wait ms",
                                "HOTKEY GUI r" to "Run prompt",
                                "ENTER" to "Press return",
                                "TAB" to "Tab key",
                                "REPEAT 2" to "Repeat last",
                                "REM // Comment" to "Notes"
                            )

                            commands.forEach { (cmd, hint) ->
                                Surface(
                                    onClick = { onInsertCommand(cmd) },
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF0F1B2F),
                                    border = BorderStroke(1.dp, DarkBorder),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    ) {
                                        Text(
                                            text = cmd.split(" ").first(),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontFamily = FontFamily.Monospace,
                                            color = CyanNeon
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = hint,
                                            fontSize = 9.5.sp,
                                            color = TextSlate
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. Example Scripts Section with Load Buttons
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "EXAMPLE PRODUCTIVITY SCRIPTS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = BlueAccent,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        val exampleScripts = listOf(
                            "Morning Workspace" to DefaultMacros.sampleEditorScript,
                            "Email Signature" to DefaultMacros.libraryMacros.find { it.id == "lib-5" }?.code.orEmpty(),
                            "Open Notepad & Date" to DefaultMacros.libraryMacros.find { it.id == "lib-1" }?.code.orEmpty(),
                            "Lock & Mute Workstation" to DefaultMacros.libraryMacros.find { it.id == "lib-7" }?.code.orEmpty()
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            exampleScripts.forEach { (title, script) ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFF0F1828),
                                    border = BorderStroke(1.dp, DarkBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = title, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                            Text(
                                                text = script.lines().take(2).joinToString(" • "),
                                                fontSize = 10.5.sp,
                                                fontFamily = FontFamily.Monospace,
                                                color = TextSlate,
                                                maxLines = 1
                                            )
                                        }

                                        Button(
                                            onClick = { onSetScriptText(script) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF152A47), contentColor = CyanNeon),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(13.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("LOAD", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Live Transmission Console Log
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF070B14)),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Terminal, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "CONSOLE OUTPUT",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanNeon,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Text(
                                text = "${state.executionLogs.size} logs",
                                fontSize = 10.sp,
                                color = TextSlate,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val lastLogs = state.executionLogs.takeLast(5)
                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            lastLogs.forEach { log ->
                                Text(
                                    text = "> $log",
                                    fontSize = 10.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (log.contains("ERROR") || log.contains("ABORT")) StatusRed else Color(0xFFCBD5E1)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
