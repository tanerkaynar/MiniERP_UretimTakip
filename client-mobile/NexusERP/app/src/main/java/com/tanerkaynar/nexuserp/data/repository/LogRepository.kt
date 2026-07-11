package com.tanerkaynar.nexuserp.data.repository

import com.tanerkaynar.nexuserp.data.api.RetrofitClient
import com.tanerkaynar.nexuserp.data.model.IslemLog

class LogRepository {
    private val api get() = RetrofitClient.getApiService()

    suspend fun getAll(): Result<List<IslemLog>> = safeCall { api.getLoglar() }
}