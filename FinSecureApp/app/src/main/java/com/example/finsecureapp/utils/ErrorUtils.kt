package com.example.finsecureapp.utils

import org.json.JSONObject

object ErrorUtils {
    fun extractMessage(raw: String?): String {
        if (raw.isNullOrBlank()) return "Unknown error"

        return try {
            val json = JSONObject(raw)
            json.optString("message", raw)
        } catch (_: Exception) {
            raw
        }
    }
}