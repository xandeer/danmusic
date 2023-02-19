import heartmusic.kotestDebug
import heartmusic.setupAppModule

plugins {
  id("com.android.application")
//  id("com.google.devtools.ksp") version libs.versions.ksp.get()
  kotlin("android")
  kotlin("kapt")
}

setupAppModule(name = "heartmusic") {
  defaultConfig {
    applicationId = "xandeer.heartmusic"
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles("proguard-rules.pro")
      signingConfig = signingConfigs["debug"]
    }
  }

  buildFeatures {
    compose = true
  }
  composeOptions {
    kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
  }
}

dependencies {
  implementation(platform(libs.compose.bom))
  implementation(libs.bundles.compose)
  implementation(libs.compose.constraintlayout)

  implementation(libs.coil)
  implementation(libs.coil.compose)

  implementation(libs.koin.android)

  implementation(libs.media3.exoplayer)
  implementation(libs.media3.ui)

  implementation(libs.nav)

  implementation(libs.paging)
  implementation(libs.paging.compose)

  implementation(libs.retrofit)
  implementation(libs.retrofit.moshi)

  implementation(libs.room.runtime)
  annotationProcessor(libs.room.compiler)
  kapt(libs.room.compiler)
  // Something wrong with `@Transaction` in KSP
//  ksp(libs.room.compiler)
  implementation(libs.room.ktx)
  implementation(libs.room.paging)

  implementation(libs.timber)

  debugImplementation(libs.leakCanary)

//  testImplementation(libs.bundles.test.jvm)
  testImplementation(platform(libs.kotlin.bom))
  testImplementation(libs.kotlin.reflect)
  testImplementation(platform(libs.kotest.bom))
  testImplementation(libs.bundles.kotest)

  // Just for temporary use
  if (kotestDebug) {
    debugImplementation(platform(libs.kotlin.bom))
    debugImplementation(libs.kotlin.reflect)
    debugImplementation(platform(libs.kotest.bom))
    debugImplementation(libs.bundles.kotest)
  }

  androidTestImplementation(libs.bundles.test.android)
}
