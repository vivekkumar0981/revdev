package com.example.revdev.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.Settings

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.revdev.ui.theme.DarkBackground
import com.example.revdev.ui.theme.DarkCardBackground
import com.example.revdev.ui.theme.DarkOnSurface
import com.example.revdev.ui.theme.DarkPrimary
import com.example.revdev.data.Course

@Composable
fun CourseSelectionScreen(
    courses: List<Course>,
    onCourseClick: (Course) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Choose a course to start learning",
            style = MaterialTheme.typography.titleLarge,
            color = DarkOnSurface
        )
        courses.forEach { course ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCourseClick(course) },
                colors = CardDefaults.cardColors(
                    containerColor = DarkCardBackground
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (course.id) {
                            "html" -> Icons.Filled.Settings
                            "css" -> Icons.Filled.Settings
                            "js" -> Icons.Filled.Settings
                            else -> Icons.Filled.Settings
                        },
                        contentDescription = null,
                        tint = DarkPrimary,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = course.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = DarkOnSurface
                        )
                        Text(
                            text = course.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkOnSurface.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${course.totalLessons} lessons • ${course.completedLessons} completed",
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkOnSurface.copy(alpha = 0.6f)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "${course.progress}%",
                            style = MaterialTheme.typography.titleMedium,
                            color = DarkPrimary
                        )
                        if (course.progress > 0) {
                            Text(
                                text = "In Progress",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF4CAF50)
                            )
                        } else {
                            Text(
                                text = "Not Started",
                                style = MaterialTheme.typography.bodySmall,
                                color = DarkOnSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }
} 