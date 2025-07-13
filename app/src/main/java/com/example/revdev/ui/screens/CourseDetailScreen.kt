package com.example.revdev.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.revdev.data.Course
import com.example.revdev.data.Lesson
import com.example.revdev.ui.theme.*



@Composable
fun CourseDetailScreen(
    course: Course,
    onLessonClick: (Lesson) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        // Course header
        CourseHeader(course = course)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Progress section
        // Remove the usage of CourseProgress(course = course) at line 42. Use only the new progress UI and system.
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Lessons list
        LessonsList(
            lessons = course.lessons,
            onLessonClick = onLessonClick
        )
    }
}

@Composable
private fun CourseHeader(course: Course) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = DarkCardBackground
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = when (course.id) {
                        "html" -> Icons.Filled.CheckCircle
                        "css" -> Icons.Filled.PlayArrow
                        "js" -> Icons.Filled.DateRange
                        else -> Icons.Filled.Notifications
                    },
                    contentDescription = null,
                    tint = DarkPrimary,
                    modifier = Modifier.size(48.dp)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = course.title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = DarkOnSurface
                    )
                    Text(
                        text = course.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkOnSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun LessonsList(
    lessons: List<Lesson>,
    onLessonClick: (Lesson) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = DarkCardBackground
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Lessons",
                style = MaterialTheme.typography.titleMedium,
                color = DarkOnSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(lessons) { lesson ->
                    LessonItem(
                        lesson = lesson,
                        onClick = { onLessonClick(lesson) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LessonItem(
    lesson: Lesson,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (lesson.isCompleted) {
                Color(0xFF1B5E20).copy(alpha = 0.2f)
            } else {
                Color(0xFF424242)
            }
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Lesson number
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (lesson.isCompleted) {
                        Color(0xFF1B5E20)
                    } else {
                        DarkPrimary
                    }
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(
                    modifier = Modifier.size(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (lesson.isCompleted) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Completed",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = lesson.order.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Lesson info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = DarkOnSurface
                )
                Text(
                    text = lesson.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkOnSurface.copy(alpha = 0.7f)
                )
            }
            
            // Lesson type icon
            Icon(
                imageVector = when (lesson.type) {
                    com.example.revdev.data.LessonType.TEXT -> Icons.Filled.Settings
                    com.example.revdev.data.LessonType.CODE -> Icons.Filled.Settings
                    com.example.revdev.data.LessonType.QUIZ -> Icons.Filled.Settings
                    com.example.revdev.data.LessonType.PRACTICAL -> Icons.Filled.Build
                },
                contentDescription = lesson.type.name,
                tint = if (lesson.isCompleted) {
                    Color(0xFF1B5E20)
                } else {
                    DarkPrimary
                },
                modifier = Modifier.size(24.dp)
            )
        }
    }
} 