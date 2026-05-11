package com.example.revdev.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.revdev.data.QuizQuestion
import com.example.revdev.data.QuizResult
import com.example.revdev.data.CourseViewModel
import com.example.revdev.data.OpenAIApi
import com.example.revdev.ui.components.*
import com.example.revdev.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun QuizScreen(
    modifier: Modifier = Modifier,
    courseViewModel: CourseViewModel? = null,
    topic: String? = null
) {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    var showResult by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }
    var isGenerating by remember { mutableStateOf(false) }
    var selectedTopic by remember { mutableStateOf(topic ?: "") }
    var quizStarted by remember { mutableStateOf(topic != null) }
    val coroutineScope = rememberCoroutineScope()

    val availableTopics = listOf(
        "HTML Basics", "CSS Styling", "JavaScript Fundamentals",
        "HTML Forms", "CSS Flexbox", "CSS Grid", "DOM Manipulation"
    )
    
    var questions by remember {
        mutableStateOf(
            listOf(
                QuizQuestion(id = "1", question = "What does HTML stand for?", options = listOf("Hyper Text Markup Language", "High Tech Modern Language", "Home Tool Markup Language", "Hyperlink and Text Markup Language"), correctAnswer = 0),
                QuizQuestion(id = "2", question = "Which CSS property controls the text size?", options = listOf("text-size", "font-size", "text-style", "font-style"), correctAnswer = 1),
                QuizQuestion(id = "3", question = "What is the correct HTML element for inserting a line break?", options = listOf("<break>", "<lb>", "<br>", "<newline>"), correctAnswer = 2),
                QuizQuestion(id = "4", question = "Which HTML attribute specifies an alternate text for an image?", options = listOf("alt", "title", "src", "href"), correctAnswer = 0),
                QuizQuestion(id = "5", question = "What does CSS stand for?", options = listOf("Computer Style Sheets", "Creative Style Sheets", "Cascading Style Sheets", "Colorful Style Sheets"), correctAnswer = 2)
            )
        )
    }
    
    val currentQuestion = if (questions.isNotEmpty()) questions[currentQuestionIndex] else null

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
                        colors = listOf(DarkPrimary, DarkPrimaryContainer)
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (quizStarted) "Quiz: $selectedTopic" else "Quiz",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = DarkOnPrimary
                    )
                    
                    if (quizStarted) {
                        Text(
                            text = "${currentQuestionIndex + 1}/${questions.size}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = DarkOnPrimary
                        )
                    }
                }
                
                if (quizStarted) {
                    Spacer(modifier = Modifier.height(16.dp))
                    ProgressBar(
                        progress = (currentQuestionIndex + 1).toFloat() / questions.size.coerceAtLeast(1),
                        color = DarkOnPrimary
                    )
                }
            }
        }

        // Topic picker before quiz starts
        if (!quizStarted) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Text(
                    text = "Choose a Topic",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = DarkOnBackground
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                availableTopics.forEach { topicName ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedTopic == topicName) DarkPrimary else DarkCardBackground
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedTopic = topicName }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (selectedTopic == topicName) DarkOnPrimary else DarkOnSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = topicName,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                                color = if (selectedTopic == topicName) DarkOnPrimary else DarkOnSurface
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                if (isGenerating) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = DarkPrimary)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Generating quiz with AI...", color = DarkOnSurfaceVariant)
                        }
                    }
                } else {
                    PrimaryButton(
                        text = "Start Quiz",
                        onClick = {
                            if (selectedTopic.isNotBlank()) {
                                quizStarted = true
                                currentQuestionIndex = 0
                                selectedAnswer = null
                                showResult = false
                                score = 0
                            }
                        },
                        enabled = selectedTopic.isNotBlank(),
                        icon = Icons.Default.PlayArrow
                    )
                }
            }
            return@Column
        }
        
        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Question
            AnimatedContent(
                targetState = currentQuestionIndex,
                transitionSpec = {
                    slideInHorizontally(
                        animationSpec = tween(300),
                        initialOffsetX = { if (targetState > initialState) it else -it }
                    ) + fadeIn(animationSpec = tween(300)) with slideOutHorizontally(
                        animationSpec = tween(300),
                        targetOffsetX = { if (targetState > initialState) -it else it }
                    ) + fadeOut(animationSpec = tween(300))
                }
            ) { index ->
                Column {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = DarkCardBackground
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Text(
                                text = "Question ${index + 1}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = DarkPrimary
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = currentQuestion?.question ?: "",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = DarkOnSurface,
                                textAlign = TextAlign.Start
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Options
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        (currentQuestion?.options ?: emptyList()).forEachIndexed { index, option ->
                            OptionCard(
                                text = option,
                                isSelected = selectedAnswer == index,
                                isCorrect = if (showResult) index == currentQuestion?.correctAnswer else null,
                                onClick = {
                                    if (!showResult) {
                                        selectedAnswer = index
                                    }
                                }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Navigation Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SecondaryButton(
                            text = "Previous",
                            onClick = {
                                if (currentQuestionIndex > 0) {
                                    currentQuestionIndex--
                                    selectedAnswer = null
                                    showResult = false
                                }
                            },
                            enabled = currentQuestionIndex > 0,
                            icon = Icons.Default.ArrowBack,
                            modifier = Modifier.weight(1f)
                        )
                        
                        PrimaryButton(
                            text = if (currentQuestionIndex == questions.size - 1) "Finish" else "Next",
                            onClick = {
                                if (selectedAnswer != null) {
                                    if (selectedAnswer == currentQuestion?.correctAnswer) {
                                        score++
                                    }
                                    
                                    if (currentQuestionIndex == questions.size - 1) {
                                        showResult = true
                                        courseViewModel?.addQuizResult(
                                            QuizResult(
                                                id = System.currentTimeMillis().toString(),
                                                quizTitle = selectedTopic.ifBlank { "General" },
                                                score = score,
                                                totalQuestions = questions.size,
                                                date = System.currentTimeMillis(),
                                                timeTaken = 0L
                                            )
                                        )
                                    } else {
                                        currentQuestionIndex++
                                        selectedAnswer = null
                                        showResult = false
                                    }
                                }
                            },
                            enabled = selectedAnswer != null,
                            icon = if (currentQuestionIndex == questions.size - 1) Icons.Default.Check else Icons.Default.ArrowForward,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            
            // Result Card
            AnimatedVisibility(
                visible = showResult && currentQuestionIndex == questions.size - 1,
                enter = fadeIn(animationSpec = tween(500)) + slideInVertically(
                    animationSpec = tween(500),
                    initialOffsetY = { it }
                )
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (score >= questions.size * 0.7) DarkSuccess else DarkError
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = if (score >= questions.size * 0.7) Icons.Default.CheckCircle else Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = DarkOnPrimary
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = if (score >= questions.size * 0.7) "Congratulations!" else "Keep Learning!",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = DarkOnPrimary
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "You scored $score out of ${questions.size}",
                            style = MaterialTheme.typography.titleMedium,
                            color = DarkOnPrimary
                        )

                        if (courseViewModel != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            val xpEarned = if (score == questions.size) "+100 XP" else if (score >= questions.size * 0.7) "+50 XP" else "+0 XP"
                            Text(text = xpEarned, style = MaterialTheme.typography.bodyMedium, color = DarkOnPrimary.copy(alpha = 0.8f))
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            SecondaryButton(
                                text = "Try Again",
                                onClick = {
                                    currentQuestionIndex = 0
                                    selectedAnswer = null
                                    showResult = false
                                    score = 0
                                },
                                modifier = Modifier.weight(1f)
                            )
                            PrimaryButton(
                                text = "New Topic",
                                onClick = {
                                    quizStarted = false
                                    selectedTopic = ""
                                    currentQuestionIndex = 0
                                    selectedAnswer = null
                                    showResult = false
                                    score = 0
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OptionCard(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean?,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isCorrect == true -> DarkSuccess
        isCorrect == false -> DarkError
        isSelected -> DarkPrimary
        else -> DarkCardBackground
    }
    
    val textColor = when {
        isCorrect != null -> DarkOnPrimary
        isSelected -> DarkOnPrimary
        else -> DarkOnSurface
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = if (isCorrect != null) DarkOnPrimary else DarkPrimary,
                    unselectedColor = DarkOnSurfaceVariant
                )
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                ),
                color = textColor,
                modifier = Modifier.weight(1f)
            )
            
            if (isCorrect != null) {
                Icon(
                    imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = null,
                    tint = DarkOnPrimary
                )
            }
        }
    }
} 