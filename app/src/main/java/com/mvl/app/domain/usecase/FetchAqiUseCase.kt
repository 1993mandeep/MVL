package com.mvl.app.domain.usecase

import com.mvl.app.domain.repository.LocationRepository
import javax.inject.Inject

class FetchAqiUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(lat: Double, lng: Double): Int =
        repository.fetchAqi(lat, lng)
}
