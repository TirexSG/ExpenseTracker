package com.tirexdev.expensetracker.ui.editor.components.pickers

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tirexdev.expensetracker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDatePicker(
    visible: Boolean,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    if (!visible) return

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                    onDismiss()
                }
            ) {
                Text(
                    text = stringResource(R.string.dialog_accept),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.dialog_cancel),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        colors = DatePickerDefaults.colors(
            containerColor = MaterialTheme.colorScheme.secondary
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline,
            shape = RoundedCornerShape(16.dp)
        )
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.secondary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                headlineContentColor = MaterialTheme.colorScheme.onPrimary,
                weekdayContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                subheadContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationContentColor = MaterialTheme.colorScheme.onPrimary,
                yearContentColor = MaterialTheme.colorScheme.onPrimary,
                disabledYearContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.38f),
                currentYearContentColor = MaterialTheme.colorScheme.primary,
                selectedYearContentColor = MaterialTheme.colorScheme.onPrimary,
                disabledSelectedYearContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.38f),
                selectedYearContainerColor = MaterialTheme.colorScheme.primary,
                disabledSelectedYearContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.38f),
                dayContentColor = MaterialTheme.colorScheme.onPrimary,
                disabledDayContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.38f),
                selectedDayContentColor = MaterialTheme.colorScheme.primary,
                disabledSelectedDayContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.38f),
                selectedDayContainerColor = MaterialTheme.colorScheme.onPrimary,
                disabledSelectedDayContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.38f),
                todayContentColor = MaterialTheme.colorScheme.onPrimary,
                todayDateBorderColor = MaterialTheme.colorScheme.onPrimary,
                dayInSelectionRangeContentColor = MaterialTheme.colorScheme.onPrimary,
                dayInSelectionRangeContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                dividerColor = MaterialTheme.colorScheme.outline,
            )
        )
    }
}