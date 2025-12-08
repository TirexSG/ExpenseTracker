package com.tirexdev.expensetracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.tirexdev.expensetracker.data.local.Converters
import com.tirexdev.expensetracker.domain.model.PaymentMethod
import java.time.LocalDateTime

/**
 * Room database entity representing an Expense
 * Maps to the 'expenses' table in the database
 */
@Entity(tableName = "expenses")
@TypeConverters(Converters::class)
data class ExpenseEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val amount: Double,
    val category: String,
    val customColor: Int?,
    val customIcon: String?,
    val date: LocalDateTime,
    val paymentMethod: PaymentMethod?,
    val tags: List<String>,
    val location: String?
)