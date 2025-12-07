scalaVersion := "3.3.7"

version := "0.4"

scalacOptions ++= Seq(
  "-deprecation",
  "-feature"
)

lazy val root = (project in file("."))
  .settings(
    name := "kuzminki-zhttp-demo",
    libraryDependencies ++= Seq(
      "com.typesafe"             % "config"         % "1.4.1",
      "dev.zio"                 %% "zio"            % "2.1.22",
      "dev.zio"                 %% "zio-streams"    % "2.1.22",
      "dev.zio"                 %% "zio-http"       % "3.7.0",
      "dev.zio"                 %% "zio-json"       % "0.7.45",
      "io.github.karimagnusson" %% "kuzminki-zio-2" % "0.9.5"
    ),
    run / fork := true,
    run / connectInput := true
  )
