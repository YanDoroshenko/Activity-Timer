scalaVersion := "2.11.12"

enablePlugins(AndroidApp)
android.useSupportVectors

versionCode := Some(1)
version := "0.1-SNAPSHOT"

instrumentTestRunner :=
  "android.support.test.runner.AndroidJUnitRunner"

platformTarget := "android-24"

javacOptions in Compile ++= "-source" :: "1.8" :: "-target" :: "1.8" :: Nil

libraryDependencies ++=
"com.android.support" % "appcompat-v7" % "24.0.0" ::
  "com.android.support.test" % "runner" % "0.5" % "androidTest" ::
  "com.android.support.test.espresso" % "espresso-core" % "2.2.2" % "androidTest" ::
  "org.scalatest" %% "scalatest" % "3.0.8" % Test ::
  Nil