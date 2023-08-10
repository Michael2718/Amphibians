package com.example.amphibians.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

//sealed interface RequestStatus {
//    data class Success(val amphibians: List<Amphibia>) : RequestStatus
//    object Error : RequestStatus
//    object Loading : RequestStatus
//}

class AmphibiansViewModel(
//    private val amphibiansRepository: AmphibiansRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        AmphibiansUiState()
    )
    val uiState: StateFlow<AmphibiansUiState> = _uiState

    init {
        // Get Amphibians
    }
}