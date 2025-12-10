package com.tirexdev.expensetracker.ui.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tirexdev.expensetracker.R
import com.tirexdev.expensetracker.ui.editor.components.CategoryGrid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseEditorScreen(
    onNavigateBack: () -> Unit,
    viewModel: ExpenseEditorViewModel = hiltViewModel()
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
            TopAppBar(
                title = {
                    Text(
                        text = if (viewModel.isEditMode) stringResource(R.string.editor_title_edit) else stringResource(R.string.editor_title_new)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_description)
                        )
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        }
    ) { padding ->
        EditorContent(
            uiState = uiState,
            isLoading = uiState.isLoadingExpense,
            onTitleChange = viewModel::onTitleChange,
            onAmountChange = viewModel::onAmountChange,
            onCategorySelect = viewModel::onCategorySelect,
            onSave = viewModel::saveExpense,
            modifier = Modifier.padding(padding)
        )
    }
}

@Composable
private fun EditorContent(
    uiState: EditorUiState,
    isLoading: Boolean,
    onTitleChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onCategorySelect: (com.tirexdev.expensetracker.domain.model.Category) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .imePadding()
    ) {
        OutlinedTextField(
            value = uiState.title,
            onValueChange = onTitleChange,
            label = { Text(stringResource(R.string.editor_label_title)) },
            placeholder = { Text(stringResource(R.string.editor_placeholder_title)) },
            enabled = !isLoading,
            isError = uiState.titleError != null,
            supportingText = {
                uiState.titleError?.let {
                    Text(stringResource(it))
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = uiState.amount,
            onValueChange = onAmountChange,
            label = { Text(stringResource(R.string.editor_label_amount)) },
            placeholder = { Text("0.00") },
            prefix = { Text("€") },
            enabled = !isLoading,
            isError = uiState.amountError != null,
            supportingText = {
                uiState.amountError?.let {
                    Text(stringResource(it))
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Text(
            text = stringResource(R.string.editor_label_category),
            style = MaterialTheme.typography.titleMedium,
            color = if (isLoading) {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )


        CategoryGrid(
            selectedCategory = uiState.selectedCategory,
            onCategorySelect = if (isLoading) { {} } else onCategorySelect,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        Button(
            onClick = onSave,
            enabled = !isLoading && !uiState.isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.isSaving) {
                CircularProgressIndicator()
            } else {
                Text(stringResource(R.string.editor_button_save))
            }
        }
    }
}