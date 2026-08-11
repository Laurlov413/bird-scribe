package com.lovrienl.birdscribe

import android.media.MediaRecorder
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
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
import androidx.lifecycle.lifecycleScope
import com.lovrienl.birdscribe.ui.theme.BirdScribeTheme
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class MainActivity : ComponentActivity() {
    private var isRecording by mutableStateOf(false)
    private var hasRecording by mutableStateOf(false)
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
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BirdScribeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RecordButton(
                        isRecording = isRecording,
                        hasRecording = hasRecording,
                        modifier = Modifier.padding(innerPadding),
                        onStartRecording = {
                            requestPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                        },
                        onStopRecording = {
                            stopRecording()
                            isRecording = false
                        },
                        onPlayRecording = {
                            playRecording()
                        }
                    )
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer?.release()
        mediaPlayer = null
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
                hasRecording = true
                uploadRecording()
            }
        }
    }

    private fun playRecording() {
        val path = audioFilePath ?: return

        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(path)
                prepare()
                setOnCompletionListener {
                    it.release()
                    mediaPlayer = null
                }
                start()
            }
        } catch (e: Exception) {
            Log.e("BirdScribe", "playRecording failed",e)
        }
    }

    private fun uploadRecording() {
        val path = audioFilePath ?: return
        val file = java.io.File(path)

        val requestBody = file.asRequestBody("audio/mp4".toMediaTypeOrNull())
        val audioPart = MultipartBody.Part.createFormData("audio", file.name, requestBody)

        lifecycleScope.launch {
            try {
                val response = ApiClient.service.transcribe(audioPart)
                if (response.isSuccessful) {
                    val transcript = response.body()?.transcript
                    Log.d("BirdScribe", "Transcript: $transcript")
                } else {
                    Log.e("BirdScribe", "Upload failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("BirdScribe", "Upload error", e)
            }
        }
    }
}

@Composable
fun RecordButton(
    isRecording: Boolean,
    modifier: Modifier = Modifier,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onPlayRecording: () -> Unit,
    hasRecording: Boolean
) {
    Column(modifier = modifier) {
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
            )
        ) {
            Text(text = if (isRecording) "Stop Recording" else "Start Recording")
        }

        if (hasRecording && !isRecording) {
            Button(onClick = onPlayRecording) {
                Text("Play Recording")
            }
        }
    }
}

