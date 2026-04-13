package com.ae.devlens.plugins.logs.store

import com.ae.devlens.plugins.logs.model.LogEntry
import com.ae.devlens.plugins.logs.model.LogFilter
import com.ae.devlens.plugins.logs.model.LogLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Thread-safe log storage.
 *
 * Each log emits immediately to the StateFlow — no batching, no delay.
 * Safe to call from any thread or coroutine context.
 */
class LogStore(private val maxEntries: Int = 500) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _logsFlow = MutableStateFlow<List<LogEntry>>(emptyList())
    val logsFlow: StateFlow<List<LogEntry>> = _logsFlow.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow(LogFilter.ALL)
    val selectedFilter: StateFlow<LogFilter> = _selectedFilter.asStateFlow()

    fun log(level: LogLevel, tag: String, message: String) {
        val entry = LogEntry(level = level, tag = tag, message = message)
        _logsFlow.update { current ->
            val updated = current.toMutableList()
            updated.add(entry)
            if (updated.size > maxEntries) {
                updated.removeAt(0)
            }
            updated
        }
    }

    fun updateSearchQuery(query: String) { _searchQuery.value = query }
    fun updateSelectedFilter(filter: LogFilter) { _selectedFilter.value = filter }

    fun clear() {
        _logsFlow.value = emptyList()
    }

    fun destroy() {
        scope.cancel()
    }
}
