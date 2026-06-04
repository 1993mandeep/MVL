package com.mvl.app.domain.usecase

import com.mvl.app.domain.repository.LocationRepository
import javax.inject.Inject

class SaveNicknameUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(lat: Double, lng: Double, nickname: String) =
        repository.saveNickname(lat, lng, nickname)
}
