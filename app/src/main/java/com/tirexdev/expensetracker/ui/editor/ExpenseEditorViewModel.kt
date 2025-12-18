package com.tirexdev.expensetracker.ui.editor

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.tirexdev.expensetracker.R
import com.tirexdev.expensetracker.domain.model.Category
import com.tirexdev.expensetracker.domain.model.Expense
import com.tirexdev.expensetracker.domain.model.PaymentMethod
import com.tirexdev.expensetracker.domain.usecase.ExpenseUseCases
import com.tirexdev.expensetracker.ui.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class ExpenseEditorViewModel @Inject constructor(
    private val expenseUseCases: ExpenseUseCases,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val expenseId: String? = savedStateHandle.toRoute<Routes.ExpenseEditor>().expenseId
    val isEditMode: Boolean = expenseId != null

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    init {
        if (isEditMode) {
            loadExpense()
        }
    }

    private fun loadExpense() {
        val id = expenseId ?: run {
            Log.e("ExpenseEditorVM", "loadExpense called with null expenseId")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingExpense = true) }

            try {
                val expense = expenseUseCases.getExpenseById(id)
                if (expense != null) {
                    _uiState.update {
                        it.copy(
                            title = expense.title,
                            amount = expense.amount.toString(),
                            selectedCategory = runCatching { Category.valueOf(expense.category) }.getOrNull(),
                            selectedDate = expense.date,
                            description = expense.description,
                            paymentMethod = expense.paymentMethod,
                            tags = expense.tags,
                            location = expense.location ?: "",
                            isLoadingExpense = false
                        )
                    }
                } else {
                    Log.e("ExpenseEditorVM", "Expense not found: $id")
                    _uiState.update { it.copy(isLoadingExpense = false) }
                }
            } catch (e: Exception) {
                Log.e("ExpenseEditorVM", "Error loading expense", e)
                _uiState.update { it.copy(isLoadingExpense = false) }
            }
        }
    }

    fun onTitleChange(title: String) {
        _uiState.update {
            it.copy(
                title = title,
                titleError = null
            )
        }
    }

    fun onAmountChange(amount: String) {
        // Only allow valid decimal input
        if (amount.isEmpty() || amount.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
            _uiState.update {
                it.copy(amount = amount, amountError = null)
            }
        }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun onPaymentMethodSelected(method: PaymentMethod) {
        _uiState.update {
            it.copy(
                paymentMethod = method,
                paymentMethodError = null
            )
        }
    }

    fun onLocationClick() {
        // TODO
        _uiState.update { it.copy(location = "Ubicación de ejemplo") }
    }

    fun onDatePickerShow() {
        _uiState.update { it.copy(showDatePicker = true) }
    }

    fun onDatePickerDismiss() {
        _uiState.update { it.copy(showDatePicker = false) }
    }

    fun onDateSelected(timestamp: Long?) {
        timestamp?.let {
            val dateTime = Instant.ofEpochMilli(it)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
            _uiState.update { state ->
                state.copy(
                    selectedDate = dateTime,
                    showDatePicker = false
                )
            }
        }
    }

    fun onCategorySelect(category: Category) {
        _uiState.update {
            it.copy(
                selectedCategory = category,
                categoryError = null
            )
        }
    }

    fun saveExpense() {
        if (!validateInputs()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            try {
                val expense = createExpenseFromState()

                if (isEditMode) {
                    expenseUseCases.updateExpense(expense)
                        .onSuccess {
                            _uiState.update {
                                it.copy(
                                    isSaving = false,
                                    saveSuccess = true
                                )
                            }
                        }
                        .onFailure { error ->
                            Log.e("ExpenseEditorVM", "Error updating expense", error)
                            _uiState.update { it.copy(isSaving = false) }
                        }
                } else {
                    expenseUseCases.addExpense(expense)
                        .onSuccess {
                            _uiState.update {
                                it.copy(
                                    isSaving = false,
                                    saveSuccess = true
                                )
                            }
                        }
                        .onFailure { error ->
                            Log.e("ExpenseEditorVM", "Error adding expense", error)
                            _uiState.update { it.copy(isSaving = false) }
                        }
                }
            } catch (e: Exception) {
                Log.e("ExpenseEditorVM", "Error saving expense", e)
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }

    private fun validateInputs(): Boolean {
        val currentState = _uiState.value
        var isValid = true

        if (currentState.title.isBlank()) {
            _uiState.update { it.copy(titleError = R.string.error_title_empty) }
            isValid = false
        }

        val amountValue = currentState.amount.toDoubleOrNull()
        if (amountValue == null || amountValue <= 0) {
            _uiState.update { it.copy(amountError = R.string.error_amount_invalid) }
            isValid = false
        }

        if (currentState.selectedCategory == null) {
            _uiState.update { it.copy(categoryError = R.string.error_category_required) }
            isValid = false
        }

        if (currentState.paymentMethod == null) {
            _uiState.update { it.copy(paymentMethodError = R.string.error_payment_method_required) }
            isValid = false
        }

        return isValid
    }

    private fun createExpenseFromState(): Expense {
        val currentState = _uiState.value
        return Expense(
            id = expenseId ?: java.util.UUID.randomUUID().toString(),
            title = currentState.title.trim(),
            description = currentState.description.trim(),
            amount = currentState.amount.toDouble(),
            category = currentState.selectedCategory!!.name,
            date = currentState.selectedDate,
            paymentMethod = currentState.paymentMethod,
            tags = currentState.tags,
            location = currentState.location.takeIf { it.isNotBlank() }
        )
    }

    fun onSaveComplete() {
        _uiState.update { it.copy(saveSuccess = false) }
    }
}