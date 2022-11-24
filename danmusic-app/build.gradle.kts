import danmusic.setupAppModule

plugins {
  id("com.android.application")
  id("kotlin-android")
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
  implementation(libs.androidx.activity.compose)
  implementation(libs.compose.ui.tooling)
  implementation(libs.compose.preview)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.compose.material3)

  testImplementation(libs.junit)

  androidTestImplementation(libs.bundles.test.android)
}
