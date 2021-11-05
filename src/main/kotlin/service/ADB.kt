package service

import java.io.InputStream
import java.io.OutputStream

interface ADBShell {
    val inputStream: InputStream
    val outputStream: OutputStream
    val errorStream: InputStream
}

fun ADB.Device.reboot() {
    execute("reboot")
}

class ADB {
    private val runtime = Runtime.getRuntime()

    open inner class Device protected constructor(
        val serial: String,
        val model: String,
        val product: String,
        val device: String,
        val transportId: Int
    ) {
        inner class ADBShellImpl : ADBShell {
            private val process = runtime.exec("adb shell -t $transportId}")
            override val inputStream: InputStream = process.inputStream
            override val outputStream: OutputStream = process.outputStream
            override val errorStream: InputStream = process.errorStream
        }

        fun execute(cmd: String): String {
            return runtime.exec("adb -t $transportId $cmd").inputStream.bufferedReader().readText()
        }

        fun openShell(): ADBShell {
            return ADBShellImpl()
        }
    }

    inner class USBDevice(serial: String, model: String, product: String, device: String, transportId: Int) :
        Device(serial, model, product, device, transportId) {
        fun openTCPIP(port: Int = 5555) {
            execute("tcpip $port")
        }
    }

    inner class NetworkDevice(
        serial: String,
        model: String,
        product: String,
        device: String,
        transportId: Int,
        val host: String,
        val port: Int
    ) : Device(serial, model, product, device, transportId) {
        fun disconnect() {
            runtime.exec("adb disconnect $host:$port").waitFor()
        }
    }

    private var devices: MutableList<Device> = mutableListOf()

    fun getDevices(): List<Device> {
        parseDevices()
        return devices
    }

    fun connect(host: String, port: Int = 5555): Int {
        val process = runtime.exec("adb connect $host:$port")
        return process.waitFor()
    }

    fun disconnectAll() {
        runtime.exec("adb disconnect").waitFor()
    }

    private fun parseDevices() {
        val result = runtime.exec("adb devices -l").inputStream.bufferedReader().readText()
        val separated = result.lines().let {
            it.subList(1, it.size)
        }.filter {
            it.isNotBlank() && it.contains("device")
        }

        devices = separated.map {
            val serial = it.split("""\s+""".toRegex())[0]
            val map = DeviceParser.parse(it)

            val transportId = map["transport_id"]!!.toInt()
            val model by map
            val product by map
            val device by map

            if (serial.contains(':')) {
                val host = serial.substringBefore(':')
                val port = serial.substringAfter(':').toInt()
                devices.find { it is NetworkDevice && it.host == host && it.port == port && it.serial == serial && it.transportId == transportId }
                    ?: NetworkDevice(serial, model, product, device, transportId, host, port)
            } else {
                devices.find {
                    it is USBDevice && it.serial == serial && it.transportId == transportId
                } ?: USBDevice(serial, model, product, device, transportId)
            }
        }.toMutableList()
    }
}

object DeviceParser {
    fun parse(str: String): Map<String, String> {
        val params = str.split("""\s+""".toRegex())
        val map = mutableMapOf<String, String>()
        for (i in 2 until params.size) {
            val (key, value) = params[i].split(":")
            map[key] = value
        }
        return map
    }
}