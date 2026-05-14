package com.example.revdev.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.revdev.data.*
import com.example.revdev.ui.components.*
import com.example.revdev.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BugHuntScreen(
    modifier: Modifier = Modifier,
    courseViewModel: CourseViewModel? = null
) {
    var selectedDifficulty by remember { mutableStateOf<BugDifficulty?>(null) }
    var currentChallenge by remember { mutableStateOf<BugChallenge?>(null) }
    var userCode by remember { mutableStateOf("") }
    var showHint by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var showPreview by remember { mutableStateOf(false) }
    var timerSeconds by remember { mutableStateOf(0) }
    var timerRunning by remember { mutableStateOf(false) }
    var feedback by remember { mutableStateOf<String?>(null) }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }
    var bugsFixed by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(timerRunning) {
        while (timerRunning) {
            delay(1000)
            timerSeconds++
        }
    }

    val hardcodedBugs = remember {
        listOf(
            BugChallenge("b1", "Missing Closing Tag", "This HTML page should show a heading and paragraph, but something is broken.", "<html>\n<body>\n  <h1>Welcome to RevDev\n  <p>Learn coding the fun way!</p>\n</body>\n</html>", "Check if all tags are properly closed", BugDifficulty.EASY, "HTML", 15),
            BugChallenge("b2", "Wrong CSS Property", "The button should have rounded corners but it's showing square.", "<style>\n.btn {\n  background: #6C63FF;\n  color: white;\n  padding: 12px 24px;\n  border-radius: none;\n  border: none;\n}\n</style>\n<button class=\"btn\">Click Me</button>", "border-radius accepts pixel values, not 'none'", BugDifficulty.EASY, "CSS", 15),
            BugChallenge("b3", "Flexbox Alignment Bug", "Items should be centered both horizontally and vertically in the container.", "<style>\n.container {\n  display: flex;\n  height: 200px;\n  background: #1e1e1e;\n  justify-content: center;\n  align-items: flex-start;\n}\n.item { color: white; padding: 20px; background: #6C63FF; }\n</style>\n<div class=\"container\">\n  <div class=\"item\">Centered?</div>\n</div>", "align-items controls vertical alignment in a row flex container", BugDifficulty.MEDIUM, "CSS", 30),
            BugChallenge("b4", "Event Handler Bug", "Clicking the button should increment the counter but nothing happens.", "<div id=\"counter\">0</div>\n<button onclick=\"increment\">+1</button>\n<script>\nlet count = 0;\nfunction increment() {\n  count++;\n  document.getElementById('counter').innerText = count;\n}\n</script>", "onclick needs to CALL the function, not just reference it", BugDifficulty.MEDIUM, "JavaScript", 30),
            BugChallenge("b5", "CSS Grid Overlap", "Three cards should display in a 3-column grid without overlapping.", "<style>\n.grid {\n  display: grid;\n  grid-template-columns: 1fr 1fr;\n  gap: 16px;\n  padding: 16px;\n}\n.card {\n  background: #2a2a2a;\n  color: white;\n  padding: 20px;\n  border-radius: 8px;\n}\n</style>\n<div class=\"grid\">\n  <div class=\"card\">Card 1</div>\n  <div class=\"card\">Card 2</div>\n  <div class=\"card\">Card 3</div>\n</div>", "grid-template-columns defines how many columns — count them", BugDifficulty.HARD, "CSS", 50)
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color(0xFFFF6B6B), Color(0xFFCC5555))))
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Bug Hunt", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = DarkOnPrimary)
                    Text("Find & fix the bugs!", style = MaterialTheme.typography.bodyMedium, color = DarkOnPrimary.copy(alpha = 0.8f))
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Fixed: $bugsFixed", style = MaterialTheme.typography.titleMedium, color = DarkOnPrimary)
                    if (timerRunning) {
                        Text("${timerSeconds}s", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = DarkOnPrimary)
                    }
                }
            }
        }

        if (currentChallenge == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Text("Choose Difficulty", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold), color = DarkOnBackground)
                Spacer(modifier = Modifier.height(16.dp))

                DifficultyCard("Easy", "Typos & missing tags", BugDifficulty.EASY, "+15 XP", Color(0xFF4CAF50), selectedDifficulty == BugDifficulty.EASY) { selectedDifficulty = BugDifficulty.EASY }
                Spacer(modifier = Modifier.height(12.dp))
                DifficultyCard("Medium", "Logic errors & wrong values", BugDifficulty.MEDIUM, "+30 XP", Color(0xFFFF9800), selectedDifficulty == BugDifficulty.MEDIUM) { selectedDifficulty = BugDifficulty.MEDIUM }
                Spacer(modifier = Modifier.height(12.dp))
                DifficultyCard("Hard", "Structural & architectural bugs", BugDifficulty.HARD, "+50 XP", Color(0xFFF44336), selectedDifficulty == BugDifficulty.HARD) { selectedDifficulty = BugDifficulty.HARD }

                Spacer(modifier = Modifier.height(24.dp))

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = DarkPrimary)
                    }
                } else {
                    PrimaryButton(
                        text = "Start Challenge",
                        onClick = {
                            selectedDifficulty?.let { diff ->
                                val filtered = hardcodedBugs.filter { it.difficulty == diff }
                                currentChallenge = filtered.random()
                                userCode = currentChallenge!!.brokenCode
                                timerSeconds = 0
                                timerRunning = true
                                showHint = false
                                feedback = null
                                isCorrect = null
                                showPreview = false
                            }
                        },
                        enabled = selectedDifficulty != null,
                        icon = Icons.Default.PlayArrow
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                val challenge = currentChallenge!!

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkCardBackground),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(challenge.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = DarkOnSurface)
                            Pill(challenge.difficulty.name, challenge.difficulty)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(challenge.description, style = MaterialTheme.typography.bodyMedium, color = DarkOnSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Fix the code:", style = MaterialTheme.typography.titleSmall, color = DarkPrimary)
                            Row {
                                IconButton(onClick = { showPreview = !showPreview }) {
                                    Icon(
                                        if (showPreview) Icons.Default.Edit else Icons.Default.PlayArrow,
                                        contentDescription = null, tint = DarkPrimary
                                    )
                                }
                            }
                        }

                        if (showPreview) {
                            Card(
                                modifier = Modifier.fillMaxWidth().height(150.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                AndroidView(
                                    factory = { ctx ->
                                        WebView(ctx).apply {
                                            webViewClient = WebViewClient()
                                            settings.javaScriptEnabled = true
                                            loadDataWithBaseURL(null, userCode, "text/html", "UTF-8", null)
                                        }
                                    },
                                    update = { it.loadDataWithBaseURL(null, userCode, "text/html", "UTF-8", null) },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        } else {
                            OutlinedTextField(
                                value = userCode,
                                onValueChange = { userCode = it },
                                modifier = Modifier.fillMaxWidth().height(200.dp),
                                textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, color = Color(0xFFE0E0E0)),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFF1E1E1E),
                                    unfocusedContainerColor = Color(0xFF1E1E1E),
                                    focusedBorderColor = DarkPrimary,
                                    unfocusedBorderColor = Color(0xFF333333)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { showHint = !showHint }) {
                        Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (showHint) "Hide Hint" else "Show Hint")
                    }
                    OutlinedButton(onClick = { userCode = challenge.brokenCode }) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset")
                    }
                }

                if (showHint) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkWarning.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(challenge.hint, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodyMedium, color = DarkWarning)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (feedback != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCorrect == true) DarkSuccess.copy(alpha = 0.15f) else DarkError.copy(alpha = 0.15f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    if (isCorrect == true) Icons.Default.CheckCircle else Icons.Default.Close,
                                    contentDescription = null,
                                    tint = if (isCorrect == true) DarkSuccess else DarkError
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    if (isCorrect == true) "Bug Fixed! +${challenge.xpReward} XP (${timerSeconds}s)" else "Not quite right...",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (isCorrect == true) DarkSuccess else DarkError
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(feedback!!, style = MaterialTheme.typography.bodyMedium, color = DarkOnSurface)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    if (isCorrect == true) {
                        PrimaryButton(text = "Next Challenge", onClick = {
                            currentChallenge = null
                            feedback = null
                            isCorrect = null
                        }, icon = Icons.Default.ArrowForward)
                    }
                } else {
                    PrimaryButton(
                        text = "Submit Fix",
                        onClick = {
                            timerRunning = false
                            coroutineScope.launch {
                                isLoading = true
                                val codeChanged = userCode.trim() != challenge.brokenCode.trim()
                                if (!codeChanged) {
                                    feedback = "You haven't changed the code yet. Find the bug and fix it!"
                                    isCorrect = false
                                } else {
                                    isCorrect = true
                                    feedback = "Your fix looks correct! Well done."
                                    bugsFixed++
                                    courseViewModel?.addXP(challenge.xpReward)
                                }
                                isLoading = false
                            }
                        },
                        enabled = !isLoading,
                        icon = Icons.Default.Check
                    )
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun DifficultyCard(
    title: String,
    description: String,
    difficulty: BugDifficulty,
    reward: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = if (isSelected) color.copy(alpha = 0.2f) else DarkCardBackground),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(if (isSelected) 8.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).background(color.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Build, contentDescription = null, tint = color)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = DarkOnSurface)
                Text(description, style = MaterialTheme.typography.bodyMedium, color = DarkOnSurfaceVariant)
            }
            Text(reward, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = color)
        }
    }
}

@Composable
private fun Pill(text: String, difficulty: BugDifficulty) {
    val color = when (difficulty) {
        BugDifficulty.EASY -> DarkSuccess
        BugDifficulty.MEDIUM -> DarkWarning
        BugDifficulty.HARD -> DarkError
    }
    Surface(
        color = color.copy(alpha = 0.2f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = color)
    }
}
