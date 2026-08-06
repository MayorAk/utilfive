package com.mohaaa.utilfive.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.clickable
import com.mohaaa.utilfive.navigation.Routes

private data class Tool(
    val title: String,
    val icon: ImageVector,
    val route: String
)

private val tools = listOf(
    Tool("Flashlight / QR", Icons.Filled.FlashOn, Routes.FLASHLIGHT),
    Tool("QR Scanner", Icons.Filled.QrCodeScanner, Routes.QR_SCANNER),
    Tool("Converter & BMI", Icons.Filled.SwapHoriz, Routes.CONVERTER),
    Tool("Calculator", Icons.Filled.Calculate, Routes.CALCULATOR),
    Tool("Sound Meter", Icons.Filled.GraphicEq, Routes.SOUND_METER),
    Tool("File Manager", Icons.Filled.Folder, Routes.FILE_MANAGER)
)

@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("UtilFive") }) }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(tools) { tool ->
                ToolCard(tool = tool, onClick = { onNavigate(tool.route) })
            }
        }
    }
}

@Composable
private fun ToolCard(tool: Tool, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = tool.icon, contentDescription = tool.title, modifier = Modifier.padding(bottom = 8.dp))
            Text(text = tool.title)
        }
    }
}
