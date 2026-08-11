#!/usr/bin/env bash
set -euo pipefail

JAVA_VERSION=$(java -XshowSettings:properties -version 2>&1 |
  awk '/java.specification.version/ {print $3}')

if [ "$JAVA_VERSION" != "17" ]; then
  echo "需要 JDK 17，目前為 JDK $JAVA_VERSION。"
  exit 1
fi

if [ ! -f gradle/wrapper/gradle-wrapper.jar ]; then
  gradle wrapper --gradle-version 8.11.1 --distribution-type bin
fi

chmod +x gradlew
./gradlew --no-daemon :app:assembleDebug --stacktrace
