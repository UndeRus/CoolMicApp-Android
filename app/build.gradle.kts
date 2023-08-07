import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties
import java.util.TimeZone

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
}

// read version info from version.properties
val versionPropertiesFile: File = project.file("version.properties")
val versionProperties = Properties()
if (versionPropertiesFile.isFile) {
    InputStreamReader(FileInputStream(versionPropertiesFile), Charsets.UTF_8).use { reader -> versionProperties.load(reader) }
} else {
    throw Exception("version.properties not found or properly set up")
}
val versionMajor = versionProperties["versionMajor"].toString().toInt()
val versionMinor = versionProperties["versionMinor"].toString().toInt()
val versionPatch = versionProperties["versionPatch"].toString().toInt()
val versionNameTmp = "$versionMajor.$versionMinor.$versionPatch"
val versionCodeTmp = versionMajor * 10000 + versionMinor * 100 + versionPatch
println("versionNameTmp: $versionNameTmp")


android {
    namespace = "cc.echonet.coolmicapp"
    compileSdk = 33

    defaultConfig {
        applicationId = "cc.echonet.coolmicapp"
        minSdk = 16
        targetSdk = 33
        versionCode = versionCodeTmp
        versionName = versionNameTmp

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "GIT_REVISION", "\"${gitRevision()}\"")
        buildConfigField("String", "GIT_BRANCH", "\"${gitBranch()}\"")
        buildConfigField("String", "GIT_AUTHOR", "\"${gitAuthor()}\"")
        buildConfigField("String", "GIT_DIRTY", "\"${gitDirty()}\"")
        buildConfigField("String", "BUILD_TS", "\"${buildTimeStamp()}\"")
        buildConfigField("String", "HTTP_PRODUCT", "\"CoolMic\"")
        buildConfigField("String", "HTTP_VERSION", "cc.echonet.coolmicapp.BuildConfig.VERSION_NAME")
        buildConfigField("String", "HTTP_COMMENT", "\"build-ts:\" + BUILD_TS + \"; git-rev:\" + GIT_REVISION")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            resValue("string", "app_version", "${defaultConfig.versionName}")
        }
        debug {
            versionNameSuffix = ".debug"
            resValue("string", "app_version", "${defaultConfig.versionName}${versionNameSuffix}")
        }
    }

    flavorDimensions += "version"
    productFlavors {
        defaultConfig
        create("debugdev") {
            dimension = "version"
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }

    externalNativeBuild {
        cmake {
            path = File("src/main/jni/CMakeLists.txt")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}


fun gitRevision(): String = "git rev-parse HEAD".runCommand()

fun gitBranch(): String = "git rev-parse --symbolic-full-name HEAD".runCommand()

fun gitAuthor(): String = "git --no-pager show -s --format=\"%aN -- <%aE>\" HEAD".runCommand()

fun gitDirty(): String = "git status --porcelain".runCommand()
    .let {
        when (it) {
            "N/A" -> it
            "" -> "false"
            else -> "true"
        }
    }

fun buildTimeStamp(): String =
    SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z")
        .apply { timeZone = TimeZone.getTimeZone("UTC") }.format(Date())

fun String.runCommand(currentWorkingDir: File = file("./")): String =
    try {
        val byteOut = ByteArrayOutputStream()
        project.exec {
            workingDir = currentWorkingDir
            commandLine = this@runCommand.split("\\s".toRegex())
            standardOutput = byteOut
        }
        String(byteOut.toByteArray()).trim()
    } catch (e: Exception) {
        "N/A"
    }

dependencies {
    implementation(libs.androidx.constraintlayout)
    implementation(libs.zxing)
    implementation(libs.annotations)
    implementation(libs.core.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.rules)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

