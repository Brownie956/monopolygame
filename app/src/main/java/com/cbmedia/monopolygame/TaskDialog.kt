package com.cbmedia.monopolygame

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun TaskDialog(
    task: TaskConfig,
    onCompleteTask: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton( onClick = { onCompleteTask() }) {
                Text("Complete Task (+£${task.reward})")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Dismiss")
            }
        },
        title = { Text(task.name) },
    )
}
