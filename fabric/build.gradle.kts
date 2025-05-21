import org.gradle.kotlin.dsl.support.uppercaseFirstChar

plugins {
    id("multiloader-loader")
    id("fabric-loom")
}

dependencies {
    minecraft("com.mojang:minecraft:${"minecraft_version"()}")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${"parchment_minecraft"()}:${"parchment_version"()}@zip")
    })
    modImplementation("net.fabricmc:fabric-loader:${"fabric_loader_version"()}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${"fabric_version"()}")
}

loom {
    val aw = project(":common").file("src/main/resources/${"mod_id"()}.accesswidener")
    if (aw.exists()) {
        accessWidenerPath.set(aw)
    }
    
    mixin {
        defaultRefmapName.set("${"mod_id"()}.refmap.json")
    }
    
    runs {
        configureEach {
            configName = "Fabric ${name.uppercaseFirstChar()}"
            runDir("runs/${name}")
            ideConfigGenerated(true)
            
            vmArg("-XX:+AllowEnhancedClassRedefinition")
            vmArg("-XX:+IgnoreUnrecognizedVMOptions")
            vmArg("-Dmixin.debug.export=true")
        }
    }
}

operator fun String.invoke(): String {
    return rootProject.ext[this] as? String
        ?: throw IllegalStateException("Property $this is not defined")
}