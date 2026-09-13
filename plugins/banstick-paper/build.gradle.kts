plugins {
    alias(libs.plugins.paper.userdev)
    alias(libs.plugins.shadow)
}

version = "2.0.1-F"

dependencies {
    paperweight {
        paperDevBundle(libs.versions.paper)
    }

    compileOnly(project(":plugins:civmodcore-paper"))
    compileOnly(project(":plugins:namelayer-paper"))

    implementation(libs.ipaddress)
    implementation(libs.jsoup)
}
