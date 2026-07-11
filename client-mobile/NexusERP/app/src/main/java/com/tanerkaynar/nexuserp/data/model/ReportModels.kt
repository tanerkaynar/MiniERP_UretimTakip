package com.tanerkaynar.nexuserp.data.model

data class DurusAnalizi(
    val makineid: Int,
    val makineadi: String,
    val makinekodu: String,
    val durum: String
)

data class GrupluRapor(
    val grupName: String,
    val uretimkaydisayisi: Int,
    val toplamuretimadedi: Long
)