package com.tirexdev.expensetracker.ui.navigation.navigationbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.tirexdev.expensetracker.R
import com.tirexdev.expensetracker.ui.navigation.Routes

data class BottomNavItem(
    val route: Routes,
    val titleRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        route = Routes.Home,
        titleRes = R.string.nav_home,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    BottomNavItem(
        route = Routes.Statistics,
        titleRes = R.string.nav_statistics,
        selectedIcon = Icons.Filled.BarChart,
        unselectedIcon = Icons.Outlined.BarChart
    )
)