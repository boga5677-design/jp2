package com.petlingo.app.data

import android.content.Context
import com.petlingo.app.model.WrongAnswer
import org.json.JSONArray
import org.json.JSONObject

class WrongAnswerStore(context: Context) {
    private val prefs = context.getSharedPreferences("wrong_answers", Context.MODE_PRIVATE)

    fun load(): List<WrongAnswer> {
        val raw = prefs.getString("items", "[]") ?: "[]"
        val array = runCatching { JSONArray(raw) }.getOrElse { JSONArray() }
        return buildList {
            for (i in 0 until array.length()) {
                val o = array.getJSONObject(i)
                add(WrongAnswer(o.getString("key"), o.getString("prompt"), o.getString("selected"), o.getString("correct"), o.optString("explanation"), o.optString("type"), o.optLong("elapsed"), o.optInt("count",1), o.optLong("last")))
            }
        }.sortedByDescending { it.lastWrongAt }
    }

    fun add(item: WrongAnswer) {
        val list = load().toMutableList()
        val index = list.indexOfFirst { it.key == item.key }
        if (index >= 0) list[index] = item.copy(wrongCount = list[index].wrongCount + 1) else list.add(item)
        save(list)
    }

    fun remove(key: String) = save(load().filterNot { it.key == key })
    fun clear() = prefs.edit().remove("items").apply()

    private fun save(items: List<WrongAnswer>) {
        val array = JSONArray()
        items.forEach { item -> array.put(JSONObject().apply {
            put("key", item.key); put("prompt", item.prompt); put("selected", item.selectedAnswer); put("correct", item.correctAnswer)
            put("explanation", item.explanation); put("type", item.typeLabel); put("elapsed", item.elapsedMillis); put("count", item.wrongCount); put("last", item.lastWrongAt)
        }) }
        prefs.edit().putString("items", array.toString()).apply()
    }
}
