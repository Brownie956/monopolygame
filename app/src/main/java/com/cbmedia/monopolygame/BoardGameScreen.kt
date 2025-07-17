package com.cbmedia.monopolygame

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
    onWin: () -> Unit,
    onQuitGame: () -> Unit
) {
    val tasks = viewModel.shuffledTasks
    val playerPos by viewModel.playerPosition
    val targetPosition by viewModel.targetPosition
    val animatedPosition by animateIntAsState(
        targetValue = targetPosition,
        animationSpec = tween(durationMillis = 800, easing = LinearOutSlowInEasing),
        label = "Animated Player Position"
    )
    val diceValue = viewModel.diceValue
    val playerMoney by viewModel.playerMoney
    val currentTask = viewModel.currentTask.value
    val extraTimePurchased by viewModel.extraTimePurchased
    val showGameBoard by viewModel.showGameBoard
    val showDialog by viewModel.showTaskDialog
    val showTimedTaskScreen by viewModel.showTimedTaskScreen

    if (showGameBoard) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(16.dp))
            Text("Money: $playerMoney", style = MaterialTheme.typography.titleLarge)

            Spacer(Modifier.height(16.dp))

            LoopingBoardGrid(
                tasks = tasks,
                playerPosition = playerPos
            )

            Spacer(Modifier.height(32.dp))

            Row {
                DiceDisplay(
                    diceValue = diceValue,
                    onRoll = { viewModel.animateDiceRoll() }
                )
                Button(onClick = {
                    viewModel.resetGame()
                    onQuitGame()
                }) {
                    Text("End the game")
                }
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
fun LoopingBoardGrid(tasks: List<TaskConfig>, playerPosition: Int) {
    val columns = 4
    val totalTasks = tasks.size

    val rows = remember(totalTasks) {
        var r = 3
        while ((2 * r + 2 * columns - 4) < totalTasks) {
            r++
        }
        r
    }

    val loopPositions = remember(rows, columns) {
        buildList {
            for (col in 0 until columns) add(Pair(0, col)) // Top
            for (row in 1 until rows - 1) add(Pair(row, columns - 1)) // Right
            for (col in (columns - 1) downTo 0) add(Pair(rows - 1, col)) // Bottom
            for (row in (rows - 2) downTo 1) add(Pair(row, 0)) // Left
        }
    }

    val loopSize = loopPositions.size
    val taskTiles = remember(tasks, loopSize) {
        if (totalTasks >= loopSize) tasks.take(loopSize)
        else buildList {
            addAll(tasks)
            repeat(loopSize - totalTasks) {
                add(tasks.random())
            }
        }
    }

    Column {
        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (col in 0 until columns) {
                    val currentPos = Pair(row, col)
                    val index = loopPositions.indexOf(currentPos)

                    if (index != -1) {
                        // Draw Task Tile
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            TaskTile(
                                task = taskTiles[index],
                                isPlayerHere = (index == playerPosition)
                            )

                            // Add arrow pointing to the next tile
                            val nextIndex = (index + 1) % loopSize
                            val nextPos = loopPositions[nextIndex]

                            val arrow = when {
                                nextPos.first == currentPos.first && nextPos.second > currentPos.second -> "→"
                                nextPos.first > currentPos.first && nextPos.second == currentPos.second -> "↓"
                                nextPos.first == currentPos.first && nextPos.second < currentPos.second -> "←"
                                nextPos.first < currentPos.first && nextPos.second == currentPos.second -> "↑"
                                else -> ""
                            }

                            if (arrow.isNotEmpty()) {
                                Arrow(arrow)
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.size(80.dp))
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
            .size(90.dp)
            .padding(2.dp)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = task.name,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 2,
                    textAlign = TextAlign.Center
                )
                val description = if (task.taskType == TaskType.FREQUENCY_BASED) {
                    "${task.frequency} times"
                } else if (task.taskType == TaskType.TIME_BASED) {
                    "for ${task.durationSeconds}s"
                } else {
                    "at ${task.speed}bpm for ${task.durationSeconds} seconds"
                }
                if (!task.isSpecialTask) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 2,
                        textAlign = TextAlign.Center
                    )
                }
                if (isPlayerHere) {
                    Text("🧍", fontSize = 24.sp)
                }
            }
        }
    }
}

@Composable
fun Arrow(direction: String) {
    Text(
        text = direction,
        fontSize = 16.sp,
        modifier = Modifier.padding(2.dp),
        textAlign = TextAlign.Center
    )
}
