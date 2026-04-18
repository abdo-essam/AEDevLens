package com.ae.devlens

import com.ae.devlens.core.DevLensPlugin
import com.ae.devlens.core.PluginContext
import com.ae.devlens.core.bus.AllDataClearedEvent
import com.ae.devlens.core.bus.AppStartedEvent
import com.ae.devlens.core.bus.AppStoppedEvent
import com.ae.devlens.core.bus.EventBus
import com.ae.devlens.core.bus.PanelClosedEvent
import com.ae.devlens.core.bus.PanelOpenedEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.reflect.KClass

/**
 * AEDevLens — Extensible on-device dev tools for Kotlin Multiplatform.
 *
 * Instance-based design: testable, supports multiple instances, no hidden globals.
 *
 * ## Quick Start
 * ```kotlin
 * // Use the convenient default instance
 * val inspector = AEDevLens.default
 *
 * // Or create a custom instance
 * val inspector = AEDevLens.create(DevLensConfig(maxLogEntries = 1000))
 * ```
 *
 * ## Plugin Installation
 * ```kotlin
 * inspector.install(LogsPlugin())
 * inspector.install(MyCustomPlugin())
 * ```
 *
 * ## App Lifecycle Integration
 * Call [notifyStart] / [notifyStop] from your app's lifecycle owner
 * so plugins that do background work can react to foreground/background transitions.
 */
public class AEDevLens private constructor(
    public val config: DevLensConfig,
) {
    // ── State ─────────────────────────────────────────────────────────────────

    private val _plugins = MutableStateFlow<List<DevLensPlugin>>(emptyList())

    /** Hot stream of all currently registered plugins. */
    public val plugins: StateFlow<List<DevLensPlugin>> = _plugins.asStateFlow()

    /**
     * Shared event bus for all plugins registered to this instance.
     * Also exposed to each plugin via [PluginContext.eventBus].
     */
    public val eventBus: EventBus = EventBus()

    /** Per-plugin coroutine scopes, keyed by plugin id. */
    private val pluginScopes = mutableMapOf<String, CoroutineScope>()

    // ── Plugin Registration ───────────────────────────────────────────────────

    /**
     * Register a plugin with this inspector.
     *
     * Duplicate plugin IDs are silently ignored.
     * [DevLensPlugin.onAttach] is called synchronously after successful registration.
     */
    public fun install(plugin: DevLensPlugin) {
        installInternal(plugin)
    }

    private fun installInternal(plugin: DevLensPlugin) {
        var attached = false
        _plugins.update { current ->
            if (current.any { it.id == plugin.id }) {
                current
            } else {
                attached = true
                current + plugin
            }
        }
        if (attached) {
            val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
            pluginScopes[plugin.id] = scope
            safeCall(plugin.id) { plugin.onAttach(buildContext(scope)) }
        }
    }

    /**
     * Unregister a plugin by ID.
     *
     * The plugin's [CoroutineScope] is canceled before [DevLensPlugin.onDetach] is called.
     */
    public fun uninstall(pluginId: String) {
        var detachedPlugin: DevLensPlugin? = null
        _plugins.update { current ->
            val plugin = current.find { it.id == pluginId }
            if (plugin == null) current
            else {
                detachedPlugin = plugin
                current.filter { it.id != pluginId }
            }
        }
        detachedPlugin?.let { plugin ->
            pluginScopes.remove(plugin.id)?.cancel()
            safeCall(pluginId) { plugin.onDetach() }
        }
    }

    // ── Plugin Lookup ─────────────────────────────────────────────────────────

    /**
     * Get a plugin by type.
     * ```kotlin
     * val logs = inspector.getPlugin<LogsPlugin>()
     * ```
     */
    public inline fun <reified T : DevLensPlugin> getPlugin(): T? =
        plugins.value.filterIsInstance<T>().firstOrNull()

    /** Get a plugin by its string ID. */
    public fun getPluginById(id: String): DevLensPlugin? =
        _plugins.value.find { it.id == id }

    // ── Lifecycle Notifications ───────────────────────────────────────────────

    /**
     * Notify all plugins the host app has moved to the **foreground**.
     *
     * Call this from your app's lifecycle callback (e.g. `onStart`).
     * Publishes [AppStartedEvent] to the [eventBus].
     */
    public fun notifyStart() {
        _plugins.value.forEach { safeCall(it.id) { it.onStart() } }
        eventBus.publish(AppStartedEvent)
    }

    /**
     * Notify all plugins the host app has moved to the **background**.
     *
     * Call this from your app's lifecycle callback (e.g. `onStop`).
     * Publishes [AppStoppedEvent] to the [eventBus].
     */
    public fun notifyStop() {
        _plugins.value.forEach { safeCall(it.id) { it.onStop() } }
        eventBus.publish(AppStoppedEvent)
    }

    /**
     * Notify all plugins the DevLens UI panel has been **opened**.
     * Publishes [PanelOpenedEvent] to the [eventBus].
     */
    public fun notifyOpen() {
        _plugins.value.forEach { safeCall(it.id) { it.onOpen() } }
        eventBus.publish(PanelOpenedEvent)
    }

    /**
     * Notify all plugins the DevLens UI panel has been **closed**.
     * Publishes [PanelClosedEvent] to the [eventBus].
     */
    public fun notifyClose() {
        _plugins.value.forEach { safeCall(it.id) { it.onClose() } }
        eventBus.publish(PanelClosedEvent)
    }

    /** Clear data in all plugins and publish [AllDataClearedEvent]. */
    public fun clearAll() {
        _plugins.value.forEach { safeCall(it.id) { it.onClear() } }
        eventBus.publish(AllDataClearedEvent)
    }

    // ── Internal ──────────────────────────────────────────────────────────────

    private fun buildContext(scope: CoroutineScope): PluginContext =
        object : PluginContext {
            override val scope: CoroutineScope = scope
            override val config: DevLensConfig = this@AEDevLens.config
            override val eventBus: EventBus = this@AEDevLens.eventBus

            @Suppress("UNCHECKED_CAST")
            override fun <T : DevLensPlugin> getPlugin(type: KClass<T>): T? =
                _plugins.value.firstOrNull { type.isInstance(it) } as? T
        }

    public companion object {
        /** Convenient shared default instance for apps that only need one inspector. */
        public val default: AEDevLens by lazy { create() }

        /** Create a new isolated instance with custom configuration. */
        public fun create(config: DevLensConfig = DevLensConfig()): AEDevLens =
            AEDevLens(config)

        internal fun safeCall(pluginId: String, block: () -> Unit) {
            runCatching { block() }
                .onFailure { /* TODO: route to configurable error handler */ }
        }
    }
}
