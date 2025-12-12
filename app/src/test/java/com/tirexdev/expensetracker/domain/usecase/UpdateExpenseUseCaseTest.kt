package com.tirexdev.expensetracker.domain.usecase

import com.tirexdev.expensetracker.domain.model.Expense
import com.tirexdev.expensetracker.domain.repository.ExpenseRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class UpdateExpenseUseCaseTest {

    private lateinit var mockRepository: ExpenseRepository
    private lateinit var useCase: UpdateExpenseUseCase

    @Before
    fun setup() {
        mockRepository = mockk()
        useCase = UpdateExpenseUseCase(mockRepository)
    }

    @Test
    fun `invoke updates expense successfully when valid`() = runTest {
        val expense = Expense(
            id = "1",
            title = "Test Expense",
            amount = 100.0,
            category = "FOOD",
            date = LocalDateTime.now()
        )

        coEvery { mockRepository.updateExpense(any()) } returns Unit

        val result = useCase(expense)

        coVerify { mockRepository.updateExpense(expense) }
        assert(result.isSuccess)
    }

    @Test
    fun `invoke returns failure when amount is zero`() = runTest {
        val expense = Expense(
            id = "1",
            title = "Test Expense",
            amount = 0.0,
            category = "FOOD",
            date = LocalDateTime.now()
        )

        coEvery { mockRepository.updateExpense(any()) } returns Unit

        val result = useCase(expense)

        coVerify(exactly = 0) { mockRepository.updateExpense(expense) }
        assert(result.isFailure)
    }

    @Test
    fun `invoke returns failure when title is blank`() = runTest {
        val expense = Expense(
            id = "1",
            title = "",
            amount = 100.0,
            category = "FOOD",
            date = LocalDateTime.now()
        )

        coEvery { mockRepository.updateExpense(any()) } returns Unit

        val result = useCase(expense)

        coVerify(exactly = 0) { mockRepository.updateExpense(expense) }
        assert(result.isFailure)
    }

    @Test
    fun `invoke trims title before updating`() = runTest {
        val slot = slot<Expense>()
        val expense = Expense(
            id = "1",
            title = "  Coffee    ",
            amount = 100.0,
            category = "FOOD",
            date = LocalDateTime.now()
        )

        coEvery { mockRepository.updateExpense(capture(slot)) } returns Unit

        val result = useCase(expense)

        assert(result.isSuccess)
        assert(slot.captured.title == "Coffee")
    }
}