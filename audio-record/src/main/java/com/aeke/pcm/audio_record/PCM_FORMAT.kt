package com.aeke.pcm.audio_record

enum class PCM_FORMAT(val value:Int) {
    PCM_FORMAT_INVALID(-1),
    PCM_FORMAT_S16_LE(0),       /* 16-bit signed */
    PCM_FORMAT_S32_LE(1),       /* 32-bit signed */
    PCM_FORMAT_S8(2),           /* 8-bit signed */
    PCM_FORMAT_S24_LE(3),       /* 24-bits in 4-bytes */
    PCM_FORMAT_S24_3LE(4);      /* 24-bits in 3-bytes */
}