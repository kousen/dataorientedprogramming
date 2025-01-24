plugins {
    id("java")
}

group = "com.kousenit"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    // Gson parser
    implementation("com.google.code.gson:gson:2.11.0")

    // Logging
    implementation("org.slf4j:slf4j-simple:2.0.16")

    // Testing libraries
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.assertj:assertj-core:3.27.3")
}

tasks.test {
    useJUnitPlatform()
    jvmArgs("-XX:+EnableDynamicAgentLoading", "-Xshare:off")
}