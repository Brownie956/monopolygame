package com.cbmedia.monopolygame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun NumericalInput(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Value",
    minValue: Int = 0,
    maxValue: Int = Int.MAX_VALUE,
    step: Int = 1,
    enabled: Boolean = true,
    suffix: @Composable (() -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value.toString(),
            onValueChange = {
                val newValue = it.toIntOrNull()
                if (newValue != null && newValue in minValue..maxValue) {
                    onValueChange(newValue)
                }
            },
            label = { Text(label) },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            enabled = enabled,
            suffix = suffix,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
        ) {
            Button(
                onClick = {
                    val newValue = (value - step).coerceAtLeast(minValue)
                    onValueChange(newValue)
                },
                enabled = enabled
            ) {
                Text("−")
            }

            Button(
                onClick = {
                    val newValue = (value + step).coerceAtMost(maxValue)
                    onValueChange(newValue)
                },
                enabled = enabled
            ) {
                Text("+")
            }
        }
    }
}
