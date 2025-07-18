package com.cbmedia.monopolygame

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val Context.dataStore by preferencesDataStore("task_config_store")

object TaskConfigStore {
    private val TASK_LIST_KEY = stringPreferencesKey("task_list_json")
    private val TASK_LISTS_KEY = stringPreferencesKey("named_task_lists")

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

    fun getNamedTaskLists(context: Context): Flow<NamedTaskLists> =
        context.dataStore.data
            .map { prefs ->
                val json = prefs[TASK_LISTS_KEY] ?: return@map NamedTaskLists()
                try {
                    Json.decodeFromString<NamedTaskLists>(json)
                } catch (e: Exception) {
                    NamedTaskLists()
                }
            }

    suspend fun saveTasks(context: Context, tasks: List<TaskConfig>) {
        val json = Json.encodeToString(ListSerializer(TaskConfig.serializer()), tasks)
        context.dataStore.edit { prefs ->
            prefs[TASK_LIST_KEY] = json
        }
    }

    suspend fun saveNamedTaskList(context: Context, name: String, tasks: List<TaskConfig>) {
        val current = getNamedTaskLists(context).firstOrNull() ?: NamedTaskLists()
        val updated = current.copy(lists = current.lists + (name to tasks))
        val json = Json.encodeToString(updated)
        context.dataStore.edit { prefs ->
            prefs[TASK_LISTS_KEY] = json
        }
    }


    suspend fun deleteNamedTaskList(context: Context, name: String) {
        val current = getNamedTaskLists(context).firstOrNull() ?: return
        val updated = current.copy(lists = current.lists - name)
        val json = Json.encodeToString(updated)
        context.dataStore.edit { prefs ->
            prefs[TASK_LISTS_KEY] = json
        }
    }
}
