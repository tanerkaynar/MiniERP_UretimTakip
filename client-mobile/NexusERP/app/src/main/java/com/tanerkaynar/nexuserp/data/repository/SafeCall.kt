package com.tanerkaynar.nexuserp.data.repository

import retrofit2.Response

suspend fun <T> safeCall(apiCall: suspend () -> Response<T>): Result<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                Result.success(body)
            } else {

                @Suppress("UNCHECKED_CAST")
                Result.success(Unit as T)
            }
        } else {
            val errorBody = response.errorBody()?.string()
            val message = parseErrorMessage(errorBody, response.code())
            Result.failure(Exception(message))
        }
    } catch (e: Exception) {
        Result.failure(Exception("Sunucu bağlantı hatası: ${e.localizedMessage}"))
    }
}

private fun parseErrorMessage(errorBody: String?, httpCode: Int): String {
    if (errorBody.isNullOrBlank()) {
        return httpCodeToMessage(httpCode)
    }

    try {
        val jsonObj = org.json.JSONObject(errorBody)

        val message = jsonObj.optString("message", "").trim()
        if (message.isNotEmpty() && message != "No message available") {
            return message
        }

        val error = jsonObj.optString("error", "").trim()
        if (error.isNotEmpty()) {
            return error
        }
    } catch (_: Exception) {
        
    }

    if (!errorBody.trimStart().startsWith("{") && !errorBody.trimStart().startsWith("[")) {
        return errorBody.take(200) 
    }

    return httpCodeToMessage(httpCode)
}

private fun httpCodeToMessage(code: Int): String {
    return when (code) {
        400 -> "Geçersiz istek. Lütfen girdiğiniz bilgileri kontrol edin."
        401 -> "Yetkilendirme hatası. Lütfen tekrar giriş yapın."
        403 -> "Bu işlem için yetkiniz bulunmuyor."
        404 -> "İstenen kayıt bulunamadı."
        409 -> "Bu kayıt zaten mevcut veya çakışma var."
        422 -> "Gönderilen veriler geçersiz."
        500 -> "Sunucu hatası oluştu. Lütfen daha sonra tekrar deneyin."
        502 -> "Sunucu şu anda erişilemez durumda."
        503 -> "Sunucu bakımda. Lütfen daha sonra tekrar deneyin."
        else -> "İşlem başarısız oldu. (Hata kodu: $code)"
    }
}