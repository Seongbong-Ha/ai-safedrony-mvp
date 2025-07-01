package com.seongbongha07.ai_safedrony.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TTSManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false

    init {
        // TTS 객체 초기화
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // TTS 언어 설정 (한국어)
            val result = tts?.setLanguage(Locale.KOREAN)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTSManager", "Error: TTS 언어 데이터가 없거나 지원되지 않습니다.")
                isTtsInitialized = false
                // 사용자에게 언어 데이터 설치를 요청하는 Intent를 시작할 수도 있음 (필요시)
                // val installIntent = Intent()
                // installIntent.action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
                // context.startActivity(installIntent)
            } else {
                Log.d("TTSManager", "TTS 초기화 성공!")
                isTtsInitialized = true
            }
        } else {
            Log.e("TTSManager", "Error: TTS 초기화 실패! Status: $status")
            isTtsInitialized = false
        }
    }

    /**
     * 지정된 텍스트를 음성으로 출력합니다.
     * @param text 음성으로 변환할 텍스트
     */
    fun speakOut(text: String) {
        if (isTtsInitialized && tts != null) {
            // QUEUE_FLUSH: 이전 음성을 중단하고 현재 음성을 즉시 시작
            // QUEUE_ADD: 이전 음성이 끝나면 현재 음성을 이어서 시작
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, text) // utteranceId로 text 사용
            Log.d("TTSManager", "Speaking: \"$text\"")
        } else {
            Log.e("TTSManager", "TTS가 아직 초기화되지 않았거나 오류가 발생했습니다.")
        }
    }

    /**
     * TTS 자원을 해제합니다. 앱 종료 시 호출해야 합니다.
     */
    fun shutdown() {
        tts?.stop() // 현재 재생 중인 음성 중단
        tts?.shutdown() // TTS 엔진 종료
        Log.d("TTSManager", "TTS 자원 해제 완료.")
        isTtsInitialized = false
    }
}