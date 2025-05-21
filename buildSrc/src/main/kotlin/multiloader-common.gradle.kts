plugins {
    id("java-library")
    id("maven-publish")
}

base {
    archivesName = "${"mod_id"()}-${project.name}-${"minecraft_version"()}"
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of("java_version"())
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
    // https://docs.gradle.org/current/userguide/declaring_repositories.html#declaring_content_exclusively_found_in_one_repository
    exclusiveContent {
        forRepository {
            maven("https://repo.spongepowered.org/repository/maven-public")
        }
        filter { includeGroupAndSubgroups("org.spongepowered") }
    }
    exclusiveContent {
        forRepositories(
                maven("https://maven.parchmentmc.org/"),
                maven("https://maven.neoforged.net/releases")
        )
        filter { includeGroup("org.parchmentmc.data") }
    }
}

// Declare capabilities on the outgoing configurations.
// Read more about capabilities here: https://docs.gradle.org/current/userguide/component_capabilities.html#sec:declaring-additional-capabilities-for-a-local-component
listOf("apiElements", "runtimeElements", "sourcesElements", "javadocElements").forEach { variant ->
    configurations[variant].outgoing {
        capability("$group:${base.archivesName.get()}:$version")
        capability("$group:${"mod_id"()}-${project.name}-${"minecraft_version"()}:$version")
        capability("$group:${"mod_id"()}:$version")
    }
    publishing.publications.configureEach {
        // TODO
        //suppressPomMetadataWarningsFor(variant)
    }
}

tasks.named<Jar>("sourcesJar") {
    from(rootProject.file("LICENSE")) {
        rename { "${it}_${"mod_name"()}" }
    }
}

tasks.named<Jar>("jar") {
    from(rootProject.file("LICENSE")) {
        rename { "${it}_${"mod_name"()}" }
    }

    manifest {
        attributes(
            mapOf(
                "Specification-Title"    to "mod_name"(),
                "Specification-Vendor"   to "mod_author"(),
                // TODO
                //"Specification-Version"  to project.jar.archiveVersion,
                "Implementation-Title"   to project.name,
                //"Implementation-Version" to project.jar.archiveVersion,
                "Implementation-Vendor"  to "mod_author"(),
                "Built-On-Minecraft"     to "minecraft_version"()
            )
        )
    }
}

tasks.processResources {
    val expandProps = mapOf(
            "version"                       to version,
            "group"                         to project.group, //Else we target the task's group.
            "minecraft_version"             to "minecraft_version"(),
            "minecraft_version_range"       to "minecraft_version_range"(),
            "fabric_version"                to "fabric_version"(),
            "fabric_loader_version"         to "fabric_loader_version"(),
            "mod_name"                      to "mod_name"(),
            "mod_author"                    to "mod_author"(),
            "mod_id"                        to "mod_id"(),
            "license"                       to "license"(),
            "description"                   to project.description,
            "neoforge_version"              to "neoforge_version"(),
            "neoforge_loader_version_range" to "neoforge_loader_version_range"(),
            "credits"                       to "credits"(),
            "java_version"                  to "java_version"()
    )

    filesMatching(listOf("pack.mcmeta", "fabric.mod.json", "META-INF/mods.toml", "META-INF/neoforge.mods.toml", "*.mixins.json")) {
        expand(expandProps)
    }
    inputs.properties(expandProps)
}

// TODO
//publishing {
//    publications {
//        register("mavenJava", MavenPublication) {
//            artifactId base.archivesName.get()
//            from components.java
//        }
//    }
//    repositories {
//        maven {
//            url System.getenv("local_maven_url")
//        }
//    }
//}

operator fun String.invoke(): String {
    return rootProject.ext[this] as? String
        ?: throw IllegalStateException("Property $this is not defined")
}