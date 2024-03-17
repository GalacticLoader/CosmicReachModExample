import de.undercouch.gradle.tasks.download.Download

object Properties {
    const val MOD_VERSION = "0.1"
    const val MOD_NAME = "Example Mod"
    const val MODID = "examplemod"
    const val MAVEN_GROUP = "com.example.examplemod"
    const val COSMIC_REACH_VERSION = "0.1.8"
    const val LOADER_VERSION = "0.15.7"
}

plugins {
    id("java")
    id("de.undercouch.download") version "5.6.0"
}

repositories {
    mavenCentral {
        content {
            excludeGroupByRegex("org.ow2.asm")
            excludeGroupByRegex("io.github.llamalad7")
        }
    }

    maven("https://repo.spongepowered.org/maven/")
    maven("https://maven.fabricmc.net/")
}

dependencies {
    implementation("com.google.guava:guava:33.0.0-jre")
    implementation("com.google.code.gson:gson:2.9.1")

    implementation("net.fabricmc:fabric-loader:0.15.7")
    implementation("net.fabricmc:tiny-mappings-parser:0.2.2.14")
    implementation("net.fabricmc:access-widener:2.1.0")
    implementation("net.fabricmc:sponge-mixin:0.12.5+mixin.0.8.5")

    implementation("org.ow2.asm:asm:9.6")
    implementation("org.ow2.asm:asm-util:9.6")
    implementation("org.ow2.asm:asm-tree:9.6")
    implementation("org.ow2.asm:asm-analysis:9.6")
    implementation("org.ow2.asm:asm-commons:9.6")
    implementation("io.github.llamalad7:mixinextras-fabric:0.3.5")

    implementation(files("$projectDir/run/cosmic-reach.jar"))
    implementation(files("$projectDir/run/loader.jar"))
}

base {
    archivesName = "${Properties.MOD_NAME}_${Properties.MOD_VERSION}-CR_${Properties.COSMIC_REACH_VERSION}"
}

val properties = mapOf(
    "version" to Properties.MOD_VERSION,
    "loader_version" to Properties.LOADER_VERSION,
    "cosmic_reach_version" to Properties.COSMIC_REACH_VERSION,
    "mod_name" to Properties.MOD_NAME,
    "modid" to Properties.MODID,
)

tasks.processResources {
    inputs.properties(properties)

    filesMatching("fabric.mod.json") {
        expand(properties)
    }
}

tasks.register<Download>("downloadCosmicReach") {
    src("https://cosmic-archive.netlify.app/Cosmic%20Reach-${Properties.COSMIC_REACH_VERSION}.jar")
    acceptAnyCertificate(true)
    dest("$projectDir/run/cosmic-reach.jar")
}

tasks.register<Download>("downloadLoader") {
    src("https://github.com/CosmicModders/ReachTheMoonLoader/releases/download/1.1.0/ReachTheMoonLoader-1.1.0.jar")
    acceptAnyCertificate(true)
    dest("$projectDir/run/loader.jar")
}

tasks.register("setupWork") {
    dependsOn("downloadCosmicReach")
    dependsOn("downloadLoader")
}

tasks.register<Copy>("moveJar") {
    from("$projectDir/build/libs/")
    into("$projectDir/run/mods")
    include("${Properties.MOD_NAME}_${Properties.MOD_VERSION}-CR_${Properties.COSMIC_REACH_VERSION}.jar")
}
