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
                            
                            // Upload Area
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .border(
                                        width = 2.dp,
                                        color = if (uploadedFile != null) DarkSuccess else DarkOutline,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (uploadedFile != null) DarkSuccess.copy(alpha = 0.1f)
                                        else DarkSurfaceVariant.copy(alpha = 0.3f)
                                    )
                                    .clickable {
                                        if (!isUploading) {
                                            uploadedFile = "resume.pdf"
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (uploadedFile != null) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = DarkSuccess
                                        )
                                        
                                        Spacer(modifier = Modifier.height(8.dp))
                                        
                                        Text(
                                            text = uploadedFile!!,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = DarkSuccess
                                        )
                                    }
                                } else {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            tint = DarkOnSurfaceVariant
                                        )
                                        
                                        Spacer(modifier = Modifier.height(8.dp))
                                        
                                        Text(
                                            text = "Tap to select file",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = DarkOnSurfaceVariant
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            PrimaryButton(
                                text = if (isUploading) "Analyzing..." else "Get Feedback",
                                onClick = {
                                    if (uploadedFile != null && !isUploading) {
                                        isUploading = true
                                        coroutineScope.launch {
                                            delay(3000) // Simulate analysis
                                            feedback = generateResumeFeedback()
                                            isUploading = false
                                            showFeedback = true
                                        }
                                    }
                                },
                                enabled = uploadedFile != null && !isUploading,
                                icon = if (isUploading) null else Icons.Default.CheckCircle
                            )
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
                                text = "Upload New",
                                onClick = {
                                    showFeedback = false
                                    uploadedFile = null
                                    feedback = null
                                },
                                icon = Icons.Default.Add,
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

private fun generateResumeFeedback(): ResumeReview {
    return ResumeReview(
        id = "1",
        fileName = "resume.pdf",
        score = 78,
        feedback = """
            Your resume shows good structure and relevant experience. Here are the key areas for improvement:
            
            ✅ Strengths:
            • Clear professional summary
            • Good use of action verbs
            • Relevant technical skills listed
            
            🔧 Areas to Improve:
            • Add more quantifiable achievements
            • Include specific project outcomes
            • Consider adding a skills section with proficiency levels
            • Ensure consistent formatting throughout
            
            💡 Recommendations:
            • Use bullet points for better readability
            • Include metrics where possible (e.g., "Increased efficiency by 25%")
            • Add relevant certifications
            • Consider adding a portfolio link
        """.trimIndent(),
        date = System.currentTimeMillis()
    )
} 