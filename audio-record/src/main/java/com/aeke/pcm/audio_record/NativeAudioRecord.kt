package com.aeke.pcm.audio_record

import android.util.Log

object NativeAudioRecord {
    private const val TAG = "NativeAudioRecord"

    init {
        System.loadLibrary("audio-record")
    }

    private external fun nativeStart(card: Int, device: Int, channels: Int, rate: Int, format: Int, periodSize: Int, periodCount: Int): Int
    private external fun nativeStop()
    private external fun nativeRead(): ByteArray?
    private external fun nativeReadBuffer(buffer: ByteArray):Int

    @JvmStatic
    var isCapturing = false

    /**
     * 开始录音
     * @param card Int 声卡ID
     * @param device Int 设备ID
     * @param channels Int 总录音通道数（一共录制几通道数据）
     * @param rate Int 采样率（8000、16000、44100等）
     * @param format Int PCM数据格式（8bit、16bit、24bit、32bit）[PCM_FORMAT]
     * @param periodSize Int 采样周期大小
     * @param periodCount Int 采样周期数
     * @return Boolean 是否成功
     * @see com.aeke.pcm.audio_record.PCM_FORMAT
     */
    @JvmStatic
    fun start(card: Int, device: Int, channels: Int, rate: Int, format: Int, periodSize: Int, periodCount: Int): Boolean {
        if (!isCapturing) {
            isCapturing = nativeStart(card, device, channels, rate, format, periodSize, periodCount) == 0
        } else {
            Log.w(TAG, "start: already started.")
        }
        return isCapturing
    }

    /**
     * 停止录音
     */
    @JvmStatic
    fun stop() {
        if (isCapturing) {
            isCapturing = false
            nativeStop()
        }
    }

    /**
     * 获取录音数据
     * @return ByteArray? 录音数据（可能为null）
     */
    @JvmStatic
    fun read(): ByteArray? {
        return if (isCapturing) {
            nativeRead()
        } else {
            Log.e(TAG, "read: please call start() first.")
            null
        }
    }


}