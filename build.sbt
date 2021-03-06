name := "sinsim"

version := "0.1.0"

scalaVersion := "2.11.8"

cancelable in Global := true

mainClass in(Compile, run) := Some("sinsim.cmd.Main")

resolvers += "tilab" at "http://jade.tilab.com/maven"

libraryDependencies ++= Seq(
  "commons-codec" % "commons-codec" % "1.3",
  "com.tilab.jade" % "jade" % "4.4.0",
  "com.tilab.jade" % "jade-test-suite" % "1.13.0",
  "org.slf4j" % "slf4j-api" % "1.7.21",
  "org.slf4j" % "slf4j-simple" % "1.7.21"
)
