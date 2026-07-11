package com.tanerkaynar.nexuserp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tanerkaynar.nexuserp.data.model.IslemLog
import com.tanerkaynar.nexuserp.data.repository.LogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LogViewModel(
    private val repository: LogRepository = LogRepository()
) : ViewModel() {

    sealed interface LogUiState {
        data object Idle : LogUiState
        data object Loading : LogUiState
        data class Success(val loglar: List<IslemLog>) : LogUiState
        data class Error(val message: String) : LogUiState
    }

    private val _uiState = MutableStateFlow<LogUiState>(LogUiState.Idle)
    val uiState: StateFlow<LogUiState> = _uiState.asStateFlow()

    fun loadLoglar() {
        viewModelScope.launch {
            _uiState.value = LogUiState.Loading
            repository.getAll()
                .onSuccess { loglar ->
                    _uiState.value = LogUiState.Success(loglar)
                }
                .onFailure { e ->
                    _uiState.value = LogUiState.Error(e.message ?: "Bilinmeyen hata")
                }
        }
    }
}