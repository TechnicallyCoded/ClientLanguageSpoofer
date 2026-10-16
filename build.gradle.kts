plugins {
    id("net.fabricmc.fabric-loom") version "1.17.21"
}
version = "1.0.2"
group = "fr.hyping"
base { archivesName.set("ClientLanguageSpoofer") }
loom { accessWidenerPath.set(file("src/main/resources/clientlanguagespoofer.accesswidener")) }
repositories { mavenCentral() }
dependencies {
    minecraft("com.mojang:minecraft:26.3")
    implementation("net.fabricmc:fabric-loader:0.19.5")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
    withSourcesJar()
}
tasks.withType<JavaCompile>().configureEach { options.release.set(25) }
tasks.processResources {
    inputs.property("version", project.version)
    filesMatching("fabric.mod.json") { expand("version" to project.version) }
}
tasks.test { useJUnitPlatform() }
