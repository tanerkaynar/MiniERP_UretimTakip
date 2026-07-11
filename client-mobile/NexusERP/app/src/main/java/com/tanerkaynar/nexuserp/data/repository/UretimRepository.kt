package com.tanerkaynar.nexuserp.data.repository

import com.tanerkaynar.nexuserp.data.api.RetrofitClient
import com.tanerkaynar.nexuserp.data.model.UretimKaydi
import com.tanerkaynar.nexuserp.data.model.UretimRequest

class UretimRepository {
    private val api get() = RetrofitClient.getApiService()

    suspend fun getAll(): Result<List<UretimKaydi>> = safeCall { api.getUretimKayitlari() }
    suspend fun add(request: UretimRequest): Result<Unit> = safeCall { api.addUretim(request) }
    suspend fun delete(id: Int): Result<Unit> = safeCall { api.deleteUretim(id) }
    suspend fun getRapor(baslangic: String, bitis: String): Result<List<UretimKaydi>> =
        safeCall { api.getTarihliUretimRaporu(baslangic, bitis) }
    suspend fun getGruplu(tip: String): Result<List<Map<String, Any>>> =
        safeCall { api.getGrupluRapor(tip) }
    suspend fun getDurus(baslangic: String, bitis: String): Result<List<Map<String, Any>>> =
        safeCall { api.getDurusAnalizi(baslangic, bitis) }
}