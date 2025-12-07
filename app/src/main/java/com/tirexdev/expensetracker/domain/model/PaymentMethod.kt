package com.tirexdev.expensetracker.domain.model

import androidx.annotation.StringRes
import com.tirexdev.expensetracker.R

enum class PaymentMethod(@param:StringRes val displayName: Int) {
    CASH(R.string.payment_method_cash),
    CREDIT_CARD(R.string.payment_method_credit_card),
    DEBIT_CARD(R.string.payment_method_debit_card),
    BANK_TRANSFER(R.string.payment_method_bank_transfer),
    DIGITAL_WALLET(R.string.payment_method_digital_wallet)
}