package com.tirexdev.expensetracker.domain.usecase

import com.tirexdev.expensetracker.domain.repository.ExpenseRepository
import javax.inject.Inject

class DeleteExpenseUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(id: String) {
        repository.deleteExpenseById(id)
    }
}