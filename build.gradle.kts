plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.spring") version "2.0.0"
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "org.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

val flyWayVersion = "10.10.0"
val jjwtApiVersion = "0.11.2"
val jjwtImplVersion = "0.11.5"
val jjwtJacksonVersion = "0.11.1"
val jacksonModuleKotlinVersion = "2.18.1"
val kotlinReflectVersion = "2.0.21"
val kotlinStdlibJdkVersion = "2.0.21"
val postgresqlVersion = "42.7.4"
val junitPlatformLauncherVersion = "1.11.3"
val kotlinTestJunit5 = "2.0.21"
val cloudConfigVersion = "4.1.3"
val springSecurityTestVersion = "6.3.4"
val testContainersVersion = "1.20.1"
val webmvcUiVersion = "2.6.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.cloud:spring-cloud-starter-config:$cloudConfigVersion")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$webmvcUiVersion")


    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonModuleKotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinReflectVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinStdlibJdkVersion")
    implementation("io.jsonwebtoken:jjwt-api:$jjwtApiVersion")
    implementation("org.flywaydb:flyway-core:$flyWayVersion")

    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jjwtImplVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jjwtJacksonVersion")
    runtimeOnly("org.postgresql:postgresql:$postgresqlVersion")
    runtimeOnly("org.flywaydb:flyway-database-postgresql:$flyWayVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test:$springSecurityTestVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:$kotlinStdlibJdkVersion")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:testcontainers:$testContainersVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testContainersVersion")
    testImplementation("org.testcontainers:postgresql:$testContainersVersion")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformLauncherVersion")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.3")
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
