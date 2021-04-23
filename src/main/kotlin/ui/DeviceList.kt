package ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import service.ADB

@Composable
fun DeviceList(
    modifier: Modifier = Modifier,
    list: List<ADB.Device>,
    onTcpipClick: (ADB.Device.USBDevice) -> Unit,
    onDisconnectClick: (ADB.Device.NetworkDevice) -> Unit,
    onSaveClick: (ADB.Device.NetworkDevice) -> Unit
) {
    Crossfade(list, Modifier.animateContentSize()) { list ->
        LazyColumn(
            modifier = modifier.graphicsLayer(clip = false),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(list.size) {
                when (val item = list[it]) {
                    is ADB.Device.USBDevice -> {
                        CommonDevice(Modifier.fillMaxWidth(), item.name) {
                            onTcpipClick(item)
                        }
                    }
                    is ADB.Device.NetworkDevice -> {
                        NetworkDevice(Modifier.fillMaxWidth(), item.name, {
                            onSaveClick(item)
                        }) {
                            onDisconnectClick(item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CommonDevice(
    modifier: Modifier = Modifier,
    deviceName: String,
    onTcpipClick: () -> Unit
) {
    Card(
        modifier = modifier.wrapContentHeight(),
        backgroundColor = MaterialTheme.colors.secondary,
        elevation = 8.dp
    ) {
        Row(modifier.padding(vertical = 8.dp, horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Usb, "USB 设备")
            Spacer(Modifier.width(16.dp))
            Text(
                modifier = Modifier.weight(1f),
                text = deviceName
            )
            IconButton(onClick = onTcpipClick) {
                Icon(Icons.Filled.Sensors, "Open TCPIP")
            }
        }
    }
}

@Composable
fun NetworkDevice(
    modifier: Modifier = Modifier,
    deviceName: String,
    onSaveClick: () -> Unit,
    onDisconnectClick: () -> Unit
) {
    Card(
        modifier = modifier.wrapContentHeight(),
        backgroundColor = MaterialTheme.colors.secondaryVariant,
        elevation = 8.dp
    ) {
        Row(modifier.padding(vertical = 8.dp, horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.CellWifi, "网络设备")
            Spacer(Modifier.width(16.dp))
            Text(
                modifier = Modifier.weight(1f),
                text = deviceName
            )
            IconButton(onClick = onSaveClick) {
                Icon(Icons.Filled.Save, "Save")
            }
            IconButton(onClick = onDisconnectClick) {
                Icon(Icons.Filled.LinkOff, "Disconnect")
            }
        }
    }
}