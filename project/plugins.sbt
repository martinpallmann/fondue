resolvers += "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"
addSbtCoursier
coursierVerbosity := -1
addSbtPlugin("ch.epfl.scala"    % "sbt-scalafix"        % "0.9.4")
addSbtPlugin("com.codacy"       % "sbt-codacy-coverage" % "2.3")
addSbtPlugin("com.eed3si9n"     % "sbt-assembly"        % "0.14.9")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.20")
addSbtPlugin("org.scalameta"    % "sbt-scalafmt"        % "2.0.0")
addSbtPlugin("org.scoverage"    % "sbt-scoverage"       % "1.5.1")
