package com.tirexdev.expensetracker.ui.editor

import com.tirexdev.expensetracker.domain.model.Category
import com.tirexdev.expensetracker.domain.model.PaymentMethod

data class EditorCallbacks(
    val onTitleChange: (String) -> Unit,
    val onAmountChange: (String) -> Unit,
    val onCategorySelect: (Category) -> Unit,
    val onDescriptionChange: (String) -> Unit,
    val onPaymentMethodSelected: (PaymentMethod) -> Unit,
    val onLocationClick: () -> Unit,
    val onDateSelected: (Long?) -> Unit,
    val onDatePickerShow: () -> Unit,
    val onDatePickerDismiss: () -> Unit,
    val onSave: () -> Unit
)