package com.tirexdev.expensetracker.domain.model

import java.time.LocalDateTime
import java.util.UUID

data class Expense(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val amount: Double,
    val category: String,
    val customColor: Int? = null,
    val customIcon: String? = null,
    val date: LocalDateTime = LocalDateTime.now(),
    val paymentMethod: PaymentMethod? = null,
    val tags: List<String> = emptyList(),
    val location: String? = null
)