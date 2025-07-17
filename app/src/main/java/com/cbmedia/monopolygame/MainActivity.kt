package com.cbmedia.monopolygame

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MonopolyGameApp()
        }
    }
}

@Composable
fun MonopolyGameApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val gameViewModel: GameBoardViewModel = viewModel(
        factory = GameBoardViewModelFactory(context.applicationContext as Application)
    )

    NavHost(navController = navController, startDestination = Screen.Setup.route) {
        composable(Screen.Setup.route) {
            TaskConfigScreen(
                tasks = gameViewModel.tasks,
                onAddTask = { gameViewModel.addTask(it) },
                onDeleteAll = { gameViewModel.clearTasks() },
                onStartGame = {
                    gameViewModel.shuffleTasks()
                    navController.navigate(Screen.Game.route)
                }
            )
        }
        composable(Screen.Game.route) {
            GameBoardScreen(
                viewModel = gameViewModel,
                onWin = {
                    navController.navigate(Screen.Win.route) {
                        popUpTo(Screen.Game.route) { inclusive = true }
                    }
                },
                onQuitGame = { navController.navigate(Screen.Setup.route) }
            )
        }
        composable(Screen.Win.route) {
            WinScreen {
                navController.popBackStack(Screen.Setup.route, inclusive = false)
            }
        }
    }
}

