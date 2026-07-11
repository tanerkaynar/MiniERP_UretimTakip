package com.tanerkaynar.nexuserp.data.model

data class LoginRequest(
    val kullaniciadi: String,
    val parola: String
)

data class LoginResponse(
    val kullaniciid: Int,
    val kullaniciadi: String,
    val rol: String,
    val aktifmi: Boolean,
    val personel: Personel? = null
)

data class RegisterRequest(
    val kullaniciadi: String,
    val parola: String,
    val rol: String,
    val personelid: Int? = null
)