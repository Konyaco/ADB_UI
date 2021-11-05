package ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CellWifi
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Link
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import service.RememberedDevice

@Composable
fun CachedDeviceList(
    modifier: Modifier = Modifier,
    devices: List<RememberedDevice>,
    onConnectClick: (RememberedDevice) -> Unit,
    onDeleteClick: (RememberedDevice) -> Unit
) {
    Crossfade(devices, Modifier.animateContentSize()) { devices ->
        LazyColumn(
            modifier = modifier.graphicsLayer(clip = false),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(devices) {
                CachedDevice(
                    Modifier.fillParentMaxWidth(),
                    it.name,
                    it.host,
                    it.port,
                    { onConnectClick(it) },
                    { onDeleteClick(it) }
                )
            }
        }
    }
}

@Composable
fun CachedDevice(
    modifier: Modifier = Modifier,
    name: String,
    host: String,
    port: Int,
    onConnectClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    DeviceListItem(modifier.wrapContentHeight(), MaterialTheme.colors.secondaryVariant, icon = {
        Icon(Icons.Filled.CellWifi, "网络设备")
    }, serial = "$host:$port", name, {
        IconButton(onClick = onDeleteClick) {
            Icon(Icons.Filled.Delete, "Delete")
        }
        IconButton(onClick = onConnectClick) {
            Icon(Icons.Filled.Link, "Connect")
        }
    })
}