package com.cbmedia.monopolygame

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.ceil

@Composable
fun GameBoardScreen(
    viewModel: GameBoardViewModel,
    onWin: () -> Unit
) {
    val tasks = viewModel.tasks
    val playerPos by viewModel.playerPosition
    val playerMoney by viewModel.playerMoney
    val currentTask = viewModel.currentTask.value
    val extraTimePurchased by viewModel.extraTimePurchased
    val showGameBoard by viewModel.showGameBoard
    val showDialog by viewModel.showTaskDialog
    val showTimedTaskScreen by viewModel.showTimedTaskScreen

    if (showGameBoard) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Game Board", style = MaterialTheme.typography.titleLarge)
            Text("Money: $playerMoney", style = MaterialTheme.typography.titleLarge)

            Spacer(Modifier.height(16.dp))

            BoardGrid(
                tasks = tasks,
                playerPosition = playerPos
            )

            Spacer(Modifier.height(32.dp))

            Button(onClick = {
                viewModel.rollDice()
            }) {
                Text("Roll Dice")
            }
        }
    }

    if (currentTask != null) {
        if (showDialog) {
            if (!currentTask.isSpecialTask) {
                TaskDialog(
                    task = currentTask,
                    onDismiss = { viewModel.setShowTaskDialog(false) },
                    onCompleteTask = { viewModel.completeTask() }
                )
            } else {
                SpecialTaskDialog(
                    task = currentTask,
                    playerMoney = playerMoney,
                    onStartTask = { extraTime ->
                        viewModel.spendMoney(extraTime)
                        viewModel.setShowTaskDialog(false)
                        viewModel.setShowTimedTaskScreen(true)
                    },
                    onSkipTask = {
                        viewModel.setShowTaskDialog(false)
                    }
                )
            }
        } else if (showTimedTaskScreen) {
            viewModel.setShowGameBoard(false)

            TimedTaskScreen(
                task = currentTask,
                baseTime = currentTask.specialTaskTimeLimit ?: 30,
                extraTime = extraTimePurchased,
                onTaskComplete = {
                    viewModel.setShowTimedTaskScreen(false)
                    viewModel.resetGame()
                    onWin()
                },
                onTaskFail = {
                    viewModel.setShowGameBoard(true)
                    viewModel.setShowTimedTaskScreen(false)
                }
            )
        }
    }
}

@Composable
fun BoardGrid(tasks: List<TaskConfig>, playerPosition: Int) {
    val columns = 4 // Adjust for your board shape
    val rows = ceil(tasks.size / columns.toDouble()).toInt()

    Column {
        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                for (col in 0 until columns) {
                    val index = row * columns + col
                    if (index < tasks.size) {
                        val task = tasks[index]
                        TaskTile(
                            task = task,
                            isPlayerHere = (index == playerPosition)
                        )
                    } else {
                        Spacer(modifier = Modifier.size(64.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TaskTile(task: TaskConfig, isPlayerHere: Boolean) {
    val borderColor = if (isPlayerHere) Color.Green else Color.Gray

    Card(
        modifier = Modifier
            .size(80.dp)
            .padding(4.dp)
            .border(2.dp, borderColor, RoundedCornerShape(8.dp)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = task.name.take(10),
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 2,
                    textAlign = TextAlign.Center
                )
                if (isPlayerHere) {
                    Text("🧍", fontSize = 24.sp)
                }
            }
        }
    }
}

