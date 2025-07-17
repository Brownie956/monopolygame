package com.cbmedia.monopolygame

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val Context.dataStore by preferencesDataStore("task_config_store")

object TaskConfigStore {
    private val TASK_LIST_KEY = stringPreferencesKey("task_list_json")

    fun getTasks(context: Context): Flow<List<TaskConfig>> {
        return context.dataStore.data.map { preferences ->
            preferences[TASK_LIST_KEY]?.let {
                try {
                    Json.decodeFromString(ListSerializer(TaskConfig.serializer()), it)
                } catch (e: Exception) {
                    emptyList()
                }
            } ?: emptyList()
        }
    }

    suspend fun saveTasks(context: Context, tasks: List<TaskConfig>) {
        val json = Json.encodeToString(ListSerializer(TaskConfig.serializer()), tasks)
        context.dataStore.edit { prefs ->
            prefs[TASK_LIST_KEY] = json
        }
    }

    suspend fun clearTasks(context: Context) {
        context.dataStore.edit { prefs ->
            prefs.remove(TASK_LIST_KEY)
        }
    }
}
