package com.tanerkaynar.nexuserp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tanerkaynar.nexuserp.data.model.Makine
import com.tanerkaynar.nexuserp.data.model.Personel
import com.tanerkaynar.nexuserp.data.model.UretimKaydi
import com.tanerkaynar.nexuserp.data.model.UretimRequest
import com.tanerkaynar.nexuserp.data.model.Urun
import com.tanerkaynar.nexuserp.data.repository.TanimlamaRepository
import com.tanerkaynar.nexuserp.data.repository.UretimRepository
import com.tanerkaynar.nexuserp.data.repository.UrunRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UretimViewModel(
    private val uretimRepository: UretimRepository = UretimRepository(),
    private val tanimlamaRepository: TanimlamaRepository = TanimlamaRepository(),
    private val urunRepository: UrunRepository = UrunRepository()
) : ViewModel() {

    sealed interface UretimUiState {
        data object Idle : UretimUiState
        data object Loading : UretimUiState
        data class Success(val kayitlar: List<UretimKaydi>) : UretimUiState
        data class Error(val message: String) : UretimUiState
    }

    sealed interface RaporUiState {
        data object Idle : RaporUiState
        data object Loading : RaporUiState
        data class Success(val data: List<UretimKaydi>) : RaporUiState
        data class Error(val message: String) : RaporUiState
    }

    sealed interface GrupluUiState {
        data object Idle : GrupluUiState
        data object Loading : GrupluUiState
        data class Success(val gruplar: List<Map<String, Any?>>) : GrupluUiState
        data class Error(val message: String) : GrupluUiState
    }

    sealed interface DurusUiState {
        data object Idle : DurusUiState
        data object Loading : DurusUiState
        data class Success(val duruslar: List<Map<String, Any?>>) : DurusUiState
        data class Error(val message: String) : DurusUiState
    }

    private val _uretimState = MutableStateFlow<UretimUiState>(UretimUiState.Idle)
    val uretimState: StateFlow<UretimUiState> = _uretimState.asStateFlow()

    private val _raporState = MutableStateFlow<RaporUiState>(RaporUiState.Idle)
    val raporState: StateFlow<RaporUiState> = _raporState.asStateFlow()

    private val _grupluState = MutableStateFlow<GrupluUiState>(GrupluUiState.Idle)
    val grupluState: StateFlow<GrupluUiState> = _grupluState.asStateFlow()

    private val _durusState = MutableStateFlow<DurusUiState>(DurusUiState.Idle)
    val durusState: StateFlow<DurusUiState> = _durusState.asStateFlow()

    private val _aktifUrunler = MutableStateFlow<List<Urun>>(emptyList())
    val aktifUrunler: StateFlow<List<Urun>> = _aktifUrunler.asStateFlow()

    private val _aktifMakineler = MutableStateFlow<List<Makine>>(emptyList())
    val aktifMakineler: StateFlow<List<Makine>> = _aktifMakineler.asStateFlow()

    private val _aktifPersoneller = MutableStateFlow<List<Personel>>(emptyList())
    val aktifPersoneller: StateFlow<List<Personel>> = _aktifPersoneller.asStateFlow()

    fun loadAll() {
        viewModelScope.launch {
            _uretimState.value = UretimUiState.Loading
            uretimRepository.getAll()
                .onSuccess { kayitlar ->
                    _uretimState.value = UretimUiState.Success(kayitlar)
                }
                .onFailure { e ->
                    _uretimState.value = UretimUiState.Error(e.message ?: "Bilinmeyen hata")
                }
        }
    }

    fun loadDropdowns() {
        viewModelScope.launch {
            urunRepository.getAll()
                .onSuccess { _aktifUrunler.value = it }
        }
        viewModelScope.launch {
            tanimlamaRepository.getMakineler()
                .onSuccess { _aktifMakineler.value = it }
        }
        viewModelScope.launch {
            tanimlamaRepository.getPersoneller()
                .onSuccess { _aktifPersoneller.value = it }
        }
    }

    fun addUretim(urunid: Int, makineid: Int, personelid: Int, uretimadedi: Int, aciklama: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _uretimState.value = UretimUiState.Loading
            val request = UretimRequest(
                urunid = urunid,
                makineid = makineid,
                personelid = personelid,
                uretimadedi = uretimadedi,
                aciklama = aciklama
            )
            uretimRepository.add(request)
                .onSuccess {
                    loadAll()
                    onSuccess()
                }
                .onFailure { e ->
                    val msg = e.message ?: "Ekleme hatası"
                    _uretimState.value = UretimUiState.Error(msg)
                    onError(msg)
                }
        }
    }

    fun deleteUretim(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _uretimState.value = UretimUiState.Loading
            uretimRepository.delete(id)
                .onSuccess {
                    loadAll()
                    onSuccess()
                }
                .onFailure { e ->
                    val msg = e.message ?: "Silme hatası"
                    _uretimState.value = UretimUiState.Error(msg)
                    onError(msg)
                }
        }
    }

    fun loadRapor(baslangic: String, bitis: String) {
        viewModelScope.launch {
            _raporState.value = RaporUiState.Loading
            uretimRepository.getRapor(baslangic, bitis)
                .onSuccess { rapor ->
                    _raporState.value = RaporUiState.Success(rapor)
                }
                .onFailure { e ->
                    _raporState.value = RaporUiState.Error(e.message ?: "Rapor hatası")
                }
        }
    }

    fun loadGruplu(tip: String) {
        viewModelScope.launch {
            _grupluState.value = GrupluUiState.Loading
            uretimRepository.getGruplu(tip)
                .onSuccess { gruplar ->
                    _grupluState.value = GrupluUiState.Success(gruplar)
                }
                .onFailure { e ->
                    _grupluState.value = GrupluUiState.Error(e.message ?: "Gruplama hatası")
                }
        }
    }

    fun loadDurus(baslangic: String, bitis: String) {
        viewModelScope.launch {
            _durusState.value = DurusUiState.Loading
            uretimRepository.getDurus(baslangic, bitis)
                .onSuccess { duruslar ->
                    _durusState.value = DurusUiState.Success(duruslar)
                }
                .onFailure { e ->
                    _durusState.value = DurusUiState.Error(e.message ?: "Duruş hatası")
                }
        }
    }
}