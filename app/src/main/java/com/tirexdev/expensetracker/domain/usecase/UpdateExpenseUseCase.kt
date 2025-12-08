package com.tirexdev.expensetracker.domain.usecase

import com.tirexdev.expensetracker.domain.model.Expense
import com.tirexdev.expensetracker.domain.repository.ExpenseRepository
import javax.inject.Inject

class UpdateExpenseUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(expense: Expense): Result<Unit> {
        return try {
            require(expense.amount > 0) { "Amount must be greater than 0" }
            require(expense.title.isNotBlank()) { "Title cannot be empty" }

            val processedExpense = expense.copy(
                title = expense.title.trim()
            )

            repository.updateExpense(processedExpense)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}