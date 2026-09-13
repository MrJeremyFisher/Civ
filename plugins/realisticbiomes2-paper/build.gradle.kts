plugins {
    alias(libs.plugins.paper.userdev)
    alias(libs.plugins.runpaper)
}

version = "3.2.3"

dependencies {
    paperweight {
        paperDevBundle(libs.versions.paper)
    }

    compileOnly(project(":plugins:civmodcore-paper"))

    compileOnly(libs.worldedit)

}
