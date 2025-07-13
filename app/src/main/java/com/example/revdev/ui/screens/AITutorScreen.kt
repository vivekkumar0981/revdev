package com.example.revdev.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.revdev.data.ChatMessage
import com.example.revdev.ui.components.*
import com.example.revdev.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AITutorScreen(modifier: Modifier = Modifier) {
    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var inputText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    // Add welcome message
    LaunchedEffect(Unit) {
        messages = listOf(
            ChatMessage(
                id = "welcome",
                content = "Hello! I'm your AI tutor. I'm here to help you with HTML, CSS, and web development. Feel free to ask me any questions!",
                isUser = false
            )
        )
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            DarkPrimary,
                            DarkPrimaryContainer
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "AI Tutor",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = DarkOnPrimary
                    )
                    
                    Text(
                        text = "Ask me anything about web development",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkOnPrimary.copy(alpha = 0.8f)
                    )
                }
                
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = DarkOnPrimary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp),
                    tint = DarkOnPrimary
                )
            }
        }
        
        // Chat Messages
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(messages) { message ->
                ChatMessageItem(message = message)
            }
            
            if (isLoading) {
                item {
                    TypingIndicator()
                }
            }
        }
        
        // Input Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = DarkCardBackground
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            text = "Ask your question...",
                            color = DarkOnSurfaceVariant
                        )
                    },
                    shape = RoundedCornerShape(20.dp),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    ),
                    maxLines = 4
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                FloatingActionButton(
                    onClick = {
                        if (inputText.isNotBlank() && !isLoading) {
                            val userMessage = ChatMessage(
                                id = System.currentTimeMillis().toString(),
                                content = inputText,
                                isUser = true
                            )
                            messages = messages + userMessage
                            val currentInput = inputText
                            inputText = ""
                            isLoading = true
                            
                            coroutineScope.launch {
                                delay(1500) // Simulate AI thinking
                                val aiResponse = generateAIResponse(currentInput)
                                val aiMessage = ChatMessage(
                                    id = System.currentTimeMillis().toString(),
                                    content = aiResponse,
                                    isUser = false
                                )
                                messages = messages + aiMessage
                                isLoading = false
                                listState.animateScrollToItem(messages.size - 1)
                            }
                        }
                    },
                    modifier = Modifier.size(48.dp),
                    containerColor = DarkPrimary,
                    contentColor = DarkOnPrimary
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send"
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatMessageItem(message: ChatMessage) {
    val alignment = if (message.isUser) Alignment.End else Alignment.Start
    val backgroundColor = if (message.isUser) DarkPrimary else DarkCardBackground
    val textColor = if (message.isUser) DarkOnPrimary else DarkOnSurface
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Card(
            modifier = Modifier.widthIn(max = 280.dp),
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor
            ),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isUser) 16.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 16.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                if (!message.isUser) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = DarkPrimary
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = "AI Tutor",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = DarkPrimary
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                }
                
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor,
                    textAlign = if (message.isUser) TextAlign.End else TextAlign.Start
                )
            }
        }
    }
}

@Composable
private fun TypingIndicator() {
    Card(
        modifier = Modifier.widthIn(max = 280.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkCardBackground
        ),
        shape = RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomStart = 4.dp,
            bottomEnd = 16.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = DarkPrimary
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "AI Tutor",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = DarkPrimary
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Animated dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(3) { index ->
                    val infiniteTransition = rememberInfiniteTransition()
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(600, delayMillis = index * 200),
                            repeatMode = RepeatMode.Reverse
                        )
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = DarkPrimary.copy(alpha = alpha),
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }
            }
        }
    }
}

private fun generateAIResponse(userInput: String): String {
    val lowerInput = userInput.lowercase()
    
    return when {
        lowerInput.contains("html") && lowerInput.contains("what") -> {
            "HTML (HyperText Markup Language) is the standard markup language for creating web pages. It describes the structure of a web page semantically and originally included cues for the appearance of the document."
        }
        lowerInput.contains("css") && lowerInput.contains("what") -> {
            "CSS (Cascading Style Sheets) is a style sheet language used for describing the presentation of a document written in HTML. CSS describes how elements should be rendered on screen, on paper, in speech, or on other media."
        }
        lowerInput.contains("div") -> {
            "The `<div>` element is a block-level container used to group other HTML elements. It's commonly used for layout purposes and can be styled with CSS. For example: `<div class=\"container\">Content here</div>`"
        }
        lowerInput.contains("flexbox") || lowerInput.contains("flex") -> {
            "Flexbox is a CSS layout model that allows you to design flexible responsive layouts. It provides an efficient way to distribute space among items in a container and align them. Use `display: flex` to create a flex container."
        }
        lowerInput.contains("grid") -> {
            "CSS Grid is a two-dimensional layout system designed for the web. It lets you lay out items in rows and columns, and has many features that make building complex layouts straightforward."
        }
        lowerInput.contains("responsive") -> {
            "Responsive design ensures that web pages look good on all devices. Use media queries, flexible grids, and responsive images. Start with mobile-first design and use `@media` queries to adapt for larger screens."
        }
        else -> {
            "That's a great question! I'd be happy to help you with HTML, CSS, or web development concepts. Could you provide more specific details about what you'd like to learn?"
        }
    }
} 