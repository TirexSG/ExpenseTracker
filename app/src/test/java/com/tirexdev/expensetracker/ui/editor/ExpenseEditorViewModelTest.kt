package com.tirexdev.expensetracker.ui.editor

import androidx.lifecycle.SavedStateHandle
import com.tirexdev.expensetracker.domain.model.Category
import com.tirexdev.expensetracker.domain.model.PaymentMethod
import com.tirexdev.expensetracker.domain.usecase.ExpenseUseCases
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExpenseEditorViewModelTest {

    private lateinit var mockUseCases: ExpenseUseCases
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockUseCases = mockk()
    }

    @Test
    fun `create mode starts with empty state`() = runTest(testDispatcher) {
        val savedStateHandle = SavedStateHandle(mapOf("expenseId" to null))
        val viewModel = ExpenseEditorViewModel(mockUseCases, savedStateHandle)
        val state = viewModel.uiState.value
        assert(state.title.isEmpty())
        assert(!viewModel.isEditMode)
    }

    @Test
    fun `onAmountChange accepts valid decimal`() = runTest(testDispatcher) {
        val viewModel = ExpenseEditorViewModel(mockUseCases, SavedStateHandle())
        viewModel.onAmountChange("12.50")
        assert(viewModel.uiState.value.amount == "12.50")
    }

    @Test
    fun `onAmountChange rejects 3 decimals`() = runTest(testDispatcher) {
        val viewModel = ExpenseEditorViewModel(mockUseCases, SavedStateHandle())
        viewModel.onAmountChange("10.00")
        viewModel.onAmountChange("12.505")
        assert(viewModel.uiState.value.amount == "10.00")
    }

    @Test
    fun `onAmountChange accepts empty string`() = runTest(testDispatcher) {
        val viewModel = ExpenseEditorViewModel(mockUseCases, SavedStateHandle())
        viewModel.onAmountChange("")
        assert(viewModel.uiState.value.amount == "")
    }

    @Test
    fun `saveExpense does not save when validation fails`() = runTest(testDispatcher) {
        val viewModel = ExpenseEditorViewModel(mockUseCases, SavedStateHandle())
        viewModel.saveExpense()

        assert(viewModel.uiState.value.titleError != null)
        assert(!viewModel.uiState.value.isSaving)
        coVerify(exactly = 0) { mockUseCases.addExpense(any()) }
    }

    @Test
    fun `saveExpense creates new expense when validation passes`() = runTest(testDispatcher) {
        coEvery { mockUseCases.addExpense(any()) } returns Result.success(Unit)

        val viewModel = ExpenseEditorViewModel(mockUseCases, SavedStateHandle())

        viewModel.onTitleChange("Test")
        viewModel.onAmountChange("20.05")
        viewModel.onCategorySelect(Category.FOOD)
        viewModel.onPaymentMethodSelected(PaymentMethod.CASH)

        viewModel.saveExpense()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assert(!state.isSaving)
        assert(state.saveSuccess)
        coVerify(exactly = 1) { mockUseCases.addExpense(any()) }
    }
    @Test
    fun `saveExpense handles failure when useCase fails`() = runTest(testDispatcher) {
        coEvery { mockUseCases.addExpense(any()) } returns Result.failure(Exception("DB error"))

        val viewModel = ExpenseEditorViewModel(mockUseCases, SavedStateHandle())
        viewModel.onTitleChange("Test")
        viewModel.onAmountChange("20")
        viewModel.onCategorySelect(Category.FOOD)

        viewModel.saveExpense()
        advanceUntilIdle()

        assert(!viewModel.uiState.value.isSaving)
        assert(!viewModel.uiState.value.saveSuccess)
    }


}