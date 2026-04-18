package com.ae.devlens

import com.ae.devlens.plugins.logs.LogsPlugin

/**
 * Creates a new [AEDevLens] instance with [LogsPlugin] pre-installed.
 *
 * This is the recommended setup for most apps. For custom plugin configurations,
 * use [AEDevLens.create] and call [AEDevLens.install] manually.
 *
 * ```kotlin
 * val inspector = AEDevLens.createDefault()
 * ```
 */
public fun AEDevLens.Companion.createDefault(
    config: DevLensConfig = DevLensConfig(),
): AEDevLens = create(config).also { it.install(LogsPlugin(maxEntries = config.maxLogEntries)) }
