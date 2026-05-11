package com.example.revdev.data

object SkillTreeData {
    fun getSkillNodes(): List<SkillNode> = listOf(
        // HTML Track
        SkillNode("html_basics", "HTML Basics", "Tags, elements, document structure", SkillCategory.HTML, emptyList(), "html", isUnlocked = true),
        SkillNode("html_text", "Text & Headings", "Paragraphs, headings, formatting", SkillCategory.HTML, listOf("html_basics"), "html"),
        SkillNode("html_links", "Links & Images", "Anchor tags, image embedding", SkillCategory.HTML, listOf("html_text"), "html"),
        SkillNode("html_lists", "Lists & Tables", "Ordered, unordered lists, tables", SkillCategory.HTML, listOf("html_text"), "html"),
        SkillNode("html_forms", "Forms & Input", "Form elements, validation", SkillCategory.HTML, listOf("html_links", "html_lists"), "html"),
        SkillNode("html_semantic", "Semantic HTML", "Header, nav, main, article, aside", SkillCategory.HTML, listOf("html_forms"), "html"),

        // CSS Track
        SkillNode("css_basics", "CSS Basics", "Selectors, properties, values", SkillCategory.CSS, listOf("html_basics"), null),
        SkillNode("css_box", "Box Model", "Margin, padding, border, content", SkillCategory.CSS, listOf("css_basics"), null),
        SkillNode("css_layout", "Layout", "Display, position, float", SkillCategory.CSS, listOf("css_box"), null),
        SkillNode("css_flexbox", "Flexbox", "Flex container, items, alignment", SkillCategory.CSS, listOf("css_layout"), null),
        SkillNode("css_grid", "CSS Grid", "Grid template, areas, placement", SkillCategory.CSS, listOf("css_layout"), null),
        SkillNode("css_responsive", "Responsive Design", "Media queries, mobile-first", SkillCategory.CSS, listOf("css_flexbox", "css_grid"), null),
        SkillNode("css_animations", "Animations", "Transitions, keyframes, transforms", SkillCategory.CSS, listOf("css_responsive"), null),

        // JavaScript Track
        SkillNode("js_basics", "JS Variables", "let, const, var, data types", SkillCategory.JAVASCRIPT, listOf("html_basics"), null),
        SkillNode("js_functions", "Functions", "Declaration, expressions, arrow functions", SkillCategory.JAVASCRIPT, listOf("js_basics"), null),
        SkillNode("js_arrays", "Arrays & Objects", "Methods, destructuring, spread", SkillCategory.JAVASCRIPT, listOf("js_functions"), null),
        SkillNode("js_dom", "DOM Manipulation", "querySelector, events, classList", SkillCategory.JAVASCRIPT, listOf("js_arrays", "html_forms"), null),
        SkillNode("js_async", "Async JavaScript", "Promises, async/await, fetch", SkillCategory.JAVASCRIPT, listOf("js_dom"), null),
        SkillNode("js_es6", "ES6+ Features", "Modules, classes, template literals", SkillCategory.JAVASCRIPT, listOf("js_async"), null),

        // React Track
        SkillNode("react_basics", "React Basics", "Components, JSX, props", SkillCategory.REACT, listOf("js_es6"), null),
        SkillNode("react_state", "State & Hooks", "useState, useEffect, custom hooks", SkillCategory.REACT, listOf("react_basics"), null),
        SkillNode("react_routing", "React Router", "Routes, navigation, params", SkillCategory.REACT, listOf("react_state"), null),
        SkillNode("react_api", "API Integration", "Fetch, Axios, loading states", SkillCategory.REACT, listOf("react_routing", "js_async"), null),

        // Node Track
        SkillNode("node_basics", "Node.js Basics", "Runtime, npm, modules", SkillCategory.NODE, listOf("js_es6"), null),
        SkillNode("node_express", "Express.js", "Routes, middleware, REST API", SkillCategory.EXPRESS, listOf("node_basics"), null),
        SkillNode("node_mongo", "MongoDB", "Schema, CRUD, Mongoose", SkillCategory.MONGODB, listOf("node_express"), null),
        SkillNode("node_auth", "Authentication", "JWT, bcrypt, middleware", SkillCategory.EXPRESS, listOf("node_mongo"), null),
        SkillNode("mern_fullstack", "Full Stack App", "Connect React + Express + MongoDB", SkillCategory.REACT, listOf("react_api", "node_auth"), null)
    )

    fun getEdges(): List<Pair<String, String>> {
        val nodes = getSkillNodes()
        val edges = mutableListOf<Pair<String, String>>()
        for (node in nodes) {
            for (prereq in node.prerequisites) {
                edges.add(prereq to node.id)
            }
        }
        return edges
    }
}
