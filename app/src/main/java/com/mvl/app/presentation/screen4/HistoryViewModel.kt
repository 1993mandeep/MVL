package com.mvl.app.presentation.screen4

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mvl.app.domain.model.BookingRecord
import com.mvl.app.domain.usecase.GetBookingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class HistoryUiState(
    val records: List<BookingRecord> = emptyList(),
    val totalCount: Int = 0,
    val totalPrice: Double = 0.0,
    val isLoading: Boolean = true
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getBookingsUseCase: GetBookingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadCurrentMonth()
    }

    private fun loadCurrentMonth() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val records = getBookingsUseCase(year, month)
            _uiState.update {
                it.copy(
                    records = records,
                    totalCount = records.size,
                    totalPrice = records.sumOf { r -> r.price },
                    isLoading = false
                )
            }
        }
    }
}
