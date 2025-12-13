package com.tirexdev.expensetracker.domain.model

data class ExpenseStatistics(
    val totalExpenses: Double,
    val averageExpense: Double,
    val totalCount: Int,
    val expensesByCategory: Map<String, Double>
)