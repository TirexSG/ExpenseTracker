package com.tirexdev.expensetracker.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tirexdev.expensetracker.domain.model.Category
import com.tirexdev.expensetracker.domain.model.Expense
import com.tirexdev.expensetracker.domain.usecase.ExpenseUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val expenseUseCases: ExpenseUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadExpenses()
    }

    private fun loadExpenses() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            expenseUseCases.getExpenses()
                .catch { exception ->
                    _uiState.value = HomeUiState.Error(
                        message = exception.message ?: "Error desconocido"
                    )
                }
                .collect { expenses ->
                    val totalThisMonth = calculateTotalThisMonth(expenses)

                    _uiState.value = HomeUiState.Success(
                        expenses = expenses,
                        totalThisMonth = totalThisMonth
                    )
                }
        }
    }

    fun deleteExpense(id: String) {
        viewModelScope.launch {
            try {
                expenseUseCases.deleteExpense(id)
                // Room Flow se actualiza automáticamente
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error deleting expense", e)
                // TODO: Show error to user (Snackbar)
            }
        }
    }

    fun retry() {
        loadExpenses()
    }

    private fun calculateTotalThisMonth(expenses: List<Expense>): Double {
        val now = LocalDateTime.now()
        val startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0)

        return expenses
            .filter { it.date >= startOfMonth }
            .sumOf { it.amount }
    }

    fun selectCategory(category: Category?) {
        val current = _uiState.value
        if (current is HomeUiState.Success) {
            _uiState.value = current.copy(selectedCategory = category)
        }
    }

}