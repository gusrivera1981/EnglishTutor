package com.english.tutor.network

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Interfaz para la API de Ollama
 */
interface OllamaApi {
    @Headers("Content-Type: application/json")
    @POST("api/chat")
    suspend fun chat(@Body request: ChatRequest): Response<ChatResponse>
}

data class ChatRequest(
    val model: String,
    val messages: List<Message>,
    val stream: Boolean = false,
    val temperature: Double? = null
)

data class Message(
    val role: String,
    val content: String
) {
    init {
        require(role in listOf("user", "assistant", "system")) {
            "Rol inválido: $role"
        }
        require(content.isNotBlank()) {
            "Contenido vacío"
        }
    }
}

/**
 * Respuesta de Ollama
 */
data class ChatResponse(
    @SerializedName("model") val model: String? = null,
    @SerializedName("message") val message: Message? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("done") val done: Boolean? = null,
    @SerializedName("error") val error: String? = null  // ✅ SOLO la propiedad
)