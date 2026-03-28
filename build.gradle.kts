import org.gradle.jvm.tasks.Jar

plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.googlecode.json-simple:json-simple:1.1.1")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.14.0")
//    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.11.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.openjfx:javafx-controls:22")
    implementation("org.openjfx:javafx-fxml:22")

}
javafx{
    version = "22"
    modules = listOf("javafx.controls", "javafx.fxml")
}
application{
    mainClass.set("org.example.UILauncher")
    applicationDefaultJvmArgs = listOf(
        "--add-modules=javafx.controls,javafx.fxml"
    )
}

tasks.named<Jar>("jar") {
    manifest {
        attributes["Main-Class"] = "org.example.UILauncher"
    }
}
//tasks.withType<JavaExec> {
//    jvmArgs = listOf(
//        "--module-path", "C:/Users/nosid/javafx-sdk-21/lib",
//        "--add-modules", "javafx.controls,javafx.fxml"
//    )
//}
tasks.test {
    useJUnitPlatform()
}
