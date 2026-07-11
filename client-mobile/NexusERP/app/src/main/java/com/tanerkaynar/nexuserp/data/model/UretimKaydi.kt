package com.tanerkaynar.nexuserp.data.model

data class UretimKaydi(
    val uretimid: Int = 0,
    val urunadi: String?,
    val makineadi: String?,
    val personeladi: String?,
    val uretimadedi: Int,
    val uretimtarihi: String?,
    val aciklama: String?
)

data class UretimRequest(
    val urunid: Int,
    val makineid: Int,
    val personelid: Int,
    val uretimadedi: Int,
    val aciklama: String
)