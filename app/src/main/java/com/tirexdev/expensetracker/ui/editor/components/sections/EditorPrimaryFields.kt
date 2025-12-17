package com.tirexdev.expensetracker.ui.editor.components.sections

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tirexdev.expensetracker.R
import com.tirexdev.expensetracker.ui.editor.EditorUiState
import com.tirexdev.expensetracker.ui.editor.components.input.EditableAmountField
import com.tirexdev.expensetracker.ui.editor.components.input.LabeledInputField

@Composable
fun EditorPrimaryFields(
    uiState: EditorUiState,
    isLoading: Boolean,
    onTitleChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        EditableAmountField(
            amount = uiState.amount,
            onAmountChange = onAmountChange,
            errorMessage = uiState.amountError?.let { stringResource(it) }
        )

        LabeledInputField(
            label = stringResource(R.string.editor_label_expense_title),
            value = uiState.title,
            onValueChange = onTitleChange,
            placeholder = stringResource(R.string.editor_placeholder_title),
            isError = uiState.titleError != null,
            errorMessage = uiState.titleError?.let { stringResource(it) },
            enabled = !isLoading
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.editor_label_category),
                style = MaterialTheme.typography.titleMedium,
                color = if (isLoading) {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )

            AnimatedContent(
                targetState = uiState.categoryError,
                transitionSpec = {
                    fadeIn(animationSpec = tween(150)) + slideInVertically(
                        initialOffsetY = { -20 },
                        animationSpec = tween(150)
                    ) togetherWith fadeOut(animationSpec = tween(150))
                },
                label = "categoryErrorAnimation"
            ) { error ->
                if (error != null) {
                    Text(
                        text = stringResource(error),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}