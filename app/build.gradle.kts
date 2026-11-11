plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.apollo)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

apollo {
    service("identity") {
        packageName.set("pl.cincior.englishreminder.identity.graphql")
        srcDir("src/main/graphql/identity")

        introspection {
            endpointUrl.set("http://localhost:8080/graphql/IdentityService")
            schemaFile.set(file("src/main/graphql/identity/schema.graphqls"))
        }
    }

    service("words") {
        packageName.set("pl.cincior.englishreminder.words.graphql")
        srcDir("src/main/graphql/words")

        introspection {
            endpointUrl.set("http://localhost:8080/graphql/wordService")
            schemaFile.set(file("src/main/graphql/words/schema.graphqls"))
            headers.put("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhMjdlZTlhNy01M2U3LTRlMmMtODI0Zi1mNTRhYmJjYjRjYjEiLCJpYXQiOjE3OTM5NjI4ODEsImV4cCI6MTc5Mzk2NDY4MSwicm9sZSI6IlVTRVIifQ.XvR8etDBoONTW16RQD20g6JMnTsavrVG2_npD2UEJU8")
        }
    }
}

android {
    namespace = "pl.cincior.englishreminder"
    compileSdk = 37

    defaultConfig {
        applicationId = "pl.cincior.englishreminder"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }

}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.apollo.runtime)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.jwtdecode)
}