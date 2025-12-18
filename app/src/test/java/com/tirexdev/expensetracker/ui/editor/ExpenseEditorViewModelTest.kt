package com.tirexdev.expensetracker.ui.editor

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.google.common.truth.Truth.assertThat
import com.tirexdev.expensetracker.domain.model.Category
import com.tirexdev.expensetracker.domain.model.Expense
import com.tirexdev.expensetracker.domain.model.PaymentMethod
import com.tirexdev.expensetracker.domain.usecase.ExpenseUseCases
import com.tirexdev.expensetracker.ui.navigation.Routes
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId

@OptIn(ExperimentalCoroutinesApi::class)
class ExpenseEditorViewModelTest {

    private lateinit var mockUseCases: ExpenseUseCases
    private lateinit var mockSavedStateHandle: SavedStateHandle
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockUseCases = mockk()
        mockSavedStateHandle = mockk()
        mockkStatic("androidx.navigation.SavedStateHandleKt")
    }

    @After
    fun tearDown() {
        unmockkStatic("androidx.navigation.SavedStateHandleKt")
    }

    @Test
    fun `create mode starts with empty state and isEditMode false`() = runTest(testDispatcher) {
        val viewModel = createViewModelInCreateMode()

        val state = viewModel.uiState.value

        assertThat(state.title).isEmpty()
        assertThat(state.amount).isEmpty()
        assertThat(state.selectedCategory).isNull()
        assertThat(state.paymentMethod).isNull()
        assertThat(viewModel.isEditMode).isFalse()
    }

    // Regex: ^\d*\.?\d{0,2}$ - allows integers, decimals up to 2 places, and partial input like "10."
    @Test
    fun `onAmountChange accepts valid decimal with two decimals`() = runTest(testDispatcher) {
        val viewModel = createViewModelInCreateMode()

        viewModel.onAmountChange("12.50")

        assertThat(viewModel.uiState.value.amount).isEqualTo("12.50")
    }

    @Test
    fun `onAmountChange rejects more than two decimal places`() = runTest(testDispatcher) {
        val viewModel = createViewModelInCreateMode()
        viewModel.onAmountChange("10.00")

        viewModel.onAmountChange("12.505")

        assertThat(viewModel.uiState.value.amount).isEqualTo("10.00")
    }

    @Test
    fun `onAmountChange accepts empty string for clearing input`() = runTest(testDispatcher) {
        val viewModel = createViewModelInCreateMode()
        viewModel.onAmountChange("50.00")

        viewModel.onAmountChange("")

        assertThat(viewModel.uiState.value.amount).isEmpty()
    }

    @Test
    fun `onAmountChange accepts integer without decimals`() = runTest(testDispatcher) {
        val viewModel = createViewModelInCreateMode()

        viewModel.onAmountChange("100")

        assertThat(viewModel.uiState.value.amount).isEqualTo("100")
    }

    @Test
    fun `onAmountChange accepts partial decimal input`() = runTest(testDispatcher) {
        val viewModel = createViewModelInCreateMode()

        viewModel.onAmountChange("10.")

        assertThat(viewModel.uiState.value.amount).isEqualTo("10.")
    }

    @Test
    fun `onAmountChange clears error when valid input provided`() = runTest(testDispatcher) {
        val viewModel = createViewModelInCreateMode()
        viewModel.saveExpense()
        advanceUntilIdle()
        assertThat(viewModel.uiState.value.amountError).isNotNull()

        viewModel.onAmountChange("25.00")

        assertThat(viewModel.uiState.value.amountError).isNull()
    }

    // loadExpense() uses runCatching for Category.valueOf() which can throw on invalid values
    @Test
    fun `edit mode loads expense and maps all fields correctly`() = runTest(testDispatcher) {
        val existingExpense = Expense(
            id = "expense-123",
            title = "Almuerzo",
            description = "Con compañeros",
            amount = 25.50,
            category = Category.FOOD.name,
            date = LocalDateTime.of(2025, 6, 15, 12, 30),
            paymentMethod = PaymentMethod.CREDIT_CARD,
            tags = listOf("trabajo", "social"),
            location = "Restaurante Centro"
        )
        coEvery { mockUseCases.getExpenseById("expense-123") } returns existingExpense

        val viewModel = createViewModelInEditMode("expense-123")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(viewModel.isEditMode).isTrue()
        assertThat(state.title).isEqualTo("Almuerzo")
        assertThat(state.description).isEqualTo("Con compañeros")
        assertThat(state.amount).isEqualTo("25.5")
        assertThat(state.selectedCategory).isEqualTo(Category.FOOD)
        assertThat(state.selectedDate).isEqualTo(LocalDateTime.of(2025, 6, 15, 12, 30))
        assertThat(state.paymentMethod).isEqualTo(PaymentMethod.CREDIT_CARD)
        assertThat(state.tags).containsExactly("trabajo", "social")
        assertThat(state.location).isEqualTo("Restaurante Centro")
        assertThat(state.isLoadingExpense).isFalse()
    }

    @Test
    fun `edit mode handles expense not found gracefully`() = runTest(testDispatcher) {
        coEvery { mockUseCases.getExpenseById("nonexistent-id") } returns null

        val viewModel = createViewModelInEditMode("nonexistent-id")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.title).isEmpty()
        assertThat(state.isLoadingExpense).isFalse()
    }

    @Test
    fun `edit mode handles invalid category gracefully with runCatching`() = runTest(testDispatcher) {
        val expenseWithInvalidCategory = Expense(
            id = "expense-456",
            title = "Test",
            amount = 10.0,
            category = "CATEGORIA_INVENTADA",
            date = LocalDateTime.now()
        )
        coEvery { mockUseCases.getExpenseById("expense-456") } returns expenseWithInvalidCategory

        val viewModel = createViewModelInEditMode("expense-456")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.title).isEqualTo("Test")
        assertThat(state.selectedCategory).isNull()
        assertThat(state.isLoadingExpense).isFalse()
    }

    @Test
    fun `edit mode handles repository exception gracefully`() = runTest(testDispatcher) {
        coEvery { mockUseCases.getExpenseById("expense-789") } throws RuntimeException("Database connection failed")

        val viewModel = createViewModelInEditMode("expense-789")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isLoadingExpense).isFalse()
    }

    @Test
    fun `edit mode handles null location correctly`() = runTest(testDispatcher) {
        val expenseWithoutLocation = Expense(
            id = "expense-no-loc",
            title = "Sin ubicación",
            amount = 15.0,
            category = Category.TRANSPORT.name,
            date = LocalDateTime.now(),
            location = null
        )
        coEvery { mockUseCases.getExpenseById("expense-no-loc") } returns expenseWithoutLocation

        val viewModel = createViewModelInEditMode("expense-no-loc")
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.location).isEmpty()
    }

    // Timestamp conversion uses ZoneId.systemDefault(), tests use same zone to be deterministic
    @Test
    fun `onDateSelected converts timestamp to LocalDateTime correctly`() = runTest(testDispatcher) {
        val viewModel = createViewModelInCreateMode()
        val expectedDate = LocalDateTime.of(2025, 6, 15, 12, 0)
        val timestamp = expectedDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        viewModel.onDateSelected(timestamp)

        val selectedDate = viewModel.uiState.value.selectedDate
        assertThat(selectedDate.year).isEqualTo(2025)
        assertThat(selectedDate.monthValue).isEqualTo(6)
        assertThat(selectedDate.dayOfMonth).isEqualTo(15)
    }

    @Test
    fun `onDateSelected does nothing when timestamp is null`() = runTest(testDispatcher) {
        val viewModel = createViewModelInCreateMode()
        val originalDate = viewModel.uiState.value.selectedDate

        viewModel.onDateSelected(null)

        assertThat(viewModel.uiState.value.selectedDate).isEqualTo(originalDate)
    }

    @Test
    fun `onDateSelected closes date picker after selection`() = runTest(testDispatcher) {
        val viewModel = createViewModelInCreateMode()
        viewModel.onDatePickerShow()
        assertThat(viewModel.uiState.value.showDatePicker).isTrue()

        val timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        viewModel.onDateSelected(timestamp)

        assertThat(viewModel.uiState.value.showDatePicker).isFalse()
    }

    // validateInputs() must fail fast and set individual field errors before calling useCases
    @Test
    fun `saveExpense sets titleError when title is blank`() = runTest(testDispatcher) {
        val viewModel = createViewModelInCreateMode()
        viewModel.onAmountChange("20.00")
        viewModel.onCategorySelect(Category.FOOD)
        viewModel.onPaymentMethodSelected(PaymentMethod.CASH)

        viewModel.saveExpense()
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.titleError).isNotNull()
        coVerify(exactly = 0) { mockUseCases.addExpense(any()) }
    }

    @Test
    fun `saveExpense sets amountError when amount is zero`() = runTest(testDispatcher) {
        val viewModel = createViewModelInCreateMode()
        viewModel.onTitleChange("Test")
        viewModel.onAmountChange("0")
        viewModel.onCategorySelect(Category.FOOD)
        viewModel.onPaymentMethodSelected(PaymentMethod.CASH)

        viewModel.saveExpense()
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.amountError).isNotNull()
    }

    @Test
    fun `saveExpense sets categoryError when category not selected`() = runTest(testDispatcher) {
        val viewModel = createViewModelInCreateMode()
        viewModel.onTitleChange("Test")
        viewModel.onAmountChange("20.00")
        viewModel.onPaymentMethodSelected(PaymentMethod.CASH)

        viewModel.saveExpense()
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.categoryError).isNotNull()
    }

    @Test
    fun `saveExpense sets paymentMethodError when payment method not selected`() = runTest(testDispatcher) {
        val viewModel = createViewModelInCreateMode()
        viewModel.onTitleChange("Test")
        viewModel.onAmountChange("20.00")
        viewModel.onCategorySelect(Category.FOOD)

        viewModel.saveExpense()
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.paymentMethodError).isNotNull()
    }

    @Test
    fun `saveExpense sets multiple errors when multiple fields invalid`() = runTest(testDispatcher) {
        val viewModel = createViewModelInCreateMode()

        viewModel.saveExpense()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.titleError).isNotNull()
        assertThat(state.amountError).isNotNull()
        assertThat(state.categoryError).isNotNull()
        assertThat(state.paymentMethodError).isNotNull()
    }

    // Create mode generates new UUID and calls addExpense; also verifies trim() on title/description
    @Test
    fun `saveExpense in create mode calls addExpense with correct data`() = runTest(testDispatcher) {
        coEvery { mockUseCases.addExpense(any()) } returns Result.success(Unit)
        val viewModel = createViewModelInCreateMode()

        viewModel.onTitleChange("  Café  ")
        viewModel.onAmountChange("4.50")
        viewModel.onCategorySelect(Category.FOOD)
        viewModel.onPaymentMethodSelected(PaymentMethod.CASH)
        viewModel.onDescriptionChange("  Starbucks  ")

        viewModel.saveExpense()
        advanceUntilIdle()

        coVerify(exactly = 1) {
            mockUseCases.addExpense(match { expense ->
                expense.title == "Café" &&
                        expense.description == "Starbucks" &&
                        expense.amount == 4.50 &&
                        expense.category == Category.FOOD.name &&
                        expense.paymentMethod == PaymentMethod.CASH
            })
        }
        assertThat(viewModel.uiState.value.saveSuccess).isTrue()
    }

    @Test
    fun `saveExpense in create mode handles failure correctly`() = runTest(testDispatcher) {
        coEvery { mockUseCases.addExpense(any()) } returns Result.failure(Exception("DB error"))
        val viewModel = createViewModelInCreateMode()
        fillValidExpenseData(viewModel)

        viewModel.saveExpense()
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.isSaving).isFalse()
        assertThat(viewModel.uiState.value.saveSuccess).isFalse()
    }

    // Edit mode must call updateExpense (not addExpense) and preserve original expenseId
    @Test
    fun `saveExpense in edit mode calls updateExpense instead of addExpense`() = runTest(testDispatcher) {
        val existingExpense = createTestExpense("edit-123")
        coEvery { mockUseCases.getExpenseById("edit-123") } returns existingExpense
        coEvery { mockUseCases.updateExpense(any()) } returns Result.success(Unit)

        val viewModel = createViewModelInEditMode("edit-123")
        advanceUntilIdle()

        viewModel.onTitleChange("Título modificado")
        viewModel.saveExpense()
        advanceUntilIdle()

        coVerify(exactly = 1) { mockUseCases.updateExpense(any()) }
        coVerify(exactly = 0) { mockUseCases.addExpense(any()) }
    }

    @Test
    fun `saveExpense in edit mode preserves original expense id`() = runTest(testDispatcher) {
        val originalId = "original-id-12345"
        val existingExpense = createTestExpense(originalId)
        coEvery { mockUseCases.getExpenseById(originalId) } returns existingExpense
        coEvery { mockUseCases.updateExpense(any()) } returns Result.success(Unit)

        val viewModel = createViewModelInEditMode(originalId)
        advanceUntilIdle()

        viewModel.onTitleChange("Nuevo título")
        viewModel.saveExpense()
        advanceUntilIdle()

        coVerify {
            mockUseCases.updateExpense(match { expense ->
                expense.id == originalId
            })
        }
    }

    @Test
    fun `saveExpense in edit mode handles update failure correctly`() = runTest(testDispatcher) {
        val existingExpense = createTestExpense("fail-123")
        coEvery { mockUseCases.getExpenseById("fail-123") } returns existingExpense
        coEvery { mockUseCases.updateExpense(any()) } returns Result.failure(Exception("Update failed"))

        val viewModel = createViewModelInEditMode("fail-123")
        advanceUntilIdle()

        viewModel.saveExpense()
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.isSaving).isFalse()
        assertThat(viewModel.uiState.value.saveSuccess).isFalse()
    }

    private fun createViewModelInCreateMode(): ExpenseEditorViewModel {
        every { mockSavedStateHandle.toRoute<Routes.ExpenseEditor>() } returns Routes.ExpenseEditor(expenseId = null)
        return ExpenseEditorViewModel(mockUseCases, mockSavedStateHandle)
    }

    private fun createViewModelInEditMode(expenseId: String): ExpenseEditorViewModel {
        every { mockSavedStateHandle.toRoute<Routes.ExpenseEditor>() } returns Routes.ExpenseEditor(expenseId = expenseId)
        return ExpenseEditorViewModel(mockUseCases, mockSavedStateHandle)
    }

    private fun createTestExpense(id: String) = Expense(
        id = id,
        title = "Test Expense",
        description = "Test description",
        amount = 50.0,
        category = Category.FOOD.name,
        date = LocalDateTime.of(2025, 6, 15, 12, 0),
        paymentMethod = PaymentMethod.CREDIT_CARD,
        tags = emptyList(),
        location = null
    )

    private fun fillValidExpenseData(viewModel: ExpenseEditorViewModel) {
        viewModel.onTitleChange("Test")
        viewModel.onAmountChange("20.00")
        viewModel.onCategorySelect(Category.FOOD)
        viewModel.onPaymentMethodSelected(PaymentMethod.CASH)
    }
}