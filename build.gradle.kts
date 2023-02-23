import org.asciidoctor.gradle.jvm.AsciidoctorTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.7.22"
    id("org.springframework.boot") version "3.0.2"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    kotlin("kapt") version kotlinVersion
    id("org.asciidoctor.jvm.convert") version "4.0.0-alpha.1"
    idea
}
val snippetsDir by extra { file("build/generated-snippets") }

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

val testContainerVersion = "1.17.6"
dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:$testContainerVersion")
    }
}
val asciidoctorExt:Configuration by configurations.creating
dependencies {

    implementation("com.github.f4b6a3:ulid-creator:5.1.0")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation ("org.jetbrains.kotlin:kotlin-reflect:1.7.22")
    //database
    implementation("org.mariadb.jdbc:mariadb-java-client")

    //querydsl
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
    kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")
    kapt("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
    implementation("io.jsonwebtoken:jjwt-gson:0.11.5")
    //test
    testImplementation("com.h2database:h2")

    testImplementation ("io.mockk:mockk-jvm:1.13.3")
    testImplementation("com.ninja-squad:springmockk:4.0.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test"){exclude(module="mockito-core")}
    testImplementation("org.springframework.security:spring-security-test")

    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.springframework.restdocs:spring-restdocs-core")
    asciidoctorExt("org.springframework.restdocs:spring-restdocs-asciidoctor:3.0.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}



allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
    annotation("org.springframework.stereotype.Component")
}

noArg {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
    annotation("org.springframework.stereotype.Component")
}
idea{
    module {
        val kaptMain = file("build/generated/source/kapt/main")
        sourceDirs.add(kaptMain)
        generatedSourceDirs.add(kaptMain)
    }
}
tasks.test{

    useJUnitPlatform()
    testLogging {
        setExceptionFormat("full")
        setEvents(listOf("started", "skipped", "passed", "failed"))
        showStandardStreams=true
    }
    outputs.dir(snippetsDir)

}
tasks.register("asciidoctorCustom", AsciidoctorTask::class){
    inputs.dir(snippetsDir)
    dependsOn(tasks.test)
    doFirst { // 2
        println("=== start asciidoctor===")
        delete {
            file("src/main/resources/static/docs")
        }
    }
    options(mapOf("doctype" to "book"))
    attributes(mapOf("source-highlighter" to "coderay"))
    configurations(asciidoctorExt.name)
    outputs.dir("build/asciidoc/html5")
    baseDirFollowsSourceFile()
}

tasks.register<Copy>("copyDocument"){
    from("${tasks.asciidoctor.get().outputDir}") {
        into("static/docs")
    }
    dependsOn("asciidoctorCustom")
}
tasks.build{
    dependsOn("asciidoctor")
}
tasks.bootJar{
    dependsOn("copyDocument")
    from("${tasks.asciidoctor.get().outputDir}/html5") {
        into("BOOT-INF/classes/static/docs")
    }
}
