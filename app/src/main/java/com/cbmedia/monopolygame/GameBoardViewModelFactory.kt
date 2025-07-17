package com.cbmedia.monopolygame

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GameBoardViewModelFactory(
    private val app: Application
) : ViewModelProvider.AndroidViewModelFactory(app) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GameBoardViewModel(app) as T
    }
}
