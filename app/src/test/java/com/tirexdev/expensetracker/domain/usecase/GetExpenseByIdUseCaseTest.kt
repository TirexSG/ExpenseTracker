package com.tirexdev.expensetracker.domain.usecase

import com.tirexdev.expensetracker.domain.model.Expense
import com.tirexdev.expensetracker.domain.repository.ExpenseRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class GetExpenseByIdUseCaseTest {

    private lateinit var mockRepository: ExpenseRepository
    private lateinit var useCase: GetExpenseByIdUseCase

    @Before
    fun setup() {
        mockRepository = mockk()
        useCase = GetExpenseByIdUseCase(mockRepository)
    }

    @Test
    fun `invoke returns expense when found`() = runTest {
        val expense = Expense(
            id = "1",
            title = "Coffee",
            amount = 10.0,
            category = "FOOD",
            date = LocalDateTime.now()
        )

        coEvery { mockRepository.getExpenseById("1") } returns expense

        val result = useCase("1")

        assert(result == expense)
        coVerify(exactly = 1) { mockRepository.getExpenseById("1") }
    }

    @Test
    fun `invoke returns null when expense not found`() = runTest {
        coEvery { mockRepository.getExpenseById("999") } returns null

        val result = useCase("999")

        assert(result == null)
        coVerify(exactly = 1) { mockRepository.getExpenseById("999") }
    }


}