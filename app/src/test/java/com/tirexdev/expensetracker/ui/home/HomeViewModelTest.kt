import app.cash.turbine.test
import com.tirexdev.expensetracker.domain.model.Expense
import com.tirexdev.expensetracker.domain.usecase.ExpenseUseCases
import com.tirexdev.expensetracker.ui.home.HomeUiState
import com.tirexdev.expensetracker.ui.home.HomeViewModel
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
            val state = awaitItem() as HomeUiState.Success
            assert(state.expenses.size == 2)
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
            val state = awaitItem() as HomeUiState.Error
            assert(state.message.isNotEmpty())
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
            val state = awaitItem() as HomeUiState.Success
            assert(state.totalThisMonth == 150.0)
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
            val state = awaitItem() as HomeUiState.Success
            assert(state.totalThisMonth == 150.0)
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

    private fun createExpense(
        id: String,
        amount: Double,
        date: LocalDateTime = LocalDateTime.of(2025, 12, 10, 12, 0)
    ) = Expense(
        id = id,
        title = "Test",
        amount = amount,
        category = "FOOD",
        date = date
    )
}