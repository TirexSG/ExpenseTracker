package com.tirexdev.expensetracker.domain.model

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.ui.graphics.vector.ImageVector
import com.tirexdev.expensetracker.R

enum class PaymentMethod(@param:StringRes val displayName: Int) {
    CASH(R.string.payment_method_cash),
    CREDIT_CARD(R.string.payment_method_credit_card),
    DEBIT_CARD(R.string.payment_method_debit_card),
    BANK_TRANSFER(R.string.payment_method_bank_transfer),
    DIGITAL_WALLET(R.string.payment_method_digital_wallet)
}

fun PaymentMethod.getIcon(): ImageVector = when (this) {
    PaymentMethod.CASH -> Icons.Default.Money
    PaymentMethod.CREDIT_CARD -> Icons.Default.CreditCard
    PaymentMethod.DEBIT_CARD -> Icons.Default.CreditCard
    PaymentMethod.BANK_TRANSFER -> Icons.Default.AccountBalance
    PaymentMethod.DIGITAL_WALLET -> Icons.Default.Wallet
}