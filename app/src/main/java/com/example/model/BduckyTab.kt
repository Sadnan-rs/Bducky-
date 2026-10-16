package com.example.model

enum class BduckyTab(val label: String, val title: String) {
    CONN("CONN", "Connection & HID"),
    INPUT("INPUT", "Remote Input"),
    MACROS("MACROS", "Macro Runner"),
    LIBRARY("LIBRARY", "Macro Library"),
    EDITOR("EDITOR", "Custom Scripts"),
    SETTINGS("CFG", "Configuration"),
    MOBILE("MOBILE", "Mobile Macros")
}

enum class InputSubTab(val label: String) {
    KEYBOARD("KEYBOARD"),
    MOUSE("MOUSE"),
    QUICK_ACTIONS("QUICK ACTIONS")
}
