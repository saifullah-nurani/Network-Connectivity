import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("com.vanniktech.maven.publish") version "0.29.0"
}

android {
    namespace = "io.github.nurani.network.connectivity"
    compileSdk = 35

    defaultConfig {
        minSdk = 16

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}
dependencies {
    implementation(libs.androidx.core.ktx)
}
mavenPublishing {
    coordinates("io.github.saifullah-nurani", "network-connectivity", "1.0.0")
    pom {
        name.set("Network Connectivity")
        description.set("Network Connectivity is an easy-to-integrate Android library designed to provide real-time updates on network connectivity status and type changes. Whether your app needs to detect when a user goes offline, switches from Wi-Fi to mobile data, or encounters any connectivity changes, this library simplifies the process by delivering reliable connectivity insights directly to your app’s logic.")
        url.set("https://github.com/saifullah-nurani/Network-Connectivity")
        inceptionYear.set("2024")
        licenses {
            license {
                name.set("MIT License")
                url.set("https://github.com/saifullah-nurani/Network-Connectivity/blob/main/LICENSE")
            }
        }
        developers {
            developer {
                id.set("saifullah-nurani")
                name.set("Saifullah Nurani")
                email.set("donaldperryman04@gmail.com")
                url.set("https://github.com/saifullah-nurani")
            }
        }
        scm {
            connection.set("scm:git:https://github.com/saifullah-nurani/Network-Connectivity.git")
            developerConnection.set("scm:git:https://github.com/saifullah-nurani/Network-Connectivity.git")
            url.set("https://github.com/saifullah-nurani/Network-Connectivity")
        }
    }
    // Configure publishing to Maven Central
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    // Enable GPG signing for all publications
    signAllPublications()
}