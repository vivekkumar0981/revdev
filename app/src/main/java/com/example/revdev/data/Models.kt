package com.example.revdev.data

import androidx.compose.runtime.Immutable

@Immutable
data class User(
    val id: String,
    val name: String,
    val email: String,
    val profilePicture: String? = null,
    val quizHistory: List<QuizResult> = emptyList()
)

@Immutable
data class QuizQuestion(
    val id: String,
    val question: String,
    val options: List<String>,
    val correctAnswer: Int,
    val explanation: String? = null
)

@Immutable
data class QuizResult(
    val id: String,
    val quizTitle: String,
    val score: Int,
    val totalQuestions: Int,
    val date: Long,
    val timeTaken: Long
)

@Immutable
data class ChatMessage(
    val id: String,
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@Immutable
data class Course(
    val id: String,
    val title: String,
    val description: String,
    val progress: Int = 0,
    val totalLessons: Int = 0,
    val completedLessons: Int = 0,
    val lessons: List<Lesson> = emptyList(),
    val category: CourseCategory = CourseCategory.WEB_DEVELOPMENT
)

@Immutable
data class Lesson(
    val id: String,
    val title: String,
    val description: String,
    val content: String,
    val codeExample: String? = null,
    val isCompleted: Boolean = false,
    val order: Int,
    val type: LessonType = LessonType.TEXT
)

@Immutable
data class ResumeReview(
    val id: String,
    val fileName: String,
    val feedback: String,
    val score: Int,
    val date: Long
)

enum class CourseCategory {
    WEB_DEVELOPMENT,
    PROGRAMMING,
    DESIGN,
    DATABASE
}

enum class LessonType {
    TEXT,
    CODE,
    QUIZ,
    PRACTICAL
} 