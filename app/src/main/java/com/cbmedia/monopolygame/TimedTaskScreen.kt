package com.cbmedia.monopolygame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TimedTaskScreen(
    task: TaskConfig,
    baseTime: Int,
    extraTime: Int,
    onTaskComplete: () -> Unit,
    onTaskFail: () -> Unit
) {
    var timeLeft by remember { mutableStateOf(baseTime + extraTime) }

    LaunchedEffect(key1 = timeLeft) {
        if (timeLeft > 0) {
            kotlinx.coroutines.delay(1000L)
            timeLeft -= 1
        } else {
            onTaskFail()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Complete the task before time runs out!", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text(task.name, style = MaterialTheme.typography.titleLarge)
        Text("Time left: $timeLeft seconds", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onTaskComplete) {
            Text("I've completed the task!")
        }
    }
}
