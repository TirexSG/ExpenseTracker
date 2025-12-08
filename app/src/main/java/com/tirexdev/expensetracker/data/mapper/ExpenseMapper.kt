package com.tirexdev.expensetracker.data.mapper

import com.tirexdev.expensetracker.data.local.entity.ExpenseEntity
import com.tirexdev.expensetracker.domain.model.Expense

/**
 * Mapper for converting between Expense domain model and ExpenseEntity
 * Provides bidirectional conversion with extension functions
 */
object ExpenseMapper {

    /**
     * Converts ExpenseEntity (database layer) to Expense (domain layer)
     */
    fun ExpenseEntity.toDomain(): Expense {
        return Expense(
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

    /**
     * Converts Expense (domain layer) to ExpenseEntity (database layer)
     */
    fun Expense.toEntity(): ExpenseEntity {
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

    /**
     * Converts a list of ExpenseEntity to a list of Expense
     * Useful for mapping Flow<List<ExpenseEntity>> to Flow<List<Expense>>
     */
    fun List<ExpenseEntity>.toDomain(): List<Expense> {
        return map { it.toDomain() }
    }

    /**
     * Converts a list of Expense to a list of ExpenseEntity
     * Useful for bulk insert operations
     */
    fun List<Expense>.toEntity(): List<ExpenseEntity> {
        return map { it.toEntity() }
    }
}