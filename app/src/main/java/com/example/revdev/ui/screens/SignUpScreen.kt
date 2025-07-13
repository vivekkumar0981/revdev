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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.revdev.data.AuthViewModel
import com.example.revdev.data.AuthState
import com.example.revdev.ui.components.CustomTextField
import com.example.revdev.ui.components.PrimaryButton
import com.example.revdev.ui.theme.DarkBackground
import com.example.revdev.ui.theme.DarkOnBackground
import com.example.revdev.ui.theme.DarkOnSurfaceVariant
import com.example.revdev.ui.theme.DarkOutline
import com.example.revdev.ui.theme.DarkPrimary
import com.example.revdev.ui.theme.DarkSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    authViewModel: AuthViewModel,
    onSignUpSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val authState by authViewModel.authState.collectAsState()

    val errorMessage = (authState as? AuthState.Error)?.message
    val isLoading = authState is AuthState.Loading

    // Navigate on successful sign up
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onSignUpSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DarkBackground,
                        DarkSurface
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = DarkOnBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(60.dp))
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(1000)) + slideInVertically(
                    animationSpec = tween(1000),
                    initialOffsetY = { it }
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CustomTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = "Username",
                        leadingIcon = Icons.Default.Person
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        leadingIcon = Icons.Default.Email,
                        keyboardType = KeyboardType.Email
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        isPassword = true,
                        leadingIcon = Icons.Default.Lock
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = "Confirm Password",
                        isPassword = true,
                        leadingIcon = Icons.Default.Lock
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    PrimaryButton(
                        text = if (isLoading) "Signing Up..." else "Sign Up",
                        onClick = {
                            if (password == confirmPassword && email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty()) {
                                authViewModel.signup(email, username, password)
                            }
                        },
                        enabled = email.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && !isLoading && password == confirmPassword
                    )
                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Already have an account? ", color = DarkOnSurfaceVariant)
                        TextButton(onClick = onNavigateBack) {
                            Text("Sign In", color = DarkPrimary)
                        }
                    }
                }
            }
        }
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = DarkPrimary
            )
        }
    }
} 