package com.tirexdev.expensetracker.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.tirexdev.expensetracker.domain.model.Category

/**
 * Configuration for category visual representation
 * Contains colors and icons for each category
 */
data class CategoryVisuals(
    val color: Color,
    val icon: ImageVector
)

object CategoryConfig {

    // Colors
    private val colorFood = Color(0xFFFF6B6B)
    private val colorGroceries = Color(0xFF51CF66)
    private val colorTransport = Color(0xFF4ECDC4)
    private val colorHousing = Color(0xFF845EC2)
    private val colorUtilities = Color(0xFFFFC75F)
    private val colorSubscriptions = Color(0xFFF38181)
    private val colorEntertainment = Color(0xFFFF6B9D)
    private val colorShopping = Color(0xFF00D2FC)
    private val colorHealthcare = Color(0xFF95E1D3)
    private val colorEducation = Color(0xFF6C5CE7)
    private val colorSports = Color(0xFFFF9671)
    private val colorOther = Color(0xFFB4B4B8)

    /**
     * Get visual configuration for a category
     */
    fun getVisuals(category: Category): CategoryVisuals = when (category) {
        Category.FOOD -> CategoryVisuals(
            color = colorFood,
            icon = Icons.Default.Restaurant
        )
        Category.GROCERIES -> CategoryVisuals(
            color = colorGroceries,
            icon = Icons.Default.ShoppingCart
        )
        Category.TRANSPORT -> CategoryVisuals(
            color = colorTransport,
            icon = Icons.Default.DirectionsCar
        )
        Category.HOUSING -> CategoryVisuals(
            color = colorHousing,
            icon = Icons.Default.Home
        )
        Category.UTILITIES -> CategoryVisuals(
            color = colorUtilities,
            icon = Icons.Default.Lightbulb
        )
        Category.SUBSCRIPTIONS -> CategoryVisuals(
            color = colorSubscriptions,
            icon = Icons.Default.Subscriptions
        )
        Category.ENTERTAINMENT -> CategoryVisuals(
            color = colorEntertainment,
            icon = Icons.Default.Movie
        )
        Category.SHOPPING -> CategoryVisuals(
            color = colorShopping,
            icon = Icons.Default.ShoppingBag
        )
        Category.HEALTHCARE -> CategoryVisuals(
            color = colorHealthcare,
            icon = Icons.Default.LocalHospital
        )
        Category.EDUCATION -> CategoryVisuals(
            color = colorEducation,
            icon = Icons.Default.School
        )
        Category.SPORTS -> CategoryVisuals(
            color = colorSports,
            icon = Icons.Default.FitnessCenter
        )
        Category.OTHER -> CategoryVisuals(
            color = colorOther,
            icon = Icons.Default.MoreHoriz
        )
    }

    /**
     * Get all available categories
     */
    fun getAllCategories(): List<Category> = Category.entries
}