package com.cbmedia.monopolygame

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf

class GameBoardViewModel(application: Application) : AndroidViewModel(application) {

    private val _tasks = mutableStateListOf<TaskConfig>()
    val tasks: List<TaskConfig> get() = _tasks

    private val _playerPosition = mutableIntStateOf(0)
    val playerPosition: State<Int> = _playerPosition

    private val _currentTask = mutableStateOf<TaskConfig?>(null)
    val currentTask: State<TaskConfig?> = _currentTask

    private val _showGameBoard = mutableStateOf(true)
    val showGameBoard: State<Boolean> = _showGameBoard

    private val _showTaskDialog = mutableStateOf(false)
    val showTaskDialog: State<Boolean> = _showTaskDialog

    private val _showTimedTaskScreen = mutableStateOf(false)
    val showTimedTaskScreen: State<Boolean> = _showTimedTaskScreen

    private val _playerMoney = mutableIntStateOf(0) // starting money
    val playerMoney: State<Int> = _playerMoney

    private val _extraTimePurchased = mutableIntStateOf(0)
    val extraTimePurchased: State<Int> = _extraTimePurchased


    init {
        viewModelScope.launch {
            TaskConfigStore.getTasks(getApplication()).collect { loadedTasks ->
                _tasks.clear()
                _tasks.addAll(loadedTasks)
            }
        }
    }

    fun addTask(task: TaskConfig) {
        _tasks.add(task)
        saveTasks()
    }

    fun clearTasks() {
        _tasks.clear()
        saveTasks()
    }

    private fun saveTasks() {
        viewModelScope.launch {
            TaskConfigStore.saveTasks(getApplication(), _tasks)
        }
    }

    fun rollDice() {
        val diceRoll = (1..6).random()
        _playerPosition.intValue = (_playerPosition.intValue + diceRoll) % tasks.size
        val landedTask = tasks.getOrNull(_playerPosition.intValue)
        if (landedTask != null) {
            _currentTask.value = landedTask
            setShowTaskDialog(true)
        }
    }

    fun completeTask() {
        _currentTask.value?.let {
            _playerMoney.intValue += it.reward
        }
        setShowTaskDialog(false)
    }

    fun setShowGameBoard(showGameBoard: Boolean) {
        _showGameBoard.value = showGameBoard
    }

    fun setShowTaskDialog(showTaskDialog: Boolean) {
        _showTaskDialog.value = showTaskDialog
    }

    fun setShowTimedTaskScreen(showTimedTaskScreen: Boolean) {
        _showTimedTaskScreen.value = showTimedTaskScreen
    }

    fun resetGame() {
        _playerPosition.intValue = 0
        _playerMoney.intValue = 0
        _extraTimePurchased.intValue = 0
        _currentTask.value = null
        _showGameBoard.value = true
        setShowTaskDialog(false)
        setShowTimedTaskScreen(false)
    }

    fun spendMoney(amount: Int) {
        _playerMoney.intValue = (_playerMoney.intValue - amount).coerceAtLeast(0)
        _extraTimePurchased.intValue = amount
    }
}
