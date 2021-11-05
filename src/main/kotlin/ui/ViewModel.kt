package ui

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.*
import service.ADB
import service.Preference
import service.RememberedDevice


class ViewModel {
    private val scope = CoroutineScope(Dispatchers.Default)
    private val adb = ADB()
    private val preference = Preference()

    val rememberedDevices = mutableStateOf<List<RememberedDevice>>(emptyList())
    val devices = mutableStateOf<List<ADB.Device>>(emptyList())

    val refreshState = mutableStateOf(RefreshState.NORM)
    val connectState = mutableStateOf(ConnectState.NORM)

    enum class RefreshState { NORM, REFRESHING, FAILED }

    enum class ConnectState { NORM, CONNECTING, FAILED }

    init {
        refreshRememberedDevices()
    }

    private fun getDevices() {
        devices.value = adb.getDevices()
    }

    private fun refreshRememberedDevices() {
        rememberedDevices.value = preference.getDevices()
    }

    fun start() {
        scope.launch {
            while (isActive) {
                refreshState.value = RefreshState.REFRESHING
                try {
                    val result = adb.getDevices()
                    delay(300)
                    devices.value = result
                } catch (e: Exception) {
                    refreshState.value = RefreshState.FAILED
                    e.printStackTrace()
                    return@launch
                }
                refreshState.value = RefreshState.NORM
                delay(3000)
            }
        }
    }

    fun connect(device: RememberedDevice) {
        connect(device.host, device.port)
    }

    fun connect(address: String, port: Int) {
        scope.launch {
            connectState.value = ConnectState.CONNECTING
            try {
                if (adb.connect(address, port) != 0) {
                    delay(300)
                    connectState.value = ConnectState.FAILED
                    return@launch
                }
            } catch (e: Exception) {
                delay(300)
                connectState.value = ConnectState.FAILED
                e.printStackTrace()
                return@launch
            }
            delay(300)
            getDevices()
            connectState.value = ConnectState.NORM
        }
    }

    fun disconnect(device: ADB.NetworkDevice) {
        scope.launch {
            device.disconnect()
            getDevices()
        }
    }

    fun dispose() {
        scope.cancel()
    }

    fun openTCPIP(device: ADB.USBDevice) {
        scope.launch {
            device.openTCPIP()
        }
    }

    fun disconnectAll() {
        scope.launch {
            adb.disconnectAll()
            getDevices()
        }
    }

    fun storeDevice(device: ADB.NetworkDevice) {
        scope.launch {
            preference.addDevice(RememberedDevice(device.model, device.host, device.port))
            refreshRememberedDevices()
        }
    }

    fun deleteDevice(device: RememberedDevice) {
        scope.launch {
            preference.deleteDevice(device)
            refreshRememberedDevices()
        }
    }
}