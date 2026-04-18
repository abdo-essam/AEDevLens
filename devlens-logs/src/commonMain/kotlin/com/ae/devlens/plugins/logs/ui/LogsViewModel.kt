package com.ae.devlens.plugins.logs.ui

import com.ae.devlens.plugins.logs.model.LogEntry
import com.ae.devlens.plugins.logs.model.LogFilter
import com.ae.devlens.plugins.logs.store.LogStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/**
 * Holds UI state for the log viewer — search query, active filter, and derived filtered list.
 *
 * Created inside [LogsPlugin] using the plugin's managed [CoroutineScope],
 * so it is automatically cancelled when the plugin is detached.
 *
 * @param logStore  The underlying data source.
 * @param scope     Coroutine scope for combining flows (must outlive the UI).
 */
public class LogsViewModel(
    private val logStore: LogStore,
    scope: CoroutineScope,
) {
    private val _searchQuery = MutableStateFlow("")
    public val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow(LogFilter.ALL)
    public val selectedFilter: StateFlow<LogFilter> = _selectedFilter.asStateFlow()

    /**
     * Reactive filtered list derived from the log store + active search + filter.
     *
     * Combined in the plugin scope — no recomposition-triggered recomputation.
     */
    public val filteredLogs: StateFlow<List<LogEntry>> = combine(
        logStore.logsFlow,
        _searchQuery,
        _selectedFilter,
    ) { logs, query, filter ->
        logs
            .filter { entry ->
                when (filter) {
                    LogFilter.ALL -> true
                    LogFilter.NETWORK -> entry.isNetworkLog
                    LogFilter.ANALYTICS -> entry.isAnalytics
                }
            }
            .filter { entry ->
                query.isBlank() ||
                    entry.message.contains(query, ignoreCase = true) ||
                    entry.tag.contains(query, ignoreCase = true)
            }
    }.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList(),
    )

    public fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    public fun updateSelectedFilter(filter: LogFilter) {
        _selectedFilter.value = filter
    }
}
