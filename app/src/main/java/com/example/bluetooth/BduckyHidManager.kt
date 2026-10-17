package com.example.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHidDevice
import android.bluetooth.BluetoothHidDeviceAppQosSettings
import android.bluetooth.BluetoothHidDeviceAppSdpSettings
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.os.Build
import android.util.Log
import kotlinx.coroutines.delay
import java.util.concurrent.Executors

class BduckyHidManager(private val context: Context) {

    interface Callback {
        fun onServiceRegistered(registered: Boolean)
        fun onDeviceConnecting(device: BluetoothDevice?)
        fun onDeviceConnected(device: BluetoothDevice)
        fun onDeviceDisconnected(device: BluetoothDevice?)
        fun onLog(message: String)
        fun onError(message: String)
    }

    private var callback: Callback? = null
    private var bluetoothHidDevice: BluetoothHidDevice? = null
    var connectedDevice: BluetoothDevice? = null
        private set
    var isRegistered: Boolean = false
        private set

    private val executor = Executors.newSingleThreadExecutor()

    fun setCallback(cb: Callback) {
        this.callback = cb
    }

    @SuppressLint("MissingPermission")
    fun initialize() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            callback?.onError("Bluetooth HID Device requires Android 9 (Pie) or higher.")
            return
        }

        try {
            val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
            val adapter = bluetoothManager?.adapter ?: BluetoothAdapter.getDefaultAdapter()

            if (adapter == null) {
                callback?.onError("Bluetooth adapter is not available on this device.")
                return
            }

            if (!adapter.isEnabled) {
                callback?.onError("Bluetooth is currently turned off.")
                return
            }

            adapter.getProfileProxy(context, object : BluetoothProfile.ServiceListener {
                override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
                    if (profile == BluetoothProfile.HID_DEVICE) {
                        bluetoothHidDevice = proxy as BluetoothHidDevice
                        callback?.onLog("[HID] Connected to Bluetooth HID Device profile proxy.")
                        registerApp()
                    }
                }

                override fun onServiceDisconnected(profile: Int) {
                    if (profile == BluetoothProfile.HID_DEVICE) {
                        bluetoothHidDevice = null
                        isRegistered = false
                        connectedDevice = null
                        callback?.onLog("[HID] HID Device profile disconnected.")
                        callback?.onServiceRegistered(false)
                    }
                }
            }, BluetoothProfile.HID_DEVICE)
        } catch (e: SecurityException) {
            callback?.onError("Bluetooth permission denied: ${e.message}")
        } catch (e: Exception) {
            callback?.onError("Failed to init HID: ${e.message}")
        }
    }

    @SuppressLint("MissingPermission")
    fun registerApp() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return
        val hid = bluetoothHidDevice ?: return

        try {
            val sdp = BluetoothHidDeviceAppSdpSettings(
                "BDUCKY Keyboard & Mouse",
                "BDUCKY Bluetooth HID Automation by Sadat",
                "Sadat",
                BluetoothHidDevice.SUBCLASS1_COMBO,
                HidConstants.HID_REPORT_DESCRIPTOR
            )

            val inQos = BluetoothHidDeviceAppQosSettings(
                BluetoothHidDeviceAppQosSettings.SERVICE_BEST_EFFORT,
                800, 9, 0, 11250, BluetoothHidDeviceAppQosSettings.MAX
            )

            val outQos = BluetoothHidDeviceAppQosSettings(
                BluetoothHidDeviceAppQosSettings.SERVICE_BEST_EFFORT,
                800, 9, 0, 11250, BluetoothHidDeviceAppQosSettings.MAX
            )

            val hidCallback = object : BluetoothHidDevice.Callback() {
                override fun onAppStatusChanged(pluggedDevice: BluetoothDevice?, registered: Boolean) {
                    isRegistered = registered
                    callback?.onServiceRegistered(registered)
                    if (registered) {
                        callback?.onLog("[HID] Service successfully registered with SDP record.")
                    } else {
                        callback?.onLog("[HID] Service registration revoked.")
                    }
                    if (pluggedDevice != null) {
                        connectedDevice = pluggedDevice
                        callback?.onDeviceConnected(pluggedDevice)
                    }
                }

                override fun onConnectionStateChanged(device: BluetoothDevice?, state: Int) {
                    when (state) {
                        BluetoothProfile.STATE_CONNECTING -> {
                            callback?.onDeviceConnecting(device)
                            callback?.onLog("[HID] Handshake in progress with ${device?.name ?: device?.address}...")
                        }
                        BluetoothProfile.STATE_CONNECTED -> {
                            device?.let {
                                connectedDevice = it
                                callback?.onDeviceConnected(it)
                                callback?.onLog("[HID] Connected to host: ${it.name ?: it.address}")
                            }
                        }
                        BluetoothProfile.STATE_DISCONNECTED -> {
                            callback?.onDeviceDisconnected(device)
                            callback?.onLog("[HID] Disconnected from host: ${device?.name ?: device?.address}")
                            if (connectedDevice?.address == device?.address) {
                                connectedDevice = null
                            }
                        }
                    }
                }
            }

            hid.registerApp(sdp, inQos, outQos, executor, hidCallback)
        } catch (e: SecurityException) {
            callback?.onError("Permission needed to register HID: ${e.message}")
        } catch (e: Exception) {
            callback?.onError("Register App failed: ${e.message}")
        }
    }

    @SuppressLint("MissingPermission")
    fun unregisterApp() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return
        try {
            bluetoothHidDevice?.unregisterApp()
            isRegistered = false
            callback?.onServiceRegistered(false)
            callback?.onLog("[HID] Unregistered HID service.")
        } catch (e: Exception) {
            callback?.onError("Unregister failed: ${e.message}")
        }
    }

    @SuppressLint("MissingPermission")
    fun connectToDevice(device: BluetoothDevice) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return
        try {
            connectedDevice = device
            val ok = bluetoothHidDevice?.connect(device) ?: false
            if (ok) {
                callback?.onLog("[HID] Initiated connection to ${device.name ?: device.address}...")
            } else {
                callback?.onLog("[HID] Connection command sent to ${device.name ?: device.address}")
            }
        } catch (e: SecurityException) {
            callback?.onError("Permission error connecting: ${e.message}")
        } catch (e: Exception) {
            callback?.onError("Connect error: ${e.message}")
        }
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return
        val dev = connectedDevice ?: return
        try {
            bluetoothHidDevice?.disconnect(dev)
            connectedDevice = null
            callback?.onLog("[HID] Disconnecting from host ${dev.name ?: dev.address}...")
        } catch (e: Exception) {
            callback?.onError("Disconnect error: ${e.message}")
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun sendKey(modifier: Byte, keyCode: Byte, delayMs: Long = 25L): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return false
        val hid = bluetoothHidDevice ?: return false
        val dev = connectedDevice ?: return false

        try {
            val pressReport = byteArrayOf(modifier, 0, keyCode, 0, 0, 0, 0, 0)
            hid.sendReport(dev, HidConstants.REPORT_ID_KEYBOARD.toInt(), pressReport)
            delay(delayMs)
            val releaseReport = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0)
            hid.sendReport(dev, HidConstants.REPORT_ID_KEYBOARD.toInt(), releaseReport)
            delay(delayMs)
            return true
        } catch (e: Exception) {
            Log.e("BduckyHidManager", "Send key error", e)
            return false
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun sendString(text: String, delayMs: Long = 25L): Int {
        var count = 0
        for (c in text) {
            val mapping = HidConstants.getCharMapping(c)
            if (mapping != null) {
                if (sendKey(mapping.modifier, mapping.keyCode, delayMs)) {
                    count++
                }
            } else {
                delay(delayMs)
            }
        }
        return count
    }

    @SuppressLint("MissingPermission")
    fun sendMouseMove(dx: Int, dy: Int, buttons: Byte = 0) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return
        val hid = bluetoothHidDevice ?: return
        val dev = connectedDevice ?: return

        try {
            val clampedX = dx.coerceIn(-127, 127).toByte()
            val clampedY = dy.coerceIn(-127, 127).toByte()
            val report = byteArrayOf(buttons, clampedX, clampedY, 0)
            hid.sendReport(dev, HidConstants.REPORT_ID_MOUSE.toInt(), report)
        } catch (e: Exception) {
            Log.e("BduckyHidManager", "Mouse move error", e)
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun sendMouseClick(buttonMask: Byte, delayMs: Long = 30L) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return
        val hid = bluetoothHidDevice ?: return
        val dev = connectedDevice ?: return

        try {
            val pressReport = byteArrayOf(buttonMask, 0, 0, 0)
            hid.sendReport(dev, HidConstants.REPORT_ID_MOUSE.toInt(), pressReport)
            delay(delayMs)
            val releaseReport = byteArrayOf(0, 0, 0, 0)
            hid.sendReport(dev, HidConstants.REPORT_ID_MOUSE.toInt(), releaseReport)
        } catch (e: Exception) {
            Log.e("BduckyHidManager", "Mouse click error", e)
        }
    }

    @SuppressLint("MissingPermission")
    fun sendMouseScroll(wheelDelta: Int) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return
        val hid = bluetoothHidDevice ?: return
        val dev = connectedDevice ?: return

        try {
            val clamped = wheelDelta.coerceIn(-127, 127).toByte()
            val report = byteArrayOf(0, 0, 0, clamped)
            hid.sendReport(dev, HidConstants.REPORT_ID_MOUSE.toInt(), report)
        } catch (e: Exception) {
            Log.e("BduckyHidManager", "Mouse scroll error", e)
        }
    }

    fun release() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            unregisterApp()
        }
        executor.shutdown()
    }
}
