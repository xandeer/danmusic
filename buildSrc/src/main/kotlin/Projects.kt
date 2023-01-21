package heartmusic

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.Lint
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

fun Project.setupLibraryModule(
  name: String?,
  buildConfig: Boolean = false,
  block: LibraryExtension.() -> Unit = {}
) = setupBaseModule<LibraryExtension>(name) {
  libraryVariants.all {
    generateBuildConfigProvider?.configure { enabled = buildConfig }
  }
  block()
}

fun Project.setupAppModule(
  name: String?,
  block: BaseAppModuleExtension.() -> Unit = {}
) = setupBaseModule<BaseAppModuleExtension>(name) {
  defaultConfig {
    versionCode = project.versionCode
    versionName = project.versionName
    resourceConfigurations += "en"
    vectorDrawables.useSupportLibrary = true
  }
  block()
}

private inline fun <reified T : BaseExtension> Project.setupBaseModule(
  name: String?,
  crossinline block: T.() -> Unit = {}
) = extensions.configure<T>("android") {
  namespace = name
  compileSdkVersion(compileSdk)
  defaultConfig {
    minSdk = project.minSdk
    targetSdk = project.targetSdk
//    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    testInstrumentationRunner = "heartmusic.InstrumentationTestRunner"
  }
  compileOptions {
    sourceCompatibility = jvmTargetVersion
    targetCompatibility = jvmTargetVersion
  }
  // affects kapt
  kotlinExtension.jvmToolchain(jvmTargetVersion.toString().toInt())
  kotlinOptions {
//    jvmTarget = project.jvmTargetVersion.toString()
    allWarningsAsErrors = true

    val arguments = mutableListOf(
      // https://kotlinlang.org/docs/compiler-reference.html#progressive
      "-progressive",
      // Enable Java default method generation.
      "-Xjvm-default=all",
      // Generate smaller bytecode by not generating runtime not-null assertions.
      "-Xno-call-assertions",
      "-Xno-param-assertions",
      "-Xno-receiver-assertions",
      // kapt currently doesn't support experimental K2 compiler
//      "-Xuse-k2",
    )
//    if (project.name != "coil-test") {
//      arguments += "-opt-in=coil.annotation.ExperimentalCoilApi"
//    }
    // https://youtrack.jetbrains.com/issue/KT-41985
    freeCompilerArgs = freeCompilerArgs + arguments
  }
  packagingOptions {
    resources.pickFirsts += arrayOf(
      "META-INF/AL2.0",
      "META-INF/LGPL2.1",
      "META-INF/*kotlin_module",
    )
  }
  testOptions {
    unitTests.isIncludeAndroidResources = true
  }
  lint {
    warningsAsErrors = true
    disable += arrayOf(
      "UnusedResources",
      "VectorPath",
      "VectorRaster",
    )
  }
  block()
}

private fun BaseExtension.kotlinOptions(block: KotlinJvmOptions.() -> Unit) {
  (this as ExtensionAware).extensions.configure("kotlinOptions", block)
}

private fun BaseExtension.lint(block: Lint.() -> Unit) {
  (this as CommonExtension<*, *, *, *>).lint(block)
}
