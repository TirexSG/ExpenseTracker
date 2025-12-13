package com.tirexdev.expensetracker.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tirexdev.expensetracker.domain.usecase.ExpenseUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val useCases: ExpenseUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow<StatisticsUiState>(StatisticsUiState.Loading)
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        loadStatistics()
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            useCases.getStatistics()
                .catch { e ->
                    _uiState.value = StatisticsUiState.Error(
                        e.message ?: "Error loading statistics"
                    )
                }
                .collect { stats ->
                    _uiState.value = StatisticsUiState.Success(stats)
                }
        }
    }

    fun retry() {
        _uiState.value = StatisticsUiState.Loading
        loadStatistics()
    }
}