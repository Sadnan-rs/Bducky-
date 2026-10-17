package com.example.model

enum class MacroCategory {
    ALL, PRODUCTIVITY, MEDIA, SYSTEM, CUSTOM, MOBILE
}

enum class MacroDifficulty(val label: String) {
    EASY("Easy"),
    MEDIUM("Medium"),
    ADVANCED("Advanced"),
    CRITICAL("Crit")
}

data class MacroItem(
    val id: String,
    val name: String,
    val description: String,
    val category: MacroCategory,
    val difficulty: MacroDifficulty = MacroDifficulty.EASY,
    val code: String,
    val isFavorite: Boolean = false,
    val isMobile: Boolean = false,
    val platform: String = "ALL", // WIN, MAC, LNX, ANDROID, ALL
    val executionCount: Int = 0
)

data class BluetoothDeviceItem(
    val id: String,
    val name: String,
    val address: String,
    val deviceType: String, // "Windows PC", "MacBook", "Android", "Linux"
    val isPaired: Boolean,
    val isConnected: Boolean,
    val isConnecting: Boolean = false,
    val signalStrength: Int = -60 // dBm
)

data class HidProfile(
    val id: String,
    val name: String,
    val description: String,
    val vendorId: String,
    val productId: String,
    val iconName: String
)
