package com.tanerkaynar.nexuserp.data.repository

import com.tanerkaynar.nexuserp.data.api.RetrofitClient
import com.tanerkaynar.nexuserp.data.model.Siparis
import com.tanerkaynar.nexuserp.data.model.SiparisRequest

class SiparisRepository {
    private val api get() = RetrofitClient.getApiService()

    suspend fun getAll(): Result<List<Siparis>> = safeCall { api.getSiparisler() }
    suspend fun getBekleyen(): Result<List<Siparis>> = safeCall { api.getBekleyenSiparisler() }
    suspend fun add(request: SiparisRequest): Result<Unit> = safeCall { api.addSiparis(request) }
    suspend fun sevkEt(id: Int): Result<Unit> = safeCall { api.sevkEt(id) }
    suspend fun update(id: Int, request: SiparisRequest): Result<Unit> = safeCall { api.updateSiparis(id, request) }
    suspend fun delete(id: Int): Result<Unit> = safeCall { api.deleteSiparis(id) }
}