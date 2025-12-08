package com.tirexdev.expensetracker.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tirexdev.expensetracker.domain.model.PaymentMethod
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Type converters for Room Database
 * Converts complex types to primitive types that Room can persist
 */
class Converters {

    private val gson = Gson()

    /**
     * Converts LocalDateTime to Unix timestamp (milliseconds)
     * Room stores this as Long in the database
     */
    @TypeConverter
    fun fromLocalDateTime(date: LocalDateTime): Long {
        return date.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli()
    }

    /**
     * Converts Unix timestamp back to LocalDateTime
     * Uses UTC timezone for consistency
     */
    @TypeConverter
    fun toLocalDateTime(timestamp: Long): LocalDateTime {
        return LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timestamp),
            ZoneId.of("UTC")
        )
    }

    /**
     * Converts PaymentMethod enum to String (enum name)
     * Stores "CASH", "CREDIT_CARD", etc. in database
     */
    @TypeConverter
    fun fromPaymentMethod(paymentMethod: PaymentMethod?): String? {
        return paymentMethod?.name
    }

    /**
     * Converts String back to PaymentMethod enum
     * Returns null if string doesn't match any enum value
     */
    @TypeConverter
    fun toPaymentMethod(value: String?): PaymentMethod? {
        return value?.let {
            try {
                PaymentMethod.valueOf(it)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }

    /**
     * Converts List<String> to JSON string
     * Example: ["work", "lunch"] → '["work","lunch"]'
     */
    @TypeConverter
    fun fromStringList(list: List<String>): String {
        return gson.toJson(list)
    }

    /**
     * Converts JSON string back to List<String>
     * Example: '["work","lunch"]' → ["work", "lunch"]
     */
    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }
}