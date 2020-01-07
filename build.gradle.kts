plugins {
    java
    kotlin("jvm") version "1.3.61"
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
    compile("org.jetbrains.exposed", "exposed-core", "0.20.1")
    compile("org.jetbrains.exposed", "exposed-dao", "0.20.1")
    compile("org.jetbrains.exposed", "exposed-jdbc", "0.20.1")
    compile("mysql:mysql-connector-java:5.1.46")
    compile("com.google.guava", "guava", "18.0")
    compile("com.uchuhimo", "konf", "0.22.1")
    compile("org.apache.commons", "commons-lang3", "3.9")
    compile("org.koin", "koin-core", "2.1.0-alpha-1")
    compile("javax.annotation", "jsr250-api", "1.0")
    compile("javax.inject","javax.inject","1")
    compile("com.atlassian.commonmark", "commonmark", "0.13.0")
    compile("com.coreoz", "wisp", "2.1.0")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}