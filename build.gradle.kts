import heartmusic.groupId
import heartmusic.versionName
import kotlinx.validation.ApiValidationExtension

buildscript {
  repositories {
    google()
    mavenCentral()
  }
  dependencies {
    classpath(libs.gradlePlugin.android)
    classpath(libs.gradlePlugin.kotlin)
  }

  configurations.classpath {
    resolutionStrategy.eachDependency {
      when (requested.group) {
        libs.ktlint.get().module.group -> useVersion(libs.versions.ktlint.get())
      }
    }
  }
}

// https://youtrack.jetbrains.com/issue/KTIJ-19369
@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
  alias(libs.plugins.binaryCompatibility)
  alias(libs.plugins.ktlint)
}

extensions.configure<ApiValidationExtension> {
  ignoredProjects += arrayOf(
//    "coil-test",
  )
}

allprojects {
  repositories {
    google()
    mavenCentral()
  }

  group = project.groupId
  version = project.versionName

  apply(plugin = "org.jmailen.kotlinter")

  kotlinter {
    disabledRules = arrayOf(
      "annotation",
      "argument-list-wrapping",
      "filename",
      "indent",
      "max-line-length",
      "parameter-list-wrapping",
      "spacing-between-declarations-with-annotations",
      "wrapping",
    )
  }

  // Uninstall test APKs after running instrumentation tests.
  tasks.whenTaskAdded {
    if (name == "connectedDebugAndroidTest") {
      finalizedBy("uninstallDebugAndroidTest")
    }
  }
}
