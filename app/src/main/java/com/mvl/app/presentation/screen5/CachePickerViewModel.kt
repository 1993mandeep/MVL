package com.mvl.app.presentation.screen5

import androidx.lifecycle.ViewModel
import com.mvl.app.domain.model.LocationPoint
import com.mvl.app.domain.usecase.GetCachedLocationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class CachePickerViewModel @Inject constructor(
    getCachedLocationsUseCase: GetCachedLocationsUseCase
) : ViewModel() {

    val cachedLocations: Flow<List<LocationPoint>> = getCachedLocationsUseCase()
}
