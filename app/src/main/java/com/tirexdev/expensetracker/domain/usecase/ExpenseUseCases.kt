package com.tirexdev.expensetracker.domain.usecase

import javax.inject.Inject

data class ExpenseUseCases @Inject constructor(
    val getExpenses: GetExpensesUseCase,
    val getExpenseById: GetExpenseByIdUseCase,
    val addExpense: AddExpenseUseCase,
    val updateExpense: UpdateExpenseUseCase,
    val deleteExpense: DeleteExpenseUseCase,
    val getStatistics: GetExpenseStatisticsUseCase
)