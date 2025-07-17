package com.cbmedia.monopolygame

sealed class Screen(val route: String) {
    object Setup : Screen("setup")
    object Game : Screen("game")
    object Win : Screen("win")
}
