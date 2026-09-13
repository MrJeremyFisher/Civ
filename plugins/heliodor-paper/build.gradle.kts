plugins {
    alias(libs.plugins.paper.userdev)
}

version = "1.0.0-F"

dependencies {
    paperweight {
        paperDevBundle(libs.versions.paper)
    }

    compileOnly(project(":plugins:civmodcore-paper"))
    compileOnly(project(":plugins:combattagplus-paper"))
}
