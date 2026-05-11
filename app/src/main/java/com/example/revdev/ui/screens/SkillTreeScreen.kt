package com.example.revdev.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.revdev.data.*
import com.example.revdev.ui.theme.*
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillTreeScreen(
    modifier: Modifier = Modifier,
    courseViewModel: CourseViewModel? = null,
    onNavigateToCourse: (String) -> Unit = {}
) {
    val nodes = remember { SkillTreeData.getSkillNodes() }
    val edges = remember { SkillTreeData.getEdges() }
    val completedLessons = courseViewModel?.userProgress?.collectAsState()?.value?.completedLessons ?: emptySet()

    var scale by remember { mutableStateOf(0.8f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var selectedNode by remember { mutableStateOf<SkillNode?>(null) }

    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(0.4f, 2f)
        offset += panChange
    }

    val nodePositions = remember(nodes) { calculateNodePositions(nodes) }

    val infiniteTransition = rememberInfiniteTransition()
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse)
    )

    val textMeasurer = rememberTextMeasurer()

    Column(modifier = modifier.fillMaxSize().background(DarkBackground)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color(0xFF1565C0), Color(0xFF0D47A1))))
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Skill Tree", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = DarkOnPrimary)
                    Text("Tap nodes to explore • Pinch to zoom", style = MaterialTheme.typography.bodySmall, color = DarkOnPrimary.copy(alpha = 0.7f))
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("${nodes.count { isNodeCompleted(it, completedLessons) }}/${nodes.size}", style = MaterialTheme.typography.titleMedium, color = DarkOnPrimary)
                    Text("completed", style = MaterialTheme.typography.bodySmall, color = DarkOnPrimary.copy(alpha = 0.7f))
                }
            }
        }

        // Legend
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LegendItem(Color(0xFFFF5722), "HTML")
            LegendItem(Color(0xFF2196F3), "CSS")
            LegendItem(Color(0xFFFFEB3B), "JS")
            LegendItem(Color(0xFF61DAFB), "React")
            LegendItem(Color(0xFF68A063), "Node")
            LegendItem(Color(0xFF4DB33D), "Mongo")
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .transformable(state = transformableState)
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        val adjustedTap = (tapOffset - offset) / scale
                        nodePositions.forEach { (nodeId, pos) ->
                            val dist = sqrt((adjustedTap.x - pos.x).pow(2) + (adjustedTap.y - pos.y).pow(2))
                            if (dist < 40f) {
                                selectedNode = nodes.find { it.id == nodeId }
                            }
                        }
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasOffset = offset
                val canvasScale = scale

                withTransform({
                    translate(canvasOffset.x, canvasOffset.y)
                    scale(canvasScale, canvasScale, Offset.Zero)
                }) {
                    // Draw edges
                    edges.forEach { (from, to) ->
                        val fromPos = nodePositions[from] ?: return@forEach
                        val toPos = nodePositions[to] ?: return@forEach
                        val fromNode = nodes.find { it.id == from }
                        val isCompleted = fromNode != null && isNodeCompleted(fromNode, completedLessons)

                        drawLine(
                            color = if (isCompleted) Color(0xFF4CAF50).copy(alpha = 0.8f) else Color(0xFF555555),
                            start = fromPos,
                            end = toPos,
                            strokeWidth = if (isCompleted) 3f else 2f,
                            pathEffect = if (!isCompleted) PathEffect.dashPathEffect(floatArrayOf(8f, 8f)) else null
                        )
                    }

                    // Draw nodes
                    nodes.forEach { node ->
                        val pos = nodePositions[node.id] ?: return@forEach
                        val completed = isNodeCompleted(node, completedLessons)
                        val unlocked = isNodeUnlocked(node, nodes, completedLessons)
                        val isSelected = selectedNode?.id == node.id

                        val nodeColor = getCategoryColor(node.category)
                        val alpha = when {
                            completed -> 1f
                            unlocked -> 0.8f
                            else -> 0.3f
                        }

                        // Glow for current unlocked nodes
                        if (unlocked && !completed) {
                            drawCircle(
                                color = nodeColor.copy(alpha = pulseAlpha * 0.3f),
                                radius = 44f,
                                center = pos
                            )
                        }

                        // Selection ring
                        if (isSelected) {
                            drawCircle(color = Color.White, radius = 42f, center = pos, style = Stroke(3f))
                        }

                        // Hexagon-ish shape (circle for simplicity + performance)
                        drawCircle(
                            color = nodeColor.copy(alpha = alpha),
                            radius = 36f,
                            center = pos
                        )

                        // Completed check
                        if (completed) {
                            drawCircle(color = Color(0xFF4CAF50), radius = 14f, center = Offset(pos.x + 24f, pos.y - 24f))
                        }

                        // Lock icon for locked nodes
                        if (!unlocked) {
                            drawCircle(color = Color(0xFF333333), radius = 12f, center = pos)
                        }

                        // Label below
                        val label = node.title.take(12)
                        val textResult = textMeasurer.measure(
                            AnnotatedString(label),
                            style = TextStyle(fontSize = 9.sp, color = Color.White.copy(alpha = alpha))
                        )
                        drawText(
                            textResult,
                            topLeft = Offset(pos.x - textResult.size.width / 2f, pos.y + 40f)
                        )
                    }
                }
            }

            // Node detail overlay
            selectedNode?.let { node ->
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkCardBackground),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(node.title, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = DarkOnSurface)
                                Text(node.description, style = MaterialTheme.typography.bodyMedium, color = DarkOnSurfaceVariant)
                            }
                            IconButton(onClick = { selectedNode = null }) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = DarkOnSurfaceVariant)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(color = getCategoryColor(node.category).copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp)) {
                                Text(node.category.name, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = getCategoryColor(node.category))
                            }
                            val status = when {
                                isNodeCompleted(node, completedLessons) -> "Completed"
                                isNodeUnlocked(node, nodes, completedLessons) -> "Unlocked"
                                else -> "Locked"
                            }
                            Surface(color = DarkSurfaceVariant, shape = RoundedCornerShape(8.dp)) {
                                Text(status, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = DarkOnSurface)
                            }
                        }
                        if (node.prerequisites.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Prerequisites: ${node.prerequisites.joinToString { id -> nodes.find { it.id == id }?.title ?: id }}", style = MaterialTheme.typography.bodySmall, color = DarkOnSurfaceVariant)
                        }
                        if (node.courseId != null && isNodeUnlocked(node, nodes, completedLessons)) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { onNavigateToCourse(node.courseId) },
                                colors = ButtonDefaults.buttonColors(containerColor = getCategoryColor(node.category))
                            ) {
                                Text("Start Learning", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(modifier = Modifier.size(10.dp)) { drawCircle(color) }
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = DarkOnSurfaceVariant)
    }
}

private fun calculateNodePositions(nodes: List<SkillNode>): Map<String, Offset> {
    val positions = mutableMapOf<String, Offset>()
    val categoryColumns = mapOf(
        SkillCategory.HTML to 0,
        SkillCategory.CSS to 1,
        SkillCategory.JAVASCRIPT to 2,
        SkillCategory.REACT to 3,
        SkillCategory.NODE to 3,
        SkillCategory.EXPRESS to 4,
        SkillCategory.MONGODB to 4
    )

    val categoryCounters = mutableMapOf<SkillCategory, Int>()
    val colWidth = 160f
    val rowHeight = 120f
    val startX = 120f
    val startY = 80f

    nodes.forEach { node ->
        val col = categoryColumns[node.category] ?: 0
        val row = categoryCounters.getOrDefault(node.category, 0)
        categoryCounters[node.category] = row + 1

        positions[node.id] = Offset(startX + col * colWidth, startY + row * rowHeight)
    }
    return positions
}

private fun getCategoryColor(category: SkillCategory): Color = when (category) {
    SkillCategory.HTML -> Color(0xFFFF5722)
    SkillCategory.CSS -> Color(0xFF2196F3)
    SkillCategory.JAVASCRIPT -> Color(0xFFFFC107)
    SkillCategory.REACT -> Color(0xFF61DAFB)
    SkillCategory.NODE -> Color(0xFF68A063)
    SkillCategory.EXPRESS -> Color(0xFF555555)
    SkillCategory.MONGODB -> Color(0xFF4DB33D)
}

private fun isNodeCompleted(node: SkillNode, completedLessons: Set<String>): Boolean {
    if (node.courseId != null) {
        return completedLessons.any { it.startsWith(node.courseId) }
    }
    return node.isCompleted
}

private fun isNodeUnlocked(node: SkillNode, allNodes: List<SkillNode>, completedLessons: Set<String>): Boolean {
    if (node.prerequisites.isEmpty()) return true
    return node.prerequisites.all { prereqId ->
        val prereqNode = allNodes.find { it.id == prereqId }
        prereqNode != null && isNodeCompleted(prereqNode, completedLessons)
    }
}
