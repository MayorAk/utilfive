package com.mohaaa.utilfive.ui.converter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Locale

private enum class Mode { LENGTH, WEIGHT, TEMPERATURE, BMI }

@Composable
fun ConverterScreen(onBack: () -> Unit) {
    var mode by remember { mutableStateOf(Mode.LENGTH) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Converter & BMI") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxWidth().padding(padding).padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Mode.entries.forEach { m ->
                    FilterChip(
                        selected = mode == m,
                        onClick = { mode = m },
                        label = { Text(m.name.lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
            }
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))
            when (mode) {
                Mode.LENGTH -> UnitConverterBody(units = lengthUnitsToMeters)
                Mode.WEIGHT -> UnitConverterBody(units = weightUnitsToKg)
                Mode.TEMPERATURE -> TemperatureConverterBody()
                Mode.BMI -> BmiCalculatorBody()
            }
        }
    }
}

private val lengthUnitsToMeters = linkedMapOf(
    "Millimeters" to 0.001,
    "Centimeters" to 0.01,
    "Meters" to 1.0,
    "Kilometers" to 1000.0,
    "Inches" to 0.0254,
    "Feet" to 0.3048,
    "Miles" to 1609.34
)

private val weightUnitsToKg = linkedMapOf(
    "Grams" to 0.001,
    "Kilograms" to 1.0,
    "Pounds" to 0.453592,
    "Ounces" to 0.0283495
)

@Composable
private fun UnitConverterBody(units: Map<String, Double>) {
    var input by remember { mutableStateOf("1") }
    var fromUnit by remember { mutableStateOf(units.keys.first()) }
    var toUnit by remember { mutableStateOf(units.keys.elementAt(1)) }

    Column {
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Value") },
            modifier = Modifier.fillMaxWidth()
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))
        UnitDropdown(label = "From", options = units.keys.toList(), selected = fromUnit, onSelected = { fromUnit = it })
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
        UnitDropdown(label = "To", options = units.keys.toList(), selected = toUnit, onSelected = { toUnit = it })

        val result = input.toDoubleOrNull()?.let { value ->
            val meters = value * (units[fromUnit] ?: 1.0)
            meters / (units[toUnit] ?: 1.0)
        }

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))
        Text(
            text = if (result != null) String.format(Locale.getDefault(), "= %.4f %s", result, toUnit) else "Enter a valid number"
        )
    }
}

@Composable
private fun TemperatureConverterBody() {
    var celsiusInput by remember { mutableStateOf("0") }
    val celsius = celsiusInput.toDoubleOrNull()

    Column {
        OutlinedTextField(
            value = celsiusInput,
            onValueChange = { celsiusInput = it },
            label = { Text("Celsius") },
            modifier = Modifier.fillMaxWidth()
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))
        if (celsius != null) {
            val fahrenheit = celsius * 9.0 / 5.0 + 32.0
            val kelvin = celsius + 273.15
            Text(text = String.format(Locale.getDefault(), "= %.2f °F", fahrenheit))
            Text(text = String.format(Locale.getDefault(), "= %.2f K", kelvin))
        } else {
            Text("Enter a valid number")
        }
    }
}

@Composable
private fun BmiCalculatorBody() {
    var heightCm by remember { mutableStateOf("170") }
    var weightKg by remember { mutableStateOf("70") }

    val height = heightCm.toDoubleOrNull()
    val weight = weightKg.toDoubleOrNull()

    Column {
        OutlinedTextField(
            value = heightCm,
            onValueChange = { heightCm = it },
            label = { Text("Height (cm)") },
            modifier = Modifier.fillMaxWidth()
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))
        OutlinedTextField(
            value = weightKg,
            onValueChange = { weightKg = it },
            label = { Text("Weight (kg)") },
            modifier = Modifier.fillMaxWidth()
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))

        if (height != null && weight != null && height > 0) {
            val heightM = height / 100.0
            val bmi = weight / (heightM * heightM)
            val category = when {
                bmi < 18.5 -> "Underweight"
                bmi < 25.0 -> "Normal weight"
                bmi < 30.0 -> "Overweight"
                else -> "Obese"
            }
            Text(text = String.format(Locale.getDefault(), "BMI = %.1f", bmi))
            Text(text = category)
        } else {
            Text("Enter valid height and weight")
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun UnitDropdown(
    label: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        androidx.compose.material3.ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
