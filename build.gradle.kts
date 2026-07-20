// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

// The shared nl.rhaydus ktlint ruleset, resolved onto its own classpath and driven through ktlint's
// rule-engine — no Spotless or ktlint-gradle plugin, so the ktlint version stays the foundation's.
// `ktlintFormat` auto-fixes the mechanizable layout rules; `ktlintCheck` gates on everything,
// including the structural rules that have no auto-fix. `-Pktlint.root=<dir>` scopes a run.
val ktlintRuleset: Configuration by configurations.creating

dependencies {
    ktlintRuleset(libs.rhaydus.ktlint.rules)
}

val ktlintScanRoot: String = (findProperty("ktlint.root") as String?) ?: rootDir.absolutePath

tasks.register<JavaExec>("ktlintFormat") {
    group = "formatting"
    description = "Auto-fixes the mechanizable layout rules (arg wrapping, trailing commas, blank lines)."
    classpath = ktlintRuleset
    mainClass.set("nl.rhaydus.ktlint.MainKt")
    args("format", ktlintScanRoot)
}

val ktlintCheck = tasks.register<JavaExec>("ktlintCheck") {
    group = "verification"
    description = "Gates on all custom-ruleset violations (layout + structural rules)."
    classpath = ktlintRuleset
    mainClass.set("nl.rhaydus.ktlint.MainKt")
    args("check", ktlintScanRoot)
}

// Style is part of verification, so `./gradlew check` fails on it rather than needing a separate call.
allprojects {
    tasks.matching { it.name == "check" }.configureEach {
        dependsOn(ktlintCheck)
    }
}
