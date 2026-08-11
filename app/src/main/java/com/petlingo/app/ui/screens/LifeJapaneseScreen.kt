package com.petlingo.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class LifeLesson(
    val level: String,
    val scene: Int,
    val title: String,
    val goal: String,
    val phrases: List<Pair<String, String>>
)

private fun quick(scene: Int, title: String, goal: String, vararg phrases: Pair<String, String>) =
    LifeLesson("便利語句", scene, title, goal, phrases.toList())
private fun a1(scene: Int, title: String, goal: String, vararg phrases: Pair<String, String>) =
    LifeLesson("A1", scene, title, goal, phrases.toList())
private fun a2(scene: Int, title: String, goal: String, vararg phrases: Pair<String, String>) =
    LifeLesson("A2", scene, title, goal, phrases.toList())
private fun b1(scene: Int, title: String, goal: String, vararg phrases: Pair<String, String>) =
    LifeLesson("B1", scene, title, goal, phrases.toList())

private val lifeLessons = listOf(
    quick(1, "想先跟別人搭話", "學會禮貌地引起對方注意。", "すみません" to "不好意思／請問", "ちょっといいですか" to "可以打擾一下嗎？"),
    quick(2, "購物時", "在商店快速說出基本需求。", "これをください" to "請給我這個", "いくらですか" to "多少錢？"),
    quick(3, "點餐時", "在餐廳完成簡單點餐。", "これをお願いします" to "我要這個，麻煩了", "おすすめは何ですか" to "推薦的是什麼？"),
    quick(4, "遇到鄰居時", "用自然簡短的方式打招呼。", "おはようございます" to "早安", "こんにちは" to "你好"),
    quick(5, "拜訪別人家時", "學會進門與告辭時的禮貌用語。", "おじゃまします" to "打擾了", "おじゃましました" to "打擾您了"),
    quick(6, "想問問題時", "用禮貌方式確認不知道的事情。", "これは何ですか" to "這是什麼？", "もう一度お願いします" to "請再說一次"),

    a1(1, "打招呼", "能依時間與場合使用基本問候。", "はじめまして" to "初次見面", "よろしくお願いします" to "請多指教"),
    a1(2, "購買身邊常用物品", "能在超市或便利商店購買日用品。", "これ、ください" to "請給我這個", "袋は要りません" to "不需要袋子"),
    a1(3, "詢問賣場與價格", "能詢問商品位置及價格。", "牛乳はどこですか" to "牛奶在哪裡？", "これはいくらですか" to "這個多少錢？"),
    a1(4, "挑選想要的商品", "能比較大小、顏色與數量。", "大きいのはありますか" to "有大一點的嗎？", "この色がいいです" to "我想要這個顏色"),
    a1(5, "向店員表達需求", "能提出簡單且具體的購物需求。", "もう少し安いのはありますか" to "有再便宜一點的嗎？", "試してもいいですか" to "可以試用／試穿嗎？"),
    a1(6, "到餐廳用餐", "能入店、點餐並結帳。", "二人です" to "兩位", "お会計お願いします" to "麻煩結帳"),
    a1(7, "使用宅配服務", "能寄送包裹並確認基本資訊。", "これを送りたいです" to "我想寄這個", "いつ届きますか" to "什麼時候會到？"),
    a1(8, "搭電車", "能確認目的地、月台及轉乘。", "この電車は新宿に行きますか" to "這班電車去新宿嗎？", "何番線ですか" to "是第幾月台？"),
    a1(9, "問路", "能詢問目的地並理解簡單方向。", "駅はどこですか" to "車站在哪裡？", "まっすぐですか" to "是直走嗎？"),
    a1(10, "使用銀行", "能表達開戶、提款等基本需求。", "口座を作りたいです" to "我想開戶", "ATMはどこですか" to "ATM在哪裡？"),
    a1(11, "理解居民生活禮儀", "能理解垃圾分類與共同生活規則。", "ごみは何曜日ですか" to "垃圾星期幾收？", "ここに出してもいいですか" to "可以放這裡嗎？"),
    a1(12, "使用職場機械", "能確認操作方式並請求協助。", "使い方を教えてください" to "請教我怎麼使用", "これでいいですか" to "這樣可以嗎？"),

    a2(1, "依場合打招呼", "能依對象與場合調整問候方式。", "お世話になっています" to "承蒙關照", "お先に失礼します" to "我先告辭了"),
    a2(2, "使用商店服務", "能詢問退換貨、配送等服務。", "交換できますか" to "可以換貨嗎？", "配送をお願いします" to "麻煩幫我配送"),
    a2(3, "選擇店家", "能詢問店家特色與比較條件。", "近くにスーパーはありますか" to "附近有超市嗎？", "どちらが安いですか" to "哪一個比較便宜？"),
    a2(4, "使用各種商店", "能在不同服務場所完成基本互動。", "予約したいです" to "我想預約", "空いていますか" to "有空位／空檔嗎？"),
    a2(5, "更順利地購物", "能確認成分、用途與優惠。", "これは何に使いますか" to "這個用來做什麼？", "割引はありますか" to "有折扣嗎？"),
    a2(6, "加入自治會", "能詢問社區活動與參加方式。", "自治会に入りたいです" to "我想加入自治會", "次の集まりはいつですか" to "下次聚會是什麼時候？"),
    a2(7, "參加活動", "能詢問活動內容、時間與報名。", "参加したいです" to "我想參加", "申し込みは必要ですか" to "需要報名嗎？"),
    a2(8, "去醫院", "能描述症狀並理解簡單問診。", "昨日から熱があります" to "從昨天開始發燒", "ここが痛いです" to "這裡會痛"),
    a2(9, "緊急時求助", "能在危急情況簡短清楚地求助。", "助けてください" to "請救我／幫幫我", "救急車を呼んでください" to "請叫救護車"),
    a2(10, "到公所辦事", "能說明要辦理的行政事項。", "住所を変更したいです" to "我想變更地址", "必要な書類は何ですか" to "需要哪些文件？"),
    a2(11, "使用圖書館", "能辦證、借書與查詢。", "利用カードを作りたいです" to "我想辦借閱證", "この本を借りられますか" to "這本書可以借嗎？"),
    a2(12, "寄明信片", "能在郵局購買郵票與寄件。", "台湾までいくらですか" to "寄到台灣多少錢？", "切手をください" to "請給我郵票"),
    a2(13, "使用網路與電話", "能處理簡單電話及網路需求。", "あとでかけ直します" to "我晚點再打", "Wi-Fiは使えますか" to "可以使用Wi‑Fi嗎？"),
    a2(14, "工作時注意安全", "能確認危險與安全規則。", "ここは危ないです" to "這裡很危險", "安全のために確認します" to "為了安全我再確認一下"),
    a2(15, "處理職場事故", "能報告事故並說明受傷狀況。", "けがをしました" to "我受傷了", "上司に連絡してください" to "請聯絡主管"),
    a2(16, "找工作", "能詢問職缺與工作條件。", "求人を見ました" to "我看到徵才資訊", "勤務時間を教えてください" to "請告訴我工作時間"),
    a2(17, "參加面試", "能做簡單自我介紹並回答工作相關問題。", "よろしくお願いいたします" to "請多多指教（正式）", "経験があります" to "我有經驗"),
    a2(18, "與同事互動", "能確認工作安排與請求協助。", "手伝ってもらえますか" to "可以幫我一下嗎？", "終わったら知らせます" to "完成後我會通知你"),
    a2(19, "申請早退或休假", "能說明原因並禮貌請假。", "今日は早退したいです" to "今天我想早退", "明日、休んでもいいですか" to "明天可以請假嗎？"),

    b1(1, "在職場打招呼", "能依職場關係進行自然問候與寒暄。", "今日もよろしくお願いします" to "今天也請多多指教", "昨日はありがとうございました" to "昨天謝謝您"),
    b1(2, "靠自己前往目的地", "能查詢路線並處理迷路或轉乘。", "乗り換えはどこですか" to "在哪裡轉車？", "この道で合っていますか" to "這條路對嗎？"),
    b1(3, "安全移動", "能理解提醒並主動確認交通安全資訊。", "気をつけて渡りましょう" to "小心過馬路吧", "通行止めですか" to "禁止通行嗎？"),
    b1(4, "使用藥局", "能說明症狀並理解用藥說明。", "この薬は一日何回ですか" to "這個藥一天吃幾次？", "食後に飲みますか" to "飯後吃嗎？"),
    b1(5, "思考防災", "能討論地震、避難與防災物資。", "避難所はどこですか" to "避難所在哪裡？", "非常持ち出し袋を準備しています" to "我有準備緊急避難包"),
    b1(6, "預防疾病", "能討論健康管理與預防方法。", "予防のために手を洗います" to "為了預防會洗手", "体調が悪いときは休みます" to "身體不舒服時會休息"),
    b1(7, "參與地區活動", "能理解活動內容並表達自己的意見。", "地域の活動に参加したいです" to "我想參加地區活動", "何か手伝えることはありますか" to "有什麼我可以幫忙的嗎？"),
    b1(8, "找搬家住所", "能比較房租、設備與交通條件。", "家賃はいくらですか" to "房租多少？", "駅から何分ですか" to "離車站幾分鐘？"),
    b1(9, "準備搬家", "能處理搬家公司、契約與地址變更。", "引っ越しを予定しています" to "我預計要搬家", "電気を止めたいです" to "我想停止電力服務"),
    b1(10, "準備生產", "能詢問產檢與生產準備事項。", "出産の準備について教えてください" to "請告訴我生產準備事項", "次の健診はいつですか" to "下次產檢是什麼時候？"),
    b1(11, "諮詢生產", "能向醫療人員說明疑問與需求。", "少し不安があります" to "我有點不安", "相談してもいいですか" to "可以請教一下嗎？"),
    b1(12, "使用育兒服務", "能詢問育兒支援與申請方式。", "子育て支援について知りたいです" to "我想了解育兒支援", "申し込み方法を教えてください" to "請告訴我申請方式"),
    b1(13, "帶孩子去醫院", "能描述孩子的症狀與變化。", "子どもが咳をしています" to "孩子在咳嗽", "食欲がありません" to "沒有食慾"),
    b1(14, "使用托育設施", "能詢問入園、時間與注意事項。", "保育園について相談したいです" to "我想諮詢保育園", "何時まで預けられますか" to "可以托育到幾點？"),
    b1(15, "諮詢育兒", "能表達育兒上的困難並尋求建議。", "夜、なかなか寝ません" to "晚上很難入睡", "どうしたらいいでしょうか" to "我該怎麼辦呢？"),
    b1(16, "準備入學", "能確認入學文件與用品。", "入学に必要なものは何ですか" to "入學需要哪些東西？", "提出期限はいつですか" to "繳交期限是什麼時候？"),
    b1(17, "理解學校生活", "能理解學校行程、通知與規則。", "学校からのお知らせを確認しました" to "我確認了學校通知", "持ち物を教えてください" to "請告訴我需要攜帶的物品"),
    b1(18, "處理學校生活問題", "能與老師溝通孩子遇到的問題。", "子どものことで相談があります" to "我想談談孩子的事情", "最近、元気がありません" to "最近看起來沒精神"),
    b1(19, "諮詢升學與未來", "能討論孩子未來的學習方向。", "進路について相談したいです" to "我想諮詢升學方向", "どんな学校がありますか" to "有哪些學校呢？"),
    b1(20, "與寵物一起生活", "能處理飼養規則、醫院與社區互動。", "ペットを飼ってもいいですか" to "可以養寵物嗎？", "動物病院は近くにありますか" to "附近有動物醫院嗎？"),
    b1(21, "企劃活動", "能提出想法、分工並協調活動。", "イベントを企画しましょう" to "來企劃活動吧", "私は受付を担当します" to "我負責接待"),
    b1(22, "享受自己的休閒時間", "能談論興趣、休閒與邀約。", "週末は映画を見ています" to "週末我會看電影", "一緒に行きませんか" to "要不要一起去？"),
    b1(23, "用各種方法學日文", "能說明自己的學習方式並尋找適合資源。", "毎日、少しずつ勉強しています" to "每天一點一點地學", "わからない言葉を調べます" to "我會查不懂的詞"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifeJapaneseScreen(onBack: () -> Unit) {
    var selected by remember { mutableStateOf<LifeLesson?>(null) }
    var level by remember { mutableStateOf("全部") }
    val uriHandler = LocalUriHandler.current

    if (selected != null) {
        val lesson = selected!!
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("${lesson.level}・場景 ${lesson.scene}") },
                    navigationIcon = {
                        IconButton(onClick = { selected = null }) {
                            Icon(Icons.Default.ArrowBack, "返回")
                        }
                    }
                )
            }
        ) { padding ->
            LazyColumn(
                Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(lesson.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text(lesson.goal, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                item {
                    Text("練習語句", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                items(lesson.phrases) { phrase ->
                    Card(shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(phrase.first, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(phrase.second, style = MaterialTheme.typography.bodyLarge)
                            Text("先看中文想日文，再把日文大聲說 3 次。", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("練習方式", fontWeight = FontWeight.Bold)
                            Text("① 想像真實生活場景  ② 說出關鍵句  ③ 換入自己的地點、物品或需求再說一次。")
                        }
                    }
                }
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("生活日語") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "返回") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("依日本生活情境學日文", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("課程架構參考日本文部科學省「つながるひろがる にほんごでのくらし」的便利語句與 A1／A2／B1 生活場景；App 內例句重新整理為繁體中文學習版。", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = { uriHandler.openUri("https://tsunagarujp.mext.go.jp/?lang_id=TW") }) {
                    Icon(Icons.Default.Language, null)
                    Spacer(Modifier.width(8.dp))
                    Text("開啟文部科學省原始教材")
                }
            }
            item {
                SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                    listOf("全部", "便利語句", "A1", "A2", "B1").forEachIndexed { index, item ->
                        SegmentedButton(
                            selected = level == item,
                            onClick = { level = item },
                            shape = SegmentedButtonDefaults.itemShape(index, 5)
                        ) { Text(item) }
                    }
                }
            }
            items(lifeLessons.filter { level == "全部" || it.level == level }) { lesson ->
                Card(onClick = { selected = lesson }, shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth().padding(16.dp)) {
                        Column(Modifier.weight(1f)) {
                            Text("${lesson.level}・場景 ${lesson.scene}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                            Text(lesson.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(lesson.goal, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Icon(Icons.Default.PlayArrow, null)
                    }
                }
            }
        }
    }
}
