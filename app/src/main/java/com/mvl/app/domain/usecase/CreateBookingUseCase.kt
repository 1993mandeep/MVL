package com.mvl.app.domain.usecase

import com.mvl.app.domain.model.BookingRecord
import com.mvl.app.domain.model.LocationPoint
import com.mvl.app.domain.repository.LocationRepository
import javax.inject.Inject

class CreateBookingUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(a: LocationPoint, b: LocationPoint): BookingRecord =
        repository.createBooking(a, b)
}
