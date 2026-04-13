-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

# Keep public API
-keep class com.ae.devlens.AEDevLens { *; }
-keep class com.ae.devlens.AEDevLensProviderKt { *; }
-keep class com.ae.devlens.core.** { *; }
-keep class com.ae.devlens.plugins.logs.model.** { *; }
-keep class com.ae.devlens.plugins.logs.store.LogStore { *; }
-keep class com.ae.devlens.plugins.logs.LogsPlugin { *; }

# Keep plugin interfaces for consumers
-keep interface com.ae.devlens.core.DevLensPlugin { *; }
-keep interface com.ae.devlens.core.UIPlugin { *; }
-keep interface com.ae.devlens.core.DataPlugin { *; }

# Kotlinx serialization
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keep class kotlinx.serialization.** { *; }
