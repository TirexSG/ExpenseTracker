package com.tirexdev.expensetracker.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Routes {

    @Serializable
    data object Home : Routes

    @Serializable
    data object Statistics : Routes

    @Serializable
    data class ExpenseEditor(val expenseId: String? = null) : Routes

}