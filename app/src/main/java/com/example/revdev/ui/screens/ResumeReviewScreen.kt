package com.example.revdev.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.revdev.data.ResumeReview
import com.example.revdev.data.OpenAIApi
import com.example.revdev.ui.components.*
import com.example.revdev.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumeReviewScreen(modifier: Modifier = Modifier) {
    var isUploading by remember { mutableStateOf(false) }
    var uploadedFile by remember { mutableStateOf<String?>(null) }
    var showFeedback by remember { mutableStateOf(false) }
    var feedback by remember { mutableStateOf<ResumeReview?>(null) }
    var resumeText by remember { mutableStateOf("") }
    var showTextInput by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    
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
                        text = "Resume Review",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = DarkOnPrimary
                    )
                    
                    Text(
                        text = "Get AI-powered feedback on your resume",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkOnPrimary.copy(alpha = 0.8f)
                    )
                }
                
                Icon(
                    imageVector = Icons.Default.Add,
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
        
        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Upload Section
            AnimatedVisibility(
                visible = !showFeedback,
                enter = fadeIn(animationSpec = tween(600)) + slideInVertically(
                    animationSpec = tween(600),
                    initialOffsetY = { it }
                ),
                exit = fadeOut(animationSpec = tween(300)) + slideOutVertically(
                    animationSpec = tween(300),
                    targetOffsetY = { -it }
                )
            ) {
                Column {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = DarkCardBackground
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = DarkPrimary
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = "Upload Your Resume",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = DarkOnSurface
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "Supported formats: PDF, DOCX (Max 5MB)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkOnSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            if (!showTextInput) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp)
                                        .border(
                                            width = 2.dp,
                                            color = DarkOutline,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(DarkSurfaceVariant.copy(alpha = 0.3f))
                                        .clickable { showTextInput = true },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = DarkOnSurfaceVariant)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(text = "Tap to paste your resume text", style = MaterialTheme.typography.bodyMedium, color = DarkOnSurfaceVariant)
                                        Text(text = "(or paste key sections)", style = MaterialTheme.typography.bodySmall, color = DarkOnSurfaceVariant.copy(alpha = 0.6f))
                                    }
                                }
                            } else {
                                OutlinedTextField(
                                    value = resumeText,
                                    onValueChange = { resumeText = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    placeholder = { Text("Paste your resume content here...\n\nInclude: Summary, Experience, Skills, Education", color = DarkOnSurfaceVariant) },
                                    shape = RoundedCornerShape(12.dp),
                                    maxLines = 20
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))

                            if (isUploading) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator(color = DarkPrimary)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("AI analyzing your resume...", color = DarkOnSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                                }
                            } else {
                                PrimaryButton(
                                    text = "Get AI Feedback",
                                    onClick = {
                                        if (resumeText.isNotBlank() && !isUploading) {
                                            isUploading = true
                                            coroutineScope.launch {
                                                try {
                                                    val aiResponse = OpenAIApi.reviewResume(resumeText)
                                                    feedback = ResumeReview(
                                                        id = System.currentTimeMillis().toString(),
                                                        fileName = "Pasted Resume",
                                                        feedback = aiResponse,
                                                        score = extractScoreFromResponse(aiResponse),
                                                        date = System.currentTimeMillis()
                                                    )
                                                    showFeedback = true
                                                } catch (e: Exception) {
                                                    feedback = ResumeReview(
                                                        id = "error",
                                                        fileName = "Error",
                                                        feedback = "Could not analyze resume: ${e.message}",
                                                        score = 0,
                                                        date = System.currentTimeMillis()
                                                    )
                                                    showFeedback = true
                                                } finally {
                                                    isUploading = false
                                                }
                                            }
                                        }
                                    },
                                    enabled = resumeText.length > 50,
                                    icon = Icons.Default.CheckCircle
                                )
                                if (resumeText.isNotBlank() && resumeText.length <= 50) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Need at least 50 characters for meaningful review", color = DarkOnSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Features
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FeatureRow(
                            icon = Icons.Default.CheckCircle,
                            title = "Grammar & Spelling",
                            description = "Check for errors and typos"
                        )
                        
                        FeatureRow(
                            icon = Icons.Default.List,
                            title = "Structure Analysis",
                            description = "Review layout and organization"
                        )
                        
                        FeatureRow(
                            icon = Icons.Default.CheckCircle,
                            title = "ATS Optimization",
                            description = "Improve keyword matching"
                        )
                        
                        FeatureRow(
                            icon = Icons.Default.Info,
                            title = "Content Suggestions",
                            description = "Get improvement recommendations"
                        )
                    }
                }
            }
            
            // Feedback Section
            AnimatedVisibility(
                visible = showFeedback && feedback != null,
                enter = fadeIn(animationSpec = tween(600)) + slideInVertically(
                    animationSpec = tween(600),
                    initialOffsetY = { it }
                )
            ) {
                feedback?.let { review ->
                    Column {
                        // Score Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = DarkCardBackground
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Resume Score",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = DarkOnSurfaceVariant
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = "${review.score}/100",
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = when {
                                        review.score >= 80 -> DarkSuccess
                                        review.score >= 60 -> DarkWarning
                                        else -> DarkError
                                    }
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                ProgressBar(
                                    progress = review.score / 100f,
                                    color = when {
                                        review.score >= 80 -> DarkSuccess
                                        review.score >= 60 -> DarkWarning
                                        else -> DarkError
                                    }
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Feedback Content
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
                                    text = "Detailed Feedback",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = DarkOnSurface
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Text(
                                    text = review.feedback,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = DarkOnSurface
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            SecondaryButton(
                                text = "Review Another",
                                onClick = {
                                    showFeedback = false
                                    uploadedFile = null
                                    feedback = null
                                    resumeText = ""
                                    showTextInput = false
                                },
                                icon = Icons.Default.Refresh,
                                modifier = Modifier.weight(1f)
                            )
                            
                            PrimaryButton(
                                text = "Download Report",
                                onClick = { /* Download functionality */ },
                                icon = Icons.Default.Add,
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
private fun FeatureRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = DarkPrimaryContainer,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp),
            tint = DarkOnPrimaryContainer
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
                color = DarkOnSurface
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = DarkOnSurfaceVariant
            )
        }
    }
}

private fun extractScoreFromResponse(response: String): Int {
    val scoreRegex = Regex("""(\d{1,3})\s*/\s*100|score[:\s]*(\d{1,3})""", RegexOption.IGNORE_CASE)
    val match = scoreRegex.find(response)
    val score = match?.groupValues?.drop(1)?.firstOrNull { it.isNotBlank() }?.toIntOrNull()
    return (score ?: 65).coerceIn(0, 100)
} 