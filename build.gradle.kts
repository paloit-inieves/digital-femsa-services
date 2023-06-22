plugins {
    //trick: for the same plugin versions in all sub-modules
    id("com.android.library").version("8.0.0").apply(false)
    kotlin("multiplatform").version("1.8.20").apply(false)
    id("com.jfrog.artifactory").version("4.15.1").apply(false)
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
