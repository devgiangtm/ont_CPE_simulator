// repositories {
//     mavenCentral()
// }

//fun nexus(host: String, port: String = "8081") = uri("http://$host:$port/repository/maven/")
repositories {
//    maven { url = nexus("172.16.28.46", "8000") }
     mavenCentral()
}

plugins {
    java
    jacoco
    id("org.springframework.boot") version "2.3.4.RELEASE"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
}


jacoco {
    toolVersion = "0.8.4"
    reportsDir = file("$buildDir/jacoco/reports")
}

group = "com.viettel.ems"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

val spring by lazy { properties["spring"] as (String, String) -> String }

extra["spring"] = { p: String, m: String ->
    val v = mapOf("boot" to "2.3.4.RELEASE", "cloud" to "2.2.3.RELEASE", "kafka" to "2.6.0")
    "org.springframework.$p:spring-$p$m:${v[p]}"
}

dependencies {
    val lombokV = "1.18.12"
    compileOnly("org.projectlombok:lombok:$lombokV")
    annotationProcessor("org.projectlombok:lombok:$lombokV")
    testCompileOnly("org.projectlombok:lombok:$lombokV")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokV")
    //
    val springBoot = "org.springframework.boot"
    val springBootV = "2.2.10.RELEASE"
    implementation("$springBoot:spring-boot-starter:$springBootV")
    implementation("$springBoot:spring-boot-starter-web:$springBootV")
    // implementation("$springBoot:spring-boot-starter-jdbc:$springBootV")
    implementation("$springBoot:spring-boot-starter-actuator:$springBootV")
    implementation("$springBoot:spring-boot-configuration-processor:$springBootV")

    // implementation("$springBoot:spring-boot-starter-mail:$springBootV")
    // implementation("com.h2database:h2:1.4.200")

    // implementation("mysql:mysql-connector-java:8.0.19")
    implementation("com.jayway.jsonpath:json-path:2.4.0")
    implementation("org.apache.commons:commons-text:1.8")
    implementation(fileTree("libs/"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
tasks.register<Copy>("copyLibs") {
    doFirst {
        println("Copy dependencies of module ${"%-15s".format(project.name)} into $rootDir/libs/${project.name}...")
    }

    from(configurations.default)
    into("$rootDir/libs/${project.name}")

    doLast {
        println("Done copying dependencies of module ${project.name}!")
    }
}

tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    doFirst {
        println(" - Module ${"%-12s".format(project.name)} -> $buildDir")
    }
}

//region JACOCO
tasks.test {
    useJUnitPlatform()

    extensions.configure(JacocoTaskExtension::class) {
        setDestinationFile(file("$rootDir/jacoco/jacocoTest.exec"))
        // classDumpDir = file("$rootDir/jacoco/classpathdumps")
    }
    doLast {
        tasks["jacocoTestReport"]
    }
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = false
        // csv.isEnabled = false
        html.destination = file("$buildDir/jacoco/html")
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.5".toBigDecimal()
            }
        }

        rule {
            enabled = false
            element = "CLASS"
            includes = listOf("org.gradle.*")

            limit {
                counter = "LINE"
                value = "TOTALCOUNT"
                maximum = "0.3".toBigDecimal()
            }
        }
    }
}
//endregion JACOCO

