# RevDev — Student Skill Tracker

A telemetry-driven programming education platform built as an Android app using **Kotlin**, **Jetpack Compose**, **Firebase Auth**, **Room DB**, and **OpenRouter AI**.

RevDev helps students learn full-stack web development (MERN stack) through interactive courses, AI tutoring, quizzes, bug-hunting challenges, voice-based learning, and a skill-tree progression system.

---

## Tech Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| Language | Kotlin | 2.0.0 |
| UI Framework | Jetpack Compose (Material 3) | BOM 2024.05.00 |
| Navigation | Navigation Compose | 2.9.1 |
| Authentication | Firebase Auth | 23.2.1 |
| Local Database | Room (SQLite) | 2.6.1 |
| AI Backend | OpenRouter API (GPT) | REST via OkHttp |
| HTTP Client | OkHttp3 | 4.12.0 |
| Serialization | Kotlinx Serialization JSON | 1.6.3 |
| Voice | Android SpeechRecognizer + TextToSpeech | Native |
| Build System | Gradle (Kotlin DSL) + KSP | AGP 8.9.1 |
| Min SDK | 24 (Android 7.0) | |
| Target SDK | 35 | |

---

## Project Structure

```
revdev/
├── app/
│   ├── build.gradle.kts          # App-level dependencies & config
│   ├── google-services.json      # Firebase config (DO NOT commit to public repos)
│   └── src/main/
│       ├── AndroidManifest.xml
│       └── java/com/example/revdev/
│           ├── MainActivity.kt              # Entry point
│           ├── data/
│           │   ├── Models.kt                # All data classes (User, Course, Lesson, Quiz, etc.)
│           │   ├── AuthViewModel.kt         # Firebase Auth logic (login/signup/logout)
│           │   ├── CourseViewModel.kt        # Course state, XP, progress, streak tracking
│           │   ├── OpenAIApi.kt             # OpenRouter API calls (ask, stream, quiz gen, bug gen)
│           │   ├── VoiceService.kt          # TTS + Speech Recognition service
│           │   ├── MERNCourseData.kt        # MERN stack course content
│           │   ├── SkillTreeData.kt         # Skill node graph (HTML→CSS→JS→React→Node→MongoDB)
│           │   └── local/
│           │       ├── AppDatabase.kt       # Room database singleton ("revdev_db")
│           │       ├── ChatMessageEntity.kt # Room entity for chat messages
│           │       └── ChatMessageDao.kt    # DAO: insert, query, clear chat messages
│           ├── navigation/
│           │   └── Navigation.kt            # NavHost, routes, bottom nav bar
│           └── ui/
│               ├── screens/
│               │   ├── LoginScreen.kt
│               │   ├── SignUpScreen.kt
│               │   ├── OnboardingScreen.kt     # Skill level selection
│               │   ├── HomeScreen.kt            # Dashboard
│               │   ├── CourseSelectionScreen.kt  # Browse courses
│               │   ├── CourseDetailScreen.kt     # Course lessons list
│               │   ├── LessonViewerScreen.kt     # Read lessons + code examples
│               │   ├── QuizScreen.kt             # AI-generated quizzes
│               │   ├── AITutorScreen.kt          # Chat with AI tutor (streaming)
│               │   ├── AITutorViewModel.kt       # AI chat state management
│               │   ├── BugHuntScreen.kt          # Find-the-bug coding challenges
│               │   ├── VoiceLearningScreen.kt    # Voice-based Q&A
│               │   ├── SkillTreeScreen.kt        # Visual skill progression tree
│               │   ├── ResumeReviewScreen.kt     # AI resume review
│               │   └── ProfileScreen.kt          # User stats, XP, badges
│               ├── components/
│               │   ├── MarkdownText.kt           # Custom Markdown renderer
│               │   └── CommonComponents.kt       # Shared UI components
│               └── theme/
│                   ├── Color.kt                  # Dark theme color palette
│                   ├── Theme.kt                  # Material 3 theme setup
│                   └── Type.kt                   # Typography
├── build.gradle.kts              # Root build config
├── gradle/libs.versions.toml     # Version catalog
└── stability_config.conf         # Compose compiler stability config
```

---

## Database

### Room (Local SQLite)

Database name: `revdev_db`

**Table: `chat_messages`**

| Column | Type | Description |
|--------|------|-------------|
| `id` | String (PK) | Unique message ID |
| `content` | String | Message text |
| `isUser` | Boolean | `true` = user message, `false` = AI response |
| `timestamp` | Long | Unix timestamp in millis |

DAO operations:
- `getAllMessages()` — returns `Flow<List<ChatMessageEntity>>` ordered by timestamp ASC
- `insertMessage()` — upsert single message
- `insertMessages()` — upsert batch
- `clearAll()` — delete all chat history

### Firebase Auth (Remote)

Handles user authentication. Stores:
- Email/password credentials
- Display name (set on signup)
- User UID

No Firestore/Realtime DB used — all course data and progress live in-memory via ViewModels.

---

## Backend / AI API

No custom backend server. App calls **OpenRouter API** directly.

### OpenRouter Configuration

| Setting | Value |
|---------|-------|
| Endpoint | `https://openrouter.ai/api/v1/chat/completions` |
| Model | `openai/gpt-oss-120b:free` |
| Max Tokens | 1024 |
| Rate Limit | 1 request/second (client-side) |
| Streaming | SSE (Server-Sent Events) for AI Tutor chat |

### API Functions (`OpenAIApi.kt`)

| Function | Purpose |
|----------|---------|
| `ask(question, systemPrompt?)` | Single-shot question → answer |
| `askStream(question, systemPrompt?, onToken)` | Streaming response for chat |
| `generateQuiz(topic, numQuestions)` | Generates JSON quiz array |
| `generateBug(topic, difficulty)` | Creates bug-fixing challenge JSON |
| `validateBugFix(brokenCode, userCode, description)` | Validates user's fix |
| `reviewResume(resumeText)` | AI resume review with scoring |

---

## Authentication Flow

```
LoginScreen → Firebase signInWithEmailAndPassword
    ↓ success
OnboardingScreen (if first time → pick skill level: BEGINNER/INTERMEDIATE/ADVANCED)
    ↓
HomeScreen (dashboard)

SignUpScreen → Firebase createUserWithEmailAndPassword + setDisplayName
    ↓ success
HomeScreen
```

Auth state managed via `AuthViewModel` with sealed class:
- `Idle` — not logged in
- `Loading` — auth in progress
- `Success(user)` — authenticated
- `Error(message)` — auth failed

---

## Navigation

Bottom nav bar with 5 tabs:

| Tab | Screen | Route |
|-----|--------|-------|
| Home | Dashboard | `home` |
| Quiz | AI Quizzes | `quiz` |
| AI Tutor | Chat | `ai_tutor` |
| Resume | Resume Review | `resume_review` |
| Profile | User Stats | `profile` |

Additional screens (navigated from Home):
- `course_selection` → `course_detail/{courseId}` → `lesson_viewer/{courseId}/{lessonId}`
- `bug_hunt` — Bug-fixing challenges
- `voice_learning` — Voice-based learning
- `skill_tree` — Skill progression graph

---

## Key Features

### 1. Course System
- Pre-built courses: HTML Fundamentals, CSS Styling, + MERN stack courses
- Each course has ordered lessons with text content + code examples
- Lesson types: TEXT, CODE, QUIZ, PRACTICAL
- Track completion, progress percentage

### 2. AI Tutor (Chat)
- Streaming chat with GPT via OpenRouter
- Messages persisted in Room DB
- Custom Markdown renderer (bold, italic, code blocks, tables, lists, headings)

### 3. Quiz Generator
- AI generates multiple-choice questions on any topic
- JSON format: question, 4 options, correct answer index, explanation
- Scoring: 100% → 100 XP, ≥70% → 50 XP

### 4. Bug Hunt Mode
- AI generates broken code at EASY/MEDIUM/HARD difficulty
- User fixes bug, AI validates the fix
- XP rewards: 15/30/50 by difficulty

### 5. Voice Learning
- Text-to-Speech reads lessons aloud
- Speech Recognition captures voice answers
- Uses Android native `SpeechRecognizer` + `TextToSpeech`

### 6. Skill Tree
- Visual node graph: HTML → CSS → JS → React → Node → Express → MongoDB
- 28 skill nodes with prerequisites
- Nodes unlock as prerequisites complete

### 7. Resume Review
- Paste resume text, AI scores it out of 100
- Provides strengths, weaknesses, improvement suggestions, ATS compatibility

### 8. XP & Gamification
- XP system with levels (200 XP per level)
- Daily login streaks with bonus XP
- Badges earned at level milestones
- XP rewards for lessons (25), quizzes (50-100), bug fixes (15-50), voice (20), skill nodes (35)

---

## How to Run

### Prerequisites

- **Android Studio** Ladybug (2024.2+) or newer
- **JDK 11+**
- **Android SDK 35** (compileSdk)
- **Min device/emulator**: Android 7.0 (API 24)

### Setup Steps

1. **Clone the repository**
   ```bash
   git clone <repo-url>
   cd revdev
   ```

2. **Firebase setup**
   - Create a Firebase project at [console.firebase.google.com](https://console.firebase.google.com)
   - Enable **Email/Password** authentication
   - Download `google-services.json` and place it in `app/`

3. **OpenRouter API key**
   - Sign up at [openrouter.ai](https://openrouter.ai)
   - Get your API key
   - Create/edit `local.properties` in project root:
     ```properties
     OPENROUTER_API_KEY=sk-or-v1-your-api-key-here
     ```

4. **Sync and build**
   ```bash
   # Open in Android Studio and sync Gradle
   # Or from command line:
   ./gradlew assembleDebug
   ```

5. **Run**
   - Connect device or start emulator (API 24+)
   - Click Run in Android Studio
   - Or: `./gradlew installDebug`

### Permissions Required

| Permission | Reason |
|-----------|--------|
| `INTERNET` | API calls, Firebase Auth |
| `RECORD_AUDIO` | Voice learning (Speech Recognition) |

---

## Theme

Dark theme by default. Color palette:

| Color | Hex | Usage |
|-------|-----|-------|
| Primary | `#6C63FF` | Buttons, accents |
| Secondary | `#03DAC6` | Teal highlights, code text |
| Tertiary | `#FF6B6B` | Errors, warnings |
| Background | `#121212` | App background |
| Surface | `#1E1E1E` | Cards, containers |
| Success | `#4CAF50` | Correct answers |
| Gradient | `#6C63FF → #03DAC6` | Header gradients |

---

## Authors

- Savyasachi Mishra (0212207223)
- Naveen Chaurasia (7427202722)
- Anish Das (02927202722)
- Mohit Kumar Saha (0602720Z722)

**Guide:** Mr. Shiraj Ahmad (Asst. Professor)
**Department:** Computer Science & Engineering
**Institution:** Greater Noida Institute of Technology (GNIT)

---

## License

This project is a B.Tech CSE Major Project (2026). Not licensed for commercial use.
