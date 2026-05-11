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
    DATABASE,
    FULL_STACK
}

enum class LessonType {
    TEXT,
    CODE,
    QUIZ,
    PRACTICAL
}

@Immutable
data class UserProgress(
    val completedLessons: Set<String> = emptySet(),
    val quizResults: List<QuizResult> = emptyList(),
    val xp: Int = 0,
    val level: Int = 1,
    val streak: Int = 0,
    val lastActiveDate: Long = 0L,
    val badges: List<Badge> = emptyList()
)

@Immutable
data class Badge(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val earnedDate: Long = 0L
)

enum class SkillLevel {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED
}

// Bug Hunt Mode
@Immutable
data class BugChallenge(
    val id: String,
    val title: String,
    val description: String,
    val brokenCode: String,
    val hint: String,
    val difficulty: BugDifficulty,
    val language: String,
    val xpReward: Int
)

enum class BugDifficulty { EASY, MEDIUM, HARD }

// Skill Tree
@Immutable
data class SkillNode(
    val id: String,
    val title: String,
    val description: String,
    val category: SkillCategory,
    val prerequisites: List<String> = emptyList(),
    val courseId: String? = null,
    val isUnlocked: Boolean = false,
    val isCompleted: Boolean = false,
    val xpReward: Int = 35
)

enum class SkillCategory { HTML, CSS, JAVASCRIPT, REACT, NODE, EXPRESS, MONGODB }

// MERN Project Steps
@Immutable
data class ProjectStep(
    val lessonId: String,
    val track: MERNTrack,
    val projectContext: String,
    val expectedOutput: String,
    val starterCode: String
)

enum class MERNTrack { MONGODB, EXPRESS, REACT, NODE }

object XPRewards {
    const val LESSON_COMPLETE = 25
    const val QUIZ_PERFECT = 100
    const val QUIZ_PASS = 50
    const val DAILY_LOGIN = 10
    const val STREAK_BONUS = 5
    const val BUG_FIX_EASY = 15
    const val BUG_FIX_MEDIUM = 30
    const val BUG_FIX_HARD = 50
    const val VOICE_QUIZ_CORRECT = 20
    const val SKILL_NODE_UNLOCK = 35
    const val MERN_STEP_COMPLETE = 30

    fun levelFromXP(xp: Int): Int = (xp / 200) + 1
    fun xpForNextLevel(level: Int): Int = level * 200
    fun xpProgressInLevel(xp: Int): Float {
        val currentLevelXP = xp % 200
        return currentLevelXP / 200f
    }
} 