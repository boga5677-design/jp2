#!/bin/sh
set -eu

APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
WRAPPER_JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

if [ -f "$WRAPPER_JAR" ]; then
  if [ -n "${JAVA_HOME:-}" ]; then
    JAVACMD="$JAVA_HOME/bin/java"
  else
    JAVACMD="java"
  fi
  exec "$JAVACMD"     -classpath "$WRAPPER_JAR"     org.gradle.wrapper.GradleWrapperMain "$@"
fi

if command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
fi

echo "gradle-wrapper.jar 尚未產生。" >&2
echo "請執行 ./bootstrap-and-build.sh，或直接推送 GitHub 由 Actions 自動產生 Wrapper 並建置 APK。" >&2
exit 1
