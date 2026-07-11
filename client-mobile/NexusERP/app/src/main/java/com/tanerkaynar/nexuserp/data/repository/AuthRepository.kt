package com.tanerkaynar.nexuserp.data.repository

import com.tanerkaynar.nexuserp.data.api.RetrofitClient
import com.tanerkaynar.nexuserp.data.model.LoginRequest
import com.tanerkaynar.nexuserp.data.model.LoginResponse
import com.tanerkaynar.nexuserp.data.model.RegisterRequest

class AuthRepository {
    private val api get() = RetrofitClient.getApiService()

    suspend fun login(kullaniciAdi: String, parola: String): Result<LoginResponse> {
        return safeCall { api.login(LoginRequest(kullaniciAdi, parola)) }
    }

    suspend fun register(kullaniciAdi: String, parola: String, rol: String, personelid: Int? = null): Result<Unit> {
        return safeCall { api.register(RegisterRequest(kullaniciAdi, parola, rol, personelid)) }
    }
}