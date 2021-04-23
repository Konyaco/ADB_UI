import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import ui.App
import ui.ViewModel
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

private fun loadImageResource(path: String): BufferedImage {
    val resource = Thread.currentThread().contextClassLoader.getResource(path)
    requireNotNull(resource) { "Resource $path not found" }
    return resource.openStream().use(ImageIO::read)
}

private val appIcon = loadImageResource("icon.png")

fun main() {
    val viewModel = ViewModel()
    Window("ADB UI", icon = appIcon) {
        MaterialTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) { App(viewModel) }
        }
    }
}