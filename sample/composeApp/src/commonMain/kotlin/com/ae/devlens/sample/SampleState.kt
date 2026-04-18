package com.ae.devlens.sample

import com.ae.devlens.plugins.analytics.AnalyticsApi
import com.ae.devlens.plugins.network.NetworkApi

/**
 * Singleton that holds plugin API references after [SampleApp.onCreate].
 *
 * Lives in commonMain so [App] (also commonMain) can access it without
 * using reified inline functions (which cause JVM target mismatches in KMP).
 *
 * APIs are `null` before init — all callers guard with `?.`.
 */
object SampleState {
    var networkApi: NetworkApi? = null
    var analyticsApi: AnalyticsApi? = null
}
