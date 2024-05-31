
plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.dokka)
    `maven-publish`
}

android {
    namespace = "com.aeke.pcm.audio_record"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        version = "1.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        externalNativeBuild {
            cmake {
                cppFlags("-std=c++11 -frtti -fexceptions -Lc++")
                arguments("-DANDROID_STL=c++_static","-DANDROID_STL=c++_shared")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    externalNativeBuild {
        cmake {
            path("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

afterEvaluate {
    publishing {
        repositories {
            maven {
                isAllowInsecureProtocol = true
//                url = uri("../repo")
                url = uri("http://192.168.190.31:8081/repository/maven-releases/")
                credentials {
                    username = "admin"
                    password = "aeke@2023"
                }
            }
        }
        publications {
            create<MavenPublication>("maven") {
                pom {
                    name = "Native Audio Record Library"
                    description = "A native audio record library"
                    developers {
                        developer {
                            id = "Damon"
                            name = "Damon He"
                            email = "hechenglin@aeke.com"
                        }
                    }
                }
                from(components["release"])
                groupId = "com.aeke.library"
                artifactId = "audio-record"
                version = android.defaultConfig.versionName
            }

        }
    }
}
