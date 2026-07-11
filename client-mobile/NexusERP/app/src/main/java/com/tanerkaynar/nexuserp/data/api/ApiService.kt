package com.tanerkaynar.nexuserp.data.api

import com.tanerkaynar.nexuserp.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    @POST("api/kullanicilar/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/kullanicilar/register")
    suspend fun register(@Body request: RegisterRequest): Response<Unit>

    @GET("api/dashboard/stats")
    suspend fun getDashboardStats(@Query("rol") rol: String): Response<Map<String, Any>>

    @GET("api/urunler")
    suspend fun getUrunler(): Response<List<Urun>>

    @GET("api/urunler/aktif")
    suspend fun getAktifUrunler(): Response<List<Urun>>

    @POST("api/urunler")
    suspend fun addUrun(@Body urun: Urun): Response<Urun>

    @PUT("api/urunler/{id}")
    suspend fun updateUrun(@Path("id") id: Int, @Body urun: Urun): Response<Urun>

    @DELETE("api/urunler/{id}")
    suspend fun deleteUrun(@Path("id") id: Int): Response<Unit>

    @GET("api/urunler/{id}/stok")
    suspend fun getUrunStok(@Path("id") id: Int): Response<Int>

    @GET("api/urunler/stok-takip")
    suspend fun getStokTakip(): Response<List<Map<String, Any>>>

    @GET("api/musteriler")
    suspend fun getMusteriler(): Response<List<Musteri>>

    @POST("api/musteriler")
    suspend fun addMusteri(@Body musteri: Musteri): Response<Musteri>

    @PUT("api/musteriler/{id}")
    suspend fun updateMusteri(@Path("id") id: Int, @Body musteri: Musteri): Response<Musteri>

    @DELETE("api/musteriler/{id}")
    suspend fun deleteMusteri(@Path("id") id: Int): Response<Unit>

    @GET("api/makineler")
    suspend fun getMakineler(): Response<List<Makine>>

    @GET("api/makineler/aktif")
    suspend fun getAktifMakineler(): Response<List<Makine>>

    @POST("api/makineler")
    suspend fun addMakine(@Body makine: Makine): Response<Makine>

    @PUT("api/makineler/{id}")
    suspend fun updateMakine(@Path("id") id: Int, @Body makine: Makine): Response<Makine>

    @DELETE("api/makineler/{id}")
    suspend fun deleteMakine(@Path("id") id: Int): Response<Unit>

    @GET("api/personeller")
    suspend fun getPersoneller(): Response<List<Personel>>

    @GET("api/personeller/aktif")
    suspend fun getAktifPersoneller(): Response<List<Personel>>

    @GET("api/personeller/kullanici-atanmamis")
    suspend fun getKullaniciAtanmamisPersoneller(): Response<List<Personel>>

    @POST("api/personeller")
    suspend fun addPersonel(@Body personel: Personel): Response<Personel>

    @PUT("api/personeller/{id}")
    suspend fun updatePersonel(@Path("id") id: Int, @Body personel: Personel): Response<Personel>

    @DELETE("api/personeller/{id}")
    suspend fun deletePersonel(@Path("id") id: Int): Response<Unit>

    @GET("api/siparisler")
    suspend fun getSiparisler(): Response<List<Siparis>>

    @GET("api/siparisler/bekleyen")
    suspend fun getBekleyenSiparisler(): Response<List<Siparis>>

    @POST("api/siparisler")
    suspend fun addSiparis(@Body request: SiparisRequest): Response<Unit>

    @POST("api/siparisler/{id}/sevk")
    suspend fun sevkEt(@Path("id") id: Int): Response<Unit>

    @PUT("api/siparisler/{id}")
    suspend fun updateSiparis(@Path("id") id: Int, @Body request: SiparisRequest): Response<Unit>

    @DELETE("api/siparisler/{id}")
    suspend fun deleteSiparis(@Path("id") id: Int): Response<Unit>

    @GET("api/uretim")
    suspend fun getUretimKayitlari(): Response<List<UretimKaydi>>

    @POST("api/uretim")
    suspend fun addUretim(@Body request: UretimRequest): Response<Unit>

    @DELETE("api/uretim/{id}")
    suspend fun deleteUretim(@Path("id") id: Int): Response<Unit>

    @GET("api/uretim/rapor")
    suspend fun getTarihliUretimRaporu(
        @Query("baslangic") baslangic: String,
        @Query("bitis") bitis: String
    ): Response<List<UretimKaydi>>

    @GET("api/uretim/gruplu")
    suspend fun getGrupluRapor(@Query("tip") tip: String): Response<List<Map<String, Any>>>

    @GET("api/uretim/durus")
    suspend fun getDurusAnalizi(
        @Query("baslangic") baslangic: String,
        @Query("bitis") bitis: String
    ): Response<List<Map<String, Any>>>

    @GET("api/loglar")
    suspend fun getLoglar(): Response<List<IslemLog>>
}