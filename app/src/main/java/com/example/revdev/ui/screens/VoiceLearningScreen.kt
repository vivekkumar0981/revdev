package com.example.revdev.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.revdev.data.*
import com.example.revdev.ui.components.*
import com.example.revdev.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceLearningScreen(
    modifier: Modifier = Modifier,
    courseViewModel: CourseViewModel? = null
) {
    val context = LocalContext.current
    val voiceService = remember { VoiceService(context) }
    var isReady by remember { mutableStateOf(false) }
    var isSpeaking by remember { mutableStateOf(false) }
    var isListening by remember { mutableStateOf(false) }
    var currentMode by remember { mutableStateOf<VoiceMode>(VoiceMode.IDLE) }
    var currentText by remember { mutableStateOf("") }
    var userAnswer by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf("") }
    var lessonIndex by remember { mutableStateOf(0) }
    var questionAsked by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val voiceLessons = remember {
        listOf(
            VoiceLesson("HTML Basics", "HTML stands for HyperText Markup Language. It is the standard language for creating web pages. HTML describes the structure of a web page using elements represented by tags. Tags are enclosed in angle brackets like this: opening tag, content, closing tag.", "What does HTML stand for?", "HyperText Markup Language"),
            VoiceLesson("HTML Tags", "HTML uses tags to define elements. Common tags include: h1 through h6 for headings, p for paragraphs, a for links, img for images, and div for divisions or sections. Every tag should be properly opened and closed.", "Name three common HTML tags.", "h1, p, a, img, div"),
            VoiceLesson("CSS Introduction", "CSS stands for Cascading Style Sheets. It controls the visual presentation of HTML elements. CSS can change colors, fonts, spacing, layout, and animations. You can write CSS inline, in a style tag, or in an external file.", "What does CSS stand for and what does it do?", "Cascading Style Sheets, controls visual presentation"),
            VoiceLesson("CSS Selectors", "CSS selectors target HTML elements to style them. The main types are: element selectors like p or h1, class selectors starting with a dot, ID selectors starting with a hash, and attribute selectors using square brackets.", "What symbol starts a class selector in CSS?", "dot or period"),
            VoiceLesson("JavaScript Basics", "JavaScript is a programming language that makes web pages interactive. It can respond to user clicks, validate forms, change page content dynamically, and communicate with servers. Variables in JavaScript can be declared with let, const, or var.", "Name the three keywords to declare variables in JavaScript.", "let, const, var")
        )
    }

    DisposableEffect(Unit) {
        voiceService.initialize { isReady = true }
        onDispose { voiceService.destroy() }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color(0xFF9C27B0), Color(0xFF6A1B9A))))
                .padding(24.dp)
        ) {
            Column {
                Text("Voice Learning", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = DarkOnPrimary)
                Text("Learn hands-free with AI voice tutor", style = MaterialTheme.typography.bodyMedium, color = DarkOnPrimary.copy(alpha = 0.8f))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isReady) {
                Spacer(modifier = Modifier.height(48.dp))
                CircularProgressIndicator(color = DarkPrimary)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Initializing voice engine...", color = DarkOnSurfaceVariant)
                return@Column
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCardBackground),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Lesson ${lessonIndex + 1} of ${voiceLessons.size}",
                        style = MaterialTheme.typography.labelMedium,
                        color = DarkPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        voiceLessons[lessonIndex].title,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = DarkOnSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ProgressBar(progress = (lessonIndex + 1f) / voiceLessons.size)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Voice animation circle
            val infiniteTransition = rememberInfiniteTransition()
            val pulseScale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = if (isSpeaking || isListening) 1.15f else 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800),
                    repeatMode = RepeatMode.Reverse
                )
            )

            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(pulseScale)
                    .background(
                        brush = Brush.radialGradient(
                            colors = when {
                                isListening -> listOf(Color(0xFF4CAF50), Color(0xFF2E7D32))
                                isSpeaking -> listOf(Color(0xFF9C27B0), Color(0xFF6A1B9A))
                                else -> listOf(DarkPrimary, DarkPrimaryContainer)
                            }
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        isListening -> Icons.Default.Mic
                        isSpeaking -> Icons.Default.VolumeUp
                        else -> Icons.Default.PlayArrow
                    },
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = when {
                    isListening -> "Listening... speak your answer"
                    isSpeaking -> "Speaking..."
                    questionAsked -> "Tap mic to answer"
                    currentMode == VoiceMode.IDLE -> "Tap Play to start lesson"
                    else -> "Ready"
                },
                style = MaterialTheme.typography.bodyLarge,
                color = DarkOnSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (currentText.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkCardBackground),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        currentText,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkOnSurface
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (userAnswer.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkPrimary.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Your answer:", style = MaterialTheme.typography.labelMedium, color = DarkPrimary)
                        Text(userAnswer, style = MaterialTheme.typography.bodyMedium, color = DarkOnSurface)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (feedback.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSuccess.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Feedback:", style = MaterialTheme.typography.labelMedium, color = DarkSuccess)
                        Text(feedback, style = MaterialTheme.typography.bodyMedium, color = DarkOnSurface)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Play/Pause lesson
                FloatingActionButton(
                    onClick = {
                        if (isSpeaking) {
                            voiceService.stopSpeaking()
                            isSpeaking = false
                        } else {
                            val lesson = voiceLessons[lessonIndex]
                            currentText = lesson.content
                            currentMode = VoiceMode.LESSON
                            isSpeaking = true
                            voiceService.speak(lesson.content) {
                                isSpeaking = false
                                questionAsked = true
                                currentText = lesson.question
                                voiceService.speak(lesson.question) {
                                    isSpeaking = false
                                }
                            }
                        }
                    },
                    containerColor = if (isSpeaking) DarkError else DarkPrimary
                ) {
                    Icon(if (isSpeaking) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                }

                // Mic button
                val micEnabled = questionAsked || currentMode != VoiceMode.IDLE
                FloatingActionButton(
                    onClick = {
                        if (!micEnabled) return@FloatingActionButton
                        if (isListening) {
                            voiceService.stopListening()
                            isListening = false
                        } else {
                            isListening = true
                            voiceService.startListening(
                                onResult = { text ->
                                    userAnswer = text
                                    isListening = false
                                    val lesson = voiceLessons[lessonIndex]
                                    val isCloseEnough = text.lowercase().contains(lesson.expectedKeywords.lowercase().split(",").first().trim())
                                    feedback = if (isCloseEnough) {
                                        courseViewModel?.addXP(XPRewards.VOICE_QUIZ_CORRECT)
                                        "Correct! Well done. +${XPRewards.VOICE_QUIZ_CORRECT} XP"
                                    } else {
                                        "Not quite. The answer includes: ${lesson.expectedKeywords}"
                                    }
                                    voiceService.speak(feedback) { isSpeaking = false }
                                    isSpeaking = true
                                },
                                onError = { err ->
                                    isListening = false
                                    feedback = err
                                }
                            )
                        }
                    },
                    containerColor = if (isListening) Color(0xFF4CAF50) else if (micEnabled) DarkCardBackground else DarkCardBackground.copy(alpha = 0.5f)
                ) {
                    Icon(Icons.Default.Mic, contentDescription = null, tint = if (isListening) Color.White else if (micEnabled) DarkOnSurface else DarkOnSurface.copy(alpha = 0.5f))
                }

                // Next lesson
                FloatingActionButton(
                    onClick = {
                        if (lessonIndex < voiceLessons.size - 1) {
                            lessonIndex++
                            currentText = ""
                            userAnswer = ""
                            feedback = ""
                            questionAsked = false
                            currentMode = VoiceMode.IDLE
                        }
                    },
                    containerColor = DarkCardBackground
                ) {
                    Icon(Icons.Default.SkipNext, contentDescription = null, tint = DarkOnSurface)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text("Play", style = MaterialTheme.typography.labelSmall, color = DarkOnSurfaceVariant)
                Text("Answer", style = MaterialTheme.typography.labelSmall, color = DarkOnSurfaceVariant)
                Text("Next", style = MaterialTheme.typography.labelSmall, color = DarkOnSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

private enum class VoiceMode { IDLE, LESSON, QUESTION }

private data class VoiceLesson(
    val title: String,
    val content: String,
    val question: String,
    val expectedKeywords: String
)
