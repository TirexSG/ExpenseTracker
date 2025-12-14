package com.tirexdev.expensetracker.ui.home

import app.cash.turbine.test
import com.tirexdev.expensetracker.domain.model.Category
import com.tirexdev.expensetracker.domain.model.Expense
import com.tirexdev.expensetracker.domain.usecase.ExpenseUseCases
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var mockUseCases: ExpenseUseCases

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        mockUseCases = mockk()
    }

    @Test
    fun `init loads expenses and emits Success state`() = runTest {
        val expenses = listOf(
            createExpense("1", 100.0),
            createExpense("2", 50.0)
        )
        every { mockUseCases.getExpenses() } returns flowOf(expenses)

        val viewModel = HomeViewModel(mockUseCases)

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is HomeUiState.Success)
            state as HomeUiState.Success
            assertEquals(2, state.expenses.size)
            assertEquals(150.0, state.totalThisMonth, 0.001)
            assertNull(state.selectedCategory)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when getExpenses fails then emits Error state`() = runTest {
        every { mockUseCases.getExpenses() } returns flow {
            throw RuntimeException("DB error")
        }

        val viewModel = HomeViewModel(mockUseCases)

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is HomeUiState.Error)
            state as HomeUiState.Error
            assertTrue(state.message.isNotEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `calculateTotalThisMonth sums only current month expenses`() = runTest {
        val expenses = listOf(
            createExpense("1", 100.0, LocalDateTime.of(2025, 12, 5, 10, 0)),
            createExpense("2", 50.0, LocalDateTime.of(2025, 12, 15, 14, 30)),
            createExpense("3", 999.0, LocalDateTime.of(2025, 11, 20, 9, 0))
        )
        every { mockUseCases.getExpenses() } returns flowOf(expenses)

        val viewModel = HomeViewModel(mockUseCases)

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is HomeUiState.Success)
            state as HomeUiState.Success
            assertEquals(150.0, state.totalThisMonth, 0.001)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `calculateTotalThisMonth includes first day of month at midnight`() = runTest {
        val expenses = listOf(
            createExpense("1", 100.0, LocalDateTime.of(2025, 12, 1, 0, 0, 0)),
            createExpense("2", 50.0, LocalDateTime.of(2025, 12, 10, 12, 0))
        )
        every { mockUseCases.getExpenses() } returns flowOf(expenses)

        val viewModel = HomeViewModel(mockUseCases)

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is HomeUiState.Success)
            state as HomeUiState.Success
            assertEquals(150.0, state.totalThisMonth, 0.001)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteExpense calls useCase with correct id`() = runTest {
        every { mockUseCases.getExpenses() } returns flowOf(emptyList())
        coEvery { mockUseCases.deleteExpense(any()) } returns Unit

        val viewModel = HomeViewModel(mockUseCases)
        viewModel.deleteExpense("123")

        coVerify(exactly = 1) { mockUseCases.deleteExpense("123") }
    }

    @Test
    fun `selectCategory updates state with selected category`() = runTest {
        every { mockUseCases.getExpenses() } returns flowOf(emptyList())
        val viewModel = HomeViewModel(mockUseCases)

        viewModel.uiState.test {
            val initialState = awaitItem()
            assertTrue(initialState is HomeUiState.Success)
            initialState as HomeUiState.Success
            assertNull(initialState.selectedCategory)

            val category = Category.FOOD
            viewModel.selectCategory(category)
            val updatedState = viewModel.uiState.value as HomeUiState.Success
            assertEquals(category, updatedState.selectedCategory)

            viewModel.selectCategory(null)
            val deselectedState = viewModel.uiState.value as HomeUiState.Success
            assertNull(deselectedState.selectedCategory)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `selectCategory does not update state if not Success`() = runTest {
        every { mockUseCases.getExpenses() } returns flow { throw RuntimeException("error") }
        val viewModel = HomeViewModel(mockUseCases)

        viewModel.selectCategory(Category.FOOD)

        assertTrue(viewModel.uiState.value is HomeUiState.Error)
    }

    private fun createExpense(
        id: String,
        amount: Double,
        date: LocalDateTime = LocalDateTime.of(2025, 12, 10, 12, 0)
    ) = Expense(
        id = id,
        title = "Test",
        amount = amount,
        category = Category.FOOD.name,
        date = date
    )
}