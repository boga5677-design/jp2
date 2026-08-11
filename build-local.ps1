$ErrorActionPreference = "Stop"

$versionOutput = & java -XshowSettings:properties -version 2>&1
$line = $versionOutput | Select-String "java.specification.version"
$javaVersion = ($line.ToString() -split "=")[1].Trim()

if ($javaVersion -ne "17") {
    throw "需要 JDK 17，目前為 JDK $javaVersion。"
}

if (-not (Test-Path "gradle/wrapper/gradle-wrapper.jar")) {
    gradle wrapper --gradle-version 8.11.1 --distribution-type bin
}

.\gradlew.bat --no-daemon :app:assembleDebug --stacktrace
