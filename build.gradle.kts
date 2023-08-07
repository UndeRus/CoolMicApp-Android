plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
}

gradle.projectsEvaluated {
    tasks.withType(JavaCompile::class.java) {
        options.compilerArgs.plusAssign("-Xlint:unchecked")
    }
}