package com.cbmedia.monopolygame

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SpecialTaskDialog(
    task: TaskConfig,
    playerMoney: Int,
    onStartTask: (extraTimeSec: Int) -> Unit,
    onSkipTask: () -> Unit
) {
    var extraTime by remember { mutableStateOf(0) }
    val maxExtraTime = playerMoney // max seconds you can buy, tweak as needed
    val costPerSecond = 1 // cost per second to buy more time

    AlertDialog(
        onDismissRequest = {},
        title = { Text("Special Task!") },
        text = {
            Column {
                Text("Task: ${task.name}")
                Text("You have ${task.specialTaskTimeLimit ?: 30} seconds")

                Spacer(modifier = Modifier.height(8.dp))

                if (playerMoney > 0) {
                    Text("Buy extra time? Each second costs $costPerSecond point.")
                    Slider(
                        value = extraTime.toFloat(),
                        onValueChange = {
                            val intVal = it.toInt()
                            if (intVal * costPerSecond <= playerMoney) {
                                extraTime = intVal
                            }
                        },
                        valueRange = 0f..maxExtraTime.toFloat(),
                        steps = playerMoney
                    )
                    Text("Extra time: $extraTime seconds (cost: ${extraTime * costPerSecond} points)")
                } else {
                    Text("You don't have any points to spend!")
                }

            }
        },
        confirmButton = {
            Button(onClick = { onStartTask(extraTime) }) {
                Text("Start Task")
            }
        },
        dismissButton = {
            Button(onClick = onSkipTask) {
                Text("Skip Task")
            }
        }
    )
}
