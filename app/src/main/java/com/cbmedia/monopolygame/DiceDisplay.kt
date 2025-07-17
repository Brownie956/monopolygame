package com.cbmedia.monopolygame

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

@Composable
fun DiceDisplay(diceValue: Int, onRoll: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "🎲 $diceValue",
            style = MaterialTheme.typography.headlineLarge
        )
        Button(onClick = onRoll) {
            Text("Roll Dice")
        }
    }
}
