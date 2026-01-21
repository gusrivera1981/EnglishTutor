package com.english.tutor.ui.viewmodel

import android.app.Application
import android.content.Intent
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.english.tutor.network.ChatRequest
import com.english.tutor.network.Message
import com.english.tutor.network.NetworkModule
import com.english.tutor.network.Constants
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.net.ConnectException
import java.util.Locale

class ConversationViewModel(application: Application) : AndroidViewModel(application) {

    /* -------------------- ESTADO -------------------- */

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _translatedTexts = MutableStateFlow<Map<String, String>>(emptyMap())
    val translatedTexts: StateFlow<Map<String, String>> = _translatedTexts.asStateFlow()

    private var lastSpokenMessage: Message? = null

    /* -------------------- TTS / STT -------------------- */

    private val tts: TextToSpeech by lazy {
        lateinit var instance: TextToSpeech
        instance = TextToSpeech(getApplication()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                instance.language = Locale.US
                instance.setSpeechRate(0.9f)
                Log.i("TTS", "Inicializado correctamente")
            } else {
                Log.e("TTS", "Error al inicializar: $status")
            }
        }
        instance
    }

    private var speechRecognizer: SpeechRecognizer? = null

    /* -------------------- API -------------------- */

    private val api = NetworkModule.api

    /* -------------------- INIT -------------------- */

    init {
        setupSpeechRecognition()
        Log.d("ViewModel", "Inicializado")
    }

    private fun setupSpeechRecognition() {
        if (SpeechRecognizer.isRecognitionAvailable(getApplication())) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplication())
            setupRecognitionListener()
            Log.i("STT", "Recognizer disponible")
        } else {
            Log.e("STT", "Recognizer no disponible")
            addMessage("assistant", "⚠️ Reconocimiento de voz no disponible")
        }
    }

    /* -------------------- STT -------------------- */

    private fun setupRecognitionListener() {
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: android.os.Bundle?) {
                _isListening.value = true
                Log.d("STT", "Ready for speech")
            }

            override fun onEndOfSpeech() {
                _isListening.value = false
                Log.d("STT", "End of speech")
            }

            override fun onError(error: Int) {
                _isListening.value = false
                val errorMsg = getSpeechErrorMessage(error)
                Log.e("STT", "Error: $errorMsg")
                addMessage("assistant", "❌ $errorMsg")
            }

            override fun onResults(results: android.os.Bundle?) {
                _isListening.value = false
                val text = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()

                if (!text.isNullOrBlank()) {
                    Log.d("STT", "Resultado: $text")
                    sendMessage(text)
                } else {
                    Log.w("STT", "Texto vacío")
                }
            }

            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onPartialResults(partialResults: android.os.Bundle?) {}
            override fun onEvent(eventType: Int, params: android.os.Bundle?) {}
        })
    }

    private fun getSpeechErrorMessage(error: Int): String = when (error) {
        SpeechRecognizer.ERROR_NO_MATCH -> "No se entendió. Intenta de nuevo."
        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Tiempo de espera agotado."
        SpeechRecognizer.ERROR_NETWORK -> "Error de red."
        else -> "Error de reconocimiento: $error"
    }

    fun startVoiceRecognition() {
        if (_isListening.value || _isSpeaking.value) {
            Log.w("STT", "Ya está en uso")
            return
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getApplication<Application>().packageName)
        }

        speechRecognizer?.startListening(intent)
        Log.d("STT", "Iniciando reconocimiento")
    }

    fun stopVoiceRecognition() {
        speechRecognizer?.stopListening()
        _isListening.value = false
        Log.d("STT", "Deteniendo reconocimiento")
    }

    /* -------------------- TTS -------------------- */

    private fun speak(text: String) {
        if (tts.isSpeaking) tts.stop()

        _isSpeaking.value = true
        Log.d("TTS", "Hablando: ${text.take(50)}...")

        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                Log.d("TTS", "Inicio: $utteranceId")
            }

            override fun onDone(utteranceId: String?) {
                _isSpeaking.value = false
                Log.d("TTS", "Finalizado: $utteranceId")
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                _isSpeaking.value = false
                Log.e("TTS", "Error $errorCode en $utteranceId")
                addMessage("assistant", "⚠️ Error al reproducir audio")
            }

            @Deprecated("Método obsoleto")
            override fun onError(utteranceId: String?) {
                _isSpeaking.value = false
                Log.e("TTS", "Error en $utteranceId")
            }
        })

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "TTS_${System.currentTimeMillis()}")
    }

    fun stopSpeaking() {
        if (tts.isSpeaking) {
            tts.stop()
            _isSpeaking.value = false
            Log.d("TTS", "Detenido manualmente")
        }
    }

    fun repeatLastMessage() {
        lastSpokenMessage?.let {
            Log.d("TTS", "Repetir: ${it.content.take(50)}...")
            speak(it.content)
        } ?: run {
            Log.w("TTS", "No hay mensaje para repetir")
        }
    }

    /* -------------------- CHAT -------------------- */

    fun sendMessage(text: String) {
        if (text.isBlank() || _isLoading.value) {
            Log.w("API", "Mensaje vacío o ya cargando")
            return
        }

        addMessage("user", text)
        _isLoading.value = true

        viewModelScope.launch {
            try {
                Log.d("API", "Enviando mensaje: $text")

                val response = api.chat(
                    ChatRequest(
                        model = "llama3.1:8b",
                        messages = _messages.value,
                        stream = false
                    )
                )

                if (response.isSuccessful) {
                    val assistantMessage = response.body()?.message

                    if (assistantMessage?.content.isNullOrBlank()) {
                        Log.e("API", "Respuesta vacía")
                        addMessage("assistant", "⚠️ El modelo no respondió")
                        return@launch
                    }

                    assistantMessage?.let { msg ->
                        Log.d("API", "Respuesta recibida: ${msg.content.take(50)}...")
                        addMessage("assistant", msg.content)
                        lastSpokenMessage = msg
                        speak(msg.content)
                    }
                } else {
                    Log.e("API", "Error HTTP: ${response.code()} - ${response.message()}")
                    addMessage("assistant", "❌ Error del servidor: ${response.code()}")
                }

            } catch (e: Exception) {
                handleApiError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun handleApiError(e: Exception) {
        // ✅ CORREGIDO: When exhaustivo con imports explícitos
        val errorMessage = when (e) {
            is SocketTimeoutException -> "⏱️ Timeout - El servidor tarda demasiado"
            is UnknownHostException -> "❌ Host no encontrado: ${Constants.BASE_URL}"
            is ConnectException -> "❌ Conexión rechazada. ¿Ollama está corriendo?"
            is HttpException -> "❌ Error HTTP ${e.code()}: ${e.message()}"
            else -> "💥 Error inesperado: ${e.message ?: e.javaClass.simpleName}"
        }

        Log.e("API_ERROR", "Error detallado", e)
        addMessage("assistant", errorMessage)
    }

    /* -------------------- TRADUCCIÓN -------------------- */

    fun toggleTranslation(messageId: String, originalText: String, isShowingTranslation: Boolean) {
        if (isShowingTranslation) {
            _translatedTexts.value = _translatedTexts.value - messageId
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d("API", "Traduciendo: $originalText")

                val response = api.chat(
                    ChatRequest(
                        model = "llama3.1:8b",
                        messages = listOf(
                            Message("user", "Translate to Spanish (only output translation):\n$originalText")
                        ),
                        stream = false
                    )
                )

                if (response.isSuccessful) {
                    val translation = response.body()?.message?.content?.trim()
                    if (!translation.isNullOrBlank()) {
                        _translatedTexts.value = _translatedTexts.value + (messageId to translation)
                        Log.d("API", "Traducción exitosa")
                    } else {
                        Log.e("API", "Traducción vacía")
                    }
                } else {
                    Log.e("API", "Error al traducir: ${response.code()}")
                }

            } catch (e: Exception) {
                Log.e("API", "Error en traducción", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /* -------------------- HELPERS -------------------- */

    private fun addMessage(role: String, content: String) {
        _messages.value = _messages.value + Message(role, content)
    }

    /* -------------------- CLEANUP -------------------- */

    override fun onCleared() {
        super.onCleared()
        Log.d("ViewModel", "Limpieza")
        speechRecognizer?.destroy()
        tts.shutdown()
    }
}