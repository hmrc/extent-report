val appName = "extent-report"

lazy val extentreport = Project(appName, file("."))
  .enablePlugins(SbtAutoBuildPlugin, SbtGitVersioning, SbtArtifactory)
  .settings(
    scalacOptions ++= Seq(
      "-feature",
      "-language:implicitConversions"
    ),
    libraryDependencies ++= Seq(
      "org.seleniumhq.selenium" % "selenium-java" % "3.6.0",
      "info.cukes" % "cucumber-junit" % "1.2.4",
      "com.aventstack" % "extentreports" % "3.1.5" % "provided",
      "org.scalatest" %% "scalatest" % "3.0.3" % Test,
      "org.pegdown" % "pegdown" % "1.6.0" % Test,
      "org.scalacheck" %% "scalacheck" % "1.13.5" % Test
    ),
    resolvers := Seq(
      Resolver.bintrayRepo("hmrc", "releases"),
      "typesafe-releases" at "http://repo.typesafe.com/typesafe/releases/"
    ),
    crossScalaVersions := Seq("2.11.11", "2.12.3"),
    majorVersion := 0,
    makePublicallyAvailableOnBintray := true
  )
