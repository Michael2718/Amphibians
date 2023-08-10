package com.example.amphibians.ui

import com.example.amphibians.model.Amphibian

data class AmphibiansUiState(
    val requestStatus: RequestStatus,
    val currentAmphibian: Amphibian?
)
