package com.tirexdev.expensetracker.data.repository

import com.tirexdev.expensetracker.data.local.dao.ExpenseDao
import com.tirexdev.expensetracker.data.mapper.ExpenseMapper.toDomain
import com.tirexdev.expensetracker.data.mapper.ExpenseMapper.toEntity
import com.tirexdev.expensetracker.domain.model.Expense
import com.tirexdev.expensetracker.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of ExpenseRepository using Room database
 */
class ExpenseRepositoryImpl @Inject constructor(
    private val dao: ExpenseDao
) : ExpenseRepository {

    override fun getAllExpenses(): Flow<List<Expense>> {
        return dao.getAllExpenses().map { it.toDomain() }
    }

    override suspend fun getExpenseById(id: String): Expense? {
        return dao.getExpenseById(id)?.toDomain()
    }

    override suspend fun insertExpense(expense: Expense) {
        dao.insertExpense(expense.toEntity())
    }

    override suspend fun updateExpense(expense: Expense) {
        dao.updateExpense(expense.toEntity())
    }

    override suspend fun deleteExpenseById(id: String) {
        dao.deleteExpenseById(id)
    }
}