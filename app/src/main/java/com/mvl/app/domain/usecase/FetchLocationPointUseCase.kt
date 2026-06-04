package com.mvl.app.domain.usecase

import com.mvl.app.domain.model.LocationPoint
import com.mvl.app.domain.repository.LocationRepository
import javax.inject.Inject

class FetchLocationPointUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(lat: Double, lng: Double): LocationPoint =
        repository.fetchLocationPoint(lat, lng)
}
