plugins {
    application
    eclipse
}

repositories {
    mavenCentral()
}

val osgi_platform = "cocoa.macosx.x86_64"
val swt_version = "3.118.0"

dependencies {
    implementation("org.apache.pdfbox:pdfbox:2.0.25")
    implementation("org.apache.commons:commons-csv:1.9.0")
    implementation("org.eclipse.platform:org.eclipse.swt.$osgi_platform:$swt_version")
}

configurations.all {
    resolutionStrategy {
        dependencySubstitution {
            // The maven property ${osgi.platform} is not handled by Gradle
            // so we replace the dependency, using the osgi platform from the project settings
            substitute(module("org.eclipse.platform:org.eclipse.swt.\${osgi.platform}")).using(module("org.eclipse.platform:org.eclipse.swt.$osgi_platform:$swt_version"))
        }
    }
}

application {
    mainClass.set("org.ellab.swt.demo.SwtDemo")
    applicationDefaultJvmArgs = listOf("-XstartOnFirstThread")
}

java.sourceSets["main"].java {
    srcDir("src/main")
}

tasks.withType<ProcessResources> {
    from("src/main")
    include("**/*.ico")
}
