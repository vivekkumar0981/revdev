package com.example.revdev.data

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CourseViewModel : ViewModel() {
    
    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses
    
    private val _currentCourse = MutableStateFlow<Course?>(null)
    val currentCourse: StateFlow<Course?> = _currentCourse
    
    private val _currentLesson = MutableStateFlow<Lesson?>(null)
    val currentLesson: StateFlow<Lesson?> = _currentLesson

    private val _userProgress = MutableStateFlow(UserProgress())
    val userProgress: StateFlow<UserProgress> = _userProgress

    private val _skillLevel = MutableStateFlow<SkillLevel?>(null)
    val skillLevel: StateFlow<SkillLevel?> = _skillLevel

    val totalCompletedLessons: Int
        get() = _userProgress.value.completedLessons.size

    val totalQuizzesTaken: Int
        get() = _userProgress.value.quizResults.size

    val averageQuizScore: Int
        get() {
            val results = _userProgress.value.quizResults
            if (results.isEmpty()) return 0
            return results.sumOf { (it.score * 100) / it.totalQuestions } / results.size
        }

    val overallProgress: Float
        get() {
            val totalLessons = _courses.value.sumOf { it.totalLessons }
            if (totalLessons == 0) return 0f
            return totalCompletedLessons.toFloat() / totalLessons
        }

    fun setSkillLevel(level: SkillLevel) {
        _skillLevel.value = level
    }

    fun addXP(amount: Int) {
        val current = _userProgress.value
        val newXP = current.xp + amount
        val newLevel = XPRewards.levelFromXP(newXP)
        val newBadges = current.badges.toMutableList()
        
        if (newLevel > current.level) {
            newBadges.add(Badge(
                id = "level_$newLevel",
                title = "Level $newLevel",
                description = "Reached level $newLevel!",
                icon = "star",
                earnedDate = System.currentTimeMillis()
            ))
        }
        
        _userProgress.value = current.copy(
            xp = newXP,
            level = newLevel,
            badges = newBadges
        )
    }

    fun updateStreak() {
        val current = _userProgress.value
        val today = System.currentTimeMillis() / 86400000L
        val lastActive = current.lastActiveDate / 86400000L
        
        val newStreak = when {
            today == lastActive -> current.streak
            today - lastActive == 1L -> current.streak + 1
            else -> 1
        }
        
        _userProgress.value = current.copy(
            streak = newStreak,
            lastActiveDate = System.currentTimeMillis()
        )
        
        if (newStreak > 0) addXP(XPRewards.DAILY_LOGIN + (XPRewards.STREAK_BONUS * newStreak))
    }

    fun addQuizResult(result: QuizResult) {
        val current = _userProgress.value
        val updatedResults = current.quizResults + result
        _userProgress.value = current.copy(quizResults = updatedResults)
        
        val scorePercent = (result.score * 100) / result.totalQuestions
        when {
            scorePercent == 100 -> addXP(XPRewards.QUIZ_PERFECT)
            scorePercent >= 70 -> addXP(XPRewards.QUIZ_PASS)
        }
    }

    init {
        loadCourses()
        updateStreak()
    }
    
    private fun loadCourses() {
        val htmlCourse = Course(
            id = "html",
            title = "HTML Fundamentals",
            description = "Learn the basics of HTML markup language",
            totalLessons = 8,
            category = CourseCategory.WEB_DEVELOPMENT,
            lessons = listOf(
                Lesson(
                    id = "html_1",
                    title = "Introduction to HTML",
                    description = "What is HTML and why it's important",
                    content = """
                        HTML (HyperText Markup Language) is the standard markup language for creating web pages.
                        
                        Key points:
                        • HTML describes the structure of web pages
                        • HTML consists of a series of elements
                        • HTML elements tell the browser how to display the content
                        • HTML elements are represented by tags
                        
                        HTML is the foundation of web development and is used by every website on the internet.
                    """.trimIndent(),
                    order = 1,
                    type = LessonType.TEXT
                ),
                Lesson(
                    id = "html_2",
                    title = "HTML Document Structure",
                    description = "Basic HTML document structure and elements",
                    content = """
                        Every HTML document has a basic structure that includes:
                        
                        1. DOCTYPE declaration
                        2. HTML element
                        3. Head section
                        4. Body section
                        
                        The basic structure looks like this:
                    """.trimIndent(),
                    codeExample = """
<!DOCTYPE html>
<html>
<head>
    <title>My First Webpage</title>
</head>
<body>
    <h1>Hello World!</h1>
    <p>This is my first HTML page.</p>
</body>
</html>
                    """.trimIndent(),
                    order = 2,
                    type = LessonType.CODE
                ),
                Lesson(
                    id = "html_3",
                    title = "HTML Headings and Paragraphs",
                    description = "Using headings and paragraphs to structure content",
                    content = """
                        HTML provides six levels of headings (h1 to h6) and paragraph tags for text content.
                        
                        Headings:
                        • h1: Main heading (largest)
                        • h2: Subheading
                        • h3-h6: Smaller subheadings
                        
                        Paragraphs:
                        • p: Used for regular text content
                        • Automatically adds spacing before and after
                    """.trimIndent(),
                    codeExample = """
<h1>Main Title</h1>
<h2>Section Title</h2>
<h3>Subsection Title</h3>
<p>This is a paragraph of text.</p>
<p>This is another paragraph.</p>
                    """.trimIndent(),
                    order = 3,
                    type = LessonType.CODE
                ),
                Lesson(
                    id = "html_4",
                    title = "HTML Links",
                    description = "Creating hyperlinks with anchor tags",
                    content = """
                        Links are created using the <a> (anchor) tag.
                        
                        Key attributes:
                        • href: Specifies the URL
                        • target: Controls how the link opens
                        • title: Provides additional information
                        
                        Types of links:
                        • External links (to other websites)
                        • Internal links (within the same site)
                        • Email links
                        • Phone number links
                    """.trimIndent(),
                    codeExample = """
<!-- External link -->
<a href="https://www.google.com">Visit Google</a>

<!-- Internal link -->
<a href="about.html">About Us</a>

<!-- Email link -->
<a href="mailto:contact@example.com">Contact Us</a>

<!-- Phone link -->
<a href="tel:+1234567890">Call Us</a>
                    """.trimIndent(),
                    order = 4,
                    type = LessonType.CODE
                ),
                Lesson(
                    id = "html_5",
                    title = "HTML Images",
                    description = "Adding images to web pages",
                    content = """
                        Images are added using the <img> tag.
                        
                        Important attributes:
                        • src: Source URL of the image
                        • alt: Alternative text for accessibility
                        • width/height: Image dimensions
                        • title: Tooltip text
                        
                        Best practices:
                        • Always include alt text
                        • Use appropriate image formats
                        • Optimize image sizes
                        • Consider responsive design
                    """.trimIndent(),
                    codeExample = """
<!-- Basic image -->
<img src="photo.jpg" alt="A beautiful landscape">

<!-- Image with dimensions -->
<img src="logo.png" alt="Company Logo" width="200" height="100">

<!-- Image with title -->
<img src="product.jpg" alt="Product Image" title="Click to enlarge">
                    """.trimIndent(),
                    order = 5,
                    type = LessonType.CODE
                ),
                Lesson(
                    id = "html_6",
                    title = "HTML Lists",
                    description = "Creating ordered and unordered lists",
                    content = """
                        HTML supports two main types of lists:
                        
                        1. Unordered Lists (<ul>)
                           • Bullet points
                           • No specific order
                        
                        2. Ordered Lists (<ol>)
                           • Numbered items
                           • Sequential order
                        
                        List items are created using <li> tags.
                    """.trimIndent(),
                    codeExample = """
<!-- Unordered list -->
<ul>
    <li>Apple</li>
    <li>Banana</li>
    <li>Orange</li>
</ul>

<!-- Ordered list -->
<ol>
    <li>First step</li>
    <li>Second step</li>
    <li>Third step</li>
</ol>

<!-- Nested lists -->
<ul>
    <li>Fruits
        <ul>
            <li>Apple</li>
            <li>Banana</li>
        </ul>
    </li>
    <li>Vegetables
        <ul>
            <li>Carrot</li>
            <li>Broccoli</li>
        </ul>
    </li>
</ul>
                    """.trimIndent(),
                    order = 6,
                    type = LessonType.CODE
                ),
                Lesson(
                    id = "html_7",
                    title = "HTML Forms",
                    description = "Creating interactive forms",
                    content = """
                        Forms allow users to input data and submit it to a server.
                        
                        Key elements:
                        • <form>: Container for form elements
                        • <input>: Various input types
                        • <textarea>: Multi-line text input
                        • <select>: Dropdown selection
                        • <button>: Submit or action buttons
                        
                        Common input types:
                        • text, email, password
                        • number, date, time
                        • checkbox, radio
                        • file, submit
                    """.trimIndent(),
                    codeExample = """
<form action="/submit" method="post">
    <label for="name">Name:</label>
    <input type="text" id="name" name="name" required>
    
    <label for="email">Email:</label>
    <input type="email" id="email" name="email" required>
    
    <label for="message">Message:</label>
    <textarea id="message" name="message" rows="4"></textarea>
    
    <label for="country">Country:</label>
    <select id="country" name="country">
        <option value="">Select a country</option>
        <option value="us">United States</option>
        <option value="uk">United Kingdom</option>
        <option value="ca">Canada</option>
    </select>
    
    <button type="submit">Submit</button>
</form>
                    """.trimIndent(),
                    order = 7,
                    type = LessonType.CODE
                ),
                Lesson(
                    id = "html_8",
                    title = "HTML Semantic Elements",
                    description = "Using semantic HTML for better structure",
                    content = """
                        Semantic HTML elements clearly describe their meaning to both the browser and developer.
                        
                        Common semantic elements:
                        • <header>: Introductory content
                        • <nav>: Navigation links
                        • <main>: Main content
                        • <section>: Themed content
                        • <article>: Self-contained content
                        • <aside>: Sidebar content
                        • <footer>: Footer content
                        
                        Benefits:
                        • Better accessibility
                        • Improved SEO
                        • Cleaner code structure
                        • Easier maintenance
                    """.trimIndent(),
                    codeExample = """
<!DOCTYPE html>
<html>
<head>
    <title>Semantic HTML Example</title>
</head>
<body>
    <header>
        <h1>Website Title</h1>
        <nav>
            <a href="#home">Home</a>
            <a href="#about">About</a>
            <a href="#contact">Contact</a>
        </nav>
    </header>
    
    <main>
        <section>
            <h2>About Us</h2>
            <article>
                <h3>Our Story</h3>
                <p>This is our company story...</p>
            </article>
        </section>
        
        <aside>
            <h3>Related Links</h3>
            <ul>
                <li><a href="#link1">Link 1</a></li>
                <li><a href="#link2">Link 2</a></li>
            </ul>
        </aside>
    </main>
    
    <footer>
        <p>&copy; 2024 Company Name. All rights reserved.</p>
    </footer>
</body>
</html>
                    """.trimIndent(),
                    order = 8,
                    type = LessonType.CODE
                )
            )
        )
        
        val cssCourse = Course(
            id = "css",
            title = "CSS Styling",
            description = "Learn to style web pages with CSS",
            totalLessons = 7,
            category = CourseCategory.WEB_DEVELOPMENT,
            lessons = listOf(
                Lesson(
                    id = "css_1",
                    title = "Introduction to CSS",
                    description = "What is CSS and how it works",
                    content = """
                        CSS (Cascading Style Sheets) is a style sheet language used for describing the presentation of a document written in HTML.
                        
                        Key concepts:
                        • CSS describes how HTML elements should be displayed
                        • CSS saves a lot of work by controlling layout of multiple pages
                        • CSS can be added to HTML in three ways:
                          1. Inline CSS
                          2. Internal CSS
                          3. External CSS
                        
                        CSS consists of selectors and declaration blocks.
                    """.trimIndent(),
                    order = 1,
                    type = LessonType.TEXT
                ),
                Lesson(
                    id = "css_2",
                    title = "CSS Selectors",
                    description = "Understanding CSS selectors and specificity",
                    content = """
                        CSS selectors are patterns used to select and style HTML elements.
                        
                        Common selectors:
                        • Element selector: p, h1, div
                        • Class selector: .classname
                        • ID selector: #idname
                        • Attribute selector: [attribute]
                        • Pseudo-class: :hover, :active
                        • Pseudo-element: ::before, ::after
                        
                        Specificity determines which styles are applied when there are conflicts.
                    """.trimIndent(),
                    codeExample = """
/* Element selector */
p {
    color: blue;
}

/* Class selector */
.highlight {
    background-color: yellow;
}

/* ID selector */
#header {
    font-size: 24px;
}

/* Attribute selector */
input[type="text"] {
    border: 1px solid gray;
}

/* Pseudo-class */
button:hover {
    background-color: lightblue;
}
                    """.trimIndent(),
                    order = 2,
                    type = LessonType.CODE
                ),
                Lesson(
                    id = "css_3",
                    title = "CSS Colors and Typography",
                    description = "Working with colors and text styling",
                    content = """
                        CSS provides extensive control over colors and typography.
                        
                        Color values:
                        • Named colors: red, blue, green
                        • Hexadecimal: #ff0000, #00ff00
                        • RGB: rgb(255, 0, 0)
                        • RGBA: rgba(255, 0, 0, 0.5)
                        • HSL: hsl(0, 100%, 50%)
                        
                        Typography properties:
                        • font-family: Typeface selection
                        • font-size: Text size
                        • font-weight: Boldness
                        • text-align: Alignment
                        • line-height: Line spacing
                    """.trimIndent(),
                    codeExample = """
/* Color examples */
.text-red {
    color: red;
}

.text-hex {
    color: #ff0000;
}

.text-rgb {
    color: rgb(255, 0, 0);
}

.text-rgba {
    color: rgba(255, 0, 0, 0.5);
}

/* Typography examples */
.heading {
    font-family: Arial, sans-serif;
    font-size: 24px;
    font-weight: bold;
    text-align: center;
}

.body-text {
    font-family: Georgia, serif;
    font-size: 16px;
    line-height: 1.6;
    text-align: justify;
}
                    """.trimIndent(),
                    order = 3,
                    type = LessonType.CODE
                ),
                Lesson(
                    id = "css_4",
                    title = "CSS Box Model",
                    description = "Understanding the CSS box model",
                    content = """
                        The CSS box model consists of:
                        
                        1. Content: The actual content of the element
                        2. Padding: Clear area around the content
                        3. Border: Border around the padding
                        4. Margin: Clear area outside the border
                        
                        Box model properties:
                        • width/height: Content dimensions
                        • padding: Inner spacing
                        • border: Border styling
                        • margin: Outer spacing
                        
                        Box-sizing property controls how dimensions are calculated.
                    """.trimIndent(),
                    codeExample = """
/* Box model example */
.box {
    width: 200px;
    height: 100px;
    padding: 20px;
    border: 2px solid black;
    margin: 10px;
    background-color: lightblue;
}

/* Box-sizing */
.border-box {
    box-sizing: border-box;
    width: 200px;
    padding: 20px;
    border: 2px solid black;
    /* Total width = 200px (includes padding and border) */
}

.content-box {
    box-sizing: content-box;
    width: 200px;
    padding: 20px;
    border: 2px solid black;
    /* Total width = 244px (200 + 40 + 4) */
}
                    """.trimIndent(),
                    order = 4,
                    type = LessonType.CODE
                ),
                Lesson(
                    id = "css_5",
                    title = "CSS Layout - Flexbox",
                    description = "Creating flexible layouts with Flexbox",
                    content = """
                        Flexbox is a one-dimensional layout method for arranging items in rows or columns.
                        
                        Key concepts:
                        • Flex container: Parent element with display: flex
                        • Flex items: Direct children of flex container
                        • Main axis: Primary direction of flex layout
                        • Cross axis: Perpendicular to main axis
                        
                        Container properties:
                        • flex-direction: Row or column
                        • justify-content: Main axis alignment
                        • align-items: Cross axis alignment
                        • flex-wrap: Wrapping behavior
                    """.trimIndent(),
                    codeExample = """
/* Flexbox container */
.flex-container {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 10px;
}

/* Flex items */
.flex-item {
    flex: 1;
    min-width: 200px;
}

/* Centered layout */
.centered {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
}

/* Navigation bar */
.navbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1rem;
    background-color: #333;
    color: white;
}
                    """.trimIndent(),
                    order = 5,
                    type = LessonType.CODE
                ),
                Lesson(
                    id = "css_6",
                    title = "CSS Grid",
                    description = "Creating two-dimensional layouts with CSS Grid",
                    content = """
                        CSS Grid is a two-dimensional layout system for creating complex web layouts.
                        
                        Key concepts:
                        • Grid container: Parent element with display: grid
                        • Grid items: Direct children of grid container
                        • Grid lines: Horizontal and vertical lines
                        • Grid tracks: Rows and columns
                        • Grid areas: Named grid areas
                        
                        Container properties:
                        • grid-template-columns: Define columns
                        • grid-template-rows: Define rows
                        • grid-template-areas: Define areas
                        • gap: Spacing between items
                    """.trimIndent(),
                    codeExample = """
/* Basic grid */
.grid-container {
    display: grid;
    grid-template-columns: 1fr 2fr 1fr;
    grid-template-rows: auto auto auto;
    gap: 20px;
    padding: 20px;
}

/* Grid with areas */
.layout {
    display: grid;
    grid-template-areas: 
        "header header header"
        "sidebar main aside"
        "footer footer footer";
    grid-template-columns: 200px 1fr 200px;
    grid-template-rows: 80px 1fr 80px;
    min-height: 100vh;
}

.header { grid-area: header; }
.sidebar { grid-area: sidebar; }
.main { grid-area: main; }
.aside { grid-area: aside; }
.footer { grid-area: footer; }

/* Responsive grid */
.responsive-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 20px;
}
                    """.trimIndent(),
                    order = 6,
                    type = LessonType.CODE
                ),
                Lesson(
                    id = "css_7",
                    title = "CSS Responsive Design",
                    description = "Making websites work on all devices",
                    content = """
                        Responsive design ensures websites look good on all devices and screen sizes.
                        
                        Key techniques:
                        • Media queries: Apply styles based on screen size
                        • Flexible units: Use relative units (%, em, rem)
                        • Flexible images: Scale images appropriately
                        • Mobile-first approach: Design for mobile first
                        
                        Common breakpoints:
                        • Mobile: < 768px
                        • Tablet: 768px - 1024px
                        • Desktop: > 1024px
                    """.trimIndent(),
                    codeExample = """
/* Mobile-first approach */
.container {
    width: 100%;
    padding: 10px;
}

/* Tablet styles */
@media (min-width: 768px) {
    .container {
        max-width: 750px;
        margin: 0 auto;
        padding: 20px;
    }
}

/* Desktop styles */
@media (min-width: 1024px) {
    .container {
        max-width: 1000px;
        padding: 30px;
    }
}

/* Flexible typography */
html {
    font-size: 16px;
}

h1 {
    font-size: 2rem; /* 32px on desktop, scales on mobile */
}

/* Flexible images */
img {
    max-width: 100%;
    height: auto;
}
                    """.trimIndent(),
                    order = 7,
                    type = LessonType.CODE
                )
            )
        )
        
        _courses.value = listOf(htmlCourse, cssCourse) + MERNCourseData.getMERNCourses()
    }
    
    fun selectCourse(courseId: String) {
        _currentCourse.value = _courses.value.find { it.id == courseId }
        _currentLesson.value = _currentCourse.value?.lessons?.firstOrNull()
    }
    
    fun selectLesson(lessonId: String) {
        _currentLesson.value = _currentCourse.value?.lessons?.find { it.id == lessonId }
    }
    
    fun markLessonAsCompleted(lessonId: String) {
        val course = _currentCourse.value ?: return
        val updatedLessons = course.lessons.map { lesson ->
            if (lesson.id == lessonId) lesson.copy(isCompleted = true) else lesson
        }
        
        val completedCount = updatedLessons.count { it.isCompleted }
        val updatedCourse = course.copy(
            lessons = updatedLessons,
            completedLessons = completedCount,
            progress = if (course.totalLessons > 0) (completedCount * 100) / course.totalLessons else 0
        )
        
        _currentCourse.value = updatedCourse
        _courses.value = _courses.value.map { if (it.id == course.id) updatedCourse else it }

        val current = _userProgress.value
        if (lessonId !in current.completedLessons) {
            _userProgress.value = current.copy(
                completedLessons = current.completedLessons + lessonId
            )
            addXP(XPRewards.LESSON_COMPLETE)
        }
    }
    
    fun getNextLesson(): Lesson? {
        val currentLesson = _currentLesson.value ?: return null
        val course = _currentCourse.value ?: return null
        
        val currentIndex = course.lessons.indexOfFirst { it.id == currentLesson.id }
        return if (currentIndex < course.lessons.size - 1) {
            course.lessons[currentIndex + 1]
        } else null
    }
    
    fun getPreviousLesson(): Lesson? {
        val currentLesson = _currentLesson.value ?: return null
        val course = _currentCourse.value ?: return null
        
        val currentIndex = course.lessons.indexOfFirst { it.id == currentLesson.id }
        return if (currentIndex > 0) {
            course.lessons[currentIndex - 1]
        } else null
    }
} 