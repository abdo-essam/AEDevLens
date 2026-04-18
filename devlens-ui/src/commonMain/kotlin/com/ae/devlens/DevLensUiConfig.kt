package com.ae.devlens

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.Alignment

/**
 * UI-specific configuration for the DevLens overlay.
 *
 * Passed to [AEDevLensProvider] separately from the core [DevLensConfig],
 * keeping Compose types out of the `devlens-core` module.
 *
 * ```kotlin
 * AEDevLensProvider(
 *     inspector = inspector,
 *     uiConfig = DevLensUiConfig(
 *         showFloatingButton = true,
 *         presentationMode = PresentationMode.Adaptive,
 *     ),
 * ) { ... }
 * ```
 */
public data class DevLensUiConfig(
    /** Show the floating debug button overlay (default: true) */
    val showFloatingButton: Boolean = true,
    /** Floating button screen position (default: BottomEnd) */
    val floatingButtonAlignment: Alignment = Alignment.BottomEnd,
    /** Enable long-press anywhere on screen to open the panel (default: true) */
    val enableLongPress: Boolean = true,
    /**
     * Custom Material3 [ColorScheme] for the DevLens UI.
     * `null` uses the built-in brand theme.
     */
    val colorScheme: ColorScheme? = null,
    /**
     * How the DevLens panel is presented on screen.
     * Defaults to [PresentationMode.Adaptive] (bottom sheet on phone, dialog on tablet).
     */
    val presentationMode: PresentationMode = PresentationMode.Adaptive,
)

/**
 * Controls how the DevLens panel container is displayed.
 */
public enum class PresentationMode {
    /** Bottom sheet on compact screens, centered dialog on large screens (default). */
    Adaptive,
    /** Always use a bottom sheet, regardless of screen size. */
    BottomSheet,
    /** Always use a centered dialog, regardless of screen size. */
    Dialog,
}
