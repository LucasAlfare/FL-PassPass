plugins {
  kotlin("jvm") version "2.3.0"
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(project(":core"))
  implementation(project(":bot"))
  testImplementation(kotlin("test"))
}

kotlin {
  jvmToolchain(25)
}

tasks.test {
  useJUnitPlatform()
}
