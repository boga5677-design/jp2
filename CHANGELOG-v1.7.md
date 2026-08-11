# NekoNihon v1.7

- App display name fixed to NekoNihon.
- Internal Kotlin package renamed from com.petlingo.app to com.nekonihon.app.
- Core class names renamed to NekoNihonApp / NekoNihonViewModel / NekoNihonTheme.
- Android release variant is disabled at Gradle variant level.
- GitHub Actions builds only assembleDebug.
- CI deletes all old APKs before build, fails if a release APK appears, and uploads exactly one file: NekoNihon.apk.
