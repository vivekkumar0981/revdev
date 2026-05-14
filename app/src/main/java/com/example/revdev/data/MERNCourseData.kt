package com.example.revdev.data

object MERNCourseData {

    fun getMERNCourses(): List<Course> = listOf(
        Course(
            id = "mern_mongo",
            title = "MongoDB - Task Manager DB",
            description = "Build the database layer for your Task Manager app",
            totalLessons = 5,
            category = CourseCategory.DATABASE,
            lessons = mongoLessons()
        ),
        Course(
            id = "mern_express",
            title = "Express.js - Task Manager API",
            description = "Create REST API endpoints for your Task Manager",
            totalLessons = 5,
            category = CourseCategory.PROGRAMMING,
            lessons = expressLessons()
        ),
        Course(
            id = "mern_react",
            title = "React - Task Manager UI",
            description = "Build the frontend interface for your Task Manager",
            totalLessons = 5,
            category = CourseCategory.WEB_DEVELOPMENT,
            lessons = reactLessons()
        ),
        Course(
            id = "mern_node",
            title = "Node.js - Task Manager Server",
            description = "Set up the runtime and connect everything together",
            totalLessons = 5,
            category = CourseCategory.PROGRAMMING,
            lessons = nodeLessons()
        )
    )

    private fun mongoLessons() = listOf(
        Lesson(
            id = "mern_mongo_1",
            title = "Define Task Schema",
            description = "You're building: The data model for tasks",
            content = """
                MongoDB stores data as documents (like JSON objects) in collections.
                For our Task Manager, we need a Task schema with:
                • title (string, required)
                • description (string)
                • completed (boolean, default: false)
                • createdAt (date)
                
                Mongoose is an ODM (Object Data Modeling) library that lets us define schemas in Node.js.
            """.trimIndent(),
            codeExample = """
const mongoose = require('mongoose');

const taskSchema = new mongoose.Schema({
  title: {
    type: String,
    required: [true, 'Task title is required'],
    trim: true,
    maxlength: 100
  },
  description: {
    type: String,
    trim: true
  },
  completed: {
    type: Boolean,
    default: false
  },
  createdAt: {
    type: Date,
    default: Date.now
  }
});

module.exports = mongoose.model('Task', taskSchema);
            """.trimIndent(),
            order = 1, type = LessonType.CODE
        ),
        Lesson(
            id = "mern_mongo_2",
            title = "CRUD Operations",
            description = "You're building: Create, Read, Update, Delete tasks",
            content = """
                CRUD = Create, Read, Update, Delete. These are the four basic database operations.
                
                MongoDB operations we'll use:
                • Create: Model.create() or new Model().save()
                • Read: Model.find(), Model.findById()
                • Update: Model.findByIdAndUpdate()
                • Delete: Model.findByIdAndDelete()
                
                Each returns a Promise, so we use async/await.
            """.trimIndent(),
            codeExample = """
// CREATE a task
const createTask = async (taskData) => {
  const task = await Task.create(taskData);
  return task;
};

// READ all tasks
const getAllTasks = async () => {
  const tasks = await Task.find({}).sort('-createdAt');
  return tasks;
};

// UPDATE a task
const updateTask = async (id, updates) => {
  const task = await Task.findByIdAndUpdate(id, updates, {
    new: true,
    runValidators: true
  });
  return task;
};

// DELETE a task
const deleteTask = async (id) => {
  await Task.findByIdAndDelete(id);
};
            """.trimIndent(),
            order = 2, type = LessonType.CODE
        ),
        Lesson(
            id = "mern_mongo_3",
            title = "Queries & Filters",
            description = "You're building: Filter tasks by status",
            content = """
                MongoDB queries let you filter documents. For our Task Manager:
                • Find incomplete tasks: { completed: false }
                • Search by title: { title: /keyword/i }
                • Sort by date: .sort('-createdAt')
                • Limit results: .limit(10)
                • Pagination: .skip(page * limit).limit(limit)
            """.trimIndent(),
            codeExample = """
// Filter tasks by completion status
const getFilteredTasks = async (completed) => {
  const filter = {};
  if (completed !== undefined) {
    filter.completed = completed;
  }
  return await Task.find(filter).sort('-createdAt');
};

// Search tasks by title
const searchTasks = async (keyword) => {
  return await Task.find({
    title: { ${'$'}regex: keyword, ${'$'}options: 'i' }
  });
};

// Paginated tasks
const getPaginatedTasks = async (page = 1, limit = 10) => {
  const skip = (page - 1) * limit;
  const tasks = await Task.find({})
    .sort('-createdAt')
    .skip(skip)
    .limit(limit);
  const total = await Task.countDocuments();
  return { tasks, total, pages: Math.ceil(total / limit) };
};
            """.trimIndent(),
            order = 3, type = LessonType.CODE
        ),
        Lesson(
            id = "mern_mongo_4",
            title = "Validation & Middleware",
            description = "You're building: Data validation rules",
            content = """
                Mongoose middleware (hooks) run before/after certain operations:
                • pre('save') - before saving a document
                • post('save') - after saving
                • pre('remove') - before deletion
                
                Validators ensure data integrity:
                • required, min, max, enum
                • Custom validators with validate function
            """.trimIndent(),
            codeExample = """
const taskSchema = new mongoose.Schema({
  title: {
    type: String,
    required: [true, 'Title is required'],
    minlength: [3, 'Title must be at least 3 chars'],
    maxlength: [100, 'Title cannot exceed 100 chars'],
    validate: {
      validator: (v) => !v.includes('<script>'),
      message: 'Title cannot contain script tags'
    }
  },
  priority: {
    type: String,
    enum: ['low', 'medium', 'high'],
    default: 'medium'
  }
});

// Middleware: auto-set updatedAt on save
taskSchema.pre('save', function(next) {
  this.updatedAt = Date.now();
  next();
});
            """.trimIndent(),
            order = 4, type = LessonType.CODE
        ),
        Lesson(
            id = "mern_mongo_5",
            title = "Connection & Config",
            description = "You're building: Database connection setup",
            content = """
                Connecting to MongoDB requires:
                • Connection string (local or MongoDB Atlas)
                • Error handling for connection failures
                • Connection options for production
                • Environment variables for secrets
            """.trimIndent(),
            codeExample = """
const mongoose = require('mongoose');
require('dotenv').config();

const connectDB = async () => {
  try {
    await mongoose.connect(process.env.MONGO_URI, {
      useNewUrlParser: true,
      useUnifiedTopology: true
    });
    console.log('MongoDB connected successfully');
  } catch (error) {
    console.error('MongoDB connection failed:', error.message);
    process.exit(1);
  }
};

module.exports = connectDB;

// .env file:
// MONGO_URI=mongodb+srv://user:pass@cluster.mongodb.net/taskmanager
            """.trimIndent(),
            order = 5, type = LessonType.CODE
        )
    )

    private fun expressLessons() = listOf(
        Lesson(
            id = "mern_express_1",
            title = "Express Setup & Routes",
            description = "You're building: API server with task endpoints",
            content = """
                Express.js is a minimal web framework for Node.js.
                For our Task Manager API:
                • GET /api/tasks - list all tasks
                • POST /api/tasks - create task
                • GET /api/tasks/:id - get one task
                • PATCH /api/tasks/:id - update task
                • DELETE /api/tasks/:id - delete task
            """.trimIndent(),
            codeExample = """
const express = require('express');
const app = express();

app.use(express.json());

// Task routes
app.get('/api/tasks', async (req, res) => {
  const tasks = await Task.find({});
  res.json({ tasks, count: tasks.length });
});

app.post('/api/tasks', async (req, res) => {
  const task = await Task.create(req.body);
  res.status(201).json({ task });
});

app.get('/api/tasks/:id', async (req, res) => {
  const task = await Task.findById(req.params.id);
  if (!task) return res.status(404).json({ error: 'Not found' });
  res.json({ task });
});

app.listen(5000, () => console.log('Server on port 5000'));
            """.trimIndent(),
            order = 1, type = LessonType.CODE
        ),
        Lesson(
            id = "mern_express_2",
            title = "Middleware",
            description = "You're building: Error handling & logging",
            content = """
                Middleware functions have access to req, res, and next.
                They execute in order and can:
                • Modify request/response
                • End the request cycle
                • Call next middleware
                
                Common middleware: CORS, body parser, auth, error handler, logger.
            """.trimIndent(),
            codeExample = """
const cors = require('cors');

// CORS for React frontend
app.use(cors({ origin: 'http://localhost:3000' }));

// Request logger
const logger = (req, res, next) => {
  console.log(`${'$'}{req.method} ${'$'}{req.path} - ${'$'}{new Date().toISOString()}`);
  next();
};
app.use(logger);

// Error handler (must be last)
const errorHandler = (err, req, res, next) => {
  console.error(err.stack);
  res.status(err.statusCode || 500).json({
    error: err.message || 'Internal Server Error'
  });
};
app.use(errorHandler);
            """.trimIndent(),
            order = 2, type = LessonType.CODE
        ),
        Lesson(
            id = "mern_express_3",
            title = "Controllers Pattern",
            description = "You're building: Clean separation of concerns",
            content = """
                Controllers separate route logic from route definitions.
                Benefits:
                • Cleaner, more maintainable code
                • Easier to test individual functions
                • Reusable logic across routes
            """.trimIndent(),
            codeExample = """
// controllers/taskController.js
const Task = require('../models/Task');

exports.getAllTasks = async (req, res) => {
  const { completed, sort } = req.query;
  const filter = {};
  if (completed) filter.completed = completed === 'true';
  
  const tasks = await Task.find(filter).sort(sort || '-createdAt');
  res.json({ tasks, count: tasks.length });
};

exports.createTask = async (req, res) => {
  const task = await Task.create(req.body);
  res.status(201).json({ task });
};

// routes/tasks.js
const router = require('express').Router();
const { getAllTasks, createTask } = require('../controllers/taskController');

router.route('/').get(getAllTasks).post(createTask);
module.exports = router;
            """.trimIndent(),
            order = 3, type = LessonType.CODE
        ),
        Lesson(
            id = "mern_express_4",
            title = "Authentication",
            description = "You're building: User login with JWT",
            content = """
                JWT (JSON Web Tokens) for stateless auth:
                1. User logs in with email/password
                2. Server verifies, creates JWT with user ID
                3. Client stores token, sends in Authorization header
                4. Server middleware verifies token on protected routes
            """.trimIndent(),
            codeExample = """
const jwt = require('jsonwebtoken');
const bcrypt = require('bcryptjs');

// Register
exports.register = async (req, res) => {
  const { name, email, password } = req.body;
  const hashedPassword = await bcrypt.hash(password, 10);
  const user = await User.create({ name, email, password: hashedPassword });
  const token = jwt.sign({ id: user._id }, process.env.JWT_SECRET, { expiresIn: '7d' });
  res.status(201).json({ user: { name, email }, token });
};

// Auth middleware
const authMiddleware = async (req, res, next) => {
  const token = req.headers.authorization?.split(' ')[1];
  if (!token) return res.status(401).json({ error: 'No token' });
  try {
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    req.user = decoded;
    next();
  } catch { res.status(401).json({ error: 'Invalid token' }); }
};
            """.trimIndent(),
            order = 4, type = LessonType.CODE
        ),
        Lesson(
            id = "mern_express_5",
            title = "Error Handling",
            description = "You're building: Robust error responses",
            content = """
                Production-ready error handling:
                • Custom error classes with status codes
                • Async wrapper to avoid try/catch everywhere
                • Mongoose validation error formatting
                • 404 handler for unknown routes
            """.trimIndent(),
            codeExample = """
// Custom error class
class AppError extends Error {
  constructor(message, statusCode) {
    super(message);
    this.statusCode = statusCode;
  }
}

// Async wrapper
const asyncHandler = (fn) => (req, res, next) =>
  Promise.resolve(fn(req, res, next)).catch(next);

// Usage in controller
exports.getTask = asyncHandler(async (req, res) => {
  const task = await Task.findById(req.params.id);
  if (!task) throw new AppError('Task not found', 404);
  res.json({ task });
});

// 404 handler
app.use('*', (req, res) => {
  res.status(404).json({ error: `Route ${'$'}{req.originalUrl} not found` });
});
            """.trimIndent(),
            order = 5, type = LessonType.CODE
        )
    )

    private fun reactLessons() = listOf(
        Lesson(
            id = "mern_react_1",
            title = "Task List Component",
            description = "You're building: Display tasks from API",
            content = """
                React components are reusable UI building blocks.
                For our Task Manager frontend:
                • TaskList: fetches and displays all tasks
                • TaskItem: renders a single task with actions
                • Uses useState for local state
                • Uses useEffect to fetch on mount
            """.trimIndent(),
            codeExample = """
import { useState, useEffect } from 'react';

function TaskList() {
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch('http://localhost:5000/api/tasks')
      .then(res => res.json())
      .then(data => {
        setTasks(data.tasks);
        setLoading(false);
      });
  }, []);

  if (loading) return <p>Loading tasks...</p>;

  return (
    <div className="task-list">
      <h2>My Tasks ({tasks.length})</h2>
      {tasks.map(task => (
        <TaskItem key={task._id} task={task} />
      ))}
    </div>
  );
}
            """.trimIndent(),
            order = 1, type = LessonType.CODE
        ),
        Lesson(
            id = "mern_react_2",
            title = "Add Task Form",
            description = "You're building: Create new tasks",
            content = """
                Controlled forms in React:
                • State tracks each input value
                • onChange updates state
                • onSubmit sends to API
                • Reset form after success
                • Show validation errors
            """.trimIndent(),
            codeExample = """
function AddTaskForm({ onTaskAdded }) {
  const [title, setTitle] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!title.trim()) {
      setError('Title is required');
      return;
    }
    
    const res = await fetch('http://localhost:5000/api/tasks', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ title })
    });
    
    if (res.ok) {
      const { task } = await res.json();
      onTaskAdded(task);
      setTitle('');
      setError('');
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        value={title}
        onChange={(e) => setTitle(e.target.value)}
        placeholder="Add a new task..."
      />
      <button type="submit">Add</button>
      {error && <p className="error">{error}</p>}
    </form>
  );
}
            """.trimIndent(),
            order = 2, type = LessonType.CODE
        ),
        Lesson(
            id = "mern_react_3",
            title = "State Management",
            description = "You're building: Toggle complete, delete tasks",
            content = """
                State updates in React must be immutable:
                • Toggle: map over array, flip completed for matching ID
                • Delete: filter out the deleted task
                • Optimistic updates: update UI before API confirms
                • Error rollback: revert if API fails
            """.trimIndent(),
            codeExample = """
function TaskItem({ task, onToggle, onDelete }) {
  const handleToggle = async () => {
    await fetch(`http://localhost:5000/api/tasks/${'$'}{task._id}`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ completed: !task.completed })
    });
    onToggle(task._id);
  };

  const handleDelete = async () => {
    await fetch(`http://localhost:5000/api/tasks/${'$'}{task._id}`, {
      method: 'DELETE'
    });
    onDelete(task._id);
  };

  return (
    <div className={`task ${'$'}{task.completed ? 'done' : ''}`}>
      <input
        type="checkbox"
        checked={task.completed}
        onChange={handleToggle}
      />
      <span>{task.title}</span>
      <button onClick={handleDelete}>Delete</button>
    </div>
  );
}
            """.trimIndent(),
            order = 3, type = LessonType.CODE
        ),
        Lesson(
            id = "mern_react_4",
            title = "React Router",
            description = "You're building: Multi-page navigation",
            content = """
                React Router for client-side navigation:
                • BrowserRouter wraps the app
                • Routes define URL → component mapping
                • Link/NavLink for navigation without reload
                • useParams for dynamic routes (/tasks/:id)
                • useNavigate for programmatic navigation
            """.trimIndent(),
            codeExample = """
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';

function App() {
  return (
    <BrowserRouter>
      <nav>
        <Link to="/">Tasks</Link>
        <Link to="/completed">Completed</Link>
        <Link to="/profile">Profile</Link>
      </nav>
      
      <Routes>
        <Route path="/" element={<TaskList />} />
        <Route path="/completed" element={<CompletedTasks />} />
        <Route path="/tasks/:id" element={<TaskDetail />} />
        <Route path="/profile" element={<Profile />} />
      </Routes>
    </BrowserRouter>
  );
}

function TaskDetail() {
  const { id } = useParams();
  // Fetch and display single task...
}
            """.trimIndent(),
            order = 4, type = LessonType.CODE
        ),
        Lesson(
            id = "mern_react_5",
            title = "API Service Layer",
            description = "You're building: Clean API integration",
            content = """
                Separate API calls into a service layer:
                • Central base URL configuration
                • Reusable fetch wrapper with auth headers
                • Error handling in one place
                • Easy to switch between dev/prod URLs
            """.trimIndent(),
            codeExample = """
// services/api.js
const BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:5000/api';

const api = {
  async request(endpoint, options = {}) {
    const token = localStorage.getItem('token');
    const res = await fetch(`${'$'}{BASE_URL}${'$'}{endpoint}`, {
      ...options,
      headers: {
        'Content-Type': 'application/json',
        ...(token && { Authorization: `Bearer ${'$'}{token}` }),
        ...options.headers
      }
    });
    if (!res.ok) throw new Error(await res.text());
    return res.json();
  },

  getTasks: () => api.request('/tasks'),
  createTask: (data) => api.request('/tasks', { method: 'POST', body: JSON.stringify(data) }),
  updateTask: (id, data) => api.request(`/tasks/${'$'}{id}`, { method: 'PATCH', body: JSON.stringify(data) }),
  deleteTask: (id) => api.request(`/tasks/${'$'}{id}`, { method: 'DELETE' }),
};

export default api;
            """.trimIndent(),
            order = 5, type = LessonType.CODE
        )
    )

    private fun nodeLessons() = listOf(
        Lesson(
            id = "mern_node_1",
            title = "Node.js & npm Setup",
            description = "You're building: Project initialization",
            content = """
                Node.js is a JavaScript runtime built on Chrome's V8 engine.
                npm (Node Package Manager) manages dependencies.
                
                Project setup:
                • npm init -y creates package.json
                • Install deps: express, mongoose, dotenv, cors
                • Dev deps: nodemon for auto-restart
                • Scripts in package.json for start/dev commands
            """.trimIndent(),
            codeExample = """
// Terminal commands:
// npm init -y
// npm install express mongoose dotenv cors bcryptjs jsonwebtoken
// npm install -D nodemon

// package.json scripts:
{
  "scripts": {
    "start": "node server.js",
    "dev": "nodemon server.js"
  }
}

// server.js - entry point
require('dotenv').config();
const express = require('express');
const cors = require('cors');
const connectDB = require('./config/db');

const app = express();

// Middleware
app.use(cors());
app.use(express.json());

// Connect to DB
connectDB();

// Routes
app.use('/api/tasks', require('./routes/tasks'));

const PORT = process.env.PORT || 5000;
app.listen(PORT, () => console.log(`Server running on port ${'$'}{PORT}`));
            """.trimIndent(),
            order = 1, type = LessonType.CODE
        ),
        Lesson(
            id = "mern_node_2",
            title = "Async/Await in Node",
            description = "You're building: Non-blocking operations",
            content = """
                Node.js is single-threaded but non-blocking.
                async/await makes async code readable:
                • Promises represent future values
                • await pauses until Promise resolves
                • try/catch for error handling
                • Promise.all() for parallel operations
            """.trimIndent(),
            codeExample = """
// Sequential (slow)
const getTasksSequential = async () => {
  const task1 = await Task.findById('id1');
  const task2 = await Task.findById('id2');
  return [task1, task2];
};

// Parallel (fast)
const getTasksParallel = async () => {
  const [task1, task2] = await Promise.all([
    Task.findById('id1'),
    Task.findById('id2')
  ]);
  return [task1, task2];
};

// Error handling
const safeOperation = async () => {
  try {
    const result = await riskyOperation();
    return { success: true, data: result };
  } catch (error) {
    console.error('Operation failed:', error);
    return { success: false, error: error.message };
  }
};
            """.trimIndent(),
            order = 2, type = LessonType.CODE
        ),
        Lesson(
            id = "mern_node_3",
            title = "Environment & Config",
            description = "You're building: Secure configuration",
            content = """
                Never hardcode secrets! Use environment variables:
                • .env file for local development
                • dotenv package loads .env into process.env
                • .gitignore must include .env
                • Different configs for dev/prod/test
            """.trimIndent(),
            codeExample = """
// .env
PORT=5000
MONGO_URI=mongodb://localhost:27017/taskmanager
JWT_SECRET=your_super_secret_key_here
NODE_ENV=development

// config/config.js
require('dotenv').config();

module.exports = {
  port: process.env.PORT || 5000,
  mongoUri: process.env.MONGO_URI,
  jwtSecret: process.env.JWT_SECRET,
  isProduction: process.env.NODE_ENV === 'production',
  corsOrigin: process.env.NODE_ENV === 'production'
    ? 'https://yourdomain.com'
    : 'http://localhost:3000'
};

// .gitignore
node_modules/
.env
            """.trimIndent(),
            order = 3, type = LessonType.CODE
        ),
        Lesson(
            id = "mern_node_4",
            title = "File Structure",
            description = "You're building: Scalable project architecture",
            content = """
                A well-organized Node project structure:
                • /models - Mongoose schemas
                • /routes - Express route definitions
                • /controllers - Business logic
                • /middleware - Auth, error handling
                • /config - DB connection, env vars
                • /utils - Helper functions
            """.trimIndent(),
            codeExample = """
task-manager/
├── server.js          # Entry point
├── package.json
├── .env
├── .gitignore
├── config/
│   ├── db.js          # MongoDB connection
│   └── config.js      # Environment config
├── models/
│   ├── Task.js        # Task schema
│   └── User.js        # User schema
├── routes/
│   ├── tasks.js       # Task routes
│   └── auth.js        # Auth routes
├── controllers/
│   ├── taskController.js
│   └── authController.js
├── middleware/
│   ├── auth.js        # JWT verification
│   ├── errorHandler.js
│   └── logger.js
└── utils/
    └── helpers.js
            """.trimIndent(),
            order = 4, type = LessonType.TEXT
        ),
        Lesson(
            id = "mern_node_5",
            title = "Deployment Ready",
            description = "You're building: Production configuration",
            content = """
                Making your MERN app production-ready:
                • Serve React build from Express in production
                • Use helmet for security headers
                • Rate limiting to prevent abuse
                • Compression for faster responses
                • Health check endpoint for monitoring
            """.trimIndent(),
            codeExample = """
const helmet = require('helmet');
const rateLimit = require('express-rate-limit');
const compression = require('compression');
const path = require('path');

// Security
app.use(helmet());
app.use(compression());

// Rate limiting
const limiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 100 // limit each IP to 100 requests per window
});
app.use('/api/', limiter);

// Serve React in production
if (process.env.NODE_ENV === 'production') {
  app.use(express.static(path.join(__dirname, 'client/build')));
  app.get('*', (req, res) => {
    res.sendFile(path.join(__dirname, 'client/build/index.html'));
  });
}

// Health check
app.get('/api/health', (req, res) => {
  res.json({ status: 'ok', uptime: process.uptime() });
});
            """.trimIndent(),
            order = 5, type = LessonType.CODE
        )
    )
}
