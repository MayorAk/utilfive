package com.mohaaa.utilfive.ui.calculator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val buttonRows = listOf(
    listOf("7", "8", "9", "÷"),
    listOf("4", "5", "6", "×"),
    listOf("1", "2", "3", "−"),
    listOf("C", "0", "=", "+")
)

@Composable
fun CalculatorScreen(onBack: () -> Unit) {
    var expression by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calculator") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text(
                text = expression.ifEmpty { "0" },
                fontSize = 32.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = result,
                fontSize = 20.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
            )

            buttonRows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { label ->
                        Button(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            onClick = {
                                when (label) {
                                    "C" -> {
                                        expression = ""
                                        result = ""
                                    }
                                    "=" -> {
                                        result = evaluateExpression(expression)
                                    }
                                    else -> expression += label
                                }
                            }
                        ) {
                            Text(label)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Minimal left-to-right evaluator (no operator precedence) for +, −, ×, ÷.
 * Good enough for a simple utility calculator without pulling in a math library.
 */
private fun evaluateExpression(expr: String): String {
    if (expr.isBlank()) return ""
    return try {
        val tokens = tokenize(expr)
        var acc = tokens[0].toDouble()
        var i = 1
        while (i < tokens.size - 1) {
            val op = tokens[i]
            val value = tokens[i + 1].toDouble()
            acc = when (op) {
                "+" -> acc + value
                "−" -> acc - value
                "×" -> acc * value
                "÷" -> if (value != 0.0) acc / value else return "Error: div by 0"
                else -> acc
            }
            i += 2
        }
        if (acc == acc.toLong().toDouble()) acc.toLong().toString() else acc.toString()
    } catch (_: Exception) {
        "Error"
    }
}

private fun tokenize(expr: String): List<String> {
    val tokens = mutableListOf<String>()
    val current = StringBuilder()
    for (char in expr) {
        if (char in listOf('+', '−', '×', '÷')) {
            if (current.isNotEmpty()) {
                tokens.add(current.toString())
                current.clear()
            }
            tokens.add(char.toString())
        } else {
            current.append(char)
        }
    }
    if (current.isNotEmpty()) tokens.add(current.toString())
    return tokens
}
