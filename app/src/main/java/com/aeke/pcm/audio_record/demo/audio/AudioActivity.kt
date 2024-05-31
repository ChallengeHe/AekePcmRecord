//package com.aeke.fitnessmirror.music.ui.audio
//
//import android.os.Environment
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.lifecycleScope
//import com.aeke.fitnessmirror.common.base.BaseActivity
//import com.aeke.fitnessmirror.common.base.BasePresenter
//import com.aeke.fitnessmirror.common.base.IBaseView
//import com.aeke.fitnessmirror.databinding.ActivityAudioBinding
//import com.aeke.fitnessmirror.utils.ext.setNoDoubleClickListener
//import com.aispeech.dui.dds.DDS
//import kotlinx.coroutines.flow.collectLatest
//
//class AudioActivity : BaseActivity<ActivityAudioBinding, BasePresenter<IBaseView>, IBaseView>(), IBaseView {
//    override fun refreshAfterLogin() {
//
//    }
//
//    private var isAudioRecording = false
//    private var hasRecordedAudio = false
//    private var isPlayingPCMAudio = false
//    private var audioPCMFileAbsolutePath = ""
//    private val viewModel: AudioViewModel by lazy {
//        ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(AudioViewModel::class.java)
//    }
//
//
//    override fun init() {
//        super.init()
////        DDS.getInstance().agent.wakeupEngine.disableMainWakeupWord()
//        vb.btnAudioRecord.setNoDoubleClickListener {
//            if (isAudioRecording) {
//                viewModel.audioStopRecord()
//                vb.btnAudioRecord.text = "Start Record"
//            } else {
//                viewModel.audioRecord()
//                vb.btnAudioRecord.text = "Stop Record"
//            }
//        }
//        vb.btnAudioPlay.setNoDoubleClickListener {
//            if (isAudioRecording) {
//                return@setNoDoubleClickListener
//            }
//            if (!hasRecordedAudio) {
//                return@setNoDoubleClickListener
//            }
//            if (isPlayingPCMAudio) {
//                viewModel.stopPlayingPCMAudio()
//                vb.btnAudioPlay.text = "Start Play"
//            } else {
//                viewModel.playPCMAudio()
//                vb.btnAudioPlay.text = "Stop Play"
//            }
//        }
//
//        vb.btnSavePcm.setNoDoubleClickListener {
//            if (isAudioRecording) {
//                return@setNoDoubleClickListener
//            }
//            if (!hasRecordedAudio) {
//                return@setNoDoubleClickListener
//            }
//            if (audioPCMFileAbsolutePath.isNotEmpty()) {
////                return@setNoDoubleClickListener
//            }
//            val outputPCMFilePath = getExternalFilesDir(Environment.DIRECTORY_MUSIC)
//                ?.absolutePath + "/Audio/audio.pcm"
//            viewModel.saveAsPCMFile(outputPCMFilePath)
//        }
//
//        lifecycleScope.launchIO {
//            viewModel.isAudioRecording.collectLatest {
//                isAudioRecording = it
//            }
//        }
//        lifecycleScope.launchIO {
//            viewModel.audioRecordingTime.collectLatest {
//                launchMain {
//                    vb.tvDuration.text = it
//                }
//            }
//        }
//        lifecycleScope.launchIO {
//            viewModel.hasRecordedAudio.collectLatest {
//                hasRecordedAudio = it
//            }
//        }
//        lifecycleScope.launchIO {
//            viewModel.isPlayingPCMAudio.collectLatest {
//                isPlayingPCMAudio = it
//            }
//        }
//        lifecycleScope.launchIO {
//            viewModel.audioPCMFileAbsolutePath.collectLatest {
//                audioPCMFileAbsolutePath = it
//                launchMain{
//                    vb.tvPcmFilePath.text = it
//                }
//            }
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
////        DDS.getInstance().agent.wakeupEngine.enableMainWakeupWord()
//    }
//}