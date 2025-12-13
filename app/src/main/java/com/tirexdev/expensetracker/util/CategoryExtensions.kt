package com.tirexdev.expensetracker.util

import androidx.compose.runtime.Composable
import com.tirexdev.expensetracker.R
import com.tirexdev.expensetracker.domain.model.Category

fun String.toCategory(): Category? = runCatching { Category.valueOf(this.uppercase()) }.getOrNull()

@Composable
fun Category.getLocalizedName(): String {
    val context = androidx.compose.ui.platform.LocalContext.current
    val resId = when (this) {
        Category.FOOD -> R.string.category_food
        Category.GROCERIES -> R.string.category_groceries
        Category.TRANSPORT -> R.string.category_transport
        Category.HOUSING -> R.string.category_housing
        Category.UTILITIES -> R.string.category_utilities
        Category.SUBSCRIPTIONS -> R.string.category_subscriptions
        Category.ENTERTAINMENT -> R.string.category_entertainment
        Category.SHOPPING -> R.string.category_shopping
        Category.HEALTHCARE -> R.string.category_healthcare
        Category.EDUCATION -> R.string.category_education
        Category.SPORTS -> R.string.category_sports
        Category.OTHER -> R.string.category_other
    }
    return context.getString(resId)
}