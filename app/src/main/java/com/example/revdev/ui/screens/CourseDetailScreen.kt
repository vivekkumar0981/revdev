package com.example.revdev.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.revdev.ui.theme.DarkBackground
import com.example.revdev.ui.theme.DarkOnSurface

@Composable
fun CourseDetailScreen(courseId: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Course: $courseId",
                style = MaterialTheme.typography.headlineMedium,
                color = DarkOnSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Course details and lessons will appear here.",
                style = MaterialTheme.typography.bodyLarge,
                color = DarkOnSurface
            )
        }
    }
} 