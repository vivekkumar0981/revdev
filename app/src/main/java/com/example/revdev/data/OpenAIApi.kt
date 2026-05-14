 package com.example.revdev.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import kotlinx.coroutines.CancellationException
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import com.example.revdev.BuildConfig

object OpenAIApi {
    private const val API_URL = "https://openrouter.ai/api/v1/chat/completions"
    private val API_KEY = BuildConfig.OPENROUTER_API_KEY
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()
    private val json = Json { ignoreUnknownKeys = true }
    
    private var lastRequestTime = 0L
    private val minRequestInterval = 1000L

    suspend fun ask(question: String, systemPrompt: String? = null): String {
        return withContext(Dispatchers.IO) {
            try {
                val currentTime = System.currentTimeMillis()
                val timeSinceLastRequest = currentTime - lastRequestTime
                if (timeSinceLastRequest < minRequestInterval) {
                    delay(minRequestInterval - timeSinceLastRequest)
                }
                lastRequestTime = System.currentTimeMillis()
                
                val messages = mutableListOf<OpenAIMessage>()
                systemPrompt?.let { messages.add(OpenAIMessage(role = "system", content = it)) }
                messages.add(OpenAIMessage(role = "user", content = question))
                
                val requestBody = json.encodeToString(
                    OpenAIRequest.serializer(),
                    OpenAIRequest(
                        model = "openai/gpt-oss-120b:free",
                        messages = messages,
                        maxTokens = 1024
                    )
                ).toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", "Bearer $API_KEY")
                    .addHeader("Content-Type", "application/json")
                    .post(requestBody)
                    .build()

                client.newCall(request).execute().use { response ->
                    when (response.code) {
                        200 -> {
                            val body = response.body?.string() ?: return@withContext "No response body"
                            val completion = json.decodeFromString(OpenAIResponse.serializer(), body)
                            return@withContext completion.choices.firstOrNull()?.message?.content?.trim() ?: "No answer"
                        }
                        429 -> {
                            delay(2000)
                            return@withContext "Rate limit exceeded. Please wait a moment and try again."
                        }
                        401 -> return@withContext "Authentication error. Please check your API key."
                        403 -> return@withContext "Access forbidden. Please check your API key permissions."
                        500, 502, 503, 504 -> return@withContext "Server error. Please try again later."
                        else -> return@withContext "Error ${response.code}: ${response.message}"
                    }
                }
            } catch (e: IOException) {
                return@withContext "Network error: ${e.message}"
            } catch (e: Exception) {
                return@withContext "Error: ${e.message}"
            }
        }
    }

    suspend fun generateQuiz(topic: String, numQuestions: Int = 5): String {
        val systemPrompt = """You are a quiz generator. Generate exactly $numQuestions multiple-choice questions about the given topic.
Format your response as JSON array:
[{"question":"...","options":["A","B","C","D"],"correctAnswer":0,"explanation":"..."}]
correctAnswer is 0-indexed. Keep questions clear and educational."""
        return ask(topic, systemPrompt)
    }

    suspend fun generateBug(topic: String, difficulty: String): String {
        val systemPrompt = """You are a coding challenge generator. Create a bug-fixing challenge for $difficulty difficulty.
Topic: $topic
Return ONLY a JSON object (no markdown, no code fences):
{"title":"short title","description":"what the code should do","brokenCode":"the code WITH a bug","hint":"subtle hint","solution":"the fixed code"}
The bug should match the difficulty:
- EASY: typo, missing closing tag, wrong attribute
- MEDIUM: logic error, wrong selector, incorrect property value
- HARD: structural issue, wrong nesting, conflicting styles"""
        return ask("Generate a $difficulty $topic bug challenge", systemPrompt)
    }

    suspend fun validateBugFix(brokenCode: String, userCode: String, description: String): String {
        val systemPrompt = """You validate bug fixes. The user was given broken code and asked to fix it.
Original broken code: $brokenCode
Task description: $description
User's fix: $userCode
Respond with ONLY a JSON object (no markdown):
{"correct":true/false,"feedback":"brief explanation of why correct or what's still wrong"}"""
        return ask("Validate this fix", systemPrompt)
    }

    suspend fun reviewResume(resumeText: String): String {
        val systemPrompt = """You are an expert resume reviewer. Analyze the resume and provide:
1. Overall score out of 100
2. Strengths (bullet points)
3. Weaknesses (bullet points)
4. Specific improvement suggestions
5. ATS compatibility assessment
Be constructive and specific."""
        return ask(resumeText, systemPrompt)
    }

    /**
     * Streams the answer as it is generated by the model.
     * Usage: OpenAIApi.askStream(question) { partialText -> ... }
     */
    suspend fun askStream(question: String, systemPrompt: String? = null, onToken: (String) -> Unit): String = withContext(Dispatchers.IO) {
        val currentTime = System.currentTimeMillis()
        val timeSinceLastRequest = currentTime - lastRequestTime
        if (timeSinceLastRequest < minRequestInterval) {
            delay(minRequestInterval - timeSinceLastRequest)
        }
        lastRequestTime = System.currentTimeMillis()

        val messagesJson = buildString {
            append("[")
            if (systemPrompt != null) {
                append("{\"role\":\"system\",\"content\":\"${systemPrompt.replace("\"", "\\\"").replace("\n", "\\n")}\"},")
            }
            append("{\"role\":\"user\",\"content\":\"${question.replace("\"", "\\\"").replace("\n", "\\n")}\"}")
            append("]")
        }
        val requestJson = buildString {
            append("{")
            append("\"model\":\"openai/gpt-oss-120b:free\",")
            append("\"messages\":$messagesJson,")
            append("\"max_tokens\":1024,")
            append("\"stream\":true")
            append("}")
        }
        val requestBody = requestJson.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(API_URL)
            .addHeader("Authorization", "Bearer $API_KEY")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()
        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext when (response.code) {
                        429 -> "Rate limit exceeded. Please wait a moment and try again."
                        401 -> "Authentication error. Please check your API key."
                        403 -> "Access forbidden. Please check your API key permissions."
                        500, 502, 503, 504 -> "Server error. Please try again later."
                        else -> "Error ${response.code}: ${response.message}"
                    }
                }
                val reader = BufferedReader(InputStreamReader(response.body?.byteStream()))
                val answer = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    if (line.isNullOrBlank() || !line!!.startsWith("data:")) continue
                    val jsonLine = line!!.removePrefix("data:").trim()
                    if (jsonLine == "[DONE]") break
                    try {
                        val event = kotlinx.serialization.json.Json.parseToJsonElement(jsonLine)
                        val content = event.jsonObject["choices"]
                            ?.jsonArray?.getOrNull(0)?.jsonObject
                            ?.get("delta")?.jsonObject?.get("content")?.jsonPrimitive?.content
                        if (!content.isNullOrEmpty()) {
                            answer.append(content)
                            onToken(answer.toString())
                        }
                    } catch (_: Exception) { /* ignore parse errors for partial lines */ }
                }
                return@withContext answer.toString().ifBlank { "No answer" }
            }
        } catch (e: IOException) {
            return@withContext "Network error: ${e.message}"
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return@withContext "Error: ${e.message}"
        }
    }
}

@Serializable
data class OpenAIRequest(
    val model: String,
    val messages: List<OpenAIMessage>,
    @SerialName("max_tokens") val maxTokens: Int = 256
)

@Serializable
data class OpenAIMessage(
    val role: String,
    val content: String
)

@Serializable
data class OpenAIResponse(
    val choices: List<OpenAIChoice>
)

@Serializable
data class OpenAIChoice(
    val message: OpenAIMessage
)