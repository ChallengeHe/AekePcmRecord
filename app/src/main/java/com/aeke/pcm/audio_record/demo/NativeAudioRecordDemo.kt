package com.aeke.pcm.audio_record.demo

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.aeke.pcm.audio_record.NativeAudioRecord
import com.aeke.pcm.audio_record.PCM_FORMAT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class NativeAudioRecordDemo : AppCompatActivity() {

    private lateinit var mLocalFilePath: String
    private lateinit var mRecordBtn: ToggleButton
    private lateinit var mAudioTrack: AudioTrack

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_native_audio_record_demo)

        mRecordBtn = findViewById(R.id.btn_record)

        // 创建文件路径
        mLocalFilePath = ContextCompat.getExternalFilesDirs(
            this,
            Environment.DIRECTORY_MUSIC
        )[0].absolutePath + File.separator + "native_audio_record.pcm"

        // 创建AudioTrack
        mAudioTrack = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(16000)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setTransferMode(AudioTrack.MODE_STREAM)
                .setBufferSizeInBytes(AudioTrack.getMinBufferSize(16000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT))
                .build()
        } else {
            AudioTrack(
                AudioManager.STREAM_MUSIC,
                16000,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                AudioTrack.getMinBufferSize(16000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT),
                AudioTrack.MODE_STREAM
            )
        }

        mRecordBtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                startRecord()
            } else {
                stopRecord()
            }
        }

    }

    /**
     * 开始录音
     */
    private fun startRecord() {
        Log.i(TAG, "startRecord: ")
        NativeAudioRecord.start(4, 0, 14, 16000, PCM_FORMAT.PCM_FORMAT_S16_LE.value, 512, 2)
        mAudioTrack.play()
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val file = File(mLocalFilePath)
                if (file.exists()) {
                    file.delete()
                }
                var buffer: ByteArray? = null
                while (NativeAudioRecord.isCapturing && NativeAudioRecord.read().also { buffer = it } != null) {
                    Log.d(TAG, "NativeAudioRecord read: " + buffer!!.size)//114688 57344
                    // PCM数据保存到本地文件
                    file.writeBytes(buffer!!)
                    // PCM数据写入AudioTrack中播放
                    val tmpBuf: ByteArray = splitChannels(buffer!!)
                    mAudioTrack.write(tmpBuf, 0, tmpBuf.size)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 停止录音
     */
    private fun stopRecord() {
        Log.i(TAG, "stopRecord: ")
        mAudioTrack.stop()
        NativeAudioRecord.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mAudioTrack.release()
    }

    /**
     * PCM数据预处理，14通道PCM数据转2通道数据
     * 1-6路：2mic+2null+2ref
     * 7-10路：2AEC+2null
     * 11-12路：2null
     * 13路：1ASR（太行）
     * 14路：协议
     * @param src 14通道PCM数据
     * @return 2通道PCM数据
     */
    private fun splitChannels(src: ByteArray): ByteArray {
        val dest = ByteArray(src.size / 14)
        var i = 0
        while (i < dest.size) {
            dest[i] = src[i * 14]
            dest[i + 1] = src[i * 14 + 1]
            i += 2
        }
        return dest
    }

    companion object {
        private const val TAG = "NativeAudioRecordDemo"
    }
}