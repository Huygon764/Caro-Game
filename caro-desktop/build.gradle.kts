plugins {
    id("java-library")
    id("application")
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

application {
    mainClass.set("com.example.carogame.desktop.CaroDesktopApp")
}

dependencies {
    implementation(project(":caro-core"))

    // Thêm các thư viện Java Swing nếu cần
    testImplementation("junit:junit:4.13.2")
}

// Tạo file JAR thực thi
tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.example.carogame.desktop.CaroDesktopApp"
    }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}