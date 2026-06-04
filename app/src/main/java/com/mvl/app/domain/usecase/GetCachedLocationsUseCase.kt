package com.mvl.app.domain.usecase

import com.mvl.app.domain.model.LocationPoint
import com.mvl.app.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCachedLocationsUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    operator fun invoke(): Flow<List<LocationPoint>> =
        repository.cachedLocationsFlow()
}
