plugins {
    java
    id("org.springframework.boot") version "3.2.4"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "com.github.bilak"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-loader")
    runtimeOnly("com.oracle.database.jdbc:ojdbc11")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.bootJar {
    layered {
        enabled = true
    }
    archiveFileName = "app.jar"
    manifest {
        attributes["Main-Class"] = "org.springframework.boot.loader.launch.PropertiesLauncher"
        attributes["Start-Class"] = "com.github.bilak.springclassloaderissue.BootstrapApp"
    }

    duplicatesStrategy = DuplicatesStrategy.WARN
    from("${layout.buildDirectory.get()}/classes/java/main/com/github/bilak/springclassloaderissue/bootstrapping") {
        into("/com/github/bilak/springclassloaderissue/bootstrapping")
    }
    enabled = true

}