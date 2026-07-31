package com.lovrienl.birdscribe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import android.media.MediaRecorder
import android.util.Log
import com.lovrienl.birdscribe.ui.theme.BirdScribeTheme

class MainActivity : ComponentActivity() {
    private var isRecording by mutableStateOf(false)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startRecording()
            isRecording = true
        } else {
            Log.e("BirdScribe", "Microphone permission denied")
        }
    }

    private var mediaRecorder: MediaRecorder? = null
    private var audioFilePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BirdScribeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RecordButton(
                        isRecording = isRecording,
                        modifier = Modifier.padding(innerPadding),
                        onStartRecording = {
                            requestPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                        },
                        onStopRecording = {
                            stopRecording()
                            isRecording = false
                        }
                    )
                }
            }
        }
    }

    private fun startRecording() {
        val fileName = "${externalCacheDir?.absolutePath}/birdscribe_${System.currentTimeMillis()}.m4a"
        audioFilePath = fileName

        try {
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(fileName)
                prepare()
                start()
            }
        } catch (e: Exception) {
            Log.e("BirdScribe", "startRecording failed", e)
        }
    }

    private fun stopRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            Log.e("BirdScribe", "stopRecording failed", e)
        } finally {
            mediaRecorder = null
            audioFilePath?.let { path ->
                val file = java.io.File(path)
                Log.d("BirdScribe", "Recording saved: $path (${file.length()} bytes")
            }
        }
    }
}

@Composable
fun RecordButton(
    isRecording: Boolean,
    modifier: Modifier = Modifier,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit
) {
    Button(
        onClick = {
            if (isRecording) {
                onStopRecording()
            } else {
                onStartRecording()
            }
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isRecording) Color.Red else Color.Green
        ),
        modifier = modifier
    ) {
        Text(text = if (isRecording) "Stop Recording" else "Start Recording")
    }
}

