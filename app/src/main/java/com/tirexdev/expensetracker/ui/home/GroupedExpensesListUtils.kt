package com.tirexdev.expensetracker.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.tirexdev.expensetracker.R
import com.tirexdev.expensetracker.domain.model.Expense
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

/**
 * Returns a user-friendly date label for the UI, using string resources for today and yesterday.
 */
@Composable
fun getDateLabel(date: LocalDate, today: LocalDate, yesterday: LocalDate): String {
    return when (date) {
        today -> stringResource(R.string.home_today)
        yesterday -> stringResource(R.string.home_yesterday)
        else -> date.format(DATE_FORMATTER)
    }
}

/**
 * Builds a flat list of (date, expense) pairs to display date headers and expenses in order.
 */
fun buildDateExpenseList(grouped: Map<LocalDate, List<Expense>>): List<Pair<LocalDate, Expense?>> =
    grouped.entries
        .sortedByDescending { it.key }
        .flatMap { entry ->
            listOf(entry.key to null) + entry.value.map { entry.key to it }
        }

/**
 * Synchronizes the animated list of items with the new data list, handling enter and exit animations.
 */
suspend fun syncAnimatedItems(
    animatedItems: MutableList<Triple<LocalDate, Expense?, Boolean>>,
    dateExpenseList: List<Pair<LocalDate, Expense?>>,
) {
    val currentOrder = animatedItems.map { it.first to it.second }
    val orderChanged = currentOrder != dateExpenseList

    if (orderChanged) {
        // Animate exit for all items
        animatedItems.replaceAll { it.copy(third = false) }
        delay(350)
        animatedItems.clear()
        // Add new items hidden
        dateExpenseList.forEach { item ->
            animatedItems.add(Triple(item.first, item.second, false))
        }
        // Animate entry in order
        for (item in dateExpenseList) {
            val i =
                animatedItems.indexOfFirst { it.first == item.first && it.second?.id == item.second?.id }
            if (i != -1 && !animatedItems[i].third) {
                animatedItems[i] = animatedItems[i].copy(third = true)
                delay(100 )
            }
        }
    } else {
        // Animate exit for removed items
        animatedItems.forEachIndexed { i, item ->
            val pair = item.first to item.second
            if (dateExpenseList.none { it.first == pair.first && it.second?.id == pair.second?.id }) {
                animatedItems[i] = item.copy(third = false)
            }
        }
        delay(350)
        animatedItems.removeAll { item ->
            val pair = item.first to item.second
            dateExpenseList.none { it.first == pair.first && it.second?.id == pair.second?.id } && !item.third
        }
        // Add new items hidden in order
        dateExpenseList.forEachIndexed { index, item ->
            if (animatedItems.none { it.first == item.first && it.second?.id == item.second?.id }) {
                animatedItems.add(index, Triple(item.first, item.second, false))
            }
        }
        // Animate entry for new items
        for (item in dateExpenseList) {
            val i =
                animatedItems.indexOfFirst { it.first == item.first && it.second?.id == item.second?.id }
            if (i != -1 && !animatedItems[i].third) {
                animatedItems[i] = animatedItems[i].copy(third = true)
                delay(100)
            }
        }
    }
}