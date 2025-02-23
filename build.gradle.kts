plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.kotlin.ksp) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

tasks.register("clean", Delete::class) {
    delete(project.layout.buildDirectory)
}