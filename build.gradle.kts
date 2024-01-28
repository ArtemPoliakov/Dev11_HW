plugins {
    id("java")
    id("war")
}

group = "com.homework"
version = "1.0"

repositories {
    mavenCentral()
}

val log4j2Version = "2.22.1"

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")

    // https://mvnrepository.com/artifact/org.thymeleaf/thymeleaf
    implementation("org.thymeleaf:thymeleaf:3.1.0.M2")

    // https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-core
    implementation("org.apache.logging.log4j:log4j-core:$log4j2Version")
    // https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-api
    implementation("org.apache.logging.log4j:log4j-api:$log4j2Version")
}

tasks.test {
    useJUnitPlatform()
}