package com.tanerkaynar.nexuserp.data.model

data class Urun(
    val urunid: Int? = null,
    val urunadi: String,
    val stokmiktari: Int,
    val birimfiyat: Double,
    val aktifmi: Boolean = true
)