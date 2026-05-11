package com.example.revdev.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.revdev.ui.theme.*

@Composable
fun MarkdownText(
    text: String,
    color: Color = DarkOnSurface,
    textAlign: TextAlign = TextAlign.Start
) {
    val blocks = parseBlocks(text)
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        for (block in blocks) {
            when (block) {
                is MdBlock.CodeBlock -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkBackground)
                            .horizontalScroll(rememberScrollState())
                            .padding(12.dp)
                    ) {
                        Text(
                            text = block.code,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 18.sp
                            ),
                            color = DarkSecondary
                        )
                    }
                }
                is MdBlock.Heading -> {
                    val style = when (block.level) {
                        1 -> MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        2 -> MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        else -> MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = block.content,
                        style = style,
                        color = color,
                        textAlign = textAlign
                    )
                }
                is MdBlock.Table -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkBackground)
                            .horizontalScroll(rememberScrollState())
                            .padding(8.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            block.rows.forEachIndexed { idx, row ->
                                val weight = if (idx == 0) FontWeight.Bold else FontWeight.Normal
                                Text(
                                    text = row,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = weight,
                                        lineHeight = 18.sp
                                    ),
                                    color = DarkOnSurface
                                )
                            }
                        }
                    }
                }
                is MdBlock.ListBlock -> {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        block.items.forEachIndexed { idx, item ->
                            val bullet = if (block.ordered) "${idx + 1}. " else "  •  "
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(SpanStyle(color = DarkPrimary, fontWeight = FontWeight.Bold)) {
                                        append(bullet)
                                    }
                                    append(parseInlineMarkdown(item, color))
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = textAlign
                            )
                        }
                    }
                }
                is MdBlock.Paragraph -> {
                    if (block.content.isNotBlank()) {
                        Text(
                            text = parseInlineMarkdown(block.content, color),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = textAlign
                        )
                    }
                }
            }
        }
    }
}

private sealed class MdBlock {
    data class CodeBlock(val language: String, val code: String) : MdBlock()
    data class Heading(val level: Int, val content: String) : MdBlock()
    data class Table(val rows: List<String>) : MdBlock()
    data class ListBlock(val items: List<String>, val ordered: Boolean) : MdBlock()
    data class Paragraph(val content: String) : MdBlock()
}

private val UNORDERED_BULLET = Regex("^\\s*[-*+]\\s+(.+)")
private val ORDERED_BULLET = Regex("^\\s*\\d+[.)\\s]+(.+)")

private fun isListLine(line: String): Boolean =
    UNORDERED_BULLET.matches(line) || ORDERED_BULLET.matches(line)

private fun extractListContent(line: String): String =
    UNORDERED_BULLET.find(line)?.groupValues?.get(1)
        ?: ORDERED_BULLET.find(line)?.groupValues?.get(1)
        ?: line.trim()

private fun isOrderedList(line: String): Boolean = ORDERED_BULLET.matches(line)

private fun parseBlocks(text: String): List<MdBlock> {
    val blocks = mutableListOf<MdBlock>()
    val lines = text.split("\n")
    var i = 0

    while (i < lines.size) {
        val line = lines[i]

        if (line.trimStart().startsWith("```")) {
            val lang = line.trimStart().removePrefix("```").trim()
            val codeLines = mutableListOf<String>()
            i++
            while (i < lines.size && !lines[i].trimStart().startsWith("```")) {
                codeLines.add(lines[i])
                i++
            }
            blocks.add(MdBlock.CodeBlock(lang, codeLines.joinToString("\n")))
            i++
            continue
        }

        if (line.trimStart().startsWith("#")) {
            val trimmed = line.trimStart()
            val level = trimmed.takeWhile { it == '#' }.length
            val content = trimmed.dropWhile { it == '#' }.trim()
            blocks.add(MdBlock.Heading(level.coerceIn(1, 3), content))
            i++
            continue
        }

        if (line.contains("|") && line.trim().startsWith("|")) {
            val tableRows = mutableListOf<String>()
            while (i < lines.size && lines[i].contains("|") && lines[i].trim().startsWith("|")) {
                val row = lines[i].trim()
                if (!row.matches(Regex("^[|\\s\\-:]+$"))) {
                    tableRows.add(row)
                }
                i++
            }
            if (tableRows.isNotEmpty()) {
                blocks.add(MdBlock.Table(tableRows))
            }
            continue
        }

        if (isListLine(line)) {
            val items = mutableListOf<String>()
            val ordered = isOrderedList(line)
            while (i < lines.size && isListLine(lines[i])) {
                items.add(extractListContent(lines[i]))
                i++
            }
            blocks.add(MdBlock.ListBlock(items, ordered))
            continue
        }

        if (line.isBlank()) {
            i++
            continue
        }

        val paraLines = mutableListOf<String>()
        while (i < lines.size &&
            !lines[i].trimStart().startsWith("```") &&
            !lines[i].trimStart().startsWith("#") &&
            !(lines[i].contains("|") && lines[i].trim().startsWith("|")) &&
            !isListLine(lines[i])
        ) {
            if (lines[i].isBlank() && paraLines.isNotEmpty()) {
                i++
                break
            }
            paraLines.add(lines[i])
            i++
        }
        blocks.add(MdBlock.Paragraph(paraLines.joinToString("\n")))
    }

    return blocks
}

private fun parseInlineMarkdown(text: String, defaultColor: Color): AnnotatedString {
    return buildAnnotatedString {
        var remaining = text
        while (remaining.isNotEmpty()) {
            val boldIdx = remaining.indexOf("**")
            val codeIdx = remaining.indexOf('`')
            val italicIdx = remaining.indexOf('*').let { idx ->
                if (idx >= 0 && idx == boldIdx) -1 else idx
            }

            val nextMarkdown = listOfNotNull(
                if (boldIdx >= 0) boldIdx to "bold" else null,
                if (codeIdx >= 0) codeIdx to "code" else null,
                if (italicIdx >= 0) italicIdx to "italic" else null
            ).minByOrNull { it.first }

            if (nextMarkdown == null) {
                withStyle(SpanStyle(color = defaultColor)) {
                    append(remaining)
                }
                break
            }

            val (idx, type) = nextMarkdown

            if (idx > 0) {
                withStyle(SpanStyle(color = defaultColor)) {
                    append(remaining.substring(0, idx))
                }
            }

            when (type) {
                "bold" -> {
                    val after = remaining.substring(idx + 2)
                    val endIdx = after.indexOf("**")
                    if (endIdx >= 0) {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = defaultColor)) {
                            append(after.substring(0, endIdx))
                        }
                        remaining = after.substring(endIdx + 2)
                    } else {
                        withStyle(SpanStyle(color = defaultColor)) {
                            append("**")
                        }
                        remaining = after
                    }
                }
                "italic" -> {
                    val after = remaining.substring(idx + 1)
                    val endIdx = after.indexOf('*')
                    if (endIdx >= 0 && endIdx > 0) {
                        withStyle(SpanStyle(fontStyle = FontStyle.Italic, color = defaultColor)) {
                            append(after.substring(0, endIdx))
                        }
                        remaining = after.substring(endIdx + 1)
                    } else {
                        withStyle(SpanStyle(color = defaultColor)) {
                            append("*")
                        }
                        remaining = after
                    }
                }
                "code" -> {
                    val after = remaining.substring(idx + 1)
                    val endIdx = after.indexOf('`')
                    if (endIdx >= 0) {
                        withStyle(
                            SpanStyle(
                                fontFamily = FontFamily.Monospace,
                                background = DarkBackground,
                                color = DarkSecondary
                            )
                        ) {
                            append(" ${after.substring(0, endIdx)} ")
                        }
                        remaining = after.substring(endIdx + 1)
                    } else {
                        withStyle(SpanStyle(color = defaultColor)) {
                            append("`")
                        }
                        remaining = after
                    }
                }
                else -> {
                    withStyle(SpanStyle(color = defaultColor)) {
                        append(remaining)
                    }
                    break
                }
            }
        }
    }
}
