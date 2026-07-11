package com.tanerkaynar.nexuserp.data.repository

import com.tanerkaynar.nexuserp.data.api.RetrofitClient
import com.tanerkaynar.nexuserp.data.model.Urun

class UrunRepository {
    private val api get() = RetrofitClient.getApiService()

    suspend fun getAll(): Result<List<Urun>> = safeCall { api.getUrunler() }
    suspend fun getAktif(): Result<List<Urun>> = safeCall { api.getAktifUrunler() }
    suspend fun add(urun: Urun): Result<Urun> = safeCall { api.addUrun(urun) }
    suspend fun update(id: Int, urun: Urun): Result<Urun> = safeCall { api.updateUrun(id, urun) }
    suspend fun delete(id: Int): Result<Unit> = safeCall { api.deleteUrun(id) }
    suspend fun getStok(id: Int): Result<Int> = safeCall { api.getUrunStok(id) }
    suspend fun getStokTakip(): Result<List<Map<String, Any>>> = safeCall { api.getStokTakip() }
}