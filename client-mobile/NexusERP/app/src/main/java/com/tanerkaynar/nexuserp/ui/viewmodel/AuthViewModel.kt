package com.tanerkaynar.nexuserp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tanerkaynar.nexuserp.data.model.LoginResponse
import com.tanerkaynar.nexuserp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface LoginUiState {
    object Idle : LoginUiState
    object Loading : LoginUiState
    data class Success(val data: LoginResponse) : LoginUiState
    data class Error(val message: String) : LoginUiState
}

sealed interface RegisterUiState {
    object Idle : RegisterUiState
    object Loading : RegisterUiState
    object Success : RegisterUiState
    data class Error(val message: String) : RegisterUiState
}

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _registerState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val registerState = _registerState.asStateFlow()

    fun login(kullaniciAdi: String, parola: String) {
        if (kullaniciAdi.isBlank() || parola.isBlank()) {
            _uiState.value = LoginUiState.Error("Lütfen tüm alanları doldurun!")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            repository.login(kullaniciAdi, parola)
                .onSuccess { response ->
                    _uiState.value = LoginUiState.Success(response)
                }
                .onFailure { exception ->
                    _uiState.value = LoginUiState.Error(exception.message ?: "Bilinmeyen hata")
                }
        }
    }

    fun register(kullaniciAdi: String, parola: String, rol: String, personelid: Int? = null) {
        if (kullaniciAdi.isBlank() || parola.isBlank() || rol.isBlank()) {
            _registerState.value = RegisterUiState.Error("Tüm alanları doldurun!")
            return
        }

        viewModelScope.launch {
            _registerState.value = RegisterUiState.Loading
            repository.register(kullaniciAdi, parola, rol, personelid)
                .onSuccess {
                    _registerState.value = RegisterUiState.Success
                }
                .onFailure { exception ->
                    _registerState.value = RegisterUiState.Error(exception.message ?: "Kayıt hatası")
                }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }

    fun resetRegisterState() {
        _registerState.value = RegisterUiState.Idle
    }
}