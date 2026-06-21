plugins {
  kotlin("jvm") version "2.3.0"
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(project(":core"))
  testImplementation(kotlin("test"))
}

kotlin {
  jvmToolchain(25)
}

tasks.test {
  useJUnitPlatform()
}
