plugins {
    id("multiloader-loader")
    id("net.neoforged.moddev")
}

neoForge {
    version = "neoforge_version"()
    // Automatically enable neoforge AccessTransformers if the file exists
    val at = project(":common").file("src/main/resources/META-INF/accesstransformer.cfg")
    if (at.exists()) {
        accessTransformers.from(at.absolutePath)
    }
    parchment {
        minecraftVersion = "parchment_minecraft"()
        mappingsVersion = "parchment_version"()
    }
    runs {
        configureEach {
            systemProperty("neoforge.enabledGameTestNamespaces", "mod_id"())
            ideName = "NeoForge ${name.replaceFirstChar { it.uppercaseChar() }} (${project.path})" // Unify the run config names with fabric
        }
        create("client") {
            client()
        }
        create("server") {
            server()
        }
    }
    mods {
        create("mod_id"()) {
            sourceSet(sourceSets["main"])
        }
    }
}

sourceSets["main"].resources { srcDir("src/generated/resources") }

operator fun String.invoke(): String {
    return rootProject.ext[this] as? String
        ?: throw IllegalStateException("Property $this is not defined")
}