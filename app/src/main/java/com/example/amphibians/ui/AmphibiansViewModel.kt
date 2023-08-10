package com.example.amphibians.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.amphibians.AmphibiansApplication
import com.example.amphibians.data.AmphibiansRepository
import com.example.amphibians.model.Amphibian
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface RequestStatus {
    data class Success(val amphibians: List<Amphibian>) : RequestStatus
    object Error : RequestStatus
    object Loading : RequestStatus
}

class AmphibiansViewModel(
    private val amphibiansRepository: AmphibiansRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        AmphibiansUiState(
            requestStatus = RequestStatus.Loading,
            currentAmphibian = null
        )
    )
    val uiState: StateFlow<AmphibiansUiState> = _uiState

    init {
        getAmphibians()
    }

    fun updateCurrentAmphibian(currentAmphibian: Amphibian) {
        _uiState.update {
            it.copy(
                currentAmphibian = currentAmphibian
            )
        }
    }

    fun getAmphibians() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    requestStatus = try {
                        RequestStatus.Success(amphibiansRepository.getAmphibians())
                    } catch (e: IOException) {
                        RequestStatus.Error
                    } catch (e: HttpException) {
                        RequestStatus.Error
                    }
                )
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AmphibiansApplication)
                val amphibiansRepository = application.container.amphibiansRepository
                AmphibiansViewModel(
                    amphibiansRepository = amphibiansRepository
                )
            }
        }
    }
}