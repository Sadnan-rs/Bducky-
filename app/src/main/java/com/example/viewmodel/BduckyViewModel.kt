package com.example.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bluetooth.BduckyHidManager
import com.example.bluetooth.HidConstants
import com.example.data.DefaultMacros
import com.example.model.BduckyTab
import com.example.model.BluetoothDeviceItem
import com.example.model.HidProfile
import com.example.model.InputSubTab
import com.example.model.MacroCategory
import com.example.model.MacroItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class BduckyUiState(
    val currentTab: BduckyTab = BduckyTab.CONN,
    val inputSubTab: InputSubTab = InputSubTab.KEYBOARD,

    // Status bar state
    val status: String = "READY",
    val targetName: String = "None",
    val profileTag: String = "K380_HID",
    val txSpeed: String = "0.0 KB/s",
    val txPackets: Int = 0,
    val isRegistered: Boolean = false,
    val isHidServiceRunning: Boolean = false,

    // Bluetooth Hardware & Permissions
    val hasBluetoothPermissions: Boolean = false,
    val isBluetoothAvailable: Boolean = true,
    val isBluetoothEnabled: Boolean = true,
    val pairedDevices: List<BluetoothDeviceItem> = emptyList(),
    val activeDeviceId: String? = null,
    val isRefreshingDevices: Boolean = false,
    val errorMessage: String? = null,

    // Keyboard & Input state
    val keyboardInputText: String = "",
    val activeModifiers: Set<String> = emptySet(),
    val recentKeySent: String? = null,

    // Mouse state
    val mouseSensitivity: Float = 1.0f,
    val mouseX: Float = 0f,
    val mouseY: Float = 0f,
    val lastMouseAction: String = "Idle",

    // Script & Runner state
    val currentScript: String = DefaultMacros.sampleEditorScript,
    val isScriptExecuting: Boolean = false,
    val scriptProgress: Float = 0f,
    val currentExecutingLine: String = "",
    val executionLogs: List<String> = listOf("BDUCKY Rootless HID Engine ready.", "Tap 'Connect' on your paired host (e.g. Infinix Hot 60i) to begin."),

    // Library state
    val librarySearchQuery: String = "",
    val libraryCategoryFilter: MacroCategory = MacroCategory.ALL,
    val libraryPlatformFilter: String = "ALL",
    val libraryMacros: List<MacroItem> = DefaultMacros.libraryMacros,

    // Custom Editor state
    val customMacros: List<MacroItem> = DefaultMacros.customEditorMacros,

    // Settings state
    val hidProfiles: List<HidProfile> = DefaultMacros.hidProfiles,
    val selectedProfile: HidProfile = DefaultMacros.hidProfiles.first(),
    val typingDelayMs: Long = 30L,
    val hapticEnabled: Boolean = true,
    val soundEnabled: Boolean = false,
    val isDarkMode: Boolean = true,

    // Mobile Macros state
    val isTargetMobile: Boolean = true,
    val mobileMacros: List<MacroItem> = DefaultMacros.mobileMacros,
    val mobileSearchQuery: String = ""
)

class BduckyViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BduckyUiState())
    val uiState: StateFlow<BduckyUiState> = _uiState.asStateFlow()

    private var hidManager: BduckyHidManager? = null
    private var executionJob: Job? = null

    fun initHid(context: Context) {
        if (hidManager == null) {
            hidManager = BduckyHidManager(context.applicationContext).apply {
                setCallback(object : BduckyHidManager.Callback {
                    override fun onServiceRegistered(registered: Boolean) {
                        _uiState.update {
                            it.copy(
                                isRegistered = registered,
                                isHidServiceRunning = registered,
                                status = if (registered) (if (it.activeDeviceId != null) "CONNECTED" else "READY") else "OFFLINE"
                            )
                        }
                    }

                    @SuppressLint("MissingPermission")
                    override fun onDeviceConnected(device: BluetoothDevice) {
                        val name = try { device.name ?: device.address } catch (_: SecurityException) { device.address }
                        _uiState.update { state ->
                            val updatedDevices = state.pairedDevices.map {
                                it.copy(isConnected = it.address.equals(device.address, ignoreCase = true))
                            }
                            state.copy(
                                activeDeviceId = device.address,
                                targetName = name,
                                status = "CONNECTED",
                                pairedDevices = updatedDevices,
                                executionLogs = (state.executionLogs + "[LINK] Connected to host: $name").takeLast(60)
                            )
                        }
                    }

                    override fun onDeviceDisconnected(device: BluetoothDevice?) {
                        _uiState.update { state ->
                            val updatedDevices = state.pairedDevices.map { it.copy(isConnected = false) }
                            state.copy(
                                activeDeviceId = null,
                                targetName = "None",
                                status = "READY",
                                pairedDevices = updatedDevices,
                                executionLogs = (state.executionLogs + "[LINK] Host disconnected.").takeLast(60)
                            )
                        }
                    }

                    override fun onLog(message: String) {
                        _uiState.update {
                            it.copy(executionLogs = (it.executionLogs + message).takeLast(60))
                        }
                    }

                    override fun onError(message: String) {
                        _uiState.update {
                            it.copy(
                                errorMessage = message,
                                executionLogs = (it.executionLogs + "[ERROR] $message").takeLast(60)
                            )
                        }
                    }
                })
                initialize()
            }
        }
    }

    fun selectTab(tab: BduckyTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun selectInputSubTab(subTab: InputSubTab) {
        _uiState.update { it.copy(inputSubTab = subTab) }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun updatePermissionsState(granted: Boolean, context: Context? = null) {
        _uiState.update { it.copy(hasBluetoothPermissions = granted) }
        if (granted && context != null) {
            initHid(context)
            refreshRealDevices(context)
        }
    }

    @SuppressLint("MissingPermission")
    fun refreshRealDevices(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshingDevices = true) }

            try {
                val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
                val adapter = bluetoothManager?.adapter ?: BluetoothAdapter.getDefaultAdapter()

                if (adapter == null) {
                    _uiState.update {
                        it.copy(
                            isBluetoothAvailable = false,
                            isRefreshingDevices = false,
                            errorMessage = "Bluetooth hardware not detected on this device."
                        )
                    }
                    return@launch
                }

                val isEnabled = adapter.isEnabled
                _uiState.update { it.copy(isBluetoothEnabled = isEnabled, isBluetoothAvailable = true) }

                if (!isEnabled) {
                    _uiState.update {
                        it.copy(
                            isRefreshingDevices = false,
                            errorMessage = "Bluetooth is turned off. Please turn on Bluetooth in settings."
                        )
                    }
                    return@launch
                }

                val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
                } else {
                    true
                }

                _uiState.update { it.copy(hasBluetoothPermissions = hasPermission) }

                if (!hasPermission) {
                    _uiState.update {
                        it.copy(
                            isRefreshingDevices = false,
                            errorMessage = "Bluetooth Nearby Devices permission is required to list paired hosts."
                        )
                    }
                    return@launch
                }

                val bonded: Set<BluetoothDevice> = try {
                    adapter.bondedDevices ?: emptySet()
                } catch (e: SecurityException) {
                    emptySet()
                }

                val activeId = _uiState.value.activeDeviceId
                val deviceList = bonded.map { dev ->
                    val type = when (dev.bluetoothClass?.majorDeviceClass) {
                        BluetoothClass.Device.Major.COMPUTER -> "PC / Workstation"
                        BluetoothClass.Device.Major.PHONE -> "Smartphone / Tablet"
                        BluetoothClass.Device.Major.AUDIO_VIDEO -> "Audio Device"
                        else -> "Bluetooth Host"
                    }
                    BluetoothDeviceItem(
                        id = dev.address,
                        name = try { dev.name ?: "Unknown Device" } catch (_: SecurityException) { "Host (${dev.address.takeLast(5)})" },
                        address = dev.address,
                        deviceType = type,
                        isPaired = true,
                        isConnected = dev.address.equals(activeId, ignoreCase = true)
                    )
                }

                _uiState.update {
                    it.copy(
                        pairedDevices = deviceList,
                        isRefreshingDevices = false,
                        errorMessage = null,
                        executionLogs = (it.executionLogs + "[BLUETOOTH] Found ${deviceList.size} paired hosts.").takeLast(60)
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isRefreshingDevices = false,
                        errorMessage = "Bluetooth scan error: ${e.message}"
                    )
                }
            }
        }
    }

    fun toggleHidService() {
        if (_uiState.value.isHidServiceRunning) {
            hidManager?.unregisterApp()
        } else {
            hidManager?.registerApp()
        }
    }

    fun registerHid() {
        hidManager?.registerApp()
    }

    fun unregisterHid() {
        hidManager?.unregisterApp()
    }

    @SuppressLint("MissingPermission")
    fun connectDevice(deviceId: String, context: Context? = null) {
        val adapter = BluetoothAdapter.getDefaultAdapter() ?: return
        try {
            val device = adapter.getRemoteDevice(deviceId)
            hidManager?.connectToDevice(device)
            _uiState.update { state ->
                val name = try { device.name ?: deviceId } catch (_: SecurityException) { deviceId }
                val updated = state.pairedDevices.map {
                    it.copy(isConnected = it.address.equals(deviceId, ignoreCase = true))
                }
                state.copy(
                    activeDeviceId = deviceId,
                    targetName = name,
                    status = "CONNECTED",
                    pairedDevices = updated,
                    executionLogs = (state.executionLogs + "[LINK] Connecting to $name...").takeLast(60)
                )
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(errorMessage = "Cannot connect to $deviceId: ${e.message}") }
        }
    }

    fun disconnectDevice() {
        hidManager?.disconnect()
        _uiState.update { state ->
            val updated = state.pairedDevices.map { it.copy(isConnected = false) }
            state.copy(
                activeDeviceId = null,
                targetName = "None",
                status = "READY",
                pairedDevices = updated,
                executionLogs = (state.executionLogs + "[LINK] Target host disconnected.").takeLast(60)
            )
        }
    }

    // Input handlers
    fun setKeyboardInputText(text: String) {
        _uiState.update { it.copy(keyboardInputText = text) }
    }

    fun toggleModifier(modifier: String) {
        _uiState.update { current ->
            val set = current.activeModifiers.toMutableSet()
            if (set.contains(modifier)) set.remove(modifier) else set.add(modifier)
            current.copy(activeModifiers = set)
        }
    }

    fun sendKey(key: String, context: Context? = null) {
        triggerHaptic(context)
        val modSet = _uiState.value.activeModifiers
        var modByte: Byte = 0
        if (modSet.contains("CTRL")) modByte = (modByte.toInt() or HidConstants.MOD_LEFT_CTRL.toInt()).toByte()
        if (modSet.contains("SHIFT")) modByte = (modByte.toInt() or HidConstants.MOD_LEFT_SHIFT.toInt()).toByte()
        if (modSet.contains("ALT")) modByte = (modByte.toInt() or HidConstants.MOD_LEFT_ALT.toInt()).toByte()
        if (modSet.contains("GUI/WIN") || modSet.contains("WIN")) modByte = (modByte.toInt() or HidConstants.MOD_LEFT_GUI.toInt()).toByte()

        val parsed = parseKeyString(key)
        val combinedMod = (modByte.toInt() or (parsed?.first?.toInt() ?: 0)).toByte()
        val keyCode = parsed?.second ?: HidConstants.KEY_SPACE

        viewModelScope.launch {
            hidManager?.sendKey(combinedMod, keyCode, _uiState.value.typingDelayMs)
        }

        _uiState.update {
            it.copy(
                txPackets = it.txPackets + 1,
                txSpeed = "1.8 KB/s",
                recentKeySent = key,
                activeModifiers = emptySet(),
                executionLogs = (it.executionLogs + "[TX KEY] $key").takeLast(60)
            )
        }
    }

    fun sendInputString(context: Context? = null) {
        val text = _uiState.value.keyboardInputText
        if (text.isBlank()) return
        triggerHaptic(context)

        viewModelScope.launch {
            hidManager?.sendString(text, _uiState.value.typingDelayMs)
        }

        _uiState.update {
            it.copy(
                keyboardInputText = "",
                txPackets = it.txPackets + text.length,
                txSpeed = "${(text.length * 0.4).coerceAtLeast(0.5)} KB/s",
                recentKeySent = "STRING \"$text\"",
                executionLogs = (it.executionLogs + "[TX STR] \"$text\"").takeLast(60)
            )
        }
    }

    fun clearInputText() {
        _uiState.update { it.copy(keyboardInputText = "") }
    }

    fun sendMouseDrag(dx: Float, dy: Float) {
        val factor = _uiState.value.mouseSensitivity * 1.5f
        val deltaX = (dx * factor).toInt()
        val deltaY = (dy * factor).toInt()

        hidManager?.sendMouseMove(deltaX, deltaY)

        _uiState.update {
            it.copy(
                mouseX = it.mouseX + deltaX,
                mouseY = it.mouseY + deltaY,
                txPackets = it.txPackets + 1,
                lastMouseAction = "Move ($deltaX, $deltaY)"
            )
        }
    }

    fun sendMouseClick(button: String, context: Context? = null) {
        triggerHaptic(context)
        val btnMask = when (button) {
            "RIGHT", "RMB" -> HidConstants.MOUSE_BTN_RIGHT
            "MIDDLE", "MMB" -> HidConstants.MOUSE_BTN_MIDDLE
            else -> HidConstants.MOUSE_BTN_LEFT
        }

        viewModelScope.launch {
            hidManager?.sendMouseClick(btnMask, 30L)
        }

        _uiState.update {
            it.copy(
                txPackets = it.txPackets + 1,
                lastMouseAction = "Click $button",
                executionLogs = (it.executionLogs + "[TX MOUSE] Click $button").takeLast(60)
            )
        }
    }

    fun sendMouseScroll(delta: Int, context: Context? = null) {
        triggerHaptic(context)
        val scrollAmount = delta * 3
        hidManager?.sendMouseScroll(scrollAmount)

        _uiState.update {
            it.copy(
                txPackets = it.txPackets + 1,
                lastMouseAction = "Scroll $delta",
                executionLogs = (it.executionLogs + "[TX SCROLL] Delta $delta").takeLast(60)
            )
        }
    }

    fun setMouseSensitivity(sensitivity: Float) {
        _uiState.update { it.copy(mouseSensitivity = sensitivity) }
    }

    // Script runner
    fun setScriptText(code: String) {
        _uiState.update { it.copy(currentScript = code) }
    }

    fun clearScriptText() {
        _uiState.update { it.copy(currentScript = "") }
    }

    fun insertCommandToScript(cmd: String) {
        _uiState.update {
            val existing = it.currentScript
            val newCode = if (existing.isEmpty() || existing.endsWith("\n")) {
                existing + cmd + "\n"
            } else {
                existing + "\n" + cmd + "\n"
            }
            it.copy(currentScript = newCode)
        }
    }

    fun executeCurrentScript(context: Context? = null) {
        val script = _uiState.value.currentScript
        if (script.isBlank()) return

        executionJob?.cancel()
        executionJob = viewModelScope.launch {
            val isConnected = _uiState.value.activeDeviceId != null
            _uiState.update {
                it.copy(
                    isScriptExecuting = true,
                    scriptProgress = 0f,
                    status = "TRANSMITTING",
                    executionLogs = it.executionLogs + if (isConnected) {
                        "[RUN] Executing DuckyScript to host ${_uiState.value.targetName}..."
                    } else {
                        "[WARN] Executing script (Note: No Bluetooth host connected. Connect to Infinix Hot 60i in CONN tab!)"
                    }
                )
            }

            val lines = script.lines().filter { it.isNotBlank() }
            val total = lines.size.coerceAtLeast(1)

            for ((index, line) in lines.withIndex()) {
                val trimmed = line.trim()
                _uiState.update {
                    it.copy(
                        currentExecutingLine = trimmed,
                        scriptProgress = (index + 1).toFloat() / total,
                        txPackets = it.txPackets + 2,
                        txSpeed = "3.2 KB/s",
                        executionLogs = (it.executionLogs + "[EXEC] $trimmed").takeLast(60)
                    )
                }

                // Execute actual HID command
                if (trimmed.startsWith("STRING ")) {
                    val textToType = trimmed.removePrefix("STRING ")
                    hidManager?.sendString(textToType, _uiState.value.typingDelayMs)
                } else if (trimmed.startsWith("DELAY")) {
                    val parts = trimmed.split("\\s+".toRegex())
                    val delayMs = parts.getOrNull(1)?.toLongOrNull()?.coerceIn(10, 5000) ?: 250L
                    delay(delayMs)
                } else if (trimmed.equals("ENTER", ignoreCase = true)) {
                    hidManager?.sendKey(0, HidConstants.KEY_ENTER, _uiState.value.typingDelayMs)
                } else if (trimmed.equals("TAB", ignoreCase = true)) {
                    hidManager?.sendKey(0, HidConstants.KEY_TAB, _uiState.value.typingDelayMs)
                } else if (trimmed.equals("SPACE", ignoreCase = true)) {
                    hidManager?.sendKey(0, HidConstants.KEY_SPACE, _uiState.value.typingDelayMs)
                } else if (trimmed.startsWith("HOTKEY ") || trimmed.startsWith("GUI ")) {
                    val keyPart = if (trimmed.startsWith("HOTKEY ")) trimmed.removePrefix("HOTKEY ") else trimmed
                    val parsed = parseHotkey(keyPart)
                    if (parsed != null) {
                        hidManager?.sendKey(parsed.first, parsed.second, _uiState.value.typingDelayMs)
                    }
                } else if (trimmed.startsWith("REPEAT ")) {
                    val count = trimmed.removePrefix("REPEAT ").toIntOrNull()?.coerceIn(1, 20) ?: 1
                    // repeat previous line
                    repeat(count) {
                        hidManager?.sendKey(0, HidConstants.KEY_ENTER, _uiState.value.typingDelayMs)
                    }
                } else if (!trimmed.startsWith("REM") && !trimmed.startsWith("//")) {
                    val parsed = parseKeyString(trimmed)
                    if (parsed != null) {
                        hidManager?.sendKey(parsed.first, parsed.second, _uiState.value.typingDelayMs)
                    }
                }

                triggerHaptic(context)
                delay(_uiState.value.typingDelayMs)
            }

            _uiState.update {
                it.copy(
                    isScriptExecuting = false,
                    scriptProgress = 1f,
                    status = if (it.activeDeviceId != null) "CONNECTED" else "READY",
                    txSpeed = "0.0 KB/s",
                    currentExecutingLine = "Execution completed successfully.",
                    executionLogs = it.executionLogs + "[DONE] Script transmission finished."
                )
            }
        }
    }

    fun stopExecution() {
        executionJob?.cancel()
        _uiState.update {
            it.copy(
                isScriptExecuting = false,
                status = if (it.activeDeviceId != null) "CONNECTED" else "READY",
                currentExecutingLine = "Execution aborted.",
                executionLogs = it.executionLogs + "[ABORT] Script stopped by user."
            )
        }
    }

    // Helper to map key string to modifier and keycode
    private fun parseKeyString(key: String): Pair<Byte, Byte>? {
        return when (key.uppercase()) {
            "ENTER" -> 0.toByte() to HidConstants.KEY_ENTER
            "ESC" -> 0.toByte() to HidConstants.KEY_ESC
            "BACKSPACE" -> 0.toByte() to HidConstants.KEY_BACKSPACE
            "TAB" -> 0.toByte() to HidConstants.KEY_TAB
            "SPACE" -> 0.toByte() to HidConstants.KEY_SPACE
            "DEL", "DELETE" -> 0.toByte() to HidConstants.KEY_DELETE
            "INS" -> 0.toByte() to HidConstants.KEY_INSERT
            "HOME" -> 0.toByte() to HidConstants.KEY_HOME
            "END" -> 0.toByte() to HidConstants.KEY_END
            "PGUP" -> 0.toByte() to HidConstants.KEY_PAGEUP
            "PGDN" -> 0.toByte() to HidConstants.KEY_PAGEDOWN
            "UP" -> 0.toByte() to HidConstants.KEY_UP
            "DOWN" -> 0.toByte() to HidConstants.KEY_DOWN
            "LEFT" -> 0.toByte() to HidConstants.KEY_LEFT
            "RIGHT" -> 0.toByte() to HidConstants.KEY_RIGHT
            "PRTSC" -> 0.toByte() to HidConstants.KEY_PRINTSCREEN
            "F1" -> 0.toByte() to HidConstants.KEY_F1
            "F2" -> 0.toByte() to HidConstants.KEY_F2
            "F3" -> 0.toByte() to HidConstants.KEY_F3
            "F4" -> 0.toByte() to HidConstants.KEY_F4
            "F5" -> 0.toByte() to HidConstants.KEY_F5
            "F6" -> 0.toByte() to HidConstants.KEY_F6
            "F7" -> 0.toByte() to HidConstants.KEY_F7
            "F8" -> 0.toByte() to HidConstants.KEY_F8
            "F9" -> 0.toByte() to HidConstants.KEY_F9
            "F10" -> 0.toByte() to HidConstants.KEY_F10
            "F11" -> 0.toByte() to HidConstants.KEY_F11
            "F12" -> 0.toByte() to HidConstants.KEY_F12
            "MEDIA_PLAY_PAUSE" -> 0.toByte() to HidConstants.KEY_MUTE
            "VOLUME_UP" -> 0.toByte() to HidConstants.KEY_VOL_UP
            "VOLUME_DOWN" -> 0.toByte() to HidConstants.KEY_VOL_DOWN
            "VOLUME_MUTE" -> 0.toByte() to HidConstants.KEY_MUTE
            "GUI+D" -> HidConstants.MOD_LEFT_GUI to HidConstants.KEY_D
            "GUI+L" -> HidConstants.MOD_LEFT_GUI to HidConstants.KEY_L
            "GUI+R" -> HidConstants.MOD_LEFT_GUI to HidConstants.KEY_R
            "ALT+TAB" -> HidConstants.MOD_LEFT_ALT to HidConstants.KEY_TAB
            "CTRL+C" -> HidConstants.MOD_LEFT_CTRL to HidConstants.KEY_C
            "CTRL+V" -> HidConstants.MOD_LEFT_CTRL to HidConstants.KEY_V
            "CTRL+Z" -> HidConstants.MOD_LEFT_CTRL to HidConstants.KEY_Z
            "CTRL+SHIFT+ESC" -> ((HidConstants.MOD_LEFT_CTRL.toInt() or HidConstants.MOD_LEFT_SHIFT.toInt()).toByte()) to HidConstants.KEY_ESC
            else -> parseHotkey(key)
        }
    }

    private fun parseHotkey(hotkey: String): Pair<Byte, Byte>? {
        val parts = hotkey.split("[ +]+".toRegex()).map { it.uppercase().trim() }
        var modifier: Byte = 0
        var keycode: Byte = HidConstants.KEY_SPACE

        for (part in parts) {
            when (part) {
                "CTRL" -> modifier = (modifier.toInt() or HidConstants.MOD_LEFT_CTRL.toInt()).toByte()
                "SHIFT" -> modifier = (modifier.toInt() or HidConstants.MOD_LEFT_SHIFT.toInt()).toByte()
                "ALT" -> modifier = (modifier.toInt() or HidConstants.MOD_LEFT_ALT.toInt()).toByte()
                "GUI", "WIN", "WINDOWS" -> modifier = (modifier.toInt() or HidConstants.MOD_LEFT_GUI.toInt()).toByte()
                "ENTER" -> keycode = HidConstants.KEY_ENTER
                "TAB" -> keycode = HidConstants.KEY_TAB
                "ESC" -> keycode = HidConstants.KEY_ESC
                "R" -> keycode = HidConstants.KEY_R
                "C" -> keycode = HidConstants.KEY_C
                "V" -> keycode = HidConstants.KEY_V
                "D" -> keycode = HidConstants.KEY_D
                "L" -> keycode = HidConstants.KEY_L
                "S" -> keycode = HidConstants.KEY_S
                else -> {
                    if (part.length == 1) {
                        val m = HidConstants.getCharMapping(part[0].lowercaseChar())
                        if (m != null) keycode = m.keyCode
                    }
                }
            }
        }
        return modifier to keycode
    }

    // Library & Filter
    fun setLibrarySearch(query: String) {
        _uiState.update { it.copy(librarySearchQuery = query) }
    }

    fun setLibraryCategory(category: MacroCategory) {
        _uiState.update { it.copy(libraryCategoryFilter = category) }
    }

    fun setLibraryPlatform(platform: String) {
        _uiState.update { it.copy(libraryPlatformFilter = platform) }
    }

    fun toggleFavoriteMacro(macroId: String) {
        _uiState.update { state ->
            val updatedLib = state.libraryMacros.map {
                if (it.id == macroId) it.copy(isFavorite = !it.isFavorite) else it
            }
            val updatedCustom = state.customMacros.map {
                if (it.id == macroId) it.copy(isFavorite = !it.isFavorite) else it
            }
            val updatedMobile = state.mobileMacros.map {
                if (it.id == macroId) it.copy(isFavorite = !it.isFavorite) else it
            }
            state.copy(
                libraryMacros = updatedLib,
                customMacros = updatedCustom,
                mobileMacros = updatedMobile
            )
        }
    }

    // Custom Macros CRUD
    fun addCustomMacro(name: String, description: String, category: MacroCategory, code: String) {
        val newMacro = MacroItem(
            id = "custom-${UUID.randomUUID().toString().take(6)}",
            name = name,
            description = description,
            category = category,
            code = code,
            isFavorite = false
        )
        _uiState.update {
            it.copy(
                customMacros = listOf(newMacro) + it.customMacros,
                executionLogs = it.executionLogs + "[EDITOR] Saved new macro: $name"
            )
        }
    }

    fun updateCustomMacro(updated: MacroItem) {
        _uiState.update { state ->
            val updatedList = state.customMacros.map {
                if (it.id == updated.id) updated else it
            }
            state.copy(customMacros = updatedList)
        }
    }

    fun deleteCustomMacro(macroId: String) {
        _uiState.update { state ->
            state.copy(customMacros = state.customMacros.filterNot { it.id == macroId })
        }
    }

    // Settings
    fun selectProfile(profile: HidProfile) {
        _uiState.update {
            it.copy(
                selectedProfile = profile,
                profileTag = profile.name.take(10).uppercase().replace(" ", "_"),
                executionLogs = it.executionLogs + "[CFG] Switched profile to ${profile.name}"
            )
        }
    }

    fun setTypingDelay(delayMs: Long) {
        _uiState.update { it.copy(typingDelayMs = delayMs) }
    }

    fun toggleHaptic() {
        _uiState.update { it.copy(hapticEnabled = !it.hapticEnabled) }
    }

    fun toggleSound() {
        _uiState.update { it.copy(soundEnabled = !it.soundEnabled) }
    }

    fun toggleDarkMode() {
        _uiState.update { it.copy(isDarkMode = !it.isDarkMode) }
    }

    // Mobile Target
    fun toggleTargetMobile() {
        _uiState.update { it.copy(isTargetMobile = !it.isTargetMobile) }
    }

    fun setMobileSearch(query: String) {
        _uiState.update { it.copy(mobileSearchQuery = query) }
    }

    fun addMobileMacro(name: String, description: String, code: String) {
        val newMacro = MacroItem(
            id = "mob-${UUID.randomUUID().toString().take(6)}",
            name = name,
            description = description,
            category = MacroCategory.MOBILE,
            code = code,
            isMobile = true
        )
        _uiState.update {
            it.copy(mobileMacros = listOf(newMacro) + it.mobileMacros)
        }
    }

    private fun triggerHaptic(context: Context?) {
        if (!_uiState.value.hapticEnabled || context == null) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(15)
                }
            }
        } catch (_: Exception) {
            // Ignore if vibrator not available
        }
    }

    override fun onCleared() {
        super.onCleared()
        hidManager?.release()
    }
}
