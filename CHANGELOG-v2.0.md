# NekoNihon v2.0

- Based directly on the uploaded jp2-main (2) project.
- Removed the obsolete GitHub Actions `assembleRelease` step.
- Removed release APK paths from artifact upload.
- CI now builds only `assembleDebug`.
- The only uploaded artifact file is `NekoNihon.apk`.
- Added checks that fail the workflow if more than one APK is staged or if a release APK is unexpectedly generated.
- Android release variant remains disabled in `app/build.gradle.kts`.
