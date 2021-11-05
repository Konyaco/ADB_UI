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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import service.ADB

@Composable
fun DeviceList(
    modifier: Modifier = Modifier,
    list: List<ADB.Device>,
    onTcpipClick: (ADB.USBDevice) -> Unit,
    onDisconnectClick: (ADB.NetworkDevice) -> Unit,
    onSaveClick: (ADB.NetworkDevice) -> Unit
) {
    Crossfade(list, Modifier.animateContentSize()) { list ->
        LazyColumn(
            modifier = modifier.graphicsLayer(clip = false),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(list.size) {
                when (val item = list[it]) {
                    is ADB.USBDevice -> {
                        CommonDevice(Modifier.fillMaxWidth(), item.model, item.serial) {
                            onTcpipClick(item)
                        }
                    }
                    is ADB.NetworkDevice -> {
                        NetworkDevice(Modifier.fillMaxWidth(), item.model, item.serial, {
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
    serial: String,
    onTcpipClick: () -> Unit
) {
    DeviceListItem(modifier, MaterialTheme.colors.secondary, icon = {
        Icon(Icons.Filled.Usb, "USB 设备")
    }, serial = serial, deviceName = deviceName, actions = {
        IconButton(onClick = onTcpipClick) {
            Icon(Icons.Filled.Sensors, "Open TCPIP")
        }
    })
}

@Composable
fun NetworkDevice(
    modifier: Modifier = Modifier,
    deviceName: String,
    serial: String,
    onSaveClick: () -> Unit,
    onDisconnectClick: () -> Unit
) {
    DeviceListItem(modifier, MaterialTheme.colors.secondaryVariant, icon = {
        Icon(Icons.Filled.CellWifi, "网络设备")
    }, serial = serial, deviceName = deviceName, actions = {
        IconButton(onClick = onSaveClick) {
            Icon(Icons.Filled.Save, "Save")
        }
        IconButton(onClick = onDisconnectClick) {
            Icon(Icons.Filled.LinkOff, "Disconnect")
        }
    })
}

@Composable
fun DeviceListItem(
    modifier: Modifier = Modifier,
    color: Color,
    icon: @Composable RowScope.() -> Unit,
    serial: String,
    deviceName: String,
    actions: @Composable RowScope.() -> Unit
) {
    Card(
        modifier = modifier.wrapContentHeight(),
        backgroundColor = color,
        elevation = 8.dp
    ) {
        Row(modifier.padding(vertical = 8.dp, horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            icon()
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = serial,
                    style = MaterialTheme.typography.caption
                )
                Text(text = deviceName)
            }
            actions()
        }
    }
}