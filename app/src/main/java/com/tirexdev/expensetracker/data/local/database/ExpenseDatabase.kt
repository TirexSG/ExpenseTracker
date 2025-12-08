package com.tirexdev.expensetracker.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tirexdev.expensetracker.data.local.Converters
import com.tirexdev.expensetracker.data.local.dao.ExpenseDao
import com.tirexdev.expensetracker.data.local.entity.ExpenseEntity

/**
 * Room Database for Expense Tracker
 * Provides access to DAOs and manages database lifecycle
 */
@Database(
    entities = [ExpenseEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ExpenseDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao

    companion object {
        const val DATABASE_NAME = "expense_tracker_db"
    }
}