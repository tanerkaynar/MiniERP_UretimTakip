package com.tanerkaynar.nexuserp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tanerkaynar.nexuserp.data.model.Makine
import com.tanerkaynar.nexuserp.data.model.Musteri
import com.tanerkaynar.nexuserp.data.model.Personel
import com.tanerkaynar.nexuserp.data.repository.TanimlamaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TanimlamaViewModel(
    private val repository: TanimlamaRepository = TanimlamaRepository()
) : ViewModel() {

    sealed interface ListUiState<out T> {
        data object Idle : ListUiState<Nothing>
        data object Loading : ListUiState<Nothing>
        data class Success<T>(val items: List<T>) : ListUiState<T>
        data class Error(val message: String) : ListUiState<Nothing>
    }

    private val _musteriListesi = MutableStateFlow<List<Musteri>>(emptyList())
    val musteriListesi: StateFlow<List<Musteri>> = _musteriListesi.asStateFlow()

    private val _makineListesi = MutableStateFlow<List<Makine>>(emptyList())
    val makineListesi: StateFlow<List<Makine>> = _makineListesi.asStateFlow()

    private val _personelListesi = MutableStateFlow<List<Personel>>(emptyList())
    val personelListesi: StateFlow<List<Personel>> = _personelListesi.asStateFlow()

    private val _musteriState = MutableStateFlow<ListUiState<Musteri>>(ListUiState.Idle)
    val musteriState: StateFlow<ListUiState<Musteri>> = _musteriState.asStateFlow()

    fun loadMusteriler() {
        viewModelScope.launch {
            _musteriState.value = ListUiState.Loading
            repository.getMusteriler()
                .onSuccess {
                    _musteriState.value = ListUiState.Success(it)
                    _musteriListesi.value = it
                }
                .onFailure { _musteriState.value = ListUiState.Error(it.message ?: "Bilinmeyen hata") }
        }
    }

    fun addMusteri(musteriadi: String) {
        viewModelScope.launch {
            val musteri = Musteri(musteriadi = musteriadi)
            repository.addMusteri(musteri)
                .onSuccess { loadMusteriler() }
                .onFailure { _musteriState.value = ListUiState.Error(it.message ?: "Ekleme hatası") }
        }
    }

    fun updateMusteri(musteriid: Int, musteriadi: String) {
        viewModelScope.launch {
            val musteri = Musteri(musteriid = musteriid, musteriadi = musteriadi)
            repository.updateMusteri(musteriid, musteri)
                .onSuccess { loadMusteriler() }
                .onFailure { _musteriState.value = ListUiState.Error(it.message ?: "Güncelleme hatası") }
        }
    }

    fun deleteMusteri(id: Int) {
        viewModelScope.launch {
            repository.deleteMusteri(id)
                .onSuccess { loadMusteriler() }
                .onFailure { _musteriState.value = ListUiState.Error(it.message ?: "Silme hatası") }
        }
    }

    private val _makineState = MutableStateFlow<ListUiState<Makine>>(ListUiState.Idle)
    val makineState: StateFlow<ListUiState<Makine>> = _makineState.asStateFlow()

    fun loadMakineler() {
        viewModelScope.launch {
            _makineState.value = ListUiState.Loading
            repository.getMakineler()
                .onSuccess {
                    _makineState.value = ListUiState.Success(it)
                    _makineListesi.value = it
                }
                .onFailure { _makineState.value = ListUiState.Error(it.message ?: "Bilinmeyen hata") }
        }
    }

    fun addMakine(makineadi: String, makinekodu: String, durum: String) {
        viewModelScope.launch {
            val makine = Makine(makineadi = makineadi, makinekodu = makinekodu, durum = durum)
            repository.addMakine(makine)
                .onSuccess { loadMakineler() }
                .onFailure { _makineState.value = ListUiState.Error(it.message ?: "Ekleme hatası") }
        }
    }

    fun updateMakine(makineid: Int, makineadi: String, makinekodu: String, durum: String) {
        viewModelScope.launch {
            val makine = Makine(makineid = makineid, makineadi = makineadi, makinekodu = makinekodu, durum = durum)
            repository.updateMakine(makineid, makine)
                .onSuccess { loadMakineler() }
                .onFailure { _makineState.value = ListUiState.Error(it.message ?: "Güncelleme hatası") }
        }
    }

    fun deleteMakine(id: Int) {
        viewModelScope.launch {
            repository.deleteMakine(id)
                .onSuccess { loadMakineler() }
                .onFailure { _makineState.value = ListUiState.Error(it.message ?: "Silme hatası") }
        }
    }

    private val _personelState = MutableStateFlow<ListUiState<Personel>>(ListUiState.Idle)
    val personelState: StateFlow<ListUiState<Personel>> = _personelState.asStateFlow()

    private val _kullaniciAtanmamisPersonelState = MutableStateFlow<ListUiState<Personel>>(ListUiState.Idle)
    val kullaniciAtanmamisPersonelState: StateFlow<ListUiState<Personel>> = _kullaniciAtanmamisPersonelState.asStateFlow()

    fun loadPersoneller() {
        viewModelScope.launch {
            _personelState.value = ListUiState.Loading
            repository.getPersoneller()
                .onSuccess {
                    _personelState.value = ListUiState.Success(it)
                    _personelListesi.value = it
                }
                .onFailure { _personelState.value = ListUiState.Error(it.message ?: "Bilinmeyen hata") }
        }
    }

    fun loadKullaniciAtanmamisPersoneller() {
        viewModelScope.launch {
            _kullaniciAtanmamisPersonelState.value = ListUiState.Loading
            repository.getKullaniciAtanmamisPersoneller()
                .onSuccess {
                    _kullaniciAtanmamisPersonelState.value = ListUiState.Success(it)
                }
                .onFailure { _kullaniciAtanmamisPersonelState.value = ListUiState.Error(it.message ?: "Bilinmeyen hata") }
        }
    }

    fun addPersonel(adsoyad: String, departman: String, aktifmi: Boolean = true) {
        viewModelScope.launch {
            val personel = Personel(adsoyad = adsoyad, departman = departman, aktifmi = aktifmi)
            repository.addPersonel(personel)
                .onSuccess { loadPersoneller() }
                .onFailure { _personelState.value = ListUiState.Error(it.message ?: "Ekleme hatası") }
        }
    }

    fun updatePersonel(personelid: Int, adsoyad: String, departman: String, aktifmi: Boolean) {
        viewModelScope.launch {
            val personel = Personel(personelid = personelid, adsoyad = adsoyad, departman = departman, aktifmi = aktifmi)
            repository.updatePersonel(personelid, personel)
                .onSuccess { loadPersoneller() }
                .onFailure { _personelState.value = ListUiState.Error(it.message ?: "Güncelleme hatası") }
        }
    }

    fun deletePersonel(id: Int) {
        viewModelScope.launch {
            repository.deletePersonel(id)
                .onSuccess { loadPersoneller() }
                .onFailure { _personelState.value = ListUiState.Error(it.message ?: "Silme hatası") }
        }
    }
    
}