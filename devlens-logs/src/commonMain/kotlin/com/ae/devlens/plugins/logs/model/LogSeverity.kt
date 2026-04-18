package com.ae.devlens.plugins.logs.model

/**
 * Log severity levels.
 *
 * AEDevLens works with any logging library by forwarding logs to this level.
 */
public enum class LogSeverity {
    VERBOSE,
    DEBUG,
    INFO,
    WARN,
    ERROR,
    ASSERT,
    ;

    public val label: String
        get() =
            when (this) {
                VERBOSE -> "V"
                DEBUG -> "D"
                INFO -> "I"
                WARN -> "W"
                ERROR -> "E"
                ASSERT -> "A"
            }
}
