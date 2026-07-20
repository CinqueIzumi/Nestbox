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

// The shared detekt baseline plus the custom `rhaydus` ruleset, run through the detekt CLI for the same
// reason as ktlint above: no Gradle plugin, so the detekt version stays ours. The ruleset jar carries
// both the rules (found by ServiceLoader) and the baseline config, so `--config-resource` reads the
// config straight off the classpath and nothing has to be extracted or vendored.
//
// This is a syntactic run: detekt's type resolution would need the full Android compile classpath wired
// per variant. The one rule in the ruleset that needs types (UnguardedFlowTerminalRead, for unguarded
// Flow.first()/single()) is therefore inert here; it has no targets in Nestbox today anyway. Everything
// in the shared baseline is syntactic and does gate.
val detektCli: Configuration by configurations.creating
val detektRuleset: Configuration by configurations.creating

dependencies {
    detektCli(libs.detekt.cli)
    detektRuleset(libs.rhaydus.detekt.rules)
}

val detektScanRoot: String = (findProperty("detekt.root") as String?) ?: rootDir.absolutePath

// The baseline is unpacked from the jar rather than read with `--config-resource`, because detekt only
// LAYERS configs given together in one comma-separated `--config` list (later files override earlier).
// Mixing `--config-resource` with `--config` makes the overlay replace the baseline instead of refining
// it, which silently re-enables everything the baseline tunes down (MagicNumber, Composable naming).
val detektConfigDir = layout.buildDirectory.dir("rhaydus-detekt")

val extractDetektBaseline = tasks.register<Copy>("extractDetektBaseline") {
    description = "Unpacks the shared detekt baseline out of the nl.rhaydus:detekt-rules jar."
    from({ zipTree(detektRuleset.filter { it.name.startsWith("detekt-rules") }.singleFile) }) {
        include("config/detekt.yml")
    }
    into(detektConfigDir)
}

val detektCheck = tasks.register<JavaExec>("detektCheck") {
    group = "verification"
    description = "Runs the shared detekt baseline + rhaydus ruleset over the sources (syntactic)."
    dependsOn(extractDetektBaseline)
    classpath = detektCli + detektRuleset
    mainClass.set("io.gitlab.arturbosch.detekt.cli.Main")
    args(
        "--input",
        detektScanRoot,
        "--config",
        listOf(
            detektConfigDir.get().file("config/detekt.yml").asFile.absolutePath,
            // Refines the baseline; only deviations that are genuinely Nestbox's belong in it.
            file("config/detekt-overrides.yml").absolutePath,
        ).joinToString(separator = ","),
        "--build-upon-default-config",
        "--excludes",
        // Generated output and Gradle build scripts are not app source; test sources are excluded to
        // match how the foundation runs the same baseline over itself.
        "**/build/**,**/.gradle/**,**/*.gradle.kts,**/src/*[Tt]est*/**",
    )
}

// Style and static analysis are part of verification, so `./gradlew check` fails on them rather than
// needing separate calls.
allprojects {
    tasks.matching { it.name == "check" }.configureEach {
        dependsOn(ktlintCheck, detektCheck)
    }
}
