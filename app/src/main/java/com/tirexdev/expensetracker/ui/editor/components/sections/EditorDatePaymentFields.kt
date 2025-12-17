import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tirexdev.expensetracker.R
import com.tirexdev.expensetracker.domain.model.PaymentMethod
import com.tirexdev.expensetracker.ui.editor.components.input.LabeledInputField
import com.tirexdev.expensetracker.ui.editor.components.pickers.PaymentMethodDropdown
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun EditorDatePaymentFields(
    selectedDate: LocalDateTime,
    selectedPaymentMethod: PaymentMethod?,
    onDateClick: () -> Unit,
    onPaymentMethodSelected: (PaymentMethod) -> Unit,
    modifier: Modifier = Modifier,
    paymentMethodError: String? = null
) {
    var showPaymentMethodMenu by remember { mutableStateOf(false) }

    val dateFormatter = DateTimeFormatter.ofPattern("d MMM", Locale.getDefault())
    val dateValue = if (isToday(selectedDate)) {
        stringResource(R.string.editor_date_today, selectedDate.format(dateFormatter))
    } else {
        selectedDate.format(dateFormatter)
    }

    val paymentMethodValue = selectedPaymentMethod?.let {
        stringResource(it.displayName)
    } ?: stringResource(R.string.editor_placeholder_payment_method)

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        LabeledInputField(
            label = stringResource(R.string.editor_label_date),
            value = dateValue,
            onValueChange = {},
            onClick = onDateClick,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            },
            modifier = Modifier.weight(1f)
        )

        Box(modifier = Modifier.weight(1f)) {
            LabeledInputField(
                label = stringResource(R.string.editor_label_payment_method),
                value = paymentMethodValue,
                onValueChange = {},
                onClick = { showPaymentMethodMenu = true },
                isError = paymentMethodError != null,
                errorMessage = paymentMethodError,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.CreditCard,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            )

            PaymentMethodDropdown(
                expanded = showPaymentMethodMenu,
                onDismiss = { showPaymentMethodMenu = false },
                onMethodSelected = { method ->
                    onPaymentMethodSelected(method)
                    showPaymentMethodMenu = false
                }
            )
        }
    }
}

private fun isToday(date: LocalDateTime): Boolean {
    val today = LocalDateTime.now()
    return date.year == today.year &&
            date.monthValue == today.monthValue &&
            date.dayOfMonth == today.dayOfMonth
}