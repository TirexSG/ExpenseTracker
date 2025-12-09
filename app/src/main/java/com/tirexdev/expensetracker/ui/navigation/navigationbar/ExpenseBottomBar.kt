package com.tirexdev.expensetracker.ui.navigation.navigationbar

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import com.tirexdev.expensetracker.ui.navigation.Routes

@Composable
fun ExpenseBottomBar(
    navController: NavHostController,
    currentDestination: NavDestination?
) {
    NavigationBar {
        bottomNavItems.forEach { item ->
            val selected = currentDestination?.hasRoute(item.route::class) == true

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(Routes.Home) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = stringResource(item.titleRes)
                    )
                },
                label = {
                    Text(text = stringResource(item.titleRes))
                }
            )
        }
    }
}