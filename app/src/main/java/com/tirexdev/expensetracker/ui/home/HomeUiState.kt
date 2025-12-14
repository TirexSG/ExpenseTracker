package com.tirexdev.expensetracker.ui.home

import com.tirexdev.expensetracker.domain.model.Category
import com.tirexdev.expensetracker.domain.model.Expense

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Success(
        val expenses: List<Expense>,
        val totalThisMonth: Double,
        val selectedCategory: Category? = null
    ) : HomeUiState

    data class Error(val message: String) : HomeUiState
}