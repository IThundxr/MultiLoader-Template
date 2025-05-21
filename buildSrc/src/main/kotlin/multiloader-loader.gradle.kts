plugins {
    id("multiloader-common")
}

configurations {
    create("commonJava") {
        isCanBeResolved = true
    }
    create("commonResources") {
        isCanBeResolved = true
    }
}

dependencies {
    compileOnly(project(":common")) {
        capabilities {
            requireCapability("${group}:${"mod_id"()}")
        }
    }
    "commonJava"(project(path = ":common", configuration = "commonJava"))
    "commonResources"(project(path = ":common", configuration = "commonResources"))
}

tasks.named<JavaCompile>("compileJava") {
    dependsOn(configurations["commonJava"])
    source(configurations["commonJava"])
}

tasks.processResources {
    dependsOn(configurations["commonResources"])
    from(configurations["commonResources"])
}

tasks.named<Javadoc>("javadoc").configure {
    dependsOn(configurations["commonJava"])
    source(configurations["commonJava"])
}

tasks.named<Jar>("sourcesJar") {
    dependsOn(configurations["commonJava"])
    from(configurations["commonJava"])
    dependsOn(configurations["commonResources"])
    from(configurations["commonResources"])
}

operator fun String.invoke(): String {
    return rootProject.ext[this] as? String
        ?: throw IllegalStateException("Property $this is not defined")
}