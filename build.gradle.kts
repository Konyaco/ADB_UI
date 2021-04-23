import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "1.4.32"
    kotlin("plugin.serialization") version "1.4.30"
    id("org.jetbrains.compose") version "0.4.0-build184"
}

group = "me.konyaco"
version = "1.1.0"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.compose.material:material-icons-extended:0.4.0-build184")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

compose.desktop {
    application {
        javaHome = System.getenv("JDK_15.0.1")
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Exe, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Rpm)
            packageName = "ADB UI"
            vendor = "Konyaco"
            windows {
                iconFile.set(file("src/main/resources/icon.ico"))
                perUserInstall = true
                shortcut = true
                upgradeUuid = "06D3C747-C4A8-4FF2-BA3A-26658F766550"
                menu = true
                menuGroup = "Konyaco"
            }
            linux {
                shortcut = true
                menuGroup = "Konyaco"
            }
        }
    }
}
