package service

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class PreferenceJson(
    val rememberedDevices: List<RememberedDevice>
)

@Serializable
data class RememberedDevice(
    val name: String,
    val host: String,
    val port: Int
)

class Preference {
    private val file = File("config.json")
    private var preference: PreferenceJson = PreferenceJson(emptyList())

    private val json = Json {
        prettyPrint = true
    }

    fun getDevices(): List<RememberedDevice> {
        val jsonStr = file.takeIf { it.exists() }?.readText() ?: return emptyList()
        preference = json.decodeFromString(jsonStr)
        return preference.rememberedDevices
    }

    fun addDevice(data: RememberedDevice) {
        val list = preference.rememberedDevices.toMutableList()
        if (!list.contains(data)) {
            list.add(data)
        }
        file.writeText(json.encodeToString(PreferenceJson(list)))
    }

    fun deleteDevice(device: RememberedDevice) {
        val list = preference.rememberedDevices.toMutableList()
        list.remove(device)
        file.writeText(json.encodeToString(PreferenceJson(list)))
    }
}