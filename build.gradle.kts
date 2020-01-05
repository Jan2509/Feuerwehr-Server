plugins {
    java
    kotlin("jvm") version "1.3.61"
}

group = "me.feuerwehr"
version = "1.0-SNAPSHOT"
val ktor_version = "1.2.6"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testCompile("junit", "junit", "4.12")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-auth:$ktor_version")
    implementation("io.ktor:ktor-html-builder:$ktor_version")
    compile("org.jetbrains.exposed", "exposed-core", "0.20.1")
    compile("org.jetbrains.exposed", "exposed-dao", "0.20.1")
    compile("org.jetbrains.exposed", "exposed-jdbc", "0.20.1")
    compile("mysql:mysql-connector-java:5.1.46")
    compile("com.google.guava", "guava", "18.0")
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