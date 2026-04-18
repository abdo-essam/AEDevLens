package com.ae.devlens

/**
 * Configuration for a [AEDevLens] instance.
 *
 * Contains only core (non-UI) settings. UI-specific config lives in
 * `DevLensUiConfig` in the `devlens-ui` module.
 *
 * ```kotlin
 * AEDevLens.create(DevLensConfig(maxLogEntries = 1000))
 * ```
 */
public data class DevLensConfig(
    /** Maximum number of log entries to keep in memory (default: 500) */
    val maxLogEntries: Int = 500,
)
