object ScalacOptions {
  def apply(): List[String] = List(
    "-language:higherKinds",
    "-language:existentials",
    "-explaintypes",
    "-Xfuture",
    "-Ypartial-unification",
//    "-Xfatal-warnings",
    "-deprecation",
    "-feature",
    "-unchecked",
    "-Xlint:_",
    "-Yrangepos",
    "-Ywarn-value-discard",
    "-Ywarn-numeric-widen",
    "-Ywarn-extra-implicit",
    "-Ywarn-unused:_",
    "-Ywarn-inaccessible",
    "-Ywarn-infer-any",
    "-Ywarn-nullary-override",
    "-Ywarn-nullary-unit",
    "-opt:l:inline"
  )
}
