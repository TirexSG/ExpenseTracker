package com.tirexdev.expensetracker.domain.usecase

import com.tirexdev.expensetracker.domain.repository.ExpenseRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DeleteExpenseUseCaseTest {

    @Test
    fun `invoke calls repository deleteExpenseById with correct id`() = runTest {
        val mockRepository = mockk<ExpenseRepository>()
        val useCase = DeleteExpenseUseCase(mockRepository)

        coEvery { mockRepository.deleteExpenseById(any()) } returns Unit

        useCase("123")

        coVerify(exactly = 1) { mockRepository.deleteExpenseById("123") }
    }


}