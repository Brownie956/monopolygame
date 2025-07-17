package com.cbmedia.monopolygame

import kotlinx.serialization.Serializable

@Serializable
enum class TaskType {
    FREQUENCY_BASED,
    TIME_BASED
}

@Serializable
enum class Difficulty(val reward: Int) {
    EASY(1),
    MEDIUM(2),
    HARD(3)
}

@Serializable
data class TaskConfig(
    val id: Int,
    val name: String,
    val taskType: TaskType,
    val difficulty: Difficulty,
    val reward: Int,
    val frequency: Int? = null,
    val durationSeconds: Int? = null,
    val isSpecialTask: Boolean = false,
    val specialTaskTimeLimit: Int? = null
)
