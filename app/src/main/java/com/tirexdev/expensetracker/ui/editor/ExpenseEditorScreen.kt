package com.tirexdev.expensetracker.ui.editor

import EditorDatePaymentFields
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tirexdev.expensetracker.R
import com.tirexdev.expensetracker.ui.editor.components.category.CategoryGrid
import com.tirexdev.expensetracker.ui.editor.components.input.LabeledInputField
import com.tirexdev.expensetracker.ui.editor.components.pickers.ExpenseDatePicker
import com.tirexdev.expensetracker.ui.editor.components.sections.EditorPrimaryFields
import com.tirexdev.expensetracker.ui.editor.components.sections.EditorSaveButton

@Composable
fun ExpenseEditorScreen(
    onNavigateBack: () -> Unit,
    viewModel: ExpenseEditorViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            viewModel.onSaveComplete()
            onNavigateBack()
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            ExpenseEditorTopAppBar(
                isEditMode = viewModel.isEditMode,
                onNavigateBack = onNavigateBack
            )
        }
    ) { padding ->
        EditorContent(
            uiState = uiState,
            callbacks = EditorCallbacks(
                onTitleChange = viewModel::onTitleChange,
                onAmountChange = viewModel::onAmountChange,
                onCategorySelect = viewModel::onCategorySelect,
                onDescriptionChange = viewModel::onDescriptionChange,
                onPaymentMethodSelected = viewModel::onPaymentMethodSelected,
                onLocationClick = viewModel::onLocationClick,
                onDateSelected = viewModel::onDateSelected,
                onDatePickerShow = viewModel::onDatePickerShow,
                onDatePickerDismiss = viewModel::onDatePickerDismiss,
                onSave = viewModel::saveExpense
            ),
            modifier = Modifier.padding(padding)
        )
    }
}

@Composable
private fun EditorContent(
    uiState: EditorUiState,
    callbacks: EditorCallbacks,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
    ) {
        EditorPrimaryFields(
            uiState = uiState,
            isLoading = uiState.isLoadingExpense,
            onTitleChange = callbacks.onTitleChange,
            onAmountChange = callbacks.onAmountChange
        )

        CategoryGrid(
            selectedCategory = uiState.selectedCategory,
            onCategorySelect = if (uiState.isLoadingExpense) { {} } else callbacks.onCategorySelect,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        EditorDatePaymentFields(
            selectedDate = uiState.selectedDate,
            selectedPaymentMethod = uiState.paymentMethod,
            onDateClick = callbacks.onDatePickerShow,
            onPaymentMethodSelected = callbacks.onPaymentMethodSelected,
            paymentMethodError = uiState.paymentMethodError?.let { stringResource(it) }
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            LabeledInputField(
                label = stringResource(R.string.editor_label_note_optional),
                value = uiState.description,
                onValueChange = callbacks.onDescriptionChange,
                placeholder = stringResource(R.string.editor_placeholder_note),
                singleLine = false,
                minLines = 3,
                maxLines = 5,
                enabled = !uiState.isLoadingExpense
            )

            LabeledInputField(
                label = stringResource(R.string.editor_label_location),
                value = uiState.location,
                onValueChange = {},
                placeholder = stringResource(R.string.editor_placeholder_location),
                onClick = callbacks.onLocationClick,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                },
                enabled = !uiState.isLoadingExpense
            )
        }

        EditorSaveButton(
            isLoading = uiState.isLoadingExpense,
            isSaving = uiState.isSaving,
            onSave = callbacks.onSave
        )
    }

    ExpenseDatePicker(
        visible = uiState.showDatePicker,
        onDateSelected = callbacks.onDateSelected,
        onDismiss = callbacks.onDatePickerDismiss
    )
}

