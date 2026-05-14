package com.example.revdev.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.revdev.ui.components.*
import com.example.revdev.ui.theme.*
import com.example.revdev.data.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    courseViewModel: com.example.revdev.data.CourseViewModel? = null,
    onNavigateToQuiz: () -> Unit,
    onNavigateToAITutor: () -> Unit,
    onNavigateToResumeReview: () -> Unit,
    onNavigateToCourseSelection: () -> Unit,
    onNavigateToBugHunt: () -> Unit = {},
    onNavigateToVoiceLearning: () -> Unit = {},
    onNavigateToSkillTree: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val userName = authViewModel.currentUser?.displayName ?: "User"
    val progress = courseViewModel?.userProgress?.collectAsState()?.value
    val completedLessons = courseViewModel?.totalCompletedLessons ?: 0
    val quizzesTaken = courseViewModel?.totalQuizzesTaken ?: 0
    val avgScore = courseViewModel?.averageQuizScore ?: 0
    val overallProgress = courseViewModel?.overallProgress ?: 0f
    val xp = progress?.xp ?: 0
    val level = progress?.level ?: 1
    val streak = progress?.streak ?: 0
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(600)) + slideInVertically(
                animationSpec = tween(600),
                initialOffsetY = { -it }
            )
        ) {
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
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Hi $userName 👋",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = DarkOnPrimary
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text(
                                text = "Ready to learn something new?",
                                style = MaterialTheme.typography.bodyLarge,
                                color = DarkOnPrimary.copy(alpha = 0.8f)
                            )
                        }
                        
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
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
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Progress Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = DarkOnPrimary.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                                Text(
                                    text = "Level $level · ${streak}🔥 streak",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = DarkOnPrimary
                                )
                                
                                Text(
                                    text = "${(overallProgress * 100).toInt()}%",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = DarkOnPrimary
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            ProgressBar(
                                progress = overallProgress,
                                color = DarkOnPrimary
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "${xp} XP · $completedLessons lessons completed",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkOnPrimary.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
        
        // Content
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "What would you like to do?",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = DarkOnBackground
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Feature Cards
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(800)) + slideInVertically(
                    animationSpec = tween(800),
                    initialOffsetY = { it }
                )
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FeatureCard(
                        title = "Start Learning",
                        description = "Continue your HTML/CSS course",
                        icon = Icons.Default.Home,
                        onClick = onNavigateToCourseSelection
                    )
                    
                    FeatureCard(
                        title = "Practice Quiz",
                        description = "Test your knowledge with interactive quizzes",
                        icon = Icons.Default.CheckCircle,
                        onClick = onNavigateToQuiz
                    )
                    
                    FeatureCard(
                        title = "Ask AI Tutor",
                        description = "Get help from our intelligent tutor",
                        icon = Icons.Default.Email,
                        onClick = onNavigateToAITutor
                    )
                    
                    FeatureCard(
                        title = "Resume Review",
                        description = "Get feedback on your resume",
                        icon = Icons.Default.Add,
                        onClick = onNavigateToResumeReview
                    )

                    FeatureCard(
                        title = "Bug Hunt",
                        description = "Find & fix bugs in broken code",
                        icon = Icons.Default.Build,
                        onClick = onNavigateToBugHunt
                    )

                    FeatureCard(
                        title = "Voice Learning",
                        description = "Learn hands-free with voice",
                        icon = Icons.Default.Mic,
                        onClick = onNavigateToVoiceLearning
                    )

                    FeatureCard(
                        title = "Skill Tree",
                        description = "RPG-style learning path",
                        icon = Icons.Default.Star,
                        onClick = onNavigateToSkillTree
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Quick Stats
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(1000)) + slideInVertically(
                    animationSpec = tween(1000),
                    initialOffsetY = { it }
                )
            ) {
                Column {
                    Text(
                        text = "Your Stats",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = DarkOnBackground
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StatCard(
                            title = "Lessons",
                            value = "$completedLessons",
                            subtitle = "Completed",
                            modifier = Modifier.weight(1f)
                        )
                        
                        StatCard(
                            title = "Quizzes",
                            value = "$quizzesTaken",
                            subtitle = "Taken",
                            modifier = Modifier.weight(1f)
                        )
                        
                        StatCard(
                            title = "Score",
                            value = "${avgScore}%",
                            subtitle = "Average",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(100.dp)) // Bottom padding for navigation
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
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
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = DarkPrimary
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = DarkOnSurface
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = DarkOnSurfaceVariant
            )
        }
    }
} 