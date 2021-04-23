package ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun App(viewModel: ViewModel) {
    val devices by viewModel.devices
    val rememberedDevices by viewModel.rememberedDevices
    val refreshState by viewModel.refreshState
    val connectState by viewModel.connectState

    DisposableEffect(Unit) {
        viewModel.start()
        onDispose {
            viewModel.dispose()
        }
    }

    Column(Modifier.fillMaxSize()) {
        Text(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp), text = "连接新设备")
        Row(
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var address by remember { mutableStateOf(TextFieldValue()) }
            var port by remember { mutableStateOf(TextFieldValue("5555")) }
            OutlinedTextField(
                label = { Text("主机") },
                enabled = connectState != ViewModel.ConnectState.CONNECTING,
                value = address,
                onValueChange = { address = it }
            )
            Spacer(Modifier.width(16.dp))
            OutlinedTextField(
                modifier = Modifier.width(92.dp),
                enabled = connectState != ViewModel.ConnectState.CONNECTING,
                label = { Text("端口") },
                value = port,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = {
                    if (it.text.isEmpty()) {
                        port = it
                        return@OutlinedTextField
                    }
                    val number = try {
                        it.text.toInt()
                    } catch (e: Exception) {
                        return@OutlinedTextField
                    }
                    if (number in 0..65535) {
                        port = it
                    }
                }
            )
            Spacer(Modifier.width(24.dp))
            OutlinedButton(
                enabled = connectState != ViewModel.ConnectState.CONNECTING,
                onClick = {
                    viewModel.connect(address.text, port.text.toInt())
                }) {
                Icon(Icons.Filled.Link, "Connect")
            }
            Spacer(Modifier.width(16.dp))
            Crossfade(connectState) { connectState ->
                when (connectState) {
                    ViewModel.ConnectState.NORM -> {
                    }
                    ViewModel.ConnectState.CONNECTING -> {
                        CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                    }
                    ViewModel.ConnectState.FAILED -> {
                        Icon(Icons.Default.Error, "Error", tint = MaterialTheme.colors.error)
                    }
                }
            }
        }
        Row(
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "设备列表")
            Spacer(Modifier.width(8.dp))
            IconButton(
                modifier = Modifier.padding(horizontal = 8.dp),
                onClick = { viewModel.disconnectAll() }
            ) {
                Icon(Icons.Filled.LinkOff, "Disconnect all")
            }
            Spacer(Modifier.width(8.dp))
            Crossfade(refreshState) { refreshState ->
                when (refreshState) {
                    ViewModel.RefreshState.NORM -> {
                    }
                    ViewModel.RefreshState.REFRESHING -> {
                        CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                    }
                    ViewModel.RefreshState.FAILED -> {
                        Icon(Icons.Default.Error, "Error", tint = MaterialTheme.colors.error)
                    }
                }
            }
        }
        DeviceList(
            modifier = Modifier.fillMaxWidth(),
            list = devices,
            onDisconnectClick = {
                viewModel.disconnect(it)
            },
            onTcpipClick = {
                viewModel.openTCPIP(it)
            }, onSaveClick = {
                viewModel.storeDevice(it)
            }
        )
        Row(
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "保存的设备")
        }
        CachedDeviceList(Modifier.fillMaxWidth(), rememberedDevices, onConnectClick = {
            viewModel.connect(it.host, it.port)
        }, onDeleteClick = {
            viewModel.deleteDevice(it)
        })
    }
}
