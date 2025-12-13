package com.tirexdev.expensetracker.domain.usecase

import com.tirexdev.expensetracker.domain.model.Expense
import com.tirexdev.expensetracker.domain.model.ExpenseStatistics
import com.tirexdev.expensetracker.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetExpenseStatisticsUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    operator fun invoke(): Flow<ExpenseStatistics> {
        return repository.getAllExpenses().map { expenses ->
            calculateStatistics(expenses)
        }
    }

    private fun calculateStatistics(expenses: List<Expense>): ExpenseStatistics {
        val total = expenses.sumOf { it.amount }
        val count = expenses.size
        val average = if (count > 0) total / count else 0.0

        val byCategory = expenses
            .groupBy { it.category }
            .mapValues { (_, expenseList) -> expenseList.sumOf { it.amount } }

        return ExpenseStatistics(
            totalExpenses = total,
            averageExpense = average,
            totalCount = count,
            expensesByCategory = byCategory
        )
    }
}