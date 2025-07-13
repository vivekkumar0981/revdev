package com.example.revdev.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.revdev.ui.screens.*
import com.google.firebase.auth.FirebaseAuth
import com.example.revdev.data.AuthViewModel
import com.example.revdev.data.AuthState

import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Login : Screen("login", "Login", Icons.Default.Person)
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Quiz : Screen("quiz", "Quiz", Icons.Default.CheckCircle)
    object AITutor : Screen("ai_tutor", "AI Tutor", Icons.Default.Email)
    object ResumeReview : Screen("resume_review", "Resume", Icons.Default.AccountCircle)
    object Profile : Screen("profile", "Profile", Icons.Default.AccountCircle)
    object CourseSelection : Screen("course_selection", "Courses", Icons.Default.AddCircle)
    object CourseDetail : Screen("course_detail/{courseId}", "Course Detail", Icons.Default.CheckCircle)
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Quiz,
    Screen.AITutor,
    Screen.ResumeReview,
    Screen.Profile
)

@Composable
fun RevdevNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()

    // Check auth state on app start
    LaunchedEffect(Unit) {
        authViewModel.checkAuth()
    }

    // Navigation logic based on auth state
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                if (navController.currentDestination?.route != Screen.Home.route) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            is AuthState.Idle, is AuthState.Error -> {
                if (navController.currentDestination?.route != Screen.Login.route) {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            else -> {}
        }
    }

    if (authState is AuthState.Loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route
        ) {
            composable(Screen.Login.route) { 
                LoginScreen(
                    authViewModel = authViewModel,
                    onNavigateToSignUp = { navController.navigate("signup") },
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable("signup") { 
                SignUpScreen(
                    authViewModel = authViewModel,
                    onSignUpSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo("signup") { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Home.route) { 
                MainScreen(
                    navController = navController,
                    currentRoute = Screen.Home.route
                ) { modifier ->
                    HomeScreen(
                        onNavigateToQuiz = { navController.navigate(route = Screen.Quiz.route) },
                        onNavigateToAITutor = { navController.navigate(route = Screen.AITutor.route) },
                        onNavigateToResumeReview = { navController.navigate(route = Screen.ResumeReview.route) },
                        onNavigateToCourseSelection = { navController.navigate(route = Screen.CourseSelection.route) },
                        modifier = modifier
                    )
                }
            }
            composable(Screen.Quiz.route) { 
                MainScreen(
                    navController = navController,
                    currentRoute = Screen.Quiz.route
                ) { modifier ->
                    QuizScreen(modifier = modifier)
                }
            }
            composable(Screen.AITutor.route) { 
                MainScreen(
                    navController = navController,
                    currentRoute = Screen.AITutor.route
                ) { modifier ->
                    AITutorScreen(modifier = modifier)
                }
            }
            composable(Screen.ResumeReview.route) { 
                MainScreen(
                    navController = navController,
                    currentRoute = Screen.ResumeReview.route
                ) { modifier ->
                    ResumeReviewScreen(modifier = modifier)
                }
            }
            composable(Screen.Profile.route) { 
                MainScreen(
                    navController = navController,
                    currentRoute = Screen.Profile.route
                ) { modifier ->
                    ProfileScreen(modifier = modifier, authViewModel = authViewModel)
                }
            }
            composable(Screen.CourseSelection.route) { 
                MainScreen(
                    navController = navController,
                    currentRoute = Screen.CourseSelection.route
                ) { modifier ->
                    val courses = listOf(
                        com.example.revdev.ui.screens.CourseProgress("html", "HTML", "Learn HTML from scratch", 75),
                        com.example.revdev.ui.screens.CourseProgress("css", "CSS", "Master CSS styling", 30),
                        com.example.revdev.ui.screens.CourseProgress("js", "JavaScript", "JavaScript programming", 10)
                    )
                    CourseSelectionScreen(
                        courses = courses,
                        onCourseClick = { course -> navController.navigate("course_detail/${course.id}") },
                        modifier = modifier
                    )
                }
            }
            composable(
                route = "course_detail/{courseId}",
                arguments = listOf(navArgument("courseId") { type = NavType.StringType })
            ) { backStackEntry ->
                val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
                MainScreen(
                    navController = navController,
                    currentRoute = "course_detail/$courseId"
                ) { modifier ->
                    com.example.revdev.ui.screens.CourseDetailScreen(courseId = courseId, modifier = modifier)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    currentRoute: String,
    content: @Composable (Modifier) -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        content(Modifier.padding(innerPadding))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    NavigationBar {
        bottomNavItems.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
} 