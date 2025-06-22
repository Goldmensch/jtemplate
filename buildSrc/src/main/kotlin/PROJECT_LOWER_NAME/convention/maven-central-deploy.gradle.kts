package PROJECT_LOWER_NAME.convention

import gradle.kotlin.dsl.accessors._91ec805279bff2b5bb12917c8cd87938.jreleaser
import gradle.kotlin.dsl.accessors._91ec805279bff2b5bb12917c8cd87938.jreleaserDeploy
import gradle.kotlin.dsl.accessors._91ec805279bff2b5bb12917c8cd87938.publish
import gradle.kotlin.dsl.accessors._91ec805279bff2b5bb12917c8cd87938.publishing
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
                name.set(rootProject.name)
                description.set(project.description)
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
                create("sonatype") {
                    active = Active.ALWAYS
                    url = "https://central.sonatype.com/api/v1/publisher"
                    stagingRepository("build/staging-deploy")
                    setStage("UPLOAD")
                }
            }
        }
    }
}

tasks.jreleaserDeploy {
    dependsOn(tasks.publish)
}