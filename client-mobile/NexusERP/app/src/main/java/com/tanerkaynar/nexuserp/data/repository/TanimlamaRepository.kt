package com.tanerkaynar.nexuserp.data.repository

import com.tanerkaynar.nexuserp.data.api.RetrofitClient
import com.tanerkaynar.nexuserp.data.model.Makine
import com.tanerkaynar.nexuserp.data.model.Musteri
import com.tanerkaynar.nexuserp.data.model.Personel

class TanimlamaRepository {
    private val api get() = RetrofitClient.getApiService()

    suspend fun getMusteriler(): Result<List<Musteri>> = safeCall { api.getMusteriler() }
    suspend fun addMusteri(musteri: Musteri): Result<Musteri> = safeCall { api.addMusteri(musteri) }
    suspend fun updateMusteri(id: Int, musteri: Musteri): Result<Musteri> = safeCall { api.updateMusteri(id, musteri) }
    suspend fun deleteMusteri(id: Int): Result<Unit> = safeCall { api.deleteMusteri(id) }

    suspend fun getMakineler(): Result<List<Makine>> = safeCall { api.getMakineler() }
    suspend fun getAktifMakineler(): Result<List<Makine>> = safeCall { api.getAktifMakineler() }
    suspend fun addMakine(makine: Makine): Result<Makine> = safeCall { api.addMakine(makine) }
    suspend fun updateMakine(id: Int, makine: Makine): Result<Makine> = safeCall { api.updateMakine(id, makine) }
    suspend fun deleteMakine(id: Int): Result<Unit> = safeCall { api.deleteMakine(id) }

    suspend fun getPersoneller(): Result<List<Personel>> = safeCall { api.getPersoneller() }
    suspend fun getAktifPersoneller(): Result<List<Personel>> = safeCall { api.getAktifPersoneller() }
    suspend fun getKullaniciAtanmamisPersoneller(): Result<List<Personel>> = safeCall { api.getKullaniciAtanmamisPersoneller() }
    suspend fun addPersonel(personel: Personel): Result<Personel> = safeCall { api.addPersonel(personel) }
    suspend fun updatePersonel(id: Int, personel: Personel): Result<Personel> = safeCall { api.updatePersonel(id, personel) }
    suspend fun deletePersonel(id: Int): Result<Unit> = safeCall { api.deletePersonel(id) }
}