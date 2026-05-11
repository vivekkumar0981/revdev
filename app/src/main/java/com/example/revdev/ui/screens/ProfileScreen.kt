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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.revdev.data.Course
import com.example.revdev.data.CourseViewModel
import com.example.revdev.data.QuizResult
import com.example.revdev.data.User
import com.example.revdev.data.UserProgress
import com.example.revdev.data.XPRewards
import com.example.revdev.ui.components.*
import com.example.revdev.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import com.example.revdev.data.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel? = null,
    courseViewModel: CourseViewModel? = null
) {
    val firebaseUser = authViewModel?.currentUser
    val userName = firebaseUser?.displayName ?: "User"
    val userEmail = firebaseUser?.email ?: ""
    val progress = courseViewModel?.userProgress?.collectAsState()?.value
    val courses = courseViewModel?.courses?.collectAsState()?.value ?: emptyList()
    
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
                    ),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
                .padding(0.dp)
        ) {
            // Soft fade overlay for depth
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                DarkPrimary.copy(alpha = 0.15f),
                                DarkPrimaryContainer.copy(alpha = 0.05f)
                            )
                        )
                    )
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 36.dp, bottom = 28.dp)
            ) {
                // Profile Picture with gradient border and shadow
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(DarkPrimary, DarkPrimaryContainer, DarkOnPrimary.copy(alpha = 0.1f)),
                                radius = 80f
                            ),
                            shape = CircleShape
                        )
                        .padding(6.dp)
                        .shadow(10.dp, CircleShape, clip = false)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(98.dp)
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
                            modifier = Modifier.size(54.dp),
                            tint = DarkOnPrimary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = userName,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = DarkOnPrimary
                )
                Text(
                    text = userEmail,
                    style = MaterialTheme.typography.bodyLarge,
                    color = DarkOnPrimary.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(28.dp))
                // Stats Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.85f),
                    colors = CardDefaults.cardColors(
                        containerColor = DarkOnPrimary.copy(alpha = 0.10f)
                    ),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 22.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem("Level", "${progress?.level ?: 1}")
                        StatItem("XP", "${progress?.xp ?: 0}")
                        StatItem("Streak", "${progress?.streak ?: 0}🔥")
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Divider(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(top = 2.dp),
                    color = DarkOnPrimary.copy(alpha = 0.15f),
                    thickness = 1.dp
                )
            }
        }
        
        // Content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                val xpProgress = XPRewards.xpProgressInLevel(progress?.xp ?: 0)
                val nextLevelXP = XPRewards.xpForNextLevel(progress?.level ?: 1)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkCardBackground),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("XP Progress", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = DarkOnSurface)
                        Spacer(modifier = Modifier.height(8.dp))
                        ProgressBar(progress = xpProgress, color = DarkPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${progress?.xp ?: 0} / $nextLevelXP XP to next level", style = MaterialTheme.typography.bodySmall, color = DarkOnSurfaceVariant)
                    }
                }
            }

            item {
                Text(
                    text = "Quiz History",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = DarkOnBackground
                )
            }

            val quizResults = progress?.quizResults ?: emptyList()
            if (quizResults.isEmpty()) {
                item {
                    Text("No quizzes taken yet", style = MaterialTheme.typography.bodyMedium, color = DarkOnSurfaceVariant)
                }
            } else {
                items(quizResults.takeLast(10).reversed()) { quiz ->
                    QuizHistoryCard(quiz = quiz)
                }
            }
            
            item {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
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
    var darkMode by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (darkMode) Icons.Default.Star else Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .background(color = DarkPrimaryContainer, shape = RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        tint = DarkOnPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Dark Mode", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = DarkOnSurface)
                        Text("Toggle app theme", style = MaterialTheme.typography.bodyMedium, color = DarkOnSurfaceVariant)
                    }
                }
                Switch(checked = darkMode, onCheckedChange = { darkMode = it })
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp), color = DarkOutline)

            SettingsRow(icon = Icons.Default.Notifications, title = "Notifications", subtitle = "Manage notification preferences")
            Divider(modifier = Modifier.padding(vertical = 12.dp), color = DarkOutline)
            SettingsRow(icon = Icons.Default.Settings, title = "Privacy", subtitle = "Control privacy settings")
            Divider(modifier = Modifier.padding(vertical = 12.dp), color = DarkOutline)
            SettingsRow(icon = Icons.Default.Home, title = "Help & Support", subtitle = "Get help and contact support")
            Divider(modifier = Modifier.padding(vertical = 12.dp), color = DarkOutline)
            SettingsRow(
                icon = Icons.Default.ExitToApp,
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