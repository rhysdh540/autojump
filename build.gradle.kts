import com.google.gson.Gson
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import xyz.wagyourtail.unimined.api.minecraft.task.RemapJarTask
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.Deflater

plugins {
    java
    id("xyz.wagyourtail.unimined") version("1.2.0-SNAPSHOT")
}

javaToolchains {
    launcherFor {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

repositories {
    mavenCentral {
        content {
            excludeGroup("ca.weblite")
        }
    }

    maven("https://jitpack.io") {
        content {
            includeGroup("com.github.bawnorton.mixinsquared")
        }
    }
}

base.archivesName = "archives_base_name"()
version = "mod_version"()
group = "maven_group"()

val localRuntime: Configuration by configurations.creating
val modCompileOnly: Configuration by configurations.creating

configurations {
    all {
        resolutionStrategy.eachDependency {
            if(requested.group == "org.lwjgl.lwjgl") {
                useVersion("2.9.4+legacyfabric.8")
            }
            exclude(group = "net.java.jinput", module = "jinput-platform")
        }
    }

    runtimeClasspath.get().extendsFrom(localRuntime)
    compileClasspath.get().extendsFrom(modCompileOnly)
}

unimined.minecraft {
    version("minecraft_version"())

    runs {
        config("server") {
            disabled = true
        }
    }

    legacyFabric {
        loader("fabric_loader_version"())
    }

    mappings {
        legacyIntermediary()
        legacyYarn(build = "yarn_build"())
    }

    mods {
        remap(modCompileOnly)
    }
}

dependencies {
    "modImplementation"("net.legacyfabric.legacy-fabric-api:legacy-fabric-api:${"fabric_api_version"()}+${"minecraft_version"()}")

    modCompileOnly(fileTree("libs/compile") { include("*.jar") })
    localRuntime(fileTree("libs/runtime") { include("*.jar") })
    "com.github.bawnorton.mixinsquared:mixinsquared-fabric:0.1.1".apply {
        implementation(this)
        annotationProcessor(this)
        "include"(this)
    }
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
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
    doLast {
        squishJar(archiveFile.get().asFile)
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
        it.entries().asIterator().forEach { entry ->
            if (!entry.isDirectory) {
                contents[entry.name] = it.getInputStream(entry).readAllBytes()
            }
        }
    }

    jar.delete()

    JarOutputStream(jar.outputStream()).use { out ->
        out.setLevel(Deflater.BEST_COMPRESSION)
        contents.forEach { var (name, data) = it
            if(name.contains("refmap"))
                return@forEach

            if(name.endsWith(".mixins.json")) {
                val json = Gson().fromJson(data.decodeToString(), Map::class.java) as MutableMap<*, *>
                json.remove("refmap")
                data = (JsonOutput.toJson(json).toByteArray())
            }

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