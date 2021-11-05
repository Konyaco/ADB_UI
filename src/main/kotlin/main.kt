import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ui.App
import ui.ViewModel

fun main() = application {
    val viewModel = ViewModel()

    Window(onCloseRequest = ::exitApplication, title = "ADB UI", icon = painterResource("icon.png")) {
        MaterialTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) { App(viewModel) }
        }
    }
}