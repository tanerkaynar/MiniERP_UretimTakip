package com.tanerkaynar.nexuserp.data.model

data class IslemLog(
    val logid: Int = 0,
    val kullaniciadi: String,
    val islemturu: String,
    val aciklama: String,
    val islemtarihi: String?
)