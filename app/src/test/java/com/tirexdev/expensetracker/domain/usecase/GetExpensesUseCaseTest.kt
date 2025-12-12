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

class GetExpensesUseCaseTest {

    private lateinit var mockRepository: ExpenseRepository
    private lateinit var useCase: GetExpensesUseCase

    @Before
    fun setup() {
        mockRepository = mockk()
        useCase = GetExpensesUseCase(mockRepository)
    }

    @Test
    fun `invoke returns flow from repository`() = runTest {
        val expenses = listOf(
            Expense(id = "1", title = "Coffee", amount = 5.0, category = "FOOD", date = LocalDateTime.now()),
            Expense(id = "2", title = "Lunch", amount = 15.0, category = "FOOD", date = LocalDateTime.now())
        )

        every { mockRepository.getAllExpenses() } returns flowOf(expenses)

        useCase().test {
            val result = awaitItem()
            assert(result == expenses)
            assert(result.size == 2)
            cancelAndIgnoreRemainingEvents()
        }
    }
}