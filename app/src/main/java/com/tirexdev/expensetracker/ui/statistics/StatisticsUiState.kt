package com.tirexdev.expensetracker.ui.statistics

import com.tirexdev.expensetracker.domain.model.ExpenseStatistics

sealed interface StatisticsUiState {
    data object Loading : StatisticsUiState
    data class Success(val statistics: ExpenseStatistics) : StatisticsUiState
    data class Error(val message: String) : StatisticsUiState
}