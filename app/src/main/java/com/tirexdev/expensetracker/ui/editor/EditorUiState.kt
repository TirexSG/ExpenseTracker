package com.tirexdev.expensetracker.ui.editor

import com.tirexdev.expensetracker.domain.model.Category
import com.tirexdev.expensetracker.domain.model.PaymentMethod
import java.time.LocalDateTime

data class EditorUiState(
    val title: String = "",
    val amount: String = "",
    val selectedCategory: Category? = null,
    val selectedDate: LocalDateTime = LocalDateTime.now(),
    val description: String = "",
    val paymentMethod: PaymentMethod? = null,
    val tags: List<String> = emptyList(),
    val location: String = "",

    // UI State
    val isLoadingExpense: Boolean = false,
    val isSaving: Boolean = false,
    val showDatePicker: Boolean = false,
    val titleError: Int? = null,
    val amountError: Int? = null,
    val categoryError: Int? = null,
    val paymentMethodError: Int? = null,
    val saveSuccess: Boolean = false
)