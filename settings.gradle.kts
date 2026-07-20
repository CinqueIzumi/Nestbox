import java.util.Properties

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

// Inner loop against a local foundation checkout. With `foundation.local=true` in local.properties
// the sibling repository is composed into this build and Gradle substitutes the published
// nl.rhaydus coordinates for local source, so foundation edits land here without a publish cycle.
// Absent or false, they resolve from mavenCentral() at the version libs.versions.toml pins.
val localProperties = Properties().apply {
    val file = rootDir.resolve("local.properties")
    if (file.exists()) file.inputStream().use(::load)
}

if (localProperties.getProperty("foundation.local").toBoolean()) {
    includeBuild("../rhaydus-foundation")
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
    // The foundation also publishes its shared third-party version set as `nl.rhaydus:catalog`, but
    // Nestbox deliberately pins the androidx/Compose stack ahead of it, so `libs` stays the single
    // catalog here. Adding it back is:
    //     versionCatalogs { create("rhaydus") { from("nl.rhaydus:catalog:<version>") } }
    // with the version spelled out, since a settings script cannot read the catalog it declares.
}

rootProject.name = "Nestbox"
include(":app")
