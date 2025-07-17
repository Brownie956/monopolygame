package com.cbmedia.monopolygame

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WinScreen(onBackToSetup: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(32.dp)) {
        Text("🎉 You Win!", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onBackToSetup) {
            Text("Back to Setup")
        }
    }
}
