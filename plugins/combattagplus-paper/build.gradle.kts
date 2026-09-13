plugins {
    alias(libs.plugins.paper.userdev)
    alias(libs.plugins.runpaper)
}

version = "2.0.1-F"

dependencies {
    paperweight {
        paperDevBundle(libs.versions.paper)
    }

    compileOnly(libs.barapi)
}
