package com.example.revdev.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.revdev.data.Lesson
import com.example.revdev.ui.theme.*

@Composable
fun LessonViewerScreen(
    lesson: Lesson,
    onNextLesson: () -> Unit,
    onPreviousLesson: () -> Unit,
    onMarkCompleted: () -> Unit,
    hasNextLesson: Boolean,
    hasPreviousLesson: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Header
        LessonHeader(
            lesson = lesson,
            onNextLesson = onNextLesson,
            onPreviousLesson = onPreviousLesson,
            hasNextLesson = hasNextLesson,
            hasPreviousLesson = hasPreviousLesson
        )
        
        // Content
        LessonContent(
            lesson = lesson,
            onMarkCompleted = onMarkCompleted,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun LessonHeader(
    lesson: Lesson,
    onNextLesson: () -> Unit,
    onPreviousLesson: () -> Unit,
    hasNextLesson: Boolean,
    hasPreviousLesson: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkCardBackground
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = lesson.title,
                style = MaterialTheme.typography.headlineSmall,
                color = DarkOnSurface,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = lesson.description,
                style = MaterialTheme.typography.bodyMedium,
                color = DarkOnSurface.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onPreviousLesson,
                    enabled = hasPreviousLesson,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (hasPreviousLesson) DarkPrimary else DarkPrimary.copy(alpha = 0.5f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Previous lesson"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Previous")
                }
                
                Button(
                    onClick = onNextLesson,
                    enabled = hasNextLesson,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (hasNextLesson) DarkPrimary else DarkPrimary.copy(alpha = 0.5f)
                    )
                ) {
                    Text("Next")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Next lesson"
                    )
                }
            }
        }
    }
}

@Composable
private fun LessonContent(
    lesson: Lesson,
    onMarkCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Lesson content
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = DarkCardBackground
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Lesson Content",
                    style = MaterialTheme.typography.titleMedium,
                    color = DarkPrimary,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Main content
                Text(
                    text = lesson.content,
                    style = MaterialTheme.typography.bodyLarge,
                    color = DarkOnSurface,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2
                )
                
                // Code example if available
                lesson.codeExample?.let { code ->
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "Code Example:",
                        style = MaterialTheme.typography.titleSmall,
                        color = DarkPrimary,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1E1E1E)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = code,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Monospace
                            ),
                            color = Color(0xFFE0E0E0),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Completion section
        if (!lesson.isCompleted) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = DarkCardBackground
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Mark as Completed",
                        style = MaterialTheme.typography.titleMedium,
                        color = DarkOnSurface,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Button(
                        onClick = onMarkCompleted,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Mark completed"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Complete Lesson")
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1B5E20)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Lesson Completed!",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
} 