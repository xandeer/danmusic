plugins {
  `kotlin-dsl`
}

repositories {
  google()
  mavenCentral()
}

dependencies {
  implementation(libs.gradlePlugin.android)
  implementation(libs.gradlePlugin.kotlin)
}

// Remember to update `Project.jvmTarget` in buildSrc/src/main/kotlin/Extensions.kt.
kotlinDslPluginOptions {
  jvmTarget.set(JavaVersion.VERSION_11.toString())
}
