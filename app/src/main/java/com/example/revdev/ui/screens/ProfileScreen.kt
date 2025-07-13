package com.example.revdev.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.revdev.data.Course
import com.example.revdev.data.QuizResult
import com.example.revdev.data.User
import com.example.revdev.ui.components.*
import com.example.revdev.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import com.example.revdev.data.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(modifier: Modifier = Modifier, authViewModel: AuthViewModel? = null) {
    val user = remember {
        User(
            id = "1",
            name = "Vivek Kumar",
            email = "vivek@example.com",
            quizHistory = listOf(
                QuizResult("1", "HTML Basics", 8, 10, System.currentTimeMillis() - 86400000, 1200000),
                QuizResult("2", "CSS Fundamentals", 7, 10, System.currentTimeMillis() - 172800000, 900000),
                QuizResult("3", "JavaScript Intro", 6, 10, System.currentTimeMillis() - 259200000, 1500000)
            )
        )
    }
    
    val courses = remember {
        listOf(
            Course("html_css", "HTML & CSS", "Learn the fundamentals of web development", 75, 20, 15),
            Course("javascript", "JavaScript", "Master JavaScript programming", 30, 15, 5)
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Picture
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(
                            color = DarkOnPrimary.copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = DarkOnPrimary
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = DarkOnPrimary
                )
                
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkOnPrimary.copy(alpha = 0.8f)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Quick Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem("Courses", courses.size.toString())
                    StatItem("Quizzes", user.quizHistory.size.toString())
                    StatItem("Avg Score", "${user.quizHistory.map { it.score * 10 / it.totalQuestions }.average().toInt()}%")
                }
            }
        }
        
        // Content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Course Progress Section
            item {
                Text(
                    text = "Course Progress",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = DarkOnBackground
                )
            }
            
            // items(courses) { course ->
            //     CourseProgressCard(course = course)
            // }
            
            // Quiz History Section
            item {
                Text(
                    text = "Quiz History",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = DarkOnBackground
                )
            }
            
            items(user.quizHistory) { quiz ->
                QuizHistoryCard(quiz = quiz)
            }
            
            // Settings Section
            item {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = DarkOnBackground
                )
            }
            
            item {
                SettingsCard(onSignOut = { authViewModel?.logout() })
            }
            
            // Bottom padding
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = DarkOnPrimary
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = DarkOnPrimary.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun QuizHistoryCard(quiz: QuizResult) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    val score = (quiz.score * 100) / quiz.totalQuestions
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = DarkCardBackground
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = when {
                            score >= 80 -> DarkSuccess
                            score >= 60 -> DarkWarning
                            else -> DarkError
                        }.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp),
                tint = when {
                    score >= 80 -> DarkSuccess
                    score >= 60 -> DarkWarning
                    else -> DarkError
                }
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = quiz.quizTitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = DarkOnSurface
                )
                
                Text(
                    text = dateFormat.format(Date(quiz.date)),
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkOnSurfaceVariant
                )
                
                Text(
                    text = "Time: ${quiz.timeTaken / 60000} min",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkOnSurfaceVariant
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "$score%",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = when {
                        score >= 80 -> DarkSuccess
                        score >= 60 -> DarkWarning
                        else -> DarkError
                    }
                )
                
                Text(
                    text = "${quiz.score}/${quiz.totalQuestions}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkOnSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SettingsCard(onSignOut: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = DarkCardBackground
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            SettingsRow(
                icon = Icons.Default.Notifications,
                title = "Notifications",
                subtitle = "Manage your notification preferences"
            )
            
            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = DarkOutline
            )
            
            SettingsRow(
                icon = Icons.Default.Settings,
                title = "Privacy",
                subtitle = "Control your privacy settings"
            )
            
            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = DarkOutline
            )
            
            SettingsRow(
                icon = Icons.Default.Home,
                title = "Help & Support",
                subtitle = "Get help and contact support"
            )
            
            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = DarkOutline
            )
            
            SettingsRow(
                icon = Icons.Default.Settings,
                title = "Sign Out",
                subtitle = "Sign out of your account",
                isDestructive = true,
                onClick = onSignOut
            )
        }
    }
}

@Composable
private fun SettingsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    isDestructive: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick?.invoke() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = if (isDestructive) DarkError.copy(alpha = 0.2f) else DarkPrimaryContainer,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp),
            tint = if (isDestructive) DarkError else DarkOnPrimaryContainer
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = if (isDestructive) DarkError else DarkOnSurface
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = DarkOnSurfaceVariant
            )
        }
        
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null,
            tint = DarkOnSurfaceVariant
        )
    }
} 