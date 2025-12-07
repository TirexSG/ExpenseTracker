package com.tirexdev.expensetracker.domain.model

import androidx.annotation.StringRes
import com.tirexdev.expensetracker.R

enum class Category(@param:StringRes val displayNameRes: Int) {
    FOOD(R.string.category_food),
    GROCERIES(R.string.category_groceries),
    TRANSPORT(R.string.category_transport),
    HOUSING(R.string.category_housing),
    UTILITIES(R.string.category_utilities),
    SUBSCRIPTIONS(R.string.category_subscriptions),
    ENTERTAINMENT(R.string.category_entertainment),
    SHOPPING(R.string.category_shopping),
    HEALTHCARE(R.string.category_healthcare),
    EDUCATION(R.string.category_education),
    SPORTS(R.string.category_sports),
    OTHER(R.string.category_other)
}