package com.example.bluetooth

object HidConstants {

    const val REPORT_ID_KEYBOARD: Byte = 1
    const val REPORT_ID_MOUSE: Byte = 2

    // Modifier bitmasks (Byte 0 of keyboard report)
    const val MOD_LEFT_CTRL: Byte = 0x01
    const val MOD_LEFT_SHIFT: Byte = 0x02
    const val MOD_LEFT_ALT: Byte = 0x04
    const val MOD_LEFT_GUI: Byte = 0x08
    const val MOD_RIGHT_CTRL: Byte = 0x10
    const val MOD_RIGHT_SHIFT: Byte = 0x20
    const val MOD_RIGHT_ALT: Byte = 0x40
    const val MOD_RIGHT_GUI: Byte = 0x80.toByte()

    // Mouse buttons (Byte 0 of mouse report)
    const val MOUSE_BTN_LEFT: Byte = 0x01
    const val MOUSE_BTN_RIGHT: Byte = 0x02
    const val MOUSE_BTN_MIDDLE: Byte = 0x04

    // USB HID Keycodes
    const val KEY_A: Byte = 0x04
    const val KEY_B: Byte = 0x05
    const val KEY_C: Byte = 0x06
    const val KEY_D: Byte = 0x07
    const val KEY_E: Byte = 0x08
    const val KEY_F: Byte = 0x09
    const val KEY_G: Byte = 0x0A
    const val KEY_H: Byte = 0x0B
    const val KEY_I: Byte = 0x0C
    const val KEY_J: Byte = 0x0D
    const val KEY_K: Byte = 0x0E
    const val KEY_L: Byte = 0x0F
    const val KEY_M: Byte = 0x10
    const val KEY_N: Byte = 0x11
    const val KEY_O: Byte = 0x12
    const val KEY_P: Byte = 0x13
    const val KEY_Q: Byte = 0x14
    const val KEY_R: Byte = 0x15
    const val KEY_S: Byte = 0x16
    const val KEY_T: Byte = 0x17
    const val KEY_U: Byte = 0x18
    const val KEY_V: Byte = 0x19
    const val KEY_W: Byte = 0x1A
    const val KEY_X: Byte = 0x1B
    const val KEY_Y: Byte = 0x1C
    const val KEY_Z: Byte = 0x1D

    const val KEY_1: Byte = 0x1E
    const val KEY_2: Byte = 0x1F
    const val KEY_3: Byte = 0x20
    const val KEY_4: Byte = 0x21
    const val KEY_5: Byte = 0x22
    const val KEY_6: Byte = 0x23
    const val KEY_7: Byte = 0x24
    const val KEY_8: Byte = 0x25
    const val KEY_9: Byte = 0x26
    const val KEY_0: Byte = 0x27

    const val KEY_ENTER: Byte = 0x28
    const val KEY_ESC: Byte = 0x29
    const val KEY_BACKSPACE: Byte = 0x2A
    const val KEY_TAB: Byte = 0x2B
    const val KEY_SPACE: Byte = 0x2C
    const val KEY_MINUS: Byte = 0x2D
    const val KEY_EQUAL: Byte = 0x2E
    const val KEY_LEFTBRACE: Byte = 0x2F
    const val KEY_RIGHTBRACE: Byte = 0x30
    const val KEY_BACKSLASH: Byte = 0x31
    const val KEY_SEMICOLON: Byte = 0x33
    const val KEY_APOSTROPHE: Byte = 0x34
    const val KEY_GRAVE: Byte = 0x35
    const val KEY_COMMA: Byte = 0x36
    const val KEY_DOT: Byte = 0x37
    const val KEY_SLASH: Byte = 0x38

    const val KEY_F1: Byte = 0x3A
    const val KEY_F2: Byte = 0x3B
    const val KEY_F3: Byte = 0x3C
    const val KEY_F4: Byte = 0x3D
    const val KEY_F5: Byte = 0x3E
    const val KEY_F6: Byte = 0x3F
    const val KEY_F7: Byte = 0x40
    const val KEY_F8: Byte = 0x41
    const val KEY_F9: Byte = 0x42
    const val KEY_F10: Byte = 0x43
    const val KEY_F11: Byte = 0x44
    const val KEY_F12: Byte = 0x45

    const val KEY_PRINTSCREEN: Byte = 0x46
    const val KEY_SCROLLLOCK: Byte = 0x47
    const val KEY_PAUSE: Byte = 0x48
    const val KEY_INSERT: Byte = 0x49
    const val KEY_HOME: Byte = 0x4A
    const val KEY_PAGEUP: Byte = 0x4B
    const val KEY_DELETE: Byte = 0x4C
    const val KEY_END: Byte = 0x4D
    const val KEY_PAGEDOWN: Byte = 0x4E
    const val KEY_RIGHT: Byte = 0x4F
    const val KEY_LEFT: Byte = 0x50
    const val KEY_DOWN: Byte = 0x51
    const val KEY_UP: Byte = 0x52

    // Media Keys
    const val KEY_MUTE: Byte = 0x7F
    const val KEY_VOL_UP: Byte = 0x80.toByte()
    const val KEY_VOL_DOWN: Byte = 0x81.toByte()

    // Full Composite HID Report Descriptor (Keyboard + Mouse)
    val HID_REPORT_DESCRIPTOR = byteArrayOf(
        // Keyboard (Report ID 1)
        0x05.toByte(), 0x01.toByte(), // Usage Page (Generic Desktop)
        0x09.toByte(), 0x06.toByte(), // Usage (Keyboard)
        0xA1.toByte(), 0x01.toByte(), // Collection (Application)
        0x85.toByte(), 0x01.toByte(), //   Report ID (1)
        0x05.toByte(), 0x07.toByte(), //   Usage Page (Key Codes)
        0x19.toByte(), 0xE0.toByte(), //   Usage Minimum (224)
        0x29.toByte(), 0xE7.toByte(), //   Usage Maximum (231)
        0x15.toByte(), 0x00.toByte(), //   Logical Minimum (0)
        0x25.toByte(), 0x01.toByte(), //   Logical Maximum (1)
        0x75.toByte(), 0x01.toByte(), //   Report Size (1)
        0x95.toByte(), 0x08.toByte(), //   Report Count (8)
        0x81.toByte(), 0x02.toByte(), //   Input (Data, Variable, Absolute) - Modifiers
        0x95.toByte(), 0x01.toByte(), //   Report Count (1)
        0x75.toByte(), 0x08.toByte(), //   Report Size (8)
        0x81.toByte(), 0x01.toByte(), //   Input (Constant) - Reserved
        0x95.toByte(), 0x06.toByte(), //   Report Count (6)
        0x75.toByte(), 0x08.toByte(), //   Report Size (8)
        0x15.toByte(), 0x00.toByte(), //   Logical Minimum (0)
        0x25.toByte(), 0x65.toByte(), //   Logical Maximum (101)
        0x05.toByte(), 0x07.toByte(), //   Usage Page (Key Codes)
        0x19.toByte(), 0x00.toByte(), //   Usage Minimum (0)
        0x29.toByte(), 0x65.toByte(), //   Usage Maximum (101)
        0x81.toByte(), 0x00.toByte(), //   Input (Data, Array) - Keys
        0xC0.toByte(),               // End Collection

        // Mouse (Report ID 2)
        0x05.toByte(), 0x01.toByte(), // Usage Page (Generic Desktop)
        0x09.toByte(), 0x02.toByte(), // Usage (Mouse)
        0xA1.toByte(), 0x01.toByte(), // Collection (Application)
        0x85.toByte(), 0x02.toByte(), //   Report ID (2)
        0x09.toByte(), 0x01.toByte(), //   Usage (Pointer)
        0xA1.toByte(), 0x00.toByte(), //   Collection (Physical)
        0x05.toByte(), 0x09.toByte(), //     Usage Page (Buttons)
        0x19.toByte(), 0x01.toByte(), //     Usage Minimum (1)
        0x29.toByte(), 0x03.toByte(), //     Usage Maximum (3)
        0x15.toByte(), 0x00.toByte(), //     Logical Minimum (0)
        0x25.toByte(), 0x01.toByte(), //     Logical Maximum (1)
        0x95.toByte(), 0x03.toByte(), //     Report Count (3)
        0x75.toByte(), 0x01.toByte(), //     Report Size (1)
        0x81.toByte(), 0x02.toByte(), //     Input (Data, Variable, Absolute) - 3 buttons
        0x95.toByte(), 0x01.toByte(), //     Report Count (1)
        0x75.toByte(), 0x05.toByte(), //     Report Size (5)
        0x81.toByte(), 0x01.toByte(), //     Input (Constant) - padding
        0x05.toByte(), 0x01.toByte(), //     Usage Page (Generic Desktop)
        0x09.toByte(), 0x01.toByte(), //     Usage (Pointer)
        0x09.toByte(), 0x30.toByte(), //     Usage (X)
        0x09.toByte(), 0x31.toByte(), //     Usage (Y)
        0x15.toByte(), 0x81.toByte(), //     Logical Minimum (-127)
        0x25.toByte(), 0x7F.toByte(), //     Logical Maximum (127)
        0x75.toByte(), 0x08.toByte(), //     Report Size (8)
        0x95.toByte(), 0x02.toByte(), //     Report Count (2)
        0x81.toByte(), 0x06.toByte(), //     Input (Data, Variable, Relative) - X, Y
        0x09.toByte(), 0x38.toByte(), //     Usage (Wheel)
        0x15.toByte(), 0x81.toByte(), //     Logical Minimum (-127)
        0x25.toByte(), 0x7F.toByte(), //     Logical Maximum (127)
        0x75.toByte(), 0x08.toByte(), //     Report Size (8)
        0x95.toByte(), 0x01.toByte(), //     Report Count (1)
        0x81.toByte(), 0x06.toByte(), //     Input (Data, Variable, Relative) - Wheel
        0xC0.toByte(),               //   End Collection
        0xC0.toByte()                // End Collection
    )

    data class CharMapping(val modifier: Byte, val keyCode: Byte)

    fun getCharMapping(c: Char): CharMapping? {
        return when (c) {
            in 'a'..'z' -> CharMapping(0, (KEY_A + (c - 'a')).toByte())
            in 'A'..'Z' -> CharMapping(MOD_LEFT_SHIFT, (KEY_A + (c - 'A')).toByte())
            in '1'..'9' -> CharMapping(0, (KEY_1 + (c - '1')).toByte())
            '0' -> CharMapping(0, KEY_0)
            ' ' -> CharMapping(0, KEY_SPACE)
            '\n' -> CharMapping(0, KEY_ENTER)
            '\t' -> CharMapping(0, KEY_TAB)
            '-' -> CharMapping(0, KEY_MINUS)
            '=' -> CharMapping(0, KEY_EQUAL)
            '[' -> CharMapping(0, KEY_LEFTBRACE)
            ']' -> CharMapping(0, KEY_RIGHTBRACE)
            '\\' -> CharMapping(0, KEY_BACKSLASH)
            ';' -> CharMapping(0, KEY_SEMICOLON)
            '\'' -> CharMapping(0, KEY_APOSTROPHE)
            '`' -> CharMapping(0, KEY_GRAVE)
            ',' -> CharMapping(0, KEY_COMMA)
            '.' -> CharMapping(0, KEY_DOT)
            '/' -> CharMapping(0, KEY_SLASH)
            // Shifted
            '!' -> CharMapping(MOD_LEFT_SHIFT, KEY_1)
            '@' -> CharMapping(MOD_LEFT_SHIFT, KEY_2)
            '#' -> CharMapping(MOD_LEFT_SHIFT, KEY_3)
            '$' -> CharMapping(MOD_LEFT_SHIFT, KEY_4)
            '%' -> CharMapping(MOD_LEFT_SHIFT, KEY_5)
            '^' -> CharMapping(MOD_LEFT_SHIFT, KEY_6)
            '&' -> CharMapping(MOD_LEFT_SHIFT, KEY_7)
            '*' -> CharMapping(MOD_LEFT_SHIFT, KEY_8)
            '(' -> CharMapping(MOD_LEFT_SHIFT, KEY_9)
            ')' -> CharMapping(MOD_LEFT_SHIFT, KEY_0)
            '_' -> CharMapping(MOD_LEFT_SHIFT, KEY_MINUS)
            '+' -> CharMapping(MOD_LEFT_SHIFT, KEY_EQUAL)
            '{' -> CharMapping(MOD_LEFT_SHIFT, KEY_LEFTBRACE)
            '}' -> CharMapping(MOD_LEFT_SHIFT, KEY_RIGHTBRACE)
            '|' -> CharMapping(MOD_LEFT_SHIFT, KEY_BACKSLASH)
            ':' -> CharMapping(MOD_LEFT_SHIFT, KEY_SEMICOLON)
            '"' -> CharMapping(MOD_LEFT_SHIFT, KEY_APOSTROPHE)
            '~' -> CharMapping(MOD_LEFT_SHIFT, KEY_GRAVE)
            '<' -> CharMapping(MOD_LEFT_SHIFT, KEY_COMMA)
            '>' -> CharMapping(MOD_LEFT_SHIFT, KEY_DOT)
            '?' -> CharMapping(MOD_LEFT_SHIFT, KEY_SLASH)
            else -> null
        }
    }
}
