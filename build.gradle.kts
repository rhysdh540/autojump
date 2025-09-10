@file:Suppress("UnstableApiUsage")

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import xyz.wagyourtail.unimined.api.minecraft.task.RemapJarTask
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.Deflater

plugins {
    id("xyz.wagyourtail.unimined") version("1.3.15-SNAPSHOT")
}

javaToolchains {
    launcherFor {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

repositories {
    maven("https://jitpack.io") {
        content {
            includeGroup("com.github.bawnorton.mixinsquared")
        }
    }

    unimined.legacyFabricMaven()

    flatDir { dirs("libs/runtime") }
}

base.archivesName = "archives_base_name"()
version = "mod_version"()
group = "maven_group"()

val localRuntime: Configuration by configurations.creating
val modCompileOnly: Configuration by configurations.creating

unimined.minecraft {
    version("minecraft_version"())

    runs {
        config("server") {
            enabled = false
        }
    }

    minecraftForge {
        loader("forge_version"() + "-" + version)
        atDependency = dependencies.create("net.neoforged:accesstransformers:9.0.3")
        atMainClass = "net.neoforged.accesstransformer.TransformerProcessor"
    }

    mappings {
        calamus()
        feather(28)

        stub.withMappings("searge", "intermediary") {
            c("va", listOf(
                "net/minecraft/entity/item/EntityMinecart",
                "net/minecraft/entity/vehicle/MinecartEntity"
            )) {
                m("getMaxSpeed", "()D", "m_9076954", "getMaxSpeedForge")
            }

            c("cx", listOf(
                "net/minecraft/util/RegistryNamespaced",
                "net/minecraft/util/registry/IdRegistry"
            )) {
                m("func_148757_b", "(Ljava/lang/Object;)I", "getByValue")
            }
        }
    }

    mods {
        remap(modCompileOnly)
    }
}

configurations {
    "minecraftLibraries" {
        resolutionStrategy.eachDependency {
            if(requested.group == "org.lwjgl.lwjgl") {
                useVersion("2.9.4+legacyfabric.10")
            }
            if(requested.group == "net.java.jinput" && requested.name == "jinput-platform" && requested.version == "2.0.5"
                && System.getProperty("os.name").lowercase().contains("mac")) {
                useTarget("runtime:jinput-platform:2.0.5")
            }
        }
    }

    runtimeClasspath.get().extendsFrom(localRuntime)
    compileClasspath.get().extendsFrom(modCompileOnly)
}

dependencies {
    localRuntime(fileTree("libs/runtime") { include("*.jar") })
    "com.github.bawnorton.mixinsquared:mixinsquared-fabric:0.1.1".apply {
        implementation(this)
        annotationProcessor(this)
    }

    compileOnly("org.spongepowered:mixin:0.8.7")
    compileOnly("io.github.llamalad7:mixinextras-common:0.5.0")
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("mcmod.info") {
        expand("version" to project.version)
    }
}

java {
    withSourcesJar()
}

tasks.jar {
    destinationDirectory = file("build/devlibs")
}

tasks.assemble {
    dependsOn("remapJar")
}

tasks.named<RemapJarTask>("remapJar") {
    if(System.getProperty("user.home").equals("/Users/rhys")) {
        asJar.destinationDirectory = file("/Users/rhys/games/prism/instances/pvp/minecraft/mods")
    }

    mixinRemap {
        disableRefmap()
        enableMixinExtra()
    }

    asJar.manifest.attributes(
        "MixinConfigs" to "aj.mixins.json",
        "FMLCorePluginContainsFMLMod" to true,
        "ForceLoadAsMod" to true,
        "TweakClass" to "org.spongepowered.asm.launch.MixinTweaker",
    )

    doLast {
        squishJar(asJar.archiveFile.get().asFile)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"

    if (JavaVersion.current().isJava9Compatible) {
        options.release = 8
    }
}

operator fun String.invoke(): String {
    return rootProject.ext[this] as? String
            ?: throw IllegalStateException("Property $this is not defined")
}

fun squishJar(jar: File) {
    val contents = linkedMapOf<String, ByteArray>()
    JarFile(jar).use {
        it.stream().forEach { entry ->
            if (!entry.isDirectory) {
                contents[entry.name] = it.getInputStream(entry).readAllBytes()
            }
        }
    }

    jar.delete()

    JarOutputStream(jar.outputStream()).use { out ->
        out.setLevel(Deflater.BEST_COMPRESSION)
        contents.forEach { var (name, data) = it
            if (name.endsWith(".json") || name.endsWith(".mcmeta")) {
                data = (JsonOutput.toJson(JsonSlurper().parse(data)).toByteArray())
            }

            out.putNextEntry(JarEntry(name))
            out.write(data)
            out.closeEntry()
        }
        out.finish()
        out.close()
    }
}