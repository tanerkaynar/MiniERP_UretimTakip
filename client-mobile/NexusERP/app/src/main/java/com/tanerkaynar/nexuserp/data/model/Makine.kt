package com.tanerkaynar.nexuserp.data.model

data class Makine(
    val makineid: Int? = null,
    val makineadi: String,
    val makinekodu: String,
    val durum: String = "Aktif"
)