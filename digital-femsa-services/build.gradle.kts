plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("maven-publish")
    jacoco
    id("com.jfrog.artifactory")
    id("com.apollographql.apollo3") version "3.8.1"
}

val artifactGroup = "com.digitalfemsa.cli"
val artifactName = "femsa_cli"
val artifactVersion = "1.0.1"
val userKey = "MVN_USER"
val passwordKey = "MVN_PASSWORD"
val urlKey = "MVN_URL"

val dependenciesFile = file("$projectDir/../buildscripts/dependencies.gradle.kts")
apply(from = "../ktlint.gradle")
apply(from = dependenciesFile.absolutePath)

kotlin {
    android {
        publishLibraryVariants("release")
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
                freeCompilerArgs = listOf("-opt-in=kotlin.RequiresOptIn")
            }
        }
    }

    sourceSets {
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlin.native.internal.InternalForKotlinNative")
        }
        val commonMain by getting {
            with(extra) {
                dependencies {
                    implementation("com.apollographql.apollo3:apollo-runtime:${get("apolloVersion")}")
                }
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting
        androidMain.dependencies {
            implementation(project(":imperva"))
        }
        val androidUnitTest by getting
    }
}

android {
    namespace = artifactGroup
    compileSdk = 33
    defaultConfig {
        minSdk = 26
        version = artifactVersion
    }
    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    libraryVariants.all {
        outputs.all {
            (this as? com.android.build.gradle.internal.api.BaseVariantOutputImpl)?.let { output ->
                output.outputFileName = "$artifactName-$name-$artifactVersion.aar"
            }
        }
    }
}
tasks.withType<JavaCompile> {
    dependsOn("ktlint")
}
val testTask = tasks.withType<Test> {
    maxParallelForks = Runtime.getRuntime().availableProcessors()
    jacoco {
        setExcludes(listOf("jdk.internal.*"))
    }
    finalizedBy(tasks.getByName("jacocoTestReport"))
}
jacoco {
    toolVersion = "0.8.9"
}


afterEvaluate {
    publishing {
        repositories {
            maven {
                credentials {
                    username = "${System.getenv()[userKey]}"
                    password = "${System.getenv()[passwordKey]}"
                }
                url = uri(System.getenv()[urlKey] ?: "")
            }
        }
        publications {
            create<MavenPublication>("release") {
                groupId = artifactGroup
                artifactId = artifactName
                version = artifactVersion
                from(components["kotlin"])
            }
        }
    }
}


tasks.create("jacocoTestReport", JacocoReport::class.java) {
    group = "Reporting"
    description = "Generate Jacoco coverage reports."
    val commons = file("${projectDir}/src/commonMain/kotlin")
    assert(commons.exists())
    val coverageSourceDirs = arrayOf(
        commons.absolutePath
    )
    val kvmFiles = file("${buildDir}/tmp/kotlin-classes/debug")
    val classFiles = kvmFiles
        .walkBottomUp()
        .toSet()
    classDirectories.setFrom(classFiles)
    sourceDirectories.setFrom(files(coverageSourceDirs))
    additionalSourceDirs.setFrom(files(coverageSourceDirs))
    val execFiles = fileTree(buildDir) {
        setIncludes(listOf("**/*.exec", "**/*.ec"))
    }
    executionData
        .setFrom(files(execFiles))
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    dependsOn(testTask)
    dependsOn(tasks.getByName("build"))
}

apollo {
    service("service") {
        packageName.set(artifactGroup)
    }
}