package com.tirexdev.expensetracker.domain.repository

import com.tirexdev.expensetracker.domain.model.Expense
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for expense operations
 * Defines the contract for data access without exposing implementation details
 */
interface ExpenseRepository {

    fun getAllExpenses(): Flow<List<Expense>>

    suspend fun getExpenseById(id: String): Expense?

    suspend fun insertExpense(expense: Expense)

    suspend fun updateExpense(expense: Expense)

    suspend fun deleteExpenseById(id: String)
}