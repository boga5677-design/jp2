# PetLingo v6.4 Local Settings

本版不需要登入，沒有Firebase、Google登入或雲端同步。

設定內容：
- 美式／英式發音
- 語音播放速度
- 預設10／20／40題
- 預設GEPT級數
- 音效與自動朗讀
- 解析顯示
- 錯題自動收錄
- 每日任務目標
- 淺色／深色／跟隨系統
- 本機資料統計與清除

所有設定使用Android SharedPreferences保存，學習紀錄仍保存在原本的本機資料庫。


## 固定更新簽章（v6.4.5 起）

GitHub Actions 產生的 debug APK 已固定使用 `signing/petlingo-update.jks`。請勿刪除、重新產生或更換此檔，否則 Android 會再次要求解除安裝舊版。此簽章是側載測試／個人使用的更新簽章，不建議拿來發布 Google Play 正式版。
