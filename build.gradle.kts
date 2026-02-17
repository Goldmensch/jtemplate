import org.jreleaser.version.SemanticVersion

allprojects {
  group = "MVN_GROUP"

  version = "0.1.0-SNAPSHOT"
  if (System.getenv("DEPLOY_ACTIVE") == "SNAPSHOT") {
    if (!version.toString().endsWith("-SNAPSHOT")) {
      val semver = SemanticVersion.of(version.toString())
      val snapshotSemver = SemanticVersion.of(semver.major, semver.minor + 1, "-", "SNAPSHOT", null)
      version = snapshotSemver.toString()
    }
  }
}
