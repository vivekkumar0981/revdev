package com.example.revdev.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.revdev.data.ChatMessage
import com.example.revdev.data.OpenAIApi
import com.example.revdev.data.local.AppDatabase
import com.example.revdev.data.local.ChatMessageEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AITutorViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).chatMessageDao()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _streamingMessage = MutableStateFlow<String?>(null)
    val streamingMessage: StateFlow<String?> = _streamingMessage.asStateFlow()

    private val _cooldownSeconds = MutableStateFlow(0)
    val cooldownSeconds: StateFlow<Int> = _cooldownSeconds.asStateFlow()

    init {
        observeMessages()
    }

    private fun observeMessages() {
        var welcomeInserted = false
        viewModelScope.launch {
            dao.getAllMessages().collect { entities ->
                if (entities.isEmpty() && !welcomeInserted) {
                    welcomeInserted = true
                    val welcome = ChatMessage(
                        id = "welcome",
                        content = "Hey there! I'm your AI tutor for all things web development — MERN stack (MongoDB, Express, React, Node.js), HTML, CSS, JavaScript, TypeScript, APIs, databases, deployment, and more. Ask me anything!",
                        isUser = false
                    )
                    withContext(Dispatchers.IO) { dao.insertMessage(welcome.toEntity()) }
                } else if (entities.isNotEmpty()) {
                    _messages.value = entities.map { it.toDomain() }
                }
            }
        }
    }

    fun sendMessage(text: String) {
        if (_isLoading.value || _cooldownSeconds.value > 0 || text.isBlank()) return

        val userMessage = ChatMessage(
            id = System.currentTimeMillis().toString(),
            content = text,
            isUser = true
        )
        _isLoading.value = true
        _streamingMessage.value = null

        viewModelScope.launch {
            withContext(Dispatchers.IO) { dao.insertMessage(userMessage.toEntity()) }
            try {
                var lastText = ""
                val systemPrompt = "You are RevDev AI Tutor, an expert in full-stack web development including the MERN stack (MongoDB, Express.js, React, Node.js), HTML, CSS, JavaScript, TypeScript, REST APIs, GraphQL, databases (SQL & NoSQL), Git, deployment, DevOps, and modern frameworks. Give clear, concise explanations with code examples when helpful. Be encouraging and educational. Never use abbreviations like 'TL;DR' — use 'Summary' instead."

                val aiResponse = OpenAIApi.askStream(text, systemPrompt) { partialText ->
                    _streamingMessage.value = partialText
                    lastText = partialText
                }

                if (aiResponse.contains("Rate limit exceeded")) {
                    startCooldown(10)
                    val errorMsg = ChatMessage(
                        id = System.currentTimeMillis().toString(),
                        content = "You are sending requests too quickly or have reached your usage limit. Please wait 10 seconds before trying again.",
                        isUser = false
                    )
                    withContext(Dispatchers.IO) { dao.insertMessage(errorMsg.toEntity()) }
                } else {
                    val aiMessage = ChatMessage(
                        id = System.currentTimeMillis().toString(),
                        content = lastText,
                        isUser = false
                    )
                    withContext(Dispatchers.IO) { dao.insertMessage(aiMessage.toEntity()) }
                }
            } catch (e: Exception) {
                val errorMsg = ChatMessage(
                    id = System.currentTimeMillis().toString(),
                    content = "AI error: ${e.message}",
                    isUser = false
                )
                withContext(Dispatchers.IO) { dao.insertMessage(errorMsg.toEntity()) }
            } finally {
                _isLoading.value = false
                _streamingMessage.value = null
            }
        }
    }

    private fun startCooldown(seconds: Int) {
        _cooldownSeconds.value = seconds
        viewModelScope.launch {
            var remaining = seconds
            while (remaining > 0) {
                kotlinx.coroutines.delay(1000)
                remaining--
                _cooldownSeconds.value = remaining
            }
        }
    }

    private fun ChatMessageEntity.toDomain() = ChatMessage(id, content, isUser, timestamp)
    private fun ChatMessage.toEntity() = ChatMessageEntity(id, content, isUser, timestamp)
}
