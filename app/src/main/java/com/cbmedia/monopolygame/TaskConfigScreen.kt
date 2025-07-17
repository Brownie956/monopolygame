package com.cbmedia.monopolygame

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TaskConfigScreen(
    tasks: List<TaskConfig>,
    onAddTask: (TaskConfig) -> Unit,
    onDeleteAll: () -> Unit,
    onStartGame: () -> Unit
) {
    var taskName by remember { mutableStateOf("") }
    var taskType by remember { mutableStateOf(TaskType.FREQUENCY_BASED) }
    var frequency by remember { mutableIntStateOf(1) }
    var duration by remember { mutableIntStateOf(10) }
    var difficulty by remember { mutableStateOf(Difficulty.MEDIUM) }
    var isSpecialTask by remember { mutableStateOf(false) }
    var isRandomAmount by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Create Task", style = MaterialTheme.typography.titleLarge)

        DropdownMenuSelector(
            label = "Task Type",
            options = TaskType.entries,
            selected = taskType.toString(),
            onSelect = { taskType = enumValueOf(it) }
        )

        OutlinedTextField(
            value = taskName,
            onValueChange = { taskName = it },
            label = { Text("Task") }
        )

        Row {
            if (taskType == TaskType.FREQUENCY_BASED) {
                OutlinedTextField(
                    value = frequency.toString(),
                    onValueChange = { frequency = it.toInt() },
                    label = { Text("How many times?") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            } else {
                OutlinedTextField(
                    value = duration.toString(),
                    onValueChange = { duration = it.toInt() },
                    label = { Text("For how long?") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            Column(modifier = Modifier.padding(start = 18.dp)) {
                Text("Random?")
                Checkbox(
                    checked = isRandomAmount,
                    onCheckedChange = { isRandomAmount = it }
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        DropdownMenuSelector(
            label = "Difficulty",
            options = Difficulty.entries,
            selected = difficulty.toString(),
            onSelect = { difficulty = enumValueOf(it) }
        )

        Row {
            Checkbox(
                checked = isSpecialTask,
                onCheckedChange = { isSpecialTask = it }
            )
            Text(
                "Mark as end game task?",
                modifier = Modifier
                    .padding(start = 8.dp, top = 16.dp)
            )
        }

        Button(onClick = {

            if (isRandomAmount) {
                if (taskType == TaskType.FREQUENCY_BASED) {
                    frequency = (1..15).random()
                } else {
                    duration = ((10..60).random() + 2) / 5 * 5 //Round to the nearest 5
                }
            }

            val newTask = TaskConfig(
                id = tasks.size + 1,
                name = taskName,
                taskType = taskType,
                difficulty = difficulty,
                reward = difficulty.reward,
                frequency = frequency,
                durationSeconds = duration,
                isSpecialTask = isSpecialTask
            )
            onAddTask(newTask)
        }) {
            Text("Add Task")
        }

        Spacer(Modifier.height(16.dp))

        Text("Your Tasks", style = MaterialTheme.typography.titleMedium)
        tasks.forEach { task ->
            val postfix = if (task.taskType == TaskType.FREQUENCY_BASED) {
                "${task.frequency} time(s)"
            } else {
                "for ${task.durationSeconds} seconds"
            }
            Text(
                "- ${task.name} ${postfix} - Reward: ${task.reward} points",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Button(onClick = onDeleteAll, colors = ButtonDefaults.buttonColors(Color.Red)) {
            Text("Delete All Tasks")
        }

        Button(
            onClick = onStartGame,
            modifier = Modifier
        ) {
            Text("Start Game")
        }
    }
}

@Preview
@Composable
fun PreviewTaskConfigScreen() {
    val dummyTasks = List(10) {
        TaskConfig(
            id = it,
            name = "Task $it",
            taskType = if (it % 2 == 0) TaskType.FREQUENCY_BASED else TaskType.TIME_BASED,
            difficulty = Difficulty.MEDIUM,
            reward = 100
        )
    }

    TaskConfigScreen(
        tasks = dummyTasks,
        onAddTask = {},
        onDeleteAll = {},
        onStartGame = {}
    )
}
