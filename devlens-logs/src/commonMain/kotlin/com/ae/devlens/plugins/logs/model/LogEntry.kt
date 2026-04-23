package com.ae.devlens.plugins.logs.model

import kotlin.time.Clock

import androidx.compose.runtime.Immutable

/**
 * Represents a single log entry captured by AEDevLens.
 *
 * This is a simple data holder. Parsing and classification logic
 * are provided via extension properties in LogClassifier.
 */
@Immutable
public data class LogEntry(
    val id: String = com.ae.devlens.core.utils.IdGenerator.generateId(),
    val severity: LogSeverity,
    val tag: String,
    val message: String,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
)
