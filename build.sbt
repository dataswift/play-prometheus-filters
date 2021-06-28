Global / onChangedBuildSource := ReloadOnSourceChanges

name := "play-prometheus-filters"
organization := "io.github.jyllands-posten"

version := "0.6.4"

lazy val root = project in file(".")

// All publishing configuration resides in sonatype.sbt
//publishTo := sonatypePublishToBundle.value
//credentials += Credentials(Path.userHome / ".sbt" / ".credentials.sonatype")

scalaVersion := "2.13.5"
//crossScalaVersions := Seq(scalaVersion.value, "2.12.12")

val playVersion             = "2.8.8"
val prometheusClientVersion = "0.9.0"

libraryDependencies ++= Seq(
  "io.prometheus" % "simpleclient"         % prometheusClientVersion,
  "io.prometheus" % "simpleclient_hotspot" % prometheusClientVersion,
  "io.prometheus" % "simpleclient_servlet" % prometheusClientVersion,
  // Play libs. Are provided not to enforce a specific version.
  "com.typesafe.play" %% "play"       % playVersion % Provided,
  "com.typesafe.play" %% "play-guice" % playVersion % Provided,
  // This library makes some Scala 2.13 APIs available on Scala 2.11 and 2.12.
  "org.scala-lang.modules" %% "scala-collection-compat" % "2.4.3"
)

libraryDependencies ++= Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0"   % Test,
  "org.scalatestplus"      %% "mockito-3-4"        % "3.2.8.0" % Test,
  "org.mockito"             % "mockito-core"       % "3.9.0"   % Test
)

scalacOptions ++= Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-encoding",
  "utf-8", // Specify character encoding used by source files.
  "-explaintypes", // Explain type errors in more detail.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
  "-language:experimental.macros", // Allow macro definition (besides implementation and application)
  "-language:higherKinds", // Allow higher-kinded types
  "-language:implicitConversions", // Allow definition of implicit functions called views
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xcheckinit", // Wrap field accessors to throw an exception on uninitialized access.
  //"-Xfatal-warnings",                  // Fail the compilation if there are any warnings.
  "-Xlint",
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-extra-implicit", // Warn when more than one implicit parameter section is defined.
  "-Ywarn-numeric-widen", // Warn when numerics are widened.
  "-Ywarn-unused:implicits", // Warn if an implicit parameter is unused.
  "-Ywarn-unused:imports", // Warn if an import selector is not referenced.
  "-Ywarn-unused:locals", // Warn if a local definition is unused.
  "-Ywarn-unused:params", // Warn if a value parameter is unused.
  "-Ywarn-unused:patvars", // Warn if a variable bound in a pattern is unused.
  "-Ywarn-unused:privates", // Warn if a private member is unused.
  "-Ywarn-value-discard" // Warn when non-Unit expression results are unused.
)

// Enable the semantic DB for scalafix
inThisBuild(
  List(
    scalaVersion := "2.13.5",
    scalafixScalaBinaryVersion := "2.13",
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.5.0"
  )
)
