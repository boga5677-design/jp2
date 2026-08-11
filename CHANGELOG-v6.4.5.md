# PetLingo v6.4.5

- versionCode 29 / versionName 6.4.5
- applicationId 維持 com.petlingo.learning。
- 專案加入固定 APK 更新簽章 `signing/petlingo-update.jks`，GitHub Actions 的 debug APK 從本版起固定使用同一把簽章。
- 淺色主題改為預設主題。
- 深色主題補齊 onBackground、onSurface、onSurfaceVariant、onPrimaryContainer 等高對比文字色，改善文字不明顯。
- 首頁標題與問候文字改用主題動態文字色，深色模式不再使用固定深色文字。

## 重要：第一次切換固定簽章
目前手機已安裝的舊版若是由 GitHub runner 臨時 debug keystore 簽署，其私人簽章金鑰已無法取得，因此 Android 不允許任何新金鑰直接覆蓋。升級到 6.4.5 時仍需最後解除安裝一次；安裝 6.4.5 後，6.4.6、6.5 等後續版本只要保留本專案的 signing/petlingo-update.jks，即可直接覆蓋更新，不必再刪除 App。
