package com.ae.devlens

internal actual class Platform actual constructor() {
    actual val name: String = "Desktop JVM (${System.getProperty("os.name")})"
}
