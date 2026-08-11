# NekoNihon v1.6

- App 顯示名稱固定為 NekoNihon。
- Gradle root project 改名為 NekoNihon。
- 暫時停用舊 PetLingo launcher 圖示，改用 Android 系統預設圖示，避免桌面仍顯示 PetLingo；正式美工後再替換。
- GitHub Actions 每次先刪除舊 APK 輸出，只建置 assembleDebug。
- Artifact staging 只允許一個 APK，並重新命名為 NekoNihon.apk。
- applicationId 與既有日文版簽名維持不變，以保留更新相容性並與英文版分開。
