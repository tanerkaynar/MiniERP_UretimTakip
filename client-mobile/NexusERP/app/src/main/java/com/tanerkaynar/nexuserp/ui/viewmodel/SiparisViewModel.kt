package com.tanerkaynar.nexuserp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tanerkaynar.nexuserp.data.model.Siparis
import com.tanerkaynar.nexuserp.data.model.SiparisRequest
import com.tanerkaynar.nexuserp.data.repository.SiparisRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SiparisViewModel(
    private val repository: SiparisRepository = SiparisRepository()
) : ViewModel() {

    sealed interface SiparisUiState {
        data object Idle : SiparisUiState
        data object Loading : SiparisUiState
        data class Success(val siparisler: List<Siparis>) : SiparisUiState
        data class Error(val message: String) : SiparisUiState
    }

    private val _uiState = MutableStateFlow<SiparisUiState>(SiparisUiState.Idle)
    val uiState: StateFlow<SiparisUiState> = _uiState.asStateFlow()

    private val _sevkState = MutableStateFlow<SiparisUiState>(SiparisUiState.Idle)
    val sevkState: StateFlow<SiparisUiState> = _sevkState.asStateFlow()

    fun loadSiparisler() {
        viewModelScope.launch {
            _uiState.value = SiparisUiState.Loading
            repository.getAll()
                .onSuccess { siparisler ->
                    _uiState.value = SiparisUiState.Success(siparisler)
                }
                .onFailure { e ->
                    _uiState.value = SiparisUiState.Error(e.message ?: "Bilinmeyen hata")
                }
        }
    }

    fun loadBekleyenler() {
        viewModelScope.launch {
            _sevkState.value = SiparisUiState.Loading
            repository.getBekleyen()
                .onSuccess { siparisler ->
                    _sevkState.value = SiparisUiState.Success(siparisler)
                }
                .onFailure { e ->
                    _sevkState.value = SiparisUiState.Error(e.message ?: "Bilinmeyen hata")
                }
        }
    }

    fun addSiparis(musteriid: Int, urunid: Int, miktar: Int, birimfiyat: Double) {
        viewModelScope.launch {
            val request = SiparisRequest(
                musteriid = musteriid,
                urunid = urunid,
                miktar = miktar,
                birimfiyat = birimfiyat
            )
            repository.add(request)
                .onSuccess { loadSiparisler() }
                .onFailure { e ->
                    _uiState.value = SiparisUiState.Error(e.message ?: "Ekleme hatası")
                }
        }
    }

    fun sevkEt(siparisId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _sevkState.value = SiparisUiState.Loading
            repository.sevkEt(siparisId)
                .onSuccess {
                    loadBekleyenler()
                    onSuccess()
                }
                .onFailure { e ->
                    _sevkState.value = SiparisUiState.Error(e.message ?: "Sevk hatası")
                }
        }
    }

    fun updateSiparis(id: Int, musteriid: Int, urunid: Int, miktar: Int, birimfiyat: Double) {
        viewModelScope.launch {
            val request = SiparisRequest(
                musteriid = musteriid,
                urunid = urunid,
                miktar = miktar,
                birimfiyat = birimfiyat
            )
            repository.update(id, request)
                .onSuccess { loadSiparisler() }
                .onFailure { e ->
                    _uiState.value = SiparisUiState.Error(e.message ?: "Güncelleme hatası")
                }
        }
    }

    fun deleteSiparis(id: Int) {
        viewModelScope.launch {
            repository.delete(id)
                .onSuccess { loadSiparisler() }
                .onFailure { e ->
                    _uiState.value = SiparisUiState.Error(e.message ?: "Silme hatası")
                }
        }
    }
}