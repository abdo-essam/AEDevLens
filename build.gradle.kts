plugins {
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.androidKotlinMultiplatformLibrary) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinxSerialization) apply false
    alias(libs.plugins.dokka)
    alias(libs.plugins.binaryCompatibilityValidator)
    alias(libs.plugins.spotless)
    alias(libs.plugins.kover) apply false
}

apiValidation {
    ignoredProjects.addAll(listOf("sample", "composeApp", "devlens-core", "devlens-ui", "devlens-logs"))
}

spotless {
    kotlin {
        target("**/*.kt")
        targetExclude("**/build/**")
        ktlint("1.5.0")
            .setEditorConfigPath("$rootDir/.editorconfig")
    }
    kotlinGradle {
        target("**/*.gradle.kts")
        ktlint("1.5.0")
    }
}
