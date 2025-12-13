package com.tirexdev.expensetracker.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tirexdev.expensetracker.data.local.database.ExpenseDatabase
import com.tirexdev.expensetracker.data.local.entity.ExpenseEntity
import com.tirexdev.expensetracker.domain.model.PaymentMethod
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class ExpenseDaoTest {

    private lateinit var database: ExpenseDatabase
    private lateinit var dao: ExpenseDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            ExpenseDatabase::class.java
        ).build()
        dao = database.expenseDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    private fun createTestExpense(
        id: String = "1",
        title: String = "Test Expense",
        description: String = "",
        amount: Double = 100.0,
        category: String = "FOOD",
        customColor: Int? = null,
        customIcon: String? = null,
        date: LocalDateTime = LocalDateTime.now(),
        paymentMethod: PaymentMethod? = null,
        tags: List<String> = emptyList(),
        location: String? = null
    ): ExpenseEntity {
        return ExpenseEntity(
            id = id,
            title = title,
            description = description,
            amount = amount,
            category = category,
            customColor = customColor,
            customIcon = customIcon,
            date = date,
            paymentMethod = paymentMethod,
            tags = tags,
            location = location
        )
    }

    @Test
    fun insertExpense_and_getById_returnsExpense() = runTest {
        val expense = createTestExpense(
            id = "1",
            title = "Coffee",
            amount = 5.0,
            category = "FOOD"
        )

        dao.insertExpense(expense)
        val retrieved = dao.getExpenseById("1")

        assert(retrieved != null)
        assert(retrieved?.id == "1")
        assert(retrieved?.title == "Coffee")
        assert(retrieved?.amount == 5.0)
    }

    @Test
    fun getAllExpenses_returnsListOrderedByDateDesc() = runTest {
        val expense1 = createTestExpense(
            id = "1",
            title = "Old",
            amount = 10.0,
            date = LocalDateTime.of(2025, 1, 1, 10, 0)
        )
        val expense2 = createTestExpense(
            id = "2",
            title = "Recent",
            amount = 20.0,
            date = LocalDateTime.of(2025, 12, 10, 14, 0)
        )
        val expense3 = createTestExpense(
            id = "3",
            title = "Newest",
            amount = 30.0,
            date = LocalDateTime.of(2025, 12, 12, 9, 0)
        )

        dao.insertExpense(expense1)
        dao.insertExpense(expense2)
        dao.insertExpense(expense3)

        val expenses = dao.getAllExpenses().first()

        assert(expenses.size == 3)
        assert(expenses[0].title == "Newest")
        assert(expenses[2].title == "Old")
    }

    @Test
    fun updateExpense_updatesData() = runTest {
        val original = createTestExpense(
            id = "1",
            title = "Original Title",
            amount = 100.0
        )
        dao.insertExpense(original)

        val updated = original.copy(title = "Updated Title")
        dao.updateExpense(updated)

        val retrieved = dao.getExpenseById("1")

        assert(retrieved?.title == "Updated Title")
        assert(retrieved?.amount == 100.0)
    }

    @Test
    fun deleteExpenseById_removesExpense() = runTest {
        val expense = createTestExpense(
            id = "1",
            title = "To Delete",
            amount = 50.0
        )
        dao.insertExpense(expense)

        dao.deleteExpenseById("1")

        val retrieved = dao.getExpenseById("1")
        assert(retrieved == null)
    }

    @Test
    fun insertExpense_withSameId_replacesExisting() = runTest {
        val expense1 = createTestExpense(
            id = "1",
            amount = 100.0
        )
        val expense2 = createTestExpense(
            id = "1",
            amount = 200.0
        )

        dao.insertExpense(expense1)
        dao.insertExpense(expense2)

        val retrieved = dao.getExpenseById("1")
        assert(retrieved?.amount == 200.0)

        val allExpenses = dao.getAllExpenses().first()
        assert(allExpenses.size == 1)
    }
}