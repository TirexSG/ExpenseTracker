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

class AddExpenseUseCaseTest {

    private lateinit var mockRepository: ExpenseRepository
    private lateinit var useCase: AddExpenseUseCase

    @Before
    fun setup() {
        mockRepository = mockk()
        useCase = AddExpenseUseCase(mockRepository)
    }

    @Test
    fun `invoke saves expense successfully when valid`() = runTest {
        val expense = Expense(
            id = "1",
            title = "Test Expense",
            amount = 100.0,
            category = "FOOD",
            date = LocalDateTime.now()
        )

        coEvery { mockRepository.insertExpense(any()) } returns Unit
        val result = useCase.invoke(expense)
        coVerify { mockRepository.insertExpense(expense) }
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
        coEvery { mockRepository.insertExpense(any()) } returns Unit
        val result = useCase.invoke(expense)
        coVerify(exactly = 0) { mockRepository.insertExpense(expense) }
        assert(result.isFailure)
    }

    @Test
    fun `invoke returns failure when title is blank`() = runTest {
        val expense = Expense(
            id = "1",
            title = "",
            amount = 100.0,
            category = "FOOD",
        )

        coEvery { mockRepository.insertExpense(any()) } returns Unit
        val result = useCase.invoke(expense)
        coVerify(exactly = 0) { mockRepository.insertExpense(expense) }
        assert(result.isFailure)
    }

    @Test
    fun `invoke returns failure when repository throws exception`() = runTest {
        val expense = Expense(
            id = "1",
            title = "Coffee",
            amount = 100.0,
            category = "FOOD",
        )

        coEvery { mockRepository.insertExpense(any()) } throws Exception("DB error")
        val result = useCase.invoke(expense)
        assert(result.isFailure)
        coVerify(exactly = 1) { mockRepository.insertExpense(any()) }
    }

    @Test
    fun `invoke trims title before saving`() = runTest {
        val slot = slot<Expense>()
        val expense = Expense(
            id = "1",
            title = "  Coffee   ",
            amount = 100.0,
            category = "FOOD",
            date = LocalDateTime.now()
        )

        coEvery { mockRepository.insertExpense(capture(slot)) } returns Unit

        val result = useCase(expense)

        assert(result.isSuccess)
        assert(slot.captured.title == "Coffee")
    }


}