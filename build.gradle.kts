plugins {
    java
    id("xyz.wagyourtail.unimined") version("1.2.0-SNAPSHOT")
}

operator fun String.invoke(): String {
    return rootProject.ext[this] as? String
            ?: throw IllegalStateException("Property $this is not defined")
}

repositories {
    mavenCentral {
        content {
            excludeGroup("ca.weblite")
        }
    }
}

base.archivesName = "archives_base_name"()
version = "mod_version"()
group = "maven_group"()

unimined.minecraft {
    version("minecraft_version"())

    runs {
        config("server") {
            disabled = true
        }
        config("client") {
            jvmArgs += "-Dos.arch=aarch64"

        }
    }

    legacyFabric {
        loader("fabric_loader_version"())
    }

    mappings {
        legacyIntermediary()
        legacyYarn(build = "yarn_build"())
    }
}

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.lwjgl.lwjgl") {
            useVersion("2.9.4+legacyfabric.8")
        }
    }
}

dependencies {
    "modImplementation"("net.legacyfabric.legacy-fabric-api:legacy-fabric-api:${"fabric_api_version"()}+${"minecraft_version"()}")
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

    withSourcesJar()
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${base.archivesName.get()}" }
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"

    if (JavaVersion.current().isJava9Compatible) {
        options.release = 8
    }
}