import heartmusic.setupAppModule

plugins {
  id("com.android.application")
  kotlin("android")
}

setupAppModule(name = "heartmusic.app") {
  defaultConfig {
    applicationId = "heartmusic.app"
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

  implementation(libs.coil)
  implementation(libs.coil.compose)

  implementation(libs.koin.android)

  implementation(libs.paging)
  implementation(libs.paging.compose)

  implementation(libs.retrofit)
  implementation(libs.retrofit.moshi)

  implementation(libs.timber)

  debugImplementation(libs.leakCanary)

  testImplementation(libs.bundles.test.jvm)

  androidTestImplementation(libs.bundles.test.android)
}
