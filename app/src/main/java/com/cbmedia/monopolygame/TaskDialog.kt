package com.cbmedia.monopolygame

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun TaskDialog(
    task: TaskConfig,
    onCompleteTask: () -> Unit,
    onDismiss: () -> Unit,
) {
    val description = if (task.taskType == TaskType.FREQUENCY_BASED) {
        "${task.frequency} times"
    } else if (task.taskType == TaskType.TIME_BASED) {
        "for ${task.durationSeconds}s"
    } else {
        "at ${task.speed}bpm for ${task.durationSeconds} seconds"
    }

    if (task.taskType == TaskType.FREQUENCY_BASED) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                TextButton( onClick = { onCompleteTask() }) {
                    Text("Complete Task (+${task.reward} points)")
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text("Dismiss")
                }
            },
            title = { Text("${task.name} $description") },
        )
    } else {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                TextButton( onClick = { onCompleteTask() }) {
                    Text("Complete Task (+${task.reward} points)")
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text("Dismiss")
                }
            },
            title = { Text("${task.name} $description") },
            text = {
                CountdownTimer(task.durationSeconds ?: 0)
            }
        )
    }
}
