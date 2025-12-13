package com.tirexdev.expensetracker.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.tirexdev.expensetracker.R
import com.tirexdev.expensetracker.domain.model.Category

fun String.toCategory(): Category? = runCatching { Category.valueOf(this.uppercase()) }.getOrNull()

@Composable
fun Category.getLocalizedName(): String {
    return when (this) {
        Category.FOOD -> stringResource(R.string.category_food)
        Category.GROCERIES -> stringResource(R.string.category_groceries)
        Category.TRANSPORT -> stringResource(R.string.category_transport)
        Category.HOUSING -> stringResource(R.string.category_housing)
        Category.UTILITIES -> stringResource(R.string.category_utilities)
        Category.SUBSCRIPTIONS -> stringResource(R.string.category_subscriptions)
        Category.ENTERTAINMENT -> stringResource(R.string.category_entertainment)
        Category.SHOPPING -> stringResource(R.string.category_shopping)
        Category.HEALTHCARE -> stringResource(R.string.category_healthcare)
        Category.EDUCATION -> stringResource(R.string.category_education)
        Category.SPORTS -> stringResource(R.string.category_sports)
        Category.OTHER -> stringResource(R.string.category_other)
    }
}