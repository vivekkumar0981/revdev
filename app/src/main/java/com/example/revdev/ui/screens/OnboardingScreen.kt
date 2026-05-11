package com.example.revdev.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.revdev.data.SkillLevel
import com.example.revdev.ui.components.PrimaryButton
import com.example.revdev.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onComplete: (SkillLevel) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedLevel by remember { mutableStateOf<SkillLevel?>(null) }
    var currentPage by remember { mutableStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(colors = listOf(DarkPrimary, DarkPrimaryContainer))
                )
                .padding(32.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = DarkOnPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Welcome to RevDev",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = DarkOnPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Let's personalize your learning journey",
                    style = MaterialTheme.typography.bodyLarge,
                    color = DarkOnPrimary.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "What's your experience level?",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = DarkOnBackground
                )
                Spacer(modifier = Modifier.height(24.dp))

                SkillLevelCard(
                    level = SkillLevel.BEGINNER,
                    title = "Beginner",
                    description = "New to programming. Want to learn from scratch.",
                    icon = Icons.Default.Face,
                    isSelected = selectedLevel == SkillLevel.BEGINNER,
                    onClick = { selectedLevel = SkillLevel.BEGINNER }
                )

                Spacer(modifier = Modifier.height(12.dp))

                SkillLevelCard(
                    level = SkillLevel.INTERMEDIATE,
                    title = "Intermediate",
                    description = "Know basics of HTML/CSS. Want to go deeper.",
                    icon = Icons.Default.Build,
                    isSelected = selectedLevel == SkillLevel.INTERMEDIATE,
                    onClick = { selectedLevel = SkillLevel.INTERMEDIATE }
                )

                Spacer(modifier = Modifier.height(12.dp))

                SkillLevelCard(
                    level = SkillLevel.ADVANCED,
                    title = "Advanced",
                    description = "Experienced dev. Looking for challenges and interview prep.",
                    icon = Icons.Default.Star,
                    isSelected = selectedLevel == SkillLevel.ADVANCED,
                    onClick = { selectedLevel = SkillLevel.ADVANCED }
                )
            }

            PrimaryButton(
                text = "Get Started",
                onClick = { selectedLevel?.let { onComplete(it) } },
                enabled = selectedLevel != null,
                icon = Icons.Default.ArrowForward
            )
        }
    }
}

@Composable
private fun SkillLevelCard(
    level: SkillLevel,
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) DarkPrimary else DarkCardBackground
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(if (isSelected) 8.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (isSelected) DarkOnPrimary.copy(alpha = 0.2f) else DarkPrimaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp),
                tint = if (isSelected) DarkOnPrimary else DarkOnPrimaryContainer
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = if (isSelected) DarkOnPrimary else DarkOnSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected) DarkOnPrimary.copy(alpha = 0.8f) else DarkOnSurfaceVariant
                )
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = DarkOnPrimary
                )
            }
        }
    }
}
