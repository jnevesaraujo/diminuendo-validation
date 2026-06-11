package dam.a50274.diminuendo.data.repository

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import dam.a50274.diminuendo.domain.repository.AudioCaptureRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log10
import kotlin.math.sqrt

class AudioCaptureRepositoryImpl @Inject constructor() : AudioCaptureRepository {

    private val sampleRate = 44100
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT

    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    private val decibelSharedFlow = MutableSharedFlow<Double>(extraBufferCapacity = 1)

    @SuppressLint("MissingPermission")
    override fun startCapture() {
        if (recordingJob?.isActive == true) return

        val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            minBufferSize,
        )

        audioRecord?.startRecording()

        recordingJob = scope.launch {
            val buffer = ShortArray(minBufferSize)
            while (isActive) {
                val readResult = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                if (readResult > 0) {
                    var sum = 0.0
                    for (i in 0 until readResult) {
                        val sample = buffer[i].toDouble()
                        sum += sample * sample
                    }
                    val rms = sqrt(sum / readResult)

                    val normalizedRms = rms / 32767.0

                    var db = if (normalizedRms <= 0.0001) {
                        0.0
                    } else {
                        20 * log10(normalizedRms)
                    }

                    // Shift dBFS to approximate positive SPL range matching expected targets
                    if (db < 0.0 && db != 0.0) {
                        db += 90.0
                    }

                    decibelSharedFlow.tryEmit(db)
                }
                delay(100) // ~10 updates per sec
            }
        }
    }

    override fun stopCapture() {
        recordingJob?.cancel()
        recordingJob = null
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }

    override fun decibelFlow(): Flow<Double> = decibelSharedFlow
}
