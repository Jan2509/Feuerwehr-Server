import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.apache.tools.ant.taskdefs.condition.Os

plugins {
    java
    kotlin("jvm") version "1.3.61"
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

group = "me.feuerwehr"
version = "1.0-SNAPSHOT"
val ktor_version = "1.3.0-beta-2"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testCompile("junit", "junit", "4.12")
    implementation("io.ktor:ktor-server-sessions:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-auth:$ktor_version")
    implementation("io.ktor:ktor-html-builder:$ktor_version")
    implementation("io.ktor:ktor-websockets:$ktor_version")
    implementation("io.ktor:ktor-jackson:$ktor_version")
    compile("org.jetbrains.exposed", "exposed-core", "0.20.2")
    compile("org.jetbrains.exposed", "exposed-dao", "0.20.2")
    compile("org.jetbrains.exposed", "exposed-jdbc", "0.20.2")
    compile("mysql:mysql-connector-java:5.1.46")
    compile("com.google.guava", "guava", "18.0")
    compile("com.uchuhimo", "konf", "0.22.1")
    compile("org.apache.commons", "commons-lang3", "3.9")
    compile("org.koin", "koin-core", "2.1.0-alpha-1")
    compile("javax.annotation", "jsr250-api", "1.0")
    compile("javax.inject","javax.inject","1")
    compile("com.atlassian.commonmark", "commonmark", "0.13.0")
    compile("com.coreoz", "wisp", "2.1.0")
    compile("info.picocli", "picocli", "4.+")
    compile("org.fusesource.jansi","jansi","1.18")
    compile("org.slf4j", "slf4j-simple", "1.7.30")

}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
val mainClassPath = "me.feuerwehr.notification.programm.Server"
tasks.withType<Jar> {

    manifest {
        attributes(
            mapOf(
                "Main-Class" to mainClassPath
            )
        )
    }
}
tasks.withType<ShadowJar> {
    baseName = "FFServer"
    classifier = null
    version = null
    // minimize{ exclude("com.fasterxml.jackson.*:.*:.*", "org.jetbrains.kotlin:kotlin-reflect:.*") }

}
fun Copy.init() {
    dependsOn("build-web-pack")
}
tasks.named(JavaPlugin.CLASSES_TASK_NAME) {
    dependsOn("copy-web-pack")
}
tasks.create<Copy>("copy-web-pack") {
    init()
    from(File(buildDir, "web/"))

    into(File(buildDir, "resources/main/web/assets/"))
    outputs.upToDateWhen { false }
}
tasks.create<Exec>("npm-install") {
    //dependsOn(JavaPlugin.PROCESS_RESOURCES_TASK_NAME)
    workingDir(File(projectDir, "src/main/web"))
    val npm = if (Os.isFamily(Os.FAMILY_WINDOWS)) "npm.cmd" else "npm"
    if (File("node_modules").exists())
        setCommandLine("""echo "already installed"""")
    else
        setCommandLine(npm, "install")
    standardOutput = System.out
    errorOutput = System.err
    outputs.upToDateWhen { File("node_modules").exists() }
}

tasks.create<Exec>("build-web-pack") {
    dependsOn(JavaPlugin.PROCESS_RESOURCES_TASK_NAME, "npm-install")
    workingDir(File(projectDir, "src/main/web"))
    println(workingDir.absolutePath)
    val npm = if (Os.isFamily(Os.FAMILY_WINDOWS)) "npm.cmd" else "npm"
    setCommandLine(npm, "run", "webpack")
    // setCommandLine("./node_modules/.bin/webpack")

    standardOutput = System.out
    errorOutput = System.err
    outputs.upToDateWhen { false }
}