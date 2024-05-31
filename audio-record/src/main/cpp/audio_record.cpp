#include <jni.h>
#include <string>
#include <android/log.h>
#include "asoundlib.h"

#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, "NativeAudioRecord_JNI", __VA_ARGS__))
#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "NativeAudioRecord_JNI", __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, "NativeAudioRecord_JNI", __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, "NativeAudioRecord_JNI", __VA_ARGS__))

struct pcm_config g_pcm_config;
struct pcm *g_pcm;
char *g_buffer;
unsigned int g_size;

jint nativeStart(JNIEnv *env, jobject obj,
                 int card, int device,
                 int channels, int rate,
                 int format, int period_size,
                 int period_count) {

    memset(&g_pcm_config, 0, sizeof(g_pcm_config));
    g_pcm_config.channels = channels;
    g_pcm_config.rate = rate;
    g_pcm_config.period_size = period_size;
    g_pcm_config.period_count = period_count;
    g_pcm_config.format = (pcm_format) format;
    g_pcm_config.start_threshold = 0;
    g_pcm_config.stop_threshold = 0;
    g_pcm_config.silence_threshold = 0;

    g_pcm = pcm_open(card, device, PCM_IN, &g_pcm_config);
    if (!g_pcm || !pcm_is_ready(g_pcm)) {
        LOGE("start: Unable to open PCM device (%s)\n", pcm_get_error(g_pcm));
        return -1;
    }
    char fn[256];
    snprintf(fn, sizeof(fn), "/dev/snd/pcmC%uD%u%c", card, device, 'c');
    LOGI("open PCM device (%s)\n", fn);
    g_size = pcm_frames_to_bytes(g_pcm, pcm_get_buffer_size(g_pcm));
    g_buffer = (char *) malloc(g_size);
    if (!g_buffer) {
        LOGE("start: Unable to allocate %u bytes\n", g_size);
        free(g_buffer);
        pcm_close(g_pcm);
        return -2;
    }

    LOGI("start: Capturing sample: %u ch, %u hz, %u bit . period_size: %u ,period_count: %u\n", channels, rate,
         pcm_format_to_bits((pcm_format) format), period_size, period_count);

    return 0;
}

jbyteArray nativeRead(JNIEnv *env, jobject obj) {
    if (!g_pcm || !pcm_is_ready(g_pcm)) {
        LOGE("read: Unable to open PCM device (%s)\n", pcm_get_error(g_pcm));
        return 0;
    }

    if (!pcm_read(g_pcm, g_buffer, g_size)) {
        jbyteArray array = env->NewByteArray(g_size);
        jbyte *bytes = env->GetByteArrayElements(array, 0);
        memcpy(bytes, g_buffer, g_size);
        env->SetByteArrayRegion(array, 0, g_size, bytes);
        return array;
    } else {
        LOGE("read, Error reading PCM device (%s)\n", pcm_get_error(g_pcm));
        return NULL;
    }
}

jint nativeReadBuffer(JNIEnv *env, jobject obj, jbyteArray array) {
    if (!g_pcm || !pcm_is_ready(g_pcm)) {
        LOGE("read: Unable to open PCM device (%s)\n", pcm_get_error(g_pcm));
        return 0;
    }
    if (!pcm_read(g_pcm, g_buffer, g_size)) {
        jbyte *bytes = env->GetByteArrayElements(array, 0);
        memcpy(bytes, g_buffer, g_size);
        env->SetByteArrayRegion(array, 0, g_size, bytes);
        return g_size;
    } else {
        LOGE("read, Error reading PCM device (%s)\n", pcm_get_error(g_pcm));
        return -1;
    }
}

void nativeStop(JNIEnv *env, jobject obj) {
    LOGI("stop");

    free(g_buffer);
    pcm_close(g_pcm);
}

JNINativeMethod methods[] = {
        {"nativeStart", "(IIIIIII)I", (void *) nativeStart},
        {"nativeRead",  "()[B",       (void *) nativeRead},
        {"nativeReadBuffer",  "([B)I",      (void *) nativeReadBuffer},
        {"nativeStop",  "()V",        (void *) nativeStop},
};

jint register_native_method(JNIEnv *env) {
    jclass cl = env->FindClass("com/aeke/pcm/audio_record/NativeAudioRecord");
    if ((env->RegisterNatives(cl, methods, sizeof(methods) / sizeof(methods[0]))) < 0) {
        return -1;
    }
    return 0;
}

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    if (register_native_method(env) != JNI_OK) {
        return -1;
    }
    return JNI_VERSION_1_6;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_aeke_pcm_audio_1record_NativeAudioRecord_nativeRead___3B(JNIEnv *env, jobject thiz, jbyteArray buffer) {
    // TODO: implement nativeRead()
}