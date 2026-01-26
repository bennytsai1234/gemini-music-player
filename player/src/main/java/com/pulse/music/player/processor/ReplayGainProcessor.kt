package com.pulse.music.player.processor

import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.BaseAudioProcessor
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * ReplayGain AudioProcessor
 *
 * Implements ReplayGain volume adjustment.
 * Currently a placeholder for simple gain application.
 * Real implementation would parse tags and apply gain.
 */
class ReplayGainProcessor : BaseAudioProcessor() {

    private var gainDb: Float = 0f
    private var preampDb: Float = 0f
    private var linearGain: Float = 1f

    fun setReplayGain(gainDb: Float, preampDb: Float) {
        if (this.gainDb != gainDb || this.preampDb != preampDb) {
            this.gainDb = gainDb
            this.preampDb = preampDb
            updateLinearGain()
        }
    }

    private fun updateLinearGain() {
        val totalGainDb = gainDb + preampDb
        linearGain = Math.pow(10.0, totalGainDb / 20.0).toFloat()
    }

    override fun onConfigure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
        // We support any sample rate, channel count, but only 16-bit PCM for now for simplicity
        if (inputAudioFormat.encoding != androidx.media3.common.C.ENCODING_PCM_16BIT) {
            return AudioProcessor.AudioFormat.NOT_SET
        }
        return inputAudioFormat
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        val position = inputBuffer.position()
        val limit = inputBuffer.limit()
        val frameCount = (limit - position) / 2
        val outputBuffer = replaceOutputBuffer(frameCount * 2)

        for (i in 0 until frameCount) {
            val sample = inputBuffer.getShort(position + i * 2)
            // Apply gain with clipping protection (simple clamp)
            var processed = (sample * linearGain).toInt()
            processed = processed.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
            outputBuffer.putShort(processed.toShort())
        }

        inputBuffer.position(limit)
        outputBuffer.flip()
    }
}
