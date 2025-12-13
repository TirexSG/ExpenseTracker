package com.tirexdev.expensetracker.domain.usecase

import app.cash.turbine.test
import com.tirexdev.expensetracker.domain.model.Expense
import com.tirexdev.expensetracker.domain.repository.ExpenseRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class GetExpenseStatisticsUseCaseTest {

    private lateinit var mockRepository: ExpenseRepository
    private lateinit var useCase: GetExpenseStatisticsUseCase

    @Before
    fun setup() {
        mockRepository = mockk()
        useCase = GetExpenseStatisticsUseCase(mockRepository)
    }

    @Test
    fun `invoke returns correct statistics for multiple expenses`() = runTest {
        val expenses = listOf(
            Expense(id = "1", title = "Coffee", amount = 5.0, category = "FOOD", date = LocalDateTime.now()),
            Expense(id = "2", title = "Lunch", amount = 15.0, category = "FOOD", date = LocalDateTime.now())
        )
        every { mockRepository.getAllExpenses() } returns flowOf(expenses)
        useCase().test{
            val result = awaitItem()
            assert(result.totalExpenses == 20.0)
            assert(result.averageExpense == 10.0)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `invoke returns zero average when expense list is empty`() = runTest {
        val expenses = emptyList<Expense>()
        every { mockRepository.getAllExpenses() } returns flowOf(expenses)
        useCase().test {
            val result = awaitItem()
            assert(result.totalExpenses == 0.0)
            assert(result.averageExpense == 0.0)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `invoke groups expenses by category correctly`() = runTest {
        val expenses = listOf(
            Expense(id = "1", title = "Coffee", amount = 10.0, category = "FOOD", date = LocalDateTime.now()),
            Expense(id = "2", title = "Lunch", amount = 20.0, category = "FOOD", date = LocalDateTime.now()),
            Expense(id = "3", title = "Bus", amount = 5.0, category = "TRANSPORT", date = LocalDateTime.now()),
            Expense(id = "4", title = "Taxi", amount = 15.0, category = "TRANSPORT", date = LocalDateTime.now()),
            Expense(id = "5", title = "Shirt", amount = 30.0, category = "SHOPPING", date = LocalDateTime.now())
        )
        every { mockRepository.getAllExpenses() } returns flowOf(expenses)
        useCase().test {
            val result = awaitItem()
            assert(result.expensesByCategory.size == 3)
            assert(result.expensesByCategory["FOOD"] == 30.0)
            assert(result.expensesByCategory["TRANSPORT"] == 20.0)
            assert(result.expensesByCategory["SHOPPING"] == 30.0)
            cancelAndIgnoreRemainingEvents()
        }
    }


}