package com.mvl.app.presentation.screen1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.mvl.app.domain.model.BookingRecord
import com.mvl.app.domain.model.LocationPoint
import com.mvl.app.domain.model.SetStep
import com.mvl.app.domain.usecase.CreateBookingUseCase
import com.mvl.app.domain.usecase.FetchAqiUseCase
import com.mvl.app.domain.usecase.FetchLocationPointUseCase
import com.mvl.app.domain.usecase.SaveNicknameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MapUiState(
    val markerPosition: LatLng = LatLng(37.5665, 126.9780),
    val currentAqi: Int = -1,
    val locationA: LocationPoint? = null,
    val locationB: LocationPoint? = null,
    val step: SetStep = SetStep.SET_A,
    val isLoadingAqi: Boolean = false,
    val currentBooking: BookingRecord? = null
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val fetchAqiUseCase: FetchAqiUseCase,
    private val fetchLocationPointUseCase: FetchLocationPointUseCase,
    private val saveNicknameUseCase: SaveNicknameUseCase,
    private val createBookingUseCase: CreateBookingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private var aqiDebounceJob: Job? = null

    fun onCameraMove(newPosition: LatLng) {
        _uiState.update { it.copy(markerPosition = newPosition, isLoadingAqi = true) }
        aqiDebounceJob?.cancel()
        aqiDebounceJob = viewModelScope.launch {
            delay(600)
            val aqi = fetchAqiUseCase(newPosition.latitude, newPosition.longitude)
            _uiState.update { it.copy(currentAqi = aqi, isLoadingAqi = false) }
        }
    }

    fun onVButtonTapped(onBook: () -> Unit) {
        val state = _uiState.value
        when (state.step) {
            SetStep.SET_A -> viewModelScope.launch {
                val point = fetchLocationPointUseCase(
                    state.markerPosition.latitude, state.markerPosition.longitude
                )
                _uiState.update { it.copy(locationA = point, step = SetStep.SET_B) }
            }
            SetStep.SET_B -> viewModelScope.launch {
                val point = fetchLocationPointUseCase(
                    state.markerPosition.latitude, state.markerPosition.longitude
                )
                _uiState.update { it.copy(locationB = point, step = SetStep.BOOK) }
            }
            SetStep.BOOK -> onBook()
        }
    }

    fun setNickname(which: String, nickname: String) {
        val state = _uiState.value
        val target = if (which == "a") state.locationA else state.locationB
        _uiState.update { s ->
            when (which) {
                "a" -> s.copy(locationA = s.locationA?.copy(nickname = nickname))
                else -> s.copy(locationB = s.locationB?.copy(nickname = nickname))
            }
        }
        target?.let { loc ->
            viewModelScope.launch {
                saveNicknameUseCase(loc.latitude, loc.longitude, nickname)
            }
        }
    }

    fun getLocation(which: String) = when (which) {
        "a" -> _uiState.value.locationA
        else -> _uiState.value.locationB
    }

    fun performBooking(onSuccess: () -> Unit) {
        val state = _uiState.value
        val a = state.locationA ?: return
        val b = state.locationB ?: return
        viewModelScope.launch {
            val record = createBookingUseCase(a, b)
            _uiState.update { it.copy(currentBooking = record) }
            onSuccess()
        }
    }

    fun setLocationFromCache(which: String, point: LocationPoint) {
        _uiState.update { state ->
            when (which) {
                "a" -> state.copy(
                    locationA = point,
                    step = if (state.step == SetStep.SET_A) SetStep.SET_B else state.step
                )
                else -> state.copy(
                    locationB = point,
                    step = if (state.locationA != null) SetStep.BOOK else state.step
                )
            }
        }
    }

    fun resetState() {
        aqiDebounceJob?.cancel()
        _uiState.update {
            it.copy(
                locationA = null,
                locationB = null,
                step = SetStep.SET_A,
                currentBooking = null,
                currentAqi = -1
            )
        }
    }

    fun loadFromHistory(record: BookingRecord) {
        viewModelScope.launch {
            val aAqi = fetchAqiUseCase(record.a.latitude, record.a.longitude)
            val bAqi = fetchAqiUseCase(record.b.latitude, record.b.longitude)
            _uiState.update {
                it.copy(
                    locationA = record.a.copy(aqi = aAqi),
                    locationB = record.b.copy(aqi = bAqi),
                    step = SetStep.BOOK
                )
            }
        }
    }
}
