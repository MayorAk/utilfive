package com.mohaaa.utilfive.ui.soundmeter

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import androidx.compose.runtime.LaunchedEffect
import java.io.File
import kotlin.math.log10
import java.util.Locale

@Composable
fun SoundMeterScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }
    var isListening by remember { mutableStateOf(false) }
    var decibels by remember { mutableStateOf(0.0) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted -> hasPermission = granted }

    var recorder by remember { mutableStateOf<MediaRecorder?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            recorder?.apply {
                try {
                    stop()
                } catch (_: Exception) {
                }
                release()
            }
        }
    }

    LaunchedEffect(isListening) {
        if (isListening) {
            while (isActive && isListening) {
                val amplitude = recorder?.maxAmplitude ?: 0
                decibels = if (amplitude > 0) 20 * log10(amplitude.toDouble()) else 0.0
                delay(200)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sound Level Meter") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!hasPermission) {
                Text("Microphone permission is needed to measure sound")
                Button(onClick = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) }) {
                    Text("Grant permission")
                }
            } else {
                Text(text = String.format(Locale.getDefault(), "%.1f dB (relative)", decibels))
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))
                LinearProgressIndicator(
                    progress = { (decibels / 100.0).coerceIn(0.0, 1.0).toFloat() },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))
                Button(onClick = {
                    if (!isListening) {
                        try {
                            val outputFile = File(context.cacheDir, "sound_meter_tmp.3gp")
                            recorder = MediaRecorder().apply {
                                setAudioSource(MediaRecorder.AudioSource.MIC)
                                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                                setOutputFile(outputFile.absolutePath)
                                prepare()
                                start()
                            }
                            isListening = true
                        } catch (_: Exception) {
                            isListening = false
                        }
                    } else {
                        recorder?.apply {
                            try {
                                stop()
                            } catch (_: Exception) {
                            }
                            release()
                        }
                        recorder = null
                        isListening = false
                        decibels = 0.0
                    }
                }) {
                    Text(if (isListening) "Stop" else "Start measuring")
                }
            }
        }
    }
}
