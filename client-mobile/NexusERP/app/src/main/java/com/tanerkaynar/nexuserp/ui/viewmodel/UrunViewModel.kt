package com.tanerkaynar.nexuserp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tanerkaynar.nexuserp.data.model.Urun
import com.tanerkaynar.nexuserp.data.repository.UrunRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UrunViewModel(
    private val repository: UrunRepository = UrunRepository()
) : ViewModel() {

    sealed interface UrunUiState {
        data object Idle : UrunUiState
        data object Loading : UrunUiState
        data class Success(val urunler: List<Urun>) : UrunUiState
        data class Error(val message: String) : UrunUiState
    }

    private val _uiState = MutableStateFlow<UrunUiState>(UrunUiState.Idle)
    val uiState: StateFlow<UrunUiState> = _uiState.asStateFlow()

    private val _urunListesi = MutableStateFlow<List<Urun>>(emptyList())
    val urunListesi: StateFlow<List<Urun>> = _urunListesi.asStateFlow()

    private val _stokTakipList = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val stokTakipList: StateFlow<List<Map<String, Any>>> = _stokTakipList.asStateFlow()

    private val _stokLoading = MutableStateFlow(false)
    val stokLoading: StateFlow<Boolean> = _stokLoading.asStateFlow()

    private val _stokError = MutableStateFlow<String?>(null)
    val stokError: StateFlow<String?> = _stokError.asStateFlow()

    fun loadUrunler() {
        viewModelScope.launch {
            _uiState.value = UrunUiState.Loading
            repository.getAll()
                .onSuccess { urunler ->
                    _uiState.value = UrunUiState.Success(urunler)
                    _urunListesi.value = urunler
                }
                .onFailure { e ->
                    _uiState.value = UrunUiState.Error(e.message ?: "Bilinmeyen hata")
                }
        }
    }

    fun loadStokTakip() {
        viewModelScope.launch {
            _stokLoading.value = true
            repository.getStokTakip()
                .onSuccess { list ->
                    _stokTakipList.value = list
                    _stokLoading.value = false
                }
                .onFailure { e ->
                    _stokError.value = e.message ?: "Stok takip verisi yüklenemedi"
                    _stokLoading.value = false
                }
        }
    }

    fun addUrun(urunadi: String, stokmiktari: Int, birimfiyat: Double) {
        viewModelScope.launch {
            val urun = Urun(urunadi = urunadi, stokmiktari = stokmiktari, birimfiyat = birimfiyat)
            repository.add(urun)
                .onSuccess { loadUrunler(); loadStokTakip() }
                .onFailure { e ->
                    _uiState.value = UrunUiState.Error(e.message ?: "Ekleme hatası")
                }
        }
    }

    fun updateUrun(id: Int, urunadi: String, stokmiktari: Int, birimfiyat: Double) {
        viewModelScope.launch {
            val urun = Urun(urunid = id, urunadi = urunadi, stokmiktari = stokmiktari, birimfiyat = birimfiyat)
            repository.update(id, urun)
                .onSuccess { loadUrunler(); loadStokTakip() }
                .onFailure { e ->
                    _uiState.value = UrunUiState.Error(e.message ?: "Güncelleme hatası")
                }
        }
    }

    fun deleteUrun(id: Int) {
        viewModelScope.launch {
            repository.delete(id)
                .onSuccess { loadUrunler(); loadStokTakip() }
                .onFailure { e ->
                    _uiState.value = UrunUiState.Error(e.message ?: "Silme hatası")
                }
        }
    }

    fun clearStokError() {
        _stokError.value = null
    }
}