package com.tirexdev.expensetracker.ui.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tirexdev.expensetracker.domain.model.Expense
import com.tirexdev.expensetracker.ui.home.getDateLabel
import java.time.LocalDate

@Composable
fun GroupedExpenseItem(
    date: LocalDate,
    expense: Expense?,
    visible: Boolean,
    today: LocalDate,
    yesterday: LocalDate,
    onExpenseClick: (String) -> Unit,
    onDeleteExpense: (String) -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(
            initialOffsetX = { -it / 2 },
            animationSpec = tween(300)
        ) + fadeIn(animationSpec = tween(300)),
        exit = slideOutHorizontally(
            targetOffsetX = { it / 2 },
            animationSpec = tween(240)
        ) + fadeOut(animationSpec = tween(160))
    ) {
        if (expense == null) {
            Text(
                text = getDateLabel(date, today, yesterday),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        } else {
            ExpenseCard(
                expense = expense,
                onClick = { onExpenseClick(expense.id) },
                onDelete = { onDeleteExpense(expense.id) }
            )
        }
    }
}
