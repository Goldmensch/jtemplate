package project.convention

import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.invoke
import org.jreleaser.model.Active

plugins {
    `maven-publish`
    id("org.jreleaser")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom {
                name.set(provider { project.name })
                description.set(provider { project.description })
                url.set("https://REPO_URL")

                licenses {
                    license {
                        name.set("LICENSE_NAME")
                        url.set("LICENSE_URL")
                    }
                }

                developers {
                    developer {
                        name.set("AUTHOR_NAME")
                    }
                }

                scm {
                    connection.set("scm:git:git://REPO_URL")
                    developerConnection.set("scm:git:ssh://REPO_URL")
                    url.set("https://REPO_URL")
                }
            }
        }
    }

    repositories {
        maven {
            setUrl(layout.buildDirectory.dir("staging-deploy"))
        }
    }
}

jreleaser {
    project {
        copyright = "AUTHOR_NAME"
    }


    signing {
        active = Active.ALWAYS
        armored = true
    }

    deploy {
        maven {
            mavenCentral {
                create("release") {
                    active = Active.RELEASE
                    url = "https://central.sonatype.com/api/v1/publisher"
                    stagingRepository("build/staging-deploy")
                    setStage("UPLOAD")
                }
            }

            nexus2 {
                create("snapshot") {
                    active = Active.SNAPSHOT
                    url = "https://central.sonatype.com/api/v1/publisher"
                    snapshotUrl = "https://central.sonatype.com/repository/maven-snapshots/"
                    applyMavenCentralRules = true
                    snapshotSupported = true
                    closeRepository = true
                    releaseRepository = true
                    stagingRepository("build/staging-deploy")
                }
            }
        }
    }
}

tasks.jreleaserDeploy {
    dependsOn(tasks.publish)
}