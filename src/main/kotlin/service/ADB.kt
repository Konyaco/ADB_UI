package service

import java.net.InetAddress

class ADB {
    private val runtime = Runtime.getRuntime()

    sealed class Device(val name: String) {
        class USBDevice(name: String) : Device(name) {

        }

        class NetworkDevice(
            name: String,
            val host: String,
            val port: Int
        ) : Device(name) {

        }
    }

    fun getDevices(): List<Device> {
        val result = runtime.exec("adb devices").inputStream.bufferedReader().readText()
        val separated = result.lines().let {
            it.subList(1, it.size)
        }.filter {
            it.isNotBlank() && it.endsWith("device")
        }
        val devices = separated.map {
            val name = it.split("""\s+""".toRegex())[0]
            if (name.contains(':')) {
                val host = name.substringBefore(':')
                val port = name.substringAfter(':').toInt()
                Device.NetworkDevice(name, host, port)
            } else {
                Device.USBDevice(name)
            }
        }
        return devices
    }

    fun openTCPIP(device: Device.USBDevice, port: Int = 5555) {
        runtime.exec("adb -s ${device.name} tcpip $port")
    }

    fun connect(host: String, port: Int = 5555): Int {
        val process = runtime.exec("adb connect $host:$port")
        return process.waitFor()
    }

    fun disconnectAll() {
        runtime.exec("adb disconnect").waitFor()
    }

    fun disconnect(inetAddress: InetAddress) {
        runtime.exec("adb disconnect ${inetAddress.hostName}").waitFor()
    }

    fun disconnect(device: Device.NetworkDevice) {
        runtime.exec("adb disconnect ${device.name}").waitFor()
    }
}