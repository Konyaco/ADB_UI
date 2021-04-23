import service.ADB
import java.net.InetAddress
import kotlin.test.Test

class ADBTest {
    val adb = ADB()

    @Test
    fun devices() {
        println(adb.getDevices().map { it.name }.joinToString())
    }

    @Test
    fun connect() {
    }
    @Test
    fun local() {
        val local = InetAddress.getLocalHost()
        println(local.hostAddress)
        println(local.hostName)
    }
}