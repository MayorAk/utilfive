package com.mohaaa.utilfive.ui.filemanager

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.documentfile.provider.DocumentFile
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Composable
fun FileManagerScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var treeUri by remember { mutableStateOf<Uri?>(null) }
    var entries by remember { mutableStateOf<List<DocumentFile>>(emptyList()) }
    var status by remember { mutableStateOf("") }

    val openFolderLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(
                uri,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION or android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            treeUri = uri
            entries = DocumentFile.fromTreeUri(context, uri)?.listFiles()?.toList() ?: emptyList()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("File Manager") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Button(
                onClick = { openFolderLauncher.launch(null) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Choose folder")
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))

            if (status.isNotEmpty()) {
                Text(status)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
            }

            if (entries.isNotEmpty()) {
                Button(
                    onClick = {
                        val uri = treeUri
                        if (uri != null) {
                            status = try {
                                compressFolderToZip(context, uri, entries)
                                "Compressed ${entries.size} item(s) into archive.zip"
                            } catch (e: Exception) {
                                "Compression failed: ${e.message}"
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Compress folder contents to archive.zip")
                }
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(entries) { doc ->
                    ListItem(
                        headlineContent = { Text(doc.name ?: "Unnamed") },
                        supportingContent = {
                            Text(if (doc.isDirectory) "Folder" else formatSize(doc.length()))
                        }
                    )
                }
            }
        }
    }
}

private fun formatSize(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB")
    var size = bytes.toDouble()
    var unitIndex = 0
    while (size >= 1024 && unitIndex < units.size - 1) {
        size /= 1024
        unitIndex++
    }
    return String.format(java.util.Locale.getDefault(), "%.1f %s", size, units[unitIndex])
}

private fun compressFolderToZip(context: android.content.Context, treeUri: Uri, files: List<DocumentFile>) {
    val parent = DocumentFile.fromTreeUri(context, treeUri) ?: return
    val existing = parent.findFile("archive.zip")
    existing?.delete()
    val zipFile = parent.createFile("application/zip", "archive.zip") ?: return

    context.contentResolver.openOutputStream(zipFile.uri)?.use { outStream ->
        ZipOutputStream(outStream).use { zos ->
            files.forEach { file ->
                if (!file.isDirectory && file.name != "archive.zip") {
                    context.contentResolver.openInputStream(file.uri)?.use { inStream ->
                        zos.putNextEntry(ZipEntry(file.name ?: "file"))
                        inStream.copyTo(zos)
                        zos.closeEntry()
                    }
                }
            }
        }
    }
}
