package com.tirexdev.expensetracker.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tirexdev.expensetracker.ui.navigation.ExpenseNavGraph
import com.tirexdev.expensetracker.ui.navigation.navigationbar.ExpenseBottomBar
import com.tirexdev.expensetracker.ui.navigation.navigationbar.bottomNavItems

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination

    val showBottomBar = bottomNavItems.any { item ->
        currentDestination?.hasRoute(item.route::class) == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                ExpenseBottomBar(
                    navController = navController,
                    currentDestination = currentDestination
                )
            }
        }
    ) { innerPadding ->
        ExpenseNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}