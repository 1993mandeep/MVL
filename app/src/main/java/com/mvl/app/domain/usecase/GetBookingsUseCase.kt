package com.mvl.app.domain.usecase

import com.mvl.app.domain.model.BookingRecord
import com.mvl.app.domain.repository.LocationRepository
import javax.inject.Inject

class GetBookingsUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(year: Int, month: Int): List<BookingRecord> =
        repository.getBookings(year, month)
}
