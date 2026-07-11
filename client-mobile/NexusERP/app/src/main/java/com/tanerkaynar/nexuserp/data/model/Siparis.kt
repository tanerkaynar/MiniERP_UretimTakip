package com.tanerkaynar.nexuserp.data.model

data class Siparis(
    val siparisid: Int = 0,
    val musteriid: Int = 0,
    val urunid: Int = 0,
    val musteriadi: String?,
    val urunadi: String?,
    val miktar: Int,
    val mevcutstok: Int = 0,
    val birimfiyat: Double,
    val siparistarihi: String?,
    val durum: String?
)

data class SiparisRequest(
    val musteriid: Int,
    val urunid: Int,
    val miktar: Int,
    val birimfiyat: Double
)