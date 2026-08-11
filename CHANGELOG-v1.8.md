# NekoNihon v1.8

- Fix GitHub merge-upload issue: CI deletes the entire legacy `app/src/main/java/com/petlingo` tree before compilation.
- Verify only `com/nekonihon/app` Kotlin sources are compiled.
- Keep release variant disabled.
- Build only `assembleDebug`.
- Upload exactly one file: `NekoNihon.apk`.
