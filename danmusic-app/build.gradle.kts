import danmusic.setupAppModule

plugins {
  id("com.android.application")
  kotlin("android")
}

setupAppModule(name = "danmusic.app") {
  defaultConfig {
    applicationId = "danmusic.app"
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

  implementation(libs.koin.android)

  implementation(libs.retrofit)
  implementation(libs.retrofit.moshi)

  implementation(libs.timber)

  testImplementation(libs.bundles.test.jvm)

  androidTestImplementation(libs.bundles.test.android)
}
