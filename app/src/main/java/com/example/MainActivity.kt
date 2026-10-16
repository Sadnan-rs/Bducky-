package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.BduckyTab
import com.example.ui.components.BduckyTopBar
import com.example.ui.screens.ConnTab
import com.example.ui.screens.EditorTab
import com.example.ui.screens.InputTab
import com.example.ui.screens.LibraryTab
import com.example.ui.screens.MacrosTab
import com.example.ui.screens.MobileTab
import com.example.ui.screens.SettingsTab
import com.example.ui.theme.DarkBg
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.BduckyViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val bduckyViewModel: BduckyViewModel = viewModel()

            MyApplicationTheme(darkTheme = true) {
                BduckyMainApp(viewModel = bduckyViewModel)
            }
        }
    }
}

@Composable
fun BduckyMainApp(
    viewModel: BduckyViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Required runtime Bluetooth permissions
    val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN
        )
    } else {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val allGranted = results.values.all { it }
        viewModel.updatePermissionsState(allGranted, context)
        if (allGranted) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Bluetooth permissions granted. Ready to connect.")
            }
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Bluetooth permission is required to list paired hosts.")
            }
        }
    }

    // Check permissions and load real bonded devices on start
    LaunchedEffect(Unit) {
        val allGranted = requiredPermissions.all { perm ->
            ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED
        }
        viewModel.updatePermissionsState(allGranted, context)
        if (!allGranted) {
            permissionLauncher.launch(requiredPermissions)
        }
    }

    // Handle system back navigation to return to CONN if on another tab
    if (uiState.currentTab != BduckyTab.CONN) {
        BackHandler {
            viewModel.selectTab(BduckyTab.CONN)
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            BduckyTopBar(
                status = uiState.status,
                targetName = uiState.targetName,
                profileTag = uiState.profileTag,
                txSpeed = uiState.txSpeed,
                txPackets = uiState.txPackets,
                isRegistered = uiState.isRegistered,
                currentTab = uiState.currentTab,
                onTabSelected = { viewModel.selectTab(it) }
            )
        },
        containerColor = DarkBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBg)
        ) {
            AnimatedContent(
                targetState = uiState.currentTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "tabTransition"
            ) { tab ->
                when (tab) {
                    BduckyTab.CONN -> ConnTab(
                        state = uiState,
                        onToggleHidService = { viewModel.toggleHidService() },
                        onRegisterHid = { viewModel.registerHid() },
                        onUnregisterHid = { viewModel.unregisterHid() },
                        onRefreshDevices = { viewModel.refreshRealDevices(it) },
                        onConnectDevice = { viewModel.connectDevice(it, context) },
                        onDisconnectDevice = { viewModel.disconnectDevice() },
                        onDismissError = { viewModel.dismissError() },
                        onRequestPermissions = { permissionLauncher.launch(requiredPermissions) }
                    )

                    BduckyTab.INPUT -> InputTab(
                        state = uiState,
                        onSelectSubTab = { viewModel.selectInputSubTab(it) },
                        onSetInputText = { viewModel.setKeyboardInputText(it) },
                        onSendKey = { key, ctx -> viewModel.sendKey(key, ctx) },
                        onSendString = { ctx -> viewModel.sendInputString(ctx) },
                        onClearInput = { viewModel.clearInputText() },
                        onToggleModifier = { viewModel.toggleModifier(it) },
                        onMouseDrag = { dx, dy -> viewModel.sendMouseDrag(dx, dy) },
                        onMouseClick = { btn, ctx -> viewModel.sendMouseClick(btn, ctx) },
                        onMouseScroll = { delta, ctx -> viewModel.sendMouseScroll(delta, ctx) },
                        onSetMouseSensitivity = { viewModel.setMouseSensitivity(it) }
                    )

                    BduckyTab.MACROS -> MacrosTab(
                        state = uiState,
                        onSetScriptText = { viewModel.setScriptText(it) },
                        onClearScript = { viewModel.clearScriptText() },
                        onExecuteScript = { ctx -> viewModel.executeCurrentScript(ctx) },
                        onStopExecution = { viewModel.stopExecution() },
                        onInsertCommand = { viewModel.insertCommandToScript(it) }
                    )

                    BduckyTab.LIBRARY -> LibraryTab(
                        state = uiState,
                        onSearchChange = { viewModel.setLibrarySearch(it) },
                        onCategoryChange = { viewModel.setLibraryCategory(it) },
                        onPlatformChange = { viewModel.setLibraryPlatform(it) },
                        onToggleFavorite = { viewModel.toggleFavoriteMacro(it) },
                        onLoadInEditor = { code ->
                            viewModel.setScriptText(code)
                            viewModel.selectTab(BduckyTab.MACROS)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Macro loaded into Editor")
                            }
                        },
                        onExecuteMacro = { macro, ctx ->
                            viewModel.setScriptText(macro.code)
                            viewModel.executeCurrentScript(ctx)
                            Toast.makeText(context, "Transmitting: ${macro.name}", Toast.LENGTH_SHORT).show()
                        }
                    )

                    BduckyTab.EDITOR -> EditorTab(
                        state = uiState,
                        onToggleFavorite = { viewModel.toggleFavoriteMacro(it) },
                        onDeleteMacro = { viewModel.deleteCustomMacro(it) },
                        onAddMacro = { name, desc, cat, code -> viewModel.addCustomMacro(name, desc, cat, code) },
                        onUpdateMacro = { viewModel.updateCustomMacro(it) },
                        onExecuteMacro = { macro, ctx ->
                            viewModel.setScriptText(macro.code)
                            viewModel.executeCurrentScript(ctx)
                            Toast.makeText(context, "Transmitting custom: ${macro.name}", Toast.LENGTH_SHORT).show()
                        }
                    )

                    BduckyTab.SETTINGS -> SettingsTab(
                        state = uiState,
                        onSelectProfile = { viewModel.selectProfile(it) },
                        onSetTypingDelay = { viewModel.setTypingDelay(it) },
                        onToggleHaptic = { viewModel.toggleHaptic() },
                        onToggleSound = { viewModel.toggleSound() },
                        onToggleDarkMode = { viewModel.toggleDarkMode() }
                    )

                    BduckyTab.MOBILE -> MobileTab(
                        state = uiState,
                        onToggleTargetMobile = { viewModel.toggleTargetMobile() },
                        onSearchChange = { viewModel.setMobileSearch(it) },
                        onToggleFavorite = { viewModel.toggleFavoriteMacro(it) },
                        onExecuteMacro = { macro, ctx ->
                            viewModel.setScriptText(macro.code)
                            viewModel.executeCurrentScript(ctx)
                            Toast.makeText(context, "Transmitting on Mobile: ${macro.name}", Toast.LENGTH_SHORT).show()
                        },
                        onLoadInEditor = { code ->
                            viewModel.setScriptText(code)
                            viewModel.selectTab(BduckyTab.MACROS)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Loaded mobile script into editor")
                            }
                        },
                        onAddMobileMacro = { name, desc, code -> viewModel.addMobileMacro(name, desc, code) }
                    )
                }
            }
        }
    }
}
